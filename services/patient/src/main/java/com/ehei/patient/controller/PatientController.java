package com.ehei.patient.controller;

import com.ehei.patient.dto.ApiResponse;
import com.ehei.patient.dto.PatientCreateRequeste;
import com.ehei.patient.dto.PatientResponse;
import com.ehei.patient.dto.PatientUpdateRequeste;
import com.ehei.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PatientResponse>> create(@RequestBody @Valid PatientCreateRequeste patientDto){
        PatientResponse response = patientService.createPatient(patientDto);
        return ResponseEntity.ok().body(ApiResponse.success(response,
                "patient creee avec succes."));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAllPatients(){
        List<PatientResponse> response = patientService.getAll();
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(@PathVariable String id){
        PatientResponse response = patientService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(@PathVariable String id, @RequestBody @Valid PatientUpdateRequeste patientDto){
        PatientResponse response = patientService.update(Long.valueOf(id), patientDto);
        return ResponseEntity.ok().body(ApiResponse.success(response,
                "Patient modifie avec succes."));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deletePatient(@PathVariable String id){
        patientService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok().body(ApiResponse.success("patient supprime avec succes."));
    }

    @GetMapping("/search")
    public ResponseEntity<com.ehei.patient.dto.ApiResponse<List<PatientResponse>>> searchPatients(
            @RequestParam String q
    ) {
        List<PatientResponse> response = patientService.searchPatients(q);
        return ResponseEntity.ok().body(com.ehei.patient.dto.ApiResponse.success(response));
    }
}