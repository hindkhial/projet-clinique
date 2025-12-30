package com.ehei.rdv;

import com.ehei.rdv.DTO.RdvCreateRequestDto;
import com.ehei.rdv.DTO.RdvResponseDto;
import com.ehei.rdv.DTO.RdvUpdateRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RdvService {
    private final RdvRepository rdvRepository;
    private final RdvMapper rdvMapper;

    public RdvResponseDto createRdv(RdvCreateRequestDto requestDto) {
        Rdv rdv = rdvRepository.save(rdvMapper.toModel(requestDto));
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
}
