package com.ehei.rdv.Service;

import com.ehei.rdv.DTO.RdvCreateRequestDto;
import com.ehei.rdv.DTO.RdvResponseDto;
import com.ehei.rdv.DTO.RdvUpdateRequestDto;
import com.ehei.rdv.Exceptions.CreneauIndisponibleException;
import com.ehei.rdv.Exceptions.EntityNotFoundException;
import com.ehei.rdv.Exceptions.MedecinNotFoundException;
import com.ehei.rdv.Exceptions.PatientNotFoundException;
import com.ehei.rdv.Mapper.RdvMapper;
import com.ehei.rdv.Model.Rdv;
import com.ehei.rdv.Repository.RdvRepository;
import com.ehei.rdv.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import com.ehei.patient.repository.PatientRepository;
import com.ehei.staff.repositorys.MedecinRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"com.ehei.rdv", "com.ehei.staff", "com.ehei.patient"})
public class RdvService {
    private final RdvRepository rdvRepository;
    private final RdvMapper rdvMapper;
    private final KafkaProducer kafkaProducer;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;


    public RdvResponseDto createRdv(RdvCreateRequestDto requestDto) {
        if(!patientRepository.existsById(requestDto.getIdPatient()))
        { throw new PatientNotFoundException(requestDto.getIdPatient());}

        if(!medecinRepository.existsById(requestDto.getIdMedecin()))
        { throw new MedecinNotFoundException(requestDto.getIdMedecin());}

        if(rdvRepository.existsByIdMedecinAndDateRdv(requestDto.getIdMedecin(),requestDto.getDateRdv()))
        { throw new CreneauIndisponibleException();}

        Rdv rdv = rdvRepository.save(rdvMapper.toModel(requestDto));
        kafkaProducer.sendEvent(rdv);
        return rdvMapper.toResponseDto(rdv);
    }

    public RdvResponseDto updateRdv(Long id,RdvUpdateRequestDto requestDto) {

        Rdv oldRdv = rdvRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun rdv n'existe avec id: " + id));

        Rdv rdv = new Rdv();
        rdv.setIdPatient(requestDto.getIdPatient());
        rdv.setIdMedecin(requestDto.getIdMedecin());
        rdv.setDateRdv(requestDto.getDateRdv());

        return rdvMapper.toResponseDto(rdvRepository.save(rdv));
    }

    public List<RdvResponseDto> getAll() {
        List<Rdv> rdvListe = rdvRepository.findAll();
        return rdvMapper.toListeResponseDto(rdvListe);
    }

    public RdvResponseDto getById(Long id) {
        Rdv rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun rdv n'existe avec id: " + id));

        return rdvMapper.toResponseDto(rdv);
    }

    public void deleteById(Long id) {
        Rdv rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun rdv n'existe avec id: " + id));
        rdvRepository.delete(rdv);
    }
}
