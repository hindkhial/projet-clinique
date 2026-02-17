package com.ehei.analyses.controller;

import com.ehei.analyses.dto.AnalyseCreateRequest;
import com.ehei.analyses.dto.AnalyseResponse;
import com.ehei.analyses.dto.AnalyseUpdateRequest;
import com.ehei.analyses.service.AnalyseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/analyse")
public class AnalyseController {

    private final AnalyseService analyseService;

    @PostMapping("/create")
    public ResponseEntity<AnalyseResponse> create(@RequestBody AnalyseCreateRequest analyseDto) {
        AnalyseResponse response = analyseService.createAnalyse(analyseDto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<AnalyseResponse>> getAllAnalyses() {
        List<AnalyseResponse> response = analyseService.getAll();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyseResponse> getAnalyseById(@PathVariable String id) {
        AnalyseResponse response = analyseService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AnalyseResponse> updateAnalyse(@PathVariable String id,
                                                         @RequestBody AnalyseUpdateRequest analyseDto) {
        AnalyseResponse response = analyseService.update(Long.valueOf(id), analyseDto);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAnalyse(@PathVariable String id) {
        analyseService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok().body("Analyse has been deleted");
    }
}