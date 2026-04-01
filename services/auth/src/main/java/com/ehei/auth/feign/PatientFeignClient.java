package com.ehei.auth.feign;

import com.ehei.auth.DTOS.ApiResponse;
import com.ehei.auth.DTOS.patient.PatientCreateRequeste;
import com.ehei.auth.DTOS.patient.PatientResponse;
import com.ehei.auth.DTOS.patient.PatientUpdateRequeste;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "PATIENT-SERVICE",
        url = "${patient.service.url}"
)
public interface PatientFeignClient {
    @PostMapping("/patient/create")
   ApiResponse<PatientResponse> create(@RequestBody PatientCreateRequeste patientDto);

    @GetMapping("/patient/{id}")
    ApiResponse<PatientResponse> getPatientById(@PathVariable String id);

    @PutMapping("/patient/update/{id}")
    ApiResponse<PatientResponse> updatePatient(@PathVariable String id, @RequestBody  PatientUpdateRequeste patientDto);

    @DeleteMapping("/patient/delete/{id}")
    ApiResponse<String> deletePatient(@PathVariable String id);
}