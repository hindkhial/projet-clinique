package com.ehei.patient.service;

import com.ehei.patient.dto.PatientCreateRequeste;
import com.ehei.patient.dto.PatientResponse;
import com.ehei.patient.dto.PatientUpdateRequeste;
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
        return patientMapper.toResponseDto(patientRepository.save(patient));
    }

    public List<PatientResponse> getAll() {
        List<Patient> patientListe = patientRepository.findAll();
        return patientMapper.toResponseDto(patientListe);
    }

    public PatientResponse getById(Long id) {
        Patient patient = patientRepository.findById(id).orElse(new Patient());
        return patientMapper.toResponseDto(patient);
    }

    public PatientResponse update(Long id, PatientUpdateRequeste patientDto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setNom(patientDto.getNom());
        patient.setPrenom(patientDto.getPrenom());
        patient.setEmail(patientDto.getEmail());
        patient.setDateNaissance(patientDto.getDateNaissance());
        patient.setTel(patientDto.getTel());

        return patientMapper.toResponseDto(patientRepository.save(patient));
    }

    public void deleteById(Long id) {
        patientRepository.deleteById(id);
    }
}