package com.ehei.rdv.Controller;

import com.ehei.rdv.DTO.RdvCreateRequestDto;
import com.ehei.rdv.DTO.RdvResponseDto;
import com.ehei.rdv.DTO.RdvUpdateRequestDto;
import com.ehei.rdv.Service.RdvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/")
    public ResponseEntity<List<RdvResponseDto>> getAll(){
        List<RdvResponseDto> response = rdvService.getAll();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RdvResponseDto> getById(@PathVariable String id){
        RdvResponseDto response = rdvService.getById(Long.valueOf(id));
        return  ResponseEntity.ok().body(response) ;
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id){
        rdvService.deleteById(Long.valueOf(id));
        return  ResponseEntity.ok().body("rdv supprime avec succes.");
    }
}
