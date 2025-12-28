package com.ehei.rapport.service;


import com.ehei.rapport.dto.CreateRapportRequest;
import com.ehei.rapport.dto.RapportResponse;
import com.ehei.rapport.exception.InvalidRapportException;
import com.ehei.rapport.exception.RapportNotFoundException;
import com.ehei.rapport.mapper.RapportMapper;
import com.ehei.rapport.model.Rapport;
import com.ehei.rapport.repository.RapportRepository;
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

    //creation du rapport
    @Transactional
    public RapportResponse createRapport(CreateRapportRequest request) {
        log.info("creation d'un nouveau rapport pour patient: {}", request.getPatientId());

        //validation
        if (request.getPatientId() == null || request.getPatientId() <= 0) {
            throw new InvalidRapportException("Patient ID est invalide");
        }

        if (request.getMedecinId() == null || request.getMedecinId() <= 0) {
            throw new InvalidRapportException("Medecin ID est invalide");
        }

        if (request.getDateRapport() == null) {
            throw new InvalidRapportException("Date du rapport est obligatoire");
        }

        Rapport rapport = rapportMapper.toEntity(request);

        Rapport savedRapport = rapportRepository.save(rapport);

        log.info("Rapport cree avec succes - ID: {}", savedRapport.getId());

        return rapportMapper.toResponse(savedRapport);
    }

    @Transactional(readOnly = true)
    public List<RapportResponse> getRapportsByPatient(Long patientId) {
        log.info("recuperation des rapports du patient: {}", patientId);

        if (patientId == null || patientId <= 0) {
            throw new InvalidRapportException("Patient ID est invalide");
        }

        List<Rapport> rapports = rapportRepository.findByPatientId(patientId);

        return rapports.stream()
                .map(rapportMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<RapportResponse> getRapportsByMedecin(Long medecinId) {
        log.info("recuperation des rapports du medecin: {}", medecinId);

        if (medecinId == null || medecinId <= 0) {
            throw new InvalidRapportException("Medecin ID est invalide");
        }

        List<Rapport> rapports = rapportRepository.findByMedecinId(medecinId);

        return rapports.stream()
                .map(rapportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RapportResponse getRapportById(Long id) {
        log.info("Recuperation du rapport: {}", id);

        if (id == null || id <= 0) {
            throw new InvalidRapportException("Rapport ID est invalide");
        }

        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RapportNotFoundException(id));

        return rapportMapper.toResponse(rapport);
    }

    @Transactional(readOnly = true)
    public List<RapportResponse> getAllRapports() {
        log.info("recuperation de tous les rapports");

        List<Rapport> rapports = rapportRepository.findAll();

        return rapports.stream()
                .map(rapportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RapportResponse generatePdf(Long id) {
        log.info("generation du rapport PDF: {}", id);

        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RapportNotFoundException(id));

        // Generate PDF
        InputStream pdfStream = pdfGeneratorService.generatePdf(rapport);

        // Upload to MinIO
        String pdfUrl = minioService.uploadPdf(pdfStream, rapport.getId());

        // Update rapport
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