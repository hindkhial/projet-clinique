package com.ehei.rdv.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(namw = "patient-service", url = "${services.patient.url}")
public interface PatientClient {
    @GetMapping("/patients/{id}")
    ResponseEntity<PatientResponse> getPatientById(@PathVariable("id") Long id);
}
