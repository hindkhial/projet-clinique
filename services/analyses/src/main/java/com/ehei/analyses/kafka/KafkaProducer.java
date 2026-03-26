package com.ehei.analyses.kafka;

import com.ehei.analyses.model.Analyse;
import com.ehei.events.AnalysesSubmittedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAnalysesSubmittedEvent(Analyse analyse) {
        AnalysesSubmittedEvent event = AnalysesSubmittedEvent.newBuilder()
                .setAnalyseId(analyse.getId())
                .setPatientId(analyse.getPatientId())
                .setAge(analyse.getAge())
                .setSexe(analyse.getSexe().name())
                .setGlycemieJeun(analyse.getGlycemieJeun())
                .setHemoglobine(analyse.getHemoglobine())
                .setGlobulesBlancs(analyse.getGlobulesBlancs())
                .setCholesterolTotal(analyse.getCholesterolTotal())
                .setCreatinine(analyse.getCreatinine())
                .setTensionSystolique(analyse.getTensionSystolique())
                .setTensionDiastolique(analyse.getTensionDiastolique())
                .setFrequenceCardiaque(analyse.getFrequenceCardiaque())
                .setTemperature(analyse.getTemperature())
                .setImc(analyse.getImc())
                .setDateAnalyse(analyse.getDateAnalyse().toString())
                .setEventId(java.util.UUID.randomUUID().toString())
                .setEventType("AnalysesSubmitted")
                .setTimestamp(java.time.LocalDateTime.now().toString())
                .build();
        try {
            kafkaTemplate.send("analyses-events", event.toByteArray());
            log.info("✅ Event AnalysesSubmitted envoyé pour l'analyse ID: {}", analyse.getId());
        } catch (Exception e) {
            log.error("❌ Erreur envoi event Kafka: {}", e.getMessage());
        }
    }
}