package com.ehei.consultation;

import com.ehei.consultation.dtos.ApiResponse;
import com.ehei.consultation.dtos.ConsultationCreateRequest;
import com.ehei.consultation.dtos.ConsultationResponse;
import com.ehei.consultation.dtos.ConsultationUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/consultation")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ConsultationResponse>> create(
            @RequestBody @Valid ConsultationCreateRequest dto
    ) {
        ConsultationResponse response = consultationService.create(dto);
        return ResponseEntity.ok(
                ApiResponse.success(response, "consultation creee avec succes.")
        );
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(consultationService.getAll())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsultationResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.success(consultationService.getById(Long.valueOf(id)))
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ConsultationResponse>> update(
            @PathVariable String id,
            @RequestBody @Valid ConsultationUpdateRequest dto
    ) {
        ConsultationResponse response = consultationService.update(Long.valueOf(id), dto);
        return ResponseEntity.ok(
                ApiResponse.success(response, "consultation modifiee avec succes.")
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable String id) {
        consultationService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok(
                ApiResponse.success("consultation supprimee avec succes.")
        );
    }
}

