package com.ehei.rapport.service;

import com.ehei.rapport.dto.CreateRapportRequest;
import com.ehei.rapport.dto.RapportResponse;
import com.ehei.rapport.mapper.RapportMapper;
import com.ehei.rapport.model.Rapport;
import com.ehei.rapport.repository.RapportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RapportService {

    private final RapportRepository rapportRepository;
    private final RapportMapper rapportMapper;

    //creation du Rapport
    @Transactional
    public RapportResponse createRapport(CreateRapportRequest request) {
        log.info("Creation d'un nouveau rapport pour patient: {}", request.getPatientId());

        Rapport rapport = rapportMapper.toEntity(request);

        Rapport savedRapport = rapportRepository.save(rapport);

        log.info("Rapport cree avec succes ID: {}", savedRapport.getId());

        return rapportMapper.toResponse(savedRapport);
    }


    //lister les rapport d'un patient
    @Transactional
    public List<RapportResponse> getRapportsByPatient(Long patientId) {
        log.info("Recuperation des rapports du patient: {}", patientId);

        List<Rapport> rapports = rapportRepository.findByPatientId(patientId);

        return rapports.stream()
                .map(rapportMapper::toResponse)
                .collect(Collectors.toList());
    }

    //lister les rapports d'un medecin
    @Transactional
    public List<RapportResponse> getRapportsByMedecin(Long medecinId) {
        log.info("Recuperation des rapports du medecin: {}", medecinId);

        List<Rapport> rapports = rapportRepository.findByMedecinId(medecinId);

        return rapports.stream()
                .map(rapportMapper::toResponse)
                .collect(Collectors.toList());
    }

    //select un rapport d'apres rapportID
    @Transactional
    public RapportResponse getRapportById(Long id) {
        log.info("Récupération du rapport: {}", id);

        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé avec ID: " + id));

        return rapportMapper.toResponse(rapport);
    }

    //lister tous les rapports
    @Transactional
    public List<RapportResponse> getAllRapports() {
        log.info("Récupération de tous les rapports");

        List<Rapport> rapports = rapportRepository.findAll();

        return rapports.stream()
                .map(rapportMapper::toResponse)
                .collect(Collectors.toList());
    }
}
