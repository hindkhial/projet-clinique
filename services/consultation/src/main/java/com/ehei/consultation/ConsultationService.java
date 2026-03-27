package com.ehei.consultation;

import com.ehei.consultation.dtos.ConsultationCreateRequest;
import com.ehei.consultation.dtos.ConsultationResponse;
import com.ehei.consultation.dtos.ConsultationUpdateRequest;
import com.ehei.consultation.kafka.KafkaProducer;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Data
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final ConsultationMapper consultationMapper;
    private final KafkaProducer kafkaProducer;

    public ConsultationResponse create(ConsultationCreateRequest requestDto) {
        Consultation consultation = consultationMapper.toModel(requestDto);
        consultation.setDateConsultation(LocalDateTime.now());

        Consultation savedConsultation = consultationRepository.save(consultation);

        kafkaProducer.sendEvent(savedConsultation);
        return consultationMapper.toResponseDto(savedConsultation);
    }

    public List<ConsultationResponse> getAll() {
        List<Consultation> list = consultationRepository.findAll();
        return consultationMapper.toResponseDto(list);
    }

    public ConsultationResponse getById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucune consultation n'existe avec id: " + id));
        return consultationMapper.toResponseDto(consultation);
    }

    public ConsultationResponse update(Long id, ConsultationUpdateRequest dto) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucune consultation n'existe avec id: " + id));

        consultationMapper.updateEntity(consultation, dto);

        return consultationMapper.toResponseDto(
                consultationRepository.save(consultation)
        );
    }

    public void deleteById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucune consultation n'existe avec id: " + id));
        consultationRepository.delete(consultation);
    }
}
