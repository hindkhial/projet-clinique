package com.ehei.rdv.Feign;

import com.ehei.rdv.DTO.staff.MedecinResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "staff-client", url = "${staff.service.url}")
public interface MedecinClient {

    @GetMapping("/medecins/{id}")
    ResponseEntity<MedecinResponseDto> getMedecinById(@PathVariable("id") Long id);
}