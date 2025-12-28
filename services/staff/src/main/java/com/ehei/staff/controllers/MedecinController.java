package com.ehei.staff.controllers;

import com.ehei.staff.DTOS.medecin.MedecinCreateRequestDto;
import com.ehei.staff.DTOS.medecin.MedecinResponseDto;
import com.ehei.staff.DTOS.medecin.MedecinUpdateRequestDto;
import com.ehei.staff.services.MedecinService;
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
    public ResponseEntity<MedecinResponseDto> createMedecin(@RequestBody MedecinCreateRequestDto requestDto){
        MedecinResponseDto response = medecinService.createMedecin(requestDto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<MedecinResponseDto>> getAllMedecins(){
        List<MedecinResponseDto> response = medecinService.getAll();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedecinResponseDto> getMedecinById(@PathVariable String id){
        MedecinResponseDto response = medecinService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMedecinById(@PathVariable String id){
        medecinService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok().body("Medecin successfully deleted");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MedecinResponseDto> updateMedecinById(@PathVariable String id, @RequestBody MedecinUpdateRequestDto requestDto){
        MedecinResponseDto response = medecinService.updateById(Long.valueOf(id), requestDto);
        return ResponseEntity.ok().body(response);
    }

}
