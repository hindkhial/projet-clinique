package com.ehei.patient.service;

import com.ehei.patient.dto.PatientCreateRequeste;
import com.ehei.patient.dto.PatientResponse;
import com.ehei.patient.dto.PatientUpdateRequeste;
import com.ehei.patient.events.PatientCreatedEvent;
import com.ehei.patient.exceptions.EmailAlreadyExistException;
import com.ehei.patient.exceptions.EntityNotFoundException;
import com.ehei.patient.mapper.PatientMapper;
import com.ehei.patient.model.Patient;
import com.ehei.patient.repository.PatientRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PatientResponse createPatient(PatientCreateRequeste requestDto) {
        // 1. Mapper le DTO vers le modèle
        Patient patient = patientMapper.toModel(requestDto);

        // 2. Vérifier si l'email existe déjà
        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new EmailAlreadyExistException(patient.getEmail());
        }

        // 3. Sauvegarder le patient
        Patient savedPatient = patientRepository.save(patient);

        // 4. Créer l'événement PatientCreated
        PatientCreatedEvent event = PatientCreatedEvent.from(
                savedPatient.getId(),
                savedPatient.getNom(),
                savedPatient.getPrenom(),
                savedPatient.getEmail(),
                savedPatient.getTel(),
                savedPatient.getDateNaissance()
        );

        // 5. Publier l'événement vers Kafka
        kafkaTemplate.send("patient-events", event);
        log.info("✅ Événement PatientCreated publié pour le patient ID: {} - Nom: {} {}",
                savedPatient.getId(), savedPatient.getNom(), savedPatient.getPrenom());

        // 6. Retourner la réponse
        return patientMapper.toResponseDto(savedPatient);
    }

    public List<PatientResponse> getAll() {
        List<Patient> patientListe = patientRepository.findAll();
        return patientMapper.toResponseDto(patientListe);
    }

    public PatientResponse getById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun patient n'existe avec id: " + id));
        return patientMapper.toResponseDto(patient);
    }

    public PatientResponse update(Long id, PatientUpdateRequeste patientDto) {
        // 1. Récupérer le patient existant
        Patient oldPatient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun patient n'existe avec id: " + id));

        // 2. Mettre à jour les champs
        oldPatient.setNom(patientDto.getNom());
        oldPatient.setPrenom(patientDto.getPrenom());
        oldPatient.setEmail(patientDto.getEmail());
        oldPatient.setDateNaissance(patientDto.getDateNaissance());
        oldPatient.setTel(patientDto.getTel());

        // 3. Vérifier si le nouveau email n'existe pas déjà
        if (patientRepository.existsByEmail(patientDto.getEmail()) &&
                !oldPatient.getEmail().equals(patientDto.getEmail())) {
            throw new EmailAlreadyExistException(patientDto.getEmail());
        }

        // 4. Sauvegarder et retourner
        return patientMapper.toResponseDto(patientRepository.save(oldPatient));
    }

    public void deleteById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun patient n'existe avec id: " + id));
        patientRepository.delete(patient);
    }
}