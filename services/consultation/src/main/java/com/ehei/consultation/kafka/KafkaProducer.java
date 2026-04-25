package com.ehei.consultation.kafka;

import com.ehei.consultation.Consultation;
import consultation.events.ConsultationCreatedEvent;
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

    public void sendEvent(Consultation consultation) {
        ConsultationCreatedEvent.Builder builder = ConsultationCreatedEvent.newBuilder()
                .setId(consultation.getId())
                .setIdPatient(consultation.getIdPatient())
                .setDateConsultation(consultation.getDateConsultation().toString())
                .setObservation(consultation.getObservation() == null ? "" : consultation.getObservation())
                .setTraitement(consultation.getTraitement() == null ? "" : consultation.getTraitement())
                .setType(consultation.getType().name());

        if (consultation.getIdMedecin() != null)  builder.setIdMedecin(consultation.getIdMedecin());
        if (consultation.getIdRdv()     != null)  builder.setIdRdv(consultation.getIdRdv());
        if (consultation.getIdRadio()   != null)  builder.setIdRadio(consultation.getIdRadio());
        if (consultation.getIdAnalyse() != null)  builder.setIdAnalyse(consultation.getIdAnalyse());
        if (consultation.getTypeMaladie() != null) builder.setTypeMaladie(consultation.getTypeMaladie());

        ConsultationCreatedEvent event = builder.build();

        try {
            kafkaTemplate.send("Consultation_Created", event.toByteArray());
            log.info("ConsultationCreatedEvent sent for consultation id{}", consultation.getId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}