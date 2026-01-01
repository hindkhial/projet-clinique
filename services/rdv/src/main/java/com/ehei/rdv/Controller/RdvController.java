package com.ehei.rdv.Controller;

import com.ehei.rdv.DTO.ApiResponse;
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
    public ResponseEntity<ApiResponse<RdvResponseDto>> createRdv(@RequestBody RdvCreateRequestDto requestDto){
        RdvResponseDto response = rdvService.createRdv(requestDto);
        return ResponseEntity.ok().body(ApiResponse.success(response,"rdv creee avec succes."));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<RdvResponseDto>> updateRdv(@PathVariable String id, @RequestBody RdvUpdateRequestDto requestDto){
        RdvResponseDto response = rdvService.updateRdv(Long.valueOf(id), requestDto);
        return ResponseEntity.ok().body(ApiResponse.success(response,"rdv modifie avec succes"));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<RdvResponseDto>>> getAll(){
        List<RdvResponseDto> response = rdvService.getAll();
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RdvResponseDto>> getById(@PathVariable String id){
        RdvResponseDto response = rdvService.getById(Long.valueOf(id));
        return  ResponseEntity.ok().body(ApiResponse.success(response)) ;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable String id){
        rdvService.deleteById(Long.valueOf(id));
        return  ResponseEntity.ok().body(ApiResponse.success("rdv supprime avec succes."));
    }
}
