package com.ehei.patient.service;

import com.ehei.patient.dto.PatientCreateRequeste;
import com.ehei.patient.dto.PatientResponse;
import com.ehei.patient.dto.PatientUpdateRequeste;
import com.ehei.patient.exceptions.EmailAlreadyExistException;
import com.ehei.patient.exceptions.EntityNotFoundException;
import com.ehei.patient.mapper.PatientMapper;
import com.ehei.patient.model.Patient;
import com.ehei.patient.repository.PatientRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientResponse createPatient(PatientCreateRequeste requestDto){
        Patient patient = patientMapper.toModel(requestDto);
        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new EmailAlreadyExistException(patient.getEmail());
        }
        return patientMapper.toResponseDto(patientRepository.save(patient));
    }

    public List<PatientResponse> getAll() {
        List<Patient> patientListe = patientRepository.findAll();
        return patientMapper.toResponseDto(patientListe);
    }

    public PatientResponse getById(Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aucun patient n'existe avec id: " + id));
        return patientMapper.toResponseDto(patient);
    }

    public PatientResponse update(Long id, PatientUpdateRequeste patientDto) {
        Patient oldPatient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun patient n'existe avec id: " + id));

        Patient patient = new Patient();
        patient.setNom(patientDto.getNom());
        patient.setPrenom(patientDto.getPrenom());
        patient.setEmail(patientDto.getEmail());
        patient.setDateNaissance(patientDto.getDateNaissance());
        patient.setTel(patientDto.getTel());

        //verifier si nouveau email n'existe pas
        if (patientRepository.existsByEmail(patient.getEmail()) &&
                !oldPatient.getEmail().equals(patient.getEmail())) {
            throw new EmailAlreadyExistException(patient.getEmail());
        }

        return patientMapper.toResponseDto(patientRepository.save(patient));
    }

    public void deleteById(Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aucun patient n'existe avec id: " + id));
        patientRepository.delete(patient);
    }
}