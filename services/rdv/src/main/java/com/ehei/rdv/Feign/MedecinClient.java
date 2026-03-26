package com.ehei.rdv.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "staff-client")
public interface MedecinClient {

    @GetMapping("/medecins/{id}")
    ResponseEntity<MedecinResponseDto> getMedecinById(@PathVariable("id") Long id);
}