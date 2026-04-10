package com.ehei.staff.services;

import com.ehei.staff.DTOS.medecin.MedecinCreateRequestDto;
import com.ehei.staff.DTOS.medecin.MedecinResponseDto;
import com.ehei.staff.DTOS.medecin.MedecinUpdateRequestDto;
import com.ehei.staff.exceptions.EntityNotFoundException;
import com.ehei.staff.mappers.MedecinMapper;
import com.ehei.staff.mappers.StaffMapper;
import com.ehei.staff.models.Medecin;
import com.ehei.staff.models.Staff;
import com.ehei.staff.repositorys.MedecinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedecinService {

    private final MedecinMapper medecinMapper;
    private  final MedecinRepository medecinRepository;
    private final StaffService staffService;
    private final StaffMapper staffMapper;

    public MedecinResponseDto createMedecin(MedecinCreateRequestDto requestDto) {
        Staff staff = staffService.CreateStaff(requestDto);
        Medecin medecin = medecinRepository.save(medecinMapper.toModel(requestDto, staff));
        return medecinMapper.toResponseDto(medecin);
    }

    public List<MedecinResponseDto> getAll() {
        List<Medecin>  medecins = medecinRepository.findAll();
        return medecinMapper.toResponseDto(medecins);
    }

    public MedecinResponseDto getById(Long id) {
        Medecin medecin = medecinRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aucun medecin n'existe avec id:  " + id));
        return medecinMapper.toResponseDto(medecin);
    }

    public void deleteById(Long id) {
        Medecin medecin = medecinRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aucun medecin n'existe avec id:  " + id));
        Staff staff = medecin.getStaff();
        medecinRepository.delete(medecin);
        staffService.deleteById(staff.getId());
    }

    public MedecinResponseDto updateById(Long id, MedecinUpdateRequestDto requestDto) {
        Medecin oldMedecin = medecinRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aucun medecin n'existe avec id:  " + id));

        // Créer le Staff avec les nouvelles données
        Staff newStaff = staffMapper.toModel(requestDto);

        // Mettre à jour via StaffService (qui gère la validation email)
        staffService.update(oldMedecin.getStaff().getId(), newStaff);

        // Mettre à jour la spécialité du médecin
        oldMedecin.setSpecialite(requestDto.getSpecialite());

        // Sauvegarder (Staff déjà sauvegardé par staffService.update)
        return medecinMapper.toResponseDto(medecinRepository.save(oldMedecin));
    }

    public MedecinResponseDto getByStaffId(Long staffId) {
        return medecinRepository.findByStaff_Id(staffId)
                .map(medecinMapper::toResponseDto)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
    }
}
