package com.ehei.staff.controllers;

import com.ehei.staff.DTOS.ApiResponse;
import com.ehei.staff.DTOS.medecin.MedecinCreateRequestDto;
import com.ehei.staff.DTOS.medecin.MedecinResponseDto;
import com.ehei.staff.DTOS.medecin.MedecinUpdateRequestDto;
import com.ehei.staff.services.MedecinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medecin")
@RequiredArgsConstructor
public class MedecinController {

    private final MedecinService medecinService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MedecinResponseDto>> createMedecin(@RequestBody @Valid MedecinCreateRequestDto requestDto){
        MedecinResponseDto response = medecinService.createMedecin(requestDto);
        return ResponseEntity.ok().body(ApiResponse.success(response,"medecin creee avec succes."));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<MedecinResponseDto>>> getAllMedecins(){
        List<MedecinResponseDto> response = medecinService.getAll();
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedecinResponseDto>> getMedecinById(@PathVariable String id){
        MedecinResponseDto response = medecinService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String> > deleteMedecinById(@PathVariable String id){
        medecinService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok().body(ApiResponse.success("Medecin supprime avec succes"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<MedecinResponseDto> > updateMedecinById(@PathVariable String id, @RequestBody @Valid MedecinUpdateRequestDto requestDto){
        MedecinResponseDto response = medecinService.updateById(Long.valueOf(id), requestDto);
        return ResponseEntity.ok().body(ApiResponse.success(response,"Medecin modifie avec succes."));
    }

}
