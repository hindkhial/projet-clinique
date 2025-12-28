package com.ehei.patient.controller;

import com.ehei.patient.dto.PatientCreateRequeste;
import com.ehei.patient.dto.PatientResponse;
import com.ehei.patient.dto.PatientUpdateRequeste;
import com.ehei.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/create")
    public ResponseEntity<PatientResponse> create(@RequestBody PatientCreateRequeste patientDto){
        PatientResponse response = patientService.createPatient(patientDto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<PatientResponse>> getAllPatients(){
        List<PatientResponse> response = patientService.getAll();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable String id){
        PatientResponse response = patientService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable String id, @RequestBody PatientUpdateRequeste patientDto){
        PatientResponse response = patientService.update(Long.valueOf(id), patientDto);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable String id){
        patientService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok().body("Patient has been deleted");
    }
}