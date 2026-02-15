package com.ehei.auth.feign;

import com.ehei.auth.DTOS.ApiResponse;
import com.ehei.auth.DTOS.medecin.MedecinCreateRequestDto;
import com.ehei.auth.DTOS.medecin.MedecinResponseDto;
import com.ehei.auth.DTOS.medecin.MedecinUpdateRequestDto;
import jakarta.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "MEDECIN-SERVICE",
        url = "${staff.service.url}"
)
public interface MedecinFeignClient {
    @PostMapping("/medecin/create")
    ApiResponse<MedecinResponseDto> createMedecin(@RequestBody @Valid MedecinCreateRequestDto requestDto);


    @GetMapping("/medecin/")
    ApiResponse<List<MedecinResponseDto>> getAllMedecins();

    @GetMapping("/medecin/{id}")
    ApiResponse<MedecinResponseDto> getMedecinById(@PathVariable String id);

    @DeleteMapping("/medecin/delete/{id}")
    ApiResponse<String>  deleteMedecinById(@PathVariable String id);

    @PutMapping("/medecin/update/{id}")
    ApiResponse<MedecinResponseDto> updateMedecinById(@PathVariable String id, @RequestBody @Valid MedecinUpdateRequestDto requestDto);
}
