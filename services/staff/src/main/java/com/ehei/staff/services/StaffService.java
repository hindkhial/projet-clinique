package com.ehei.staff.services;

import com.ehei.staff.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.staff.DTOS.Staff.StaffResponseDto;
import com.ehei.staff.mappers.StaffMapper;
import com.ehei.staff.models.Staff;
import com.ehei.staff.repositorys.StaffRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class StaffService {
    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    public StaffResponseDto CreateStaff(StaffCreateRequestDto requestdto){
        Staff staff = staffMapper.toModel(requestdto);
        return staffMapper.toResponseDto(staffRepository.save(staff)) ;
    }

    public List<StaffResponseDto> getAll() {
        List<Staff>  staffListe = staffRepository.findAll();
        return staffMapper.toResponseDto(staffListe);
    }

    public StaffResponseDto getById(Long id) {
        Staff staff = staffRepository.findById(id).orElse(new Staff());
        return staffMapper.toResponseDto(staff);
    }
}
