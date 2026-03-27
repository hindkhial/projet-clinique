package com.ehei.auth.DTOS.Staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffAccountResult {
    private StaffResponseDto staff;
    private String keycloakUserId;

    public static StaffAccountResult success(StaffResponseDto staff, String keycloakUserId) {
        return new StaffAccountResult(staff, keycloakUserId);
    }
}
