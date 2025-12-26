package com.ehei.staff.services;

import com.ehei.staff.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.staff.DTOS.Staff.StaffResponseDto;
import com.ehei.staff.mappers.StaffMapper;
import com.ehei.staff.models.Staff;
import com.ehei.staff.repositorys.StaffRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class StaffService {
    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    public StaffResponseDto CreateStaff(StaffCreateRequestDto requestdto){
        Staff staff = staffMapper.toModel(requestdto);
        return staffMapper.toResponseDto(staffRepository.save(staff)) ;
    }
}
