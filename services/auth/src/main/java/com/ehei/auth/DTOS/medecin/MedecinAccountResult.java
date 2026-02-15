package com.ehei.auth.DTOS.medecin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedecinAccountResult {
    private MedecinResponseDto medecin;
    private String keycloakUserId;

    public static MedecinAccountResult success(MedecinResponseDto medecin,String keycloakUserId) {
        return new MedecinAccountResult(medecin, keycloakUserId);
    }
}