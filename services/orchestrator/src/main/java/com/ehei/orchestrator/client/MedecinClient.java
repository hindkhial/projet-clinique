package com.ehei.orchestrator.client;

import com.ehei.orchestrator.dto.ApiResponse;
import com.ehei.orchestrator.dto.MedecinResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "staff-service", url = "${staff.service.url}/medecin")
public interface MedecinClient {
    @GetMapping("/{id}")
    ApiResponse<MedecinResponse> getMedecinById(@PathVariable("id") Long id);
}
