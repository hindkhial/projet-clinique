package com.ehei.orchestrator.client;

import com.ehei.orchestrator.dto.ApiResponse;
import com.ehei.orchestrator.dto.PatientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "${patient.service.url}/patient")
public interface PatientClient {
    @GetMapping("/{id}")
    ApiResponse<PatientResponse> getPatientById(@PathVariable("id") Long id);
}
