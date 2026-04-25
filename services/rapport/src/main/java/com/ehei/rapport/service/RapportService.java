package com.ehei.rapport.service;

import com.ehei.rapport.dto.CreateRapportRequest;
import com.ehei.rapport.dto.RapportResponse;
import com.ehei.rapport.exception.InvalidRapportException;
import com.ehei.rapport.exception.RapportNotFoundException;
import com.ehei.rapport.kafka.KafkaProducer;
import com.ehei.rapport.mapper.RapportMapper;
import com.ehei.rapport.model.Rapport;
import com.ehei.rapport.repository.RapportRepository;
import consultation.events.ConsultationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RapportService {

    private final RapportRepository rapportRepository;
    private final RapportMapper rapportMapper;
    private final PdfGeneratorService pdfGeneratorService;
    private final MinioService minioService;
    private final KafkaProducer kafkaProducer;

    // ── Manual creation (REST) ─────────────────────────────────────────────

    @Transactional
    public RapportResponse createRapport(CreateRapportRequest request) {
        log.info("creation d'un nouveau rapport pour patient: {}", request.getPatientId());

        validateRequest(request);

        Rapport rapport = rapportMapper.toEntity(request);
        Rapport savedRapport = rapportRepository.save(rapport);

        log.info("Rapport cree avec succes - ID: {}", savedRapport.getId());
        try {
            InputStream pdfStream = pdfGeneratorService.generatePdf(savedRapport);
            String pdfUrl = minioService.uploadPdf(pdfStream, savedRapport.getId());

            savedRapport.setPdfFileUrl(pdfUrl);
            savedRapport = rapportRepository.save(savedRapport);

            kafkaProducer.sendRapportGeneratedEvent(savedRapport);

            log.info("PDF generer et uploader: {}", pdfUrl);
        } catch (Exception e) {
            log.error("PDF generation failed: {}", e.getMessage());
        }

        return rapportMapper.toResponse(savedRapport);
    }

    // ── Auto creation from ConsultationCreatedEvent (Kafka) ───────────────

    @Transactional
    public void createFromConsultationEvent(ConsultationCreatedEvent event) {
        log.info("Creation rapport depuis consultation id: {} type: {}",
                event.getId(), event.getType());

        Rapport rapport = rapportMapper.fromConsultationEvent(event);
        Rapport savedRapport = rapportRepository.save(rapport);

        log.info("Rapport cree avec succes - ID: {}", savedRapport.getId());
        try {
            InputStream pdfStream = pdfGeneratorService.generatePdf(savedRapport);
            String pdfUrl = minioService.uploadPdf(pdfStream, savedRapport.getId());

            savedRapport.setPdfFileUrl(pdfUrl);
            savedRapport = rapportRepository.save(savedRapport);

            kafkaProducer.sendRapportGeneratedEvent(savedRapport);

            log.info("PDF generer et uploader: {}", pdfUrl);
        } catch (Exception e) {
            log.error("PDF generation failed: {}", e.getMessage());
        }
    }

    // ── Queries ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RapportResponse> getRapportsByPatient(Long patientId) {
        log.info("recuperation des rapports du patient: {}", patientId);

        if (patientId == null || patientId <= 0) {
            throw new InvalidRapportException("Patient ID est invalide");
        }

        return rapportRepository.findByPatientId(patientId)
                .stream()
                .map(rapportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RapportResponse> getRapportsByMedecin(Long medecinId) {
        log.info("recuperation des rapports du medecin: {}", medecinId);

        if (medecinId == null || medecinId <= 0) {
            throw new InvalidRapportException("Medecin ID est invalide");
        }

        return rapportRepository.findByMedecinId(medecinId)
                .stream()
                .map(rapportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RapportResponse getRapportById(Long id) {
        log.info("Recuperation du rapport: {}", id);

        if (id == null || id <= 0) {
            throw new InvalidRapportException("Rapport ID est invalide");
        }

        return rapportMapper.toResponse(
                rapportRepository.findById(id)
                        .orElseThrow(() -> new RapportNotFoundException(id))
        );
    }

    @Transactional(readOnly = true)
    public List<RapportResponse> getAllRapports() {
        log.info("recuperation de tous les rapports");
        return rapportRepository.findAll()
                .stream()
                .map(rapportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRapport(Long id) {
        log.info("suppression de rapport: {}", id);

        if (id == null || id <= 0) {
            throw new InvalidRapportException("id rapport invalide");
        }

        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RapportNotFoundException(id));

        if (rapport.getPdfFileUrl() != null) {
            try {
                minioService.deletePdf(rapport.getPdfFileUrl());
                log.info("PDF supprimer dans MinIO");
            } catch (Exception e) {
                log.error("suppression du PDF dans MinIO echoue: {}", e.getMessage());
            }
        }

        rapportRepository.deleteById(id);
        log.info("Rapport supprime: {}", id);
    }

    @Transactional
    public RapportResponse generatePdf(Long id) {
        log.info("generation du rapport PDF: {}", id);

        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RapportNotFoundException(id));

        InputStream pdfStream = pdfGeneratorService.generatePdf(rapport);
        String pdfUrl = minioService.uploadPdf(pdfStream, rapport.getId());

        rapport.setPdfFileUrl(pdfUrl);
        Rapport updated = rapportRepository.save(rapport);

        log.info("PDF generated: {}", pdfUrl);
        return rapportMapper.toResponse(updated);
    }

    private void validateRequest(CreateRapportRequest request) {
        if (request.getPatientId() == null || request.getPatientId() <= 0) {
            throw new InvalidRapportException("Invalid patient ID");
        }
        if (request.getMedecinId() == null || request.getMedecinId() <= 0) {
            throw new InvalidRapportException("Invalid medecin ID");
        }
        if (request.getDateRapport() == null) {
            throw new InvalidRapportException("Date est Obligatoire");
        }
    }
}