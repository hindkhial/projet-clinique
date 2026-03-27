package com.ehei.auth.mapper;

import com.ehei.auth.DTOS.medecin.MedecinCreateRequestDto;
import com.ehei.auth.DTOS.medecin.MedecinRegistrationRequest;
import com.ehei.auth.DTOS.medecin.MedecinUpdateRequestAuth;
import com.ehei.auth.DTOS.medecin.MedecinUpdateRequestDto;
import org.springframework.stereotype.Service;

@Service
public class MedecinMapper {
    /**
     * Convertit MedecinRegistrationRequest (avec password) en MedecinCreateRequestDto
     * (sans password - celui-ci est géré par Keycloak dans le service Auth)
     */
    public MedecinCreateRequestDto toMedecinCreateRequestDto(MedecinRegistrationRequest medecinRegistrationRequest) {
        MedecinCreateRequestDto medecinDto = new MedecinCreateRequestDto();
        medecinDto.setNom(medecinRegistrationRequest.getNom());
        medecinDto.setPrenom(medecinRegistrationRequest.getPrenom());
        medecinDto.setEmail(medecinRegistrationRequest.getEmail());
        medecinDto.setTel(medecinRegistrationRequest.getTel());
        medecinDto.setDateNaissance(medecinRegistrationRequest.getDateNaissance());
        medecinDto.setSpecialite(medecinRegistrationRequest.getSpecialite());
        // Note: le rôle sera automatiquement "MEDECIN" dans le microservice Staff
        // Note: le password n'est pas envoyé au microservice Staff, il reste dans Auth/Keycloak
        return medecinDto;
    }

    /**
     * Convertit MedecinUpdateRequestAuth en MedecinUpdateRequestDto
     */
    public MedecinUpdateRequestDto toMedecinUpdateRequestDto(MedecinUpdateRequestAuth updateRequest) {
        MedecinUpdateRequestDto medecinDto = new MedecinUpdateRequestDto();
        medecinDto.setNom(updateRequest.getNom());
        medecinDto.setPrenom(updateRequest.getPrenom());
        medecinDto.setEmail(updateRequest.getEmail());
        medecinDto.setTel(updateRequest.getTel());
        medecinDto.setDateNaissance(updateRequest.getDateNaissance());
        medecinDto.setSpecialite(updateRequest.getSpecialite());
        // Note: le password n'est pas envoyé au microservice Staff
        return medecinDto;
    }
}
