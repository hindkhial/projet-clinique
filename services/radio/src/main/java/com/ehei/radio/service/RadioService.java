package com.ehei.radio.service;

import com.ehei.radio.dto.CreateRadioRequest;
import com.ehei.radio.dto.RadioResponse;
import com.ehei.radio.exception.InvalidRadioException;
import com.ehei.radio.exception.RadioNotFoundException;
import com.ehei.radio.kafka.KafkaProducer;
import com.ehei.radio.mapper.RadioMapper;
import com.ehei.radio.model.Radio;
import com.ehei.radio.repository.RadioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RadioService {

    private final RadioRepository radioRepository;
    private final RadioMapper radioMapper;
    private final MinioService minioService;
    private final KafkaProducer kafkaProducer;

    @Transactional
    public RadioResponse createRadio(CreateRadioRequest request, MultipartFile file) {
        log.info("Creation d'un nouveau radio pour patient: {}", request.getPatientId());

        validateRequest(request);
        validateFile(file);

        // Ne pas sauvegarder encore, juste créer l'entité
        Radio radio = radioMapper.toEntity(request);

        // D'abord sauvegarder pour obtenir un ID
        Radio tempRadio = radioRepository.save(radio);
        Long radioId = tempRadio.getId();

        try {
            // Upload le fichier avec l'ID
            String fileUrl = minioService.uploadRadioImage(file, radioId);

            // Maintenant mettre à jour avec l'URL du fichier
            tempRadio.setFichierRadioUrl(fileUrl);
            Radio savedRadio = radioRepository.save(tempRadio);

            kafkaProducer.sendRadioUploadedEvent(savedRadio);

            log.info("Radio cree avec succes - ID: {}", savedRadio.getId());

            return radioMapper.toResponse(savedRadio);

        } catch (Exception e) {
            log.error("Erreur lors de l'upload du fichier: {}", e.getMessage());
            radioRepository.deleteById(radioId);
            throw new InvalidRadioException("Erreur lors de l'upload du fichier radio");
        }
    }

    @Transactional(readOnly = true)
    public List<RadioResponse> getRadiosByPatient(Long patientId) {
        log.info("Recuperation des radios du patient: {}", patientId);

        if (patientId == null || patientId <= 0) {
            throw new InvalidRadioException("Patient ID est invalide");
        }

        return radioRepository.findByPatientId(patientId).stream()
                .map(radioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RadioResponse> getRadiosByRadiologue(Long radiologueId) {
        log.info("Recuperation des radios du radiologue: {}", radiologueId);

        if (radiologueId == null || radiologueId <= 0) {
            throw new InvalidRadioException("Radiologue ID est invalide");
        }

        return radioRepository.findByRadiologueId(radiologueId).stream()
                .map(radioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RadioResponse> getPredictedRadiosByRadiologue(Long radiologueId) {
        log.info("Recuperation des radios PREDICTED du radiologue: {}", radiologueId);

        if (radiologueId == null || radiologueId <= 0) {
            throw new InvalidRadioException("Radiologue ID est invalide");
        }

        return radioRepository.findByRadiologueIdAndStatus(radiologueId, com.ehei.radio.model.RadioStatus.PREDICTED).stream()
                .map(radioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RadioResponse getRadioById(Long id) {
        log.info("Recuperation du radio: {}", id);

        if (id == null || id <= 0) {
            throw new InvalidRadioException("Radio ID est invalide");
        }

        Radio radio = radioRepository.findById(id)
                .orElseThrow(() -> new RadioNotFoundException(id));

        return radioMapper.toResponse(radio);
    }

    @Transactional(readOnly = true)
    public List<RadioResponse> getAllRadios() {
        log.info("Recuperation de tous les radios");

        return radioRepository.findAll().stream()
                .map(radioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRadio(Long id) {
        log.info("Suppression du radio: {}", id);

        if (id == null || id <= 0) {
            throw new InvalidRadioException("Radio ID est invalide");
        }

        Radio radio = radioRepository.findById(id)
                .orElseThrow(() -> new RadioNotFoundException(id));

        if (radio.getFichierRadioUrl() != null) {
            try {
                minioService.deleteRadioImage(radio.getFichierRadioUrl());
                log.info("Fichier radio supprime de MinIO");
            } catch (Exception e) {
                log.error("Erreur lors de la suppression du fichier: {}", e.getMessage());
            }
        }

        radioRepository.deleteById(id);
        log.info("Radio supprime: {}", id);
    }

    @Transactional
    public void updateFromAiPrediction(com.ehei.ai.proto.AIPredictionEvent event) {
        log.info("Mise a jour du radio {} à partir de la prediction AI", event.getRadioId());
        
        Long radioId = Long.parseLong(event.getRadioId());
        Radio radio = radioRepository.findById(radioId)
            .orElseThrow(() -> new RadioNotFoundException(radioId));
            
        radio.setStatus(com.ehei.radio.model.RadioStatus.PREDICTED);
        radio.setTypeMaladie(event.getTypeMaladie());
        radio.setConfidence(event.getConfidence());
        radio.setRiskLevel(event.getRiskLevel());
        radio.setHeatmapUrl(event.getHeatmapUrl());
        
        radioRepository.save(radio);
        log.info("Prediction AI enregistrée pour le radio {}", radioId);
    }

    @Transactional
    public RadioResponse validateRadio(Long id, com.ehei.radio.dto.RadioValidationRequest request) {
        log.info("Validation du radio: {}", id);

        Radio radio = radioRepository.findById(id)
                .orElseThrow(() -> new RadioNotFoundException(id));

        radio.setStatus(com.ehei.radio.model.RadioStatus.VALIDATED);
        
        if ("MODIFIE".equalsIgnoreCase(request.getDecision())) {
            if (request.getTypeMaladie() != null) radio.setTypeMaladie(request.getTypeMaladie());
            if (request.getConfidence() != null) radio.setConfidence(request.getConfidence());
            if (request.getRiskLevel() != null) radio.setRiskLevel(request.getRiskLevel());
        }

        if (request.getCommentaire() != null) {
            radio.setNotesRadiologue(request.getCommentaire());
        }

        Radio savedRadio = radioRepository.save(radio);
        
        // Notify consultation service that radio is validated
        kafkaProducer.sendRadioValidatedEvent(savedRadio);

        log.info("Radio {} validé avec succès", id);
        return radioMapper.toResponse(savedRadio);
    }

    private void validateRequest(CreateRadioRequest request) {
        if (request.getPatientId() == null || request.getPatientId() <= 0) {
            throw new InvalidRadioException("Patient ID invalide");
        }
        if (request.getRadiologueId() == null || request.getRadiologueId() <= 0) {
            throw new InvalidRadioException("Radiologue ID invalide");
        }
        if (request.getDateRadio() == null) {
            throw new InvalidRadioException("Date est obligatoire");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRadioException("Fichier radio est obligatoire");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidRadioException("Le fichier doit etre une image");
        }
    }
}