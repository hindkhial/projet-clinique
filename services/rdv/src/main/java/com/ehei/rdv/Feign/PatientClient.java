package com.ehei.rdv.Feign;

import com.ehei.rdv.DTO.patient.PatientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient( name = "patient-service", url = "${patient.service.url}")
public interface PatientClient {
    @GetMapping("/patient/{id}")
    ResponseEntity<PatientResponseDto> getPatientById(@PathVariable("id") Long id);
}
