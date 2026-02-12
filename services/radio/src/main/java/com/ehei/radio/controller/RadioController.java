package com.ehei.radio.controller;

import com.ehei.radio.dto.CreateRadioRequest;
import com.ehei.radio.dto.RadioResponse;
import com.ehei.radio.service.MinioService;
import com.ehei.radio.service.RadioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/Radio")
@RequiredArgsConstructor
@Slf4j
public class RadioController {

    private final RadioService radioService;
    private final MinioService minioService;
    private final ObjectMapper objectMapper;

    @PostMapping("/Upload")
    public ResponseEntity<RadioResponse> uploadRadio(
            @RequestParam("radioData") String radioDataJson,
            @RequestParam("file") MultipartFile file) {

        log.info("POST /Radio/Upload - Upload d'un nouveau radio");

        try {
            CreateRadioRequest request = objectMapper.readValue(radioDataJson, CreateRadioRequest.class);
            RadioResponse response = radioService.createRadio(request, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erreur lors de la creation du radio: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la creation du radio", e);
        }
    }

    @GetMapping("/Patient/{id}")
    public ResponseEntity<List<RadioResponse>> getRadiosByPatient(@PathVariable Long id) {
        log.info("GET /Radio/Patient/{} - Recuperation des radios du patient", id);

        List<RadioResponse> radios = radioService.getRadiosByPatient(id);
        return ResponseEntity.ok(radios);
    }

    @GetMapping("/Radiologue/{id}")
    public ResponseEntity<List<RadioResponse>> getRadiosByRadiologue(@PathVariable Long id) {
        log.info("GET /Radio/Radiologue/{} - Recuperation des radios du radiologue", id);

        List<RadioResponse> radios = radioService.getRadiosByRadiologue(id);
        return ResponseEntity.ok(radios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RadioResponse> getRadioById(@PathVariable Long id) {
        log.info("GET /Radio/{} - Recuperation du radio", id);

        RadioResponse radio = radioService.getRadioById(id);
        return ResponseEntity.ok(radio);
    }

    @GetMapping("/")
    public ResponseEntity<List<RadioResponse>> getAllRadios() {
        log.info("GET /Radio/ - Recuperation de tous les radios");

        List<RadioResponse> radios = radioService.getAllRadios();
        return ResponseEntity.ok(radios);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadRadioImage(@PathVariable Long id) {
        log.info("GET /Radio/{}/download - Telechargement de l'image radio", id);

        RadioResponse radio = radioService.getRadioById(id);

        if (radio.getFichierRadioUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        InputStream imageStream = minioService.downloadRadioImage(radio.getFichierRadioUrl());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=radio_" + id + ".jpg")
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(imageStream));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRadio(@PathVariable Long id) {
        log.info("DELETE /Radio/{} - Suppression du radio", id);

        radioService.deleteRadio(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Radio Service is running");
    }
}

