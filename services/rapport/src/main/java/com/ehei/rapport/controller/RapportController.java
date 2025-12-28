package com.ehei.rapport.controller;

import com.ehei.rapport.dto.CreateRapportRequest;
import com.ehei.rapport.dto.RapportResponse;
import com.ehei.rapport.service.MinioService;
import com.ehei.rapport.service.RapportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/rapport")
@RequiredArgsConstructor
@Slf4j
public class RapportController {

    private final RapportService rapportService;
    private final MinioService minioService;

    @PostMapping("/create")
    public ResponseEntity<RapportResponse> createRapport(@Valid @RequestBody CreateRapportRequest request){
        log.info("POST /rapport/create - creation d'un rapport pour patient: {}", request.getPatientId());
        RapportResponse rapportResponse = rapportService.createRapport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rapportResponse);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<RapportResponse>> getRapportsByPatient(@PathVariable Long id){
        log.info("GET /rapport/patient/{} - recuperation des rapports du patient", id);
        List<RapportResponse> rapports = rapportService.getRapportsByPatient(id);
        return ResponseEntity.ok(rapports);
    }

    @GetMapping("/medecin/{id}")
    public ResponseEntity<List<RapportResponse>> getRapportsByMedcin(@PathVariable Long id){
        log.info("GET /rapport/medecin/{} - recuperation des rapports du medecin", id);
        List<RapportResponse> rapports = rapportService.getRapportsByMedecin(id);
        return ResponseEntity.ok(rapports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RapportResponse> getRapportById(@PathVariable Long id){
        log.info("GET /rapport/{} - recuperation de rapport", id);
        RapportResponse rapport = rapportService.getRapportById(id);
        return ResponseEntity.ok(rapport);
    }

    @GetMapping("/")
    public ResponseEntity<List<RapportResponse>> getAllRapport(){
        log.info("GET /rapport/ - recuperation de tous les rapports");
        List<RapportResponse> rapports = rapportService.getAllRapports();
        return ResponseEntity.ok(rapports);
    }

    @PostMapping("/{id}/generatepdf")
    public ResponseEntity<RapportResponse> generatePdf(@PathVariable Long id) {
        log.info("POST /rapport/{}/generatepdf", id);
        RapportResponse rapport = rapportService.generatePdf(id);
        return ResponseEntity.ok(rapport);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadPdf(@PathVariable Long id) {
        log.info("GET /rapport/{}/download", id);

        // Get rapport
        RapportResponse rapport = rapportService.getRapportById(id);

        // Check if PDF exists
        if (rapport.getPdfFileUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        // Download PDF from MinIO
        InputStream pdfStream = minioService.downloadPdf(rapport.getPdfFileUrl());

        // Return PDF file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }

}
