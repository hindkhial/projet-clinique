package com.ehei.rdv;

import com.ehei.rdv.DTO.RdvCreateRequestDto;
import com.ehei.rdv.DTO.RdvResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RdvService {
    private final RdvRepository rdvRepository;
    private final RdvMapper rdvMapper;

    public RdvResponseDto createRdv(RdvCreateRequestDto requestDto) {
        Rdv rdv = rdvRepository.save(rdvMapper.toModel(requestDto));
        return rdvMapper.toCreateResponseDto(rdv);
    }
}
