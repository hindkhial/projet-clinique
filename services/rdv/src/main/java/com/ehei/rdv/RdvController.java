package com.ehei.rdv;

import com.ehei.rdv.DTO.RdvCreateRequestDto;
import com.ehei.rdv.DTO.RdvResponseDto;
import com.ehei.rdv.DTO.RdvUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rdv")
@RequiredArgsConstructor
public class RdvController {

    private final RdvService rdvService;

    @PostMapping("/create")
    public ResponseEntity<RdvResponseDto> createRdv(@RequestBody RdvCreateRequestDto requestDto){
        RdvResponseDto response = rdvService.createRdv(requestDto);
        return ResponseEntity.ok().body(response);
    }


    @PostMapping("/update/{id}")
    public ResponseEntity<RdvResponseDto> updateRdv(@PathVariable String id, @RequestBody RdvUpdateRequestDto requestDto){
        RdvResponseDto response = rdvService.updateRdv(Long.valueOf(id), requestDto);
        return ResponseEntity.ok().body(response);
    }
}
