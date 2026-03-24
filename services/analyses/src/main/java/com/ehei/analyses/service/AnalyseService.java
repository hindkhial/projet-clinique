package com.ehei.analyses.service;

import com.ehei.analyses.dto.AnalyseCreateRequest;
import com.ehei.analyses.dto.AnalyseResponse;
import com.ehei.analyses.dto.AnalyseUpdateRequest;
import com.ehei.analyses.mapper.AnalyseMapper;
import com.ehei.analyses.model.Analyse;
import com.ehei.analyses.repository.AnalyseRepository;
import com.ehei.events.AnalysesSubmittedEvent; // ✅ classe générée par Protobuf
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@Slf4j
public class AnalyseService {

    private final AnalyseRepository analyseRepository;
    private final AnalyseMapper analyseMapper;
    private final KafkaTemplate<String, byte[]> kafkaTemplate; // ✅ byte[] au lieu de Object

    public AnalyseResponse createAnalyse(AnalyseCreateRequest requestDto) {
        // 1. Sauvegarder l'analyse
        Analyse analyse = analyseMapper.toModel(requestDto);
        Analyse savedAnalyse = analyseRepository.save(analyse);

        // 2. ✅ Construire l'event Protobuf avec newBuilder()
        AnalysesSubmittedEvent event = AnalysesSubmittedEvent.newBuilder()
                .setAnalyseId(savedAnalyse.getId())
                .setPatientId(savedAnalyse.getPatientId())
                .setAge(savedAnalyse.getAge())
                .setSexe(savedAnalyse.getSexe().name())
                .setGlycemieJeun(savedAnalyse.getGlycemieJeun())
                .setHemoglobine(savedAnalyse.getHemoglobine())
                .setGlobulesBlancs(savedAnalyse.getGlobulesBlancs())
                .setCholesterolTotal(savedAnalyse.getCholesterolTotal())
                .setCreatinine(savedAnalyse.getCreatinine())
                .setTensionSystolique(savedAnalyse.getTensionSystolique())
                .setTensionDiastolique(savedAnalyse.getTensionDiastolique())
                .setFrequenceCardiaque(savedAnalyse.getFrequenceCardiaque())
                .setTemperature(savedAnalyse.getTemperature())
                .setImc(savedAnalyse.getImc())
                .setDateAnalyse(savedAnalyse.getDateAnalyse().toString())
                .setEventId(java.util.UUID.randomUUID().toString())
                .setEventType("AnalysesSubmitted")
                .setTimestamp(java.time.LocalDateTime.now().toString())
                .build();

        // 3. ✅ Sérialiser en byte[] et publier vers Kafka
        kafkaTemplate.send("analyses-events", event.toByteArray());
        log.info("✅ Événement AnalysesSubmitted publié pour l'analyse ID: {} - Patient ID: {}",
                savedAnalyse.getId(), savedAnalyse.getPatientId());

        // 4. Retourner la réponse
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