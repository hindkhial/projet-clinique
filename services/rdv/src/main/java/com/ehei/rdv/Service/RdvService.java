package com.ehei.rdv.Service;

import com.ehei.rdv.DTO.patient.PatientResponseDto;
import com.ehei.rdv.DTO.rdv.RdvCreateRequestDto;
import com.ehei.rdv.DTO.rdv.RdvResponseDto;
import com.ehei.rdv.DTO.rdv.RdvUpdateRequestDto;
import com.ehei.rdv.DTO.staff.MedecinResponseDto;
import com.ehei.rdv.Exceptions.*;
import com.ehei.rdv.Feign.MedecinClient;
import com.ehei.rdv.Feign.PatientClient;
import com.ehei.rdv.Mapper.RdvMapper;
import com.ehei.rdv.Model.Rdv;
import com.ehei.rdv.Repository.RdvRepository;
import com.ehei.rdv.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RdvService {
    private final RdvRepository rdvRepository;
    private final RdvMapper rdvMapper;
    private final KafkaProducer kafkaProducer;
    private final PatientClient patientClient;
    private final MedecinClient medecinClient;


    public RdvResponseDto createRdv(RdvCreateRequestDto requestDto) {


        ResponseEntity<PatientResponseDto> patientResponse = patientClient.getPatientById(requestDto.getIdPatient());
        if (!patientResponse.getStatusCode().is2xxSuccessful() || patientResponse.getBody() == null)
        {
            throw new PatientNotFoundException(requestDto.getIdPatient());
        }

        ResponseEntity<MedecinResponseDto> medecinResponse = medecinClient.getMedecinById(requestDto.getIdMedecin());
        if (!medecinResponse.getStatusCode().is2xxSuccessful() || medecinResponse.getBody() == null)
        {
            throw new MedecinNotFoundException(requestDto.getIdMedecin());
        }

        //verifie minutes :00 ou :30
        validerHoraireRdv(requestDto.getDateRdv());

        if(rdvRepository.existsByIdMedecinAndDateRdv(requestDto.getIdMedecin(),requestDto.getDateRdv()))
        { throw new CreneauIndisponibleException();}

        Rdv rdv = rdvRepository.save(rdvMapper.toModel(requestDto));
        kafkaProducer.sendEvent(rdv);
        return rdvMapper.toResponseDto(rdv);
    }

    public RdvResponseDto updateRdv(Long id,RdvUpdateRequestDto requestDto) {

        validerHoraireRdv(requestDto.getDateRdv());

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

    public List<RdvResponseDto> getRdvByPatient(Long idPatient) {
        ResponseEntity<PatientResponseDto> patientResponse = patientClient.getPatientById(idPatient);
        if (!patientResponse.getStatusCode().is2xxSuccessful() || patientResponse.getBody() == null)
            throw new PatientNotFoundException(idPatient);

        return rdvMapper.toListeResponseDto(rdvRepository.findAllByIdPatient(idPatient));
    }

    public List<RdvResponseDto> getRdvByMedecin(Long idMedecin) {
        ResponseEntity<MedecinResponseDto> medecinResponse = medecinClient.getMedecinById(idMedecin);
        if (!medecinResponse.getStatusCode().is2xxSuccessful() || medecinResponse.getBody() == null)
            throw new MedecinNotFoundException(idMedecin);

        return rdvMapper.toListeResponseDto(rdvRepository.findAllByIdMedecin(idMedecin));
    }

    public List<LocalDateTime> getDisponibilites(Long idMedecin, LocalDate date) {
        ResponseEntity<MedecinResponseDto> medecinResponse = medecinClient.getMedecinById(idMedecin);
        if (!medecinResponse.getStatusCode().is2xxSuccessful() || medecinResponse.getBody() == null)
            throw new MedecinNotFoundException(idMedecin);

        LocalDateTime start = date.atTime(8, 0);
        LocalDateTime end   = date.atTime(17, 0);

        // Tous les créneaux possibles (08h00 → 16h30, toutes les 30 min)
        List<LocalDateTime> tousLesCreneaux = new ArrayList<>();
        LocalDateTime current = start;
        while (current.isBefore(end)) {
            tousLesCreneaux.add(current);
            current = current.plusMinutes(30);
        }

        // Créneaux déjà pris
        List<LocalDateTime> pris = rdvRepository
                .findAllByIdMedecinAndDateRdvBetween(idMedecin, start, end)
                .stream()
                .map(Rdv::getDateRdv)
                .toList();

        tousLesCreneaux.removeAll(pris);
        return tousLesCreneaux;
    }

    private void validerHoraireRdv(LocalDateTime dateRdv) {
        int hour = dateRdv.getHour();
        int minute = dateRdv.getMinute();

        if (hour < 8 || hour >= 17) {
            throw new HoraireInvalideException(
                    "Le RDV doit être entre 08h00 et 17h00."
            );
        }

        if (minute != 0 && minute != 30) {
            throw new HoraireInvalideException(
                    "Le RDV doit être sur un créneau de 30 minutes (ex: 09h00 ou 09h30)."
            );
        }
    }
}
