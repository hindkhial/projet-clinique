package com.ehei.radio.kafka;

import com.ehei.ai.proto.AIPredictionEvent;
import com.ehei.radio.service.RadioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPredictionKafkaConsumer {

    private final RadioService radioService;

    @KafkaListener(
            topics = "ai.prediction.done",
            groupId = "radio-service-group"
    )
    public void consumeAiPrediction(byte[] payload) {
        try {
            AIPredictionEvent event = AIPredictionEvent.parseFrom(payload);
            log.info("Evenement AI Prediction recu pour radio id: {}, type_maladie: {}", 
                     event.getRadioId(), event.getTypeMaladie());
            
            radioService.updateFromAiPrediction(event);
            
        } catch (Exception e) {
            log.error("Erreur lors de la consommation de l'evenement AI Prediction: {}", e.getMessage());
        }
    }
}
