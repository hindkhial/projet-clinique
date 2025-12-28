package com.ehei.staff.services;

import com.ehei.staff.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.staff.DTOS.Staff.StaffResponseDto;
import com.ehei.staff.DTOS.Staff.StaffUpdateRequestDto;
import com.ehei.staff.exceptions.EmailAlreadyExistException;
import com.ehei.staff.exceptions.EntityNotFoundException;
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
        if (staffRepository.existsByEmail(staff.getEmail())) {
            throw new EmailAlreadyExistException(staff.getEmail());
        }
        return staffMapper.toResponseDto(staffRepository.save(staff)) ;
    }

    public List<StaffResponseDto> getAll() {
        List<Staff> staffListe = staffRepository.findAll();
        return staffMapper.toResponseDto(staffListe);
    }

    public StaffResponseDto getById(Long id) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aucun staff n'existe avec id: " + id));
        return staffMapper.toResponseDto(staff);
    }

    public StaffResponseDto update(Long id, StaffUpdateRequestDto staffdto) {
        Staff oldStaff = staffRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Aucun staff n'existe avec id:  " + id));
        Staff updatedStaff = new Staff();
        updatedStaff.setNom(staffdto.getNom());
        updatedStaff.setPrenom(staffdto.getPrenom());
        updatedStaff.setEmail(staffdto.getEmail());
        updatedStaff.setDateNaissance(staffdto.getDateNaissance());
        updatedStaff.setRole(staffdto.getRole());
        updatedStaff.setTel(staffdto.getTel());

        //verifier si nouveau email n'existe pas
        if (staffRepository.existsByEmail(updatedStaff.getEmail()) &&
            !oldStaff.getEmail().equals(updatedStaff.getEmail())) {
            throw new EmailAlreadyExistException(updatedStaff.getEmail());
        }
        return staffMapper.toResponseDto(staffRepository.save(updatedStaff));
    }

    public void deleteById(Long id) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aucun staff n'existe avec id:  " + id));
        staffRepository.delete(staff);
    }
}
