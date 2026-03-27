package com.ehei.analyses.service;

import com.ehei.analyses.dto.AnalyseCreateRequest;
import com.ehei.analyses.dto.AnalyseResponse;
import com.ehei.analyses.dto.AnalyseUpdateRequest;
import com.ehei.analyses.kafka.KafkaProducer;
import com.ehei.analyses.mapper.AnalyseMapper;
import com.ehei.analyses.model.Analyse;
import com.ehei.analyses.repository.AnalyseRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@Slf4j
public class AnalyseService {

    private final AnalyseRepository analyseRepository;
    private final AnalyseMapper analyseMapper;
    private final KafkaProducer kafkaProducer; // ✅ KafkaProducer au lieu de KafkaTemplate

    public AnalyseResponse createAnalyse(AnalyseCreateRequest requestDto) {
        Analyse analyse = analyseMapper.toModel(requestDto);
        Analyse savedAnalyse = analyseRepository.save(analyse);

        // ✅ Déléguer l'envoi au KafkaProducer
        kafkaProducer.sendAnalysesSubmittedEvent(savedAnalyse);

        return analyseMapper.toResponseDto(savedAnalyse);
    }

    public List<AnalyseResponse> getAll() {
        return analyseMapper.toResponseDto(analyseRepository.findAll());
    }

    public AnalyseResponse getById(Long id) {
        Analyse analyse = analyseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analyse not found"));
        return analyseMapper.toResponseDto(analyse);
    }

    public AnalyseResponse update(Long id, AnalyseUpdateRequest analyseDto) {
        Analyse analyse = analyseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analyse not found"));

        analyse.setPatientId(analyseDto.getPatientId());
        analyse.setAge(analyseDto.getAge());
        analyse.setSexe(analyseDto.getSexe());
        analyse.setGlycemieJeun(analyseDto.getGlycemieJeun());
        analyse.setHemoglobine(analyseDto.getHemoglobine());
        analyse.setGlobulesBlancs(analyseDto.getGlobulesBlancs());
        analyse.setCholesterolTotal(analyseDto.getCholesterolTotal());
        analyse.setCreatinine(analyseDto.getCreatinine());
        analyse.setTensionSystolique(analyseDto.getTensionSystolique());
        analyse.setTensionDiastolique(analyseDto.getTensionDiastolique());
        analyse.setFrequenceCardiaque(analyseDto.getFrequenceCardiaque());
        analyse.setTemperature(analyseDto.getTemperature());
        analyse.setImc(analyseDto.getImc());

        return analyseMapper.toResponseDto(analyseRepository.save(analyse));
    }

    public void deleteById(Long id) {
        analyseRepository.deleteById(id);
    }
}