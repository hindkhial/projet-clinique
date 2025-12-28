package com.ehei.staff.services;

import com.ehei.staff.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.staff.DTOS.Staff.StaffResponseDto;
import com.ehei.staff.DTOS.Staff.StaffUpdateRequestDto;
import com.ehei.staff.DTOS.medecin.MedecinCreateRequestDto;
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

    public Staff CreateStaff(Staff newStaff){

        if (staffRepository.existsByEmail(newStaff.getEmail())) {
            throw new EmailAlreadyExistException(newStaff.getEmail());
        }
        return staffRepository.save(newStaff) ;
    }

    public List<StaffResponseDto> getAll() {
        List<Staff> staffListe = staffRepository.findAll();
        return staffMapper.toResponseDto(staffListe);
    }

    public StaffResponseDto getById(Long id) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aucun staff n'existe avec id: " + id));
        return staffMapper.toResponseDto(staff);
    }

    public Staff update(Long id, Staff newStaff) {
        Staff oldStaff = staffRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Aucun staff n'existe avec id:  " + id));

        //verifier si nouveau email n'existe pas
        if (staffRepository.existsByEmail(newStaff.getEmail()) &&
            !oldStaff.getEmail().equals(newStaff.getEmail())) {
            throw new EmailAlreadyExistException(newStaff.getEmail());
        }

        // Mettre à jour l'objet existant directement
        oldStaff.setNom(newStaff.getNom());
        oldStaff.setPrenom(newStaff.getPrenom());
        oldStaff.setEmail(newStaff.getEmail());
        oldStaff.setDateNaissance(newStaff.getDateNaissance());
        oldStaff.setRole(newStaff.getRole());
        oldStaff.setTel(newStaff.getTel());

        return staffRepository.save(oldStaff);
    }

    public void deleteById(Long id) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aucun staff n'existe avec id:  " + id));
        staffRepository.delete(staff);
    }

    public Staff CreateStaff(MedecinCreateRequestDto requestdto){
        Staff staff = staffMapper.toModel(requestdto);

        return CreateStaff(staff) ;
    }

    public StaffResponseDto CreateStaff(StaffCreateRequestDto requestdto){
        Staff staff = staffMapper.toModel(requestdto);

        return staffMapper.toResponseDto(CreateStaff(staff));
    }

    public StaffResponseDto update(Long id, StaffUpdateRequestDto staffdto) {

        return staffMapper.toResponseDto(update(id, staffMapper.toModel(staffdto)));
    }
}
