package com.ehei.rdv;

import com.ehei.rdv.DTO.RdvCreateRequestDto;
import com.ehei.rdv.DTO.RdvResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
