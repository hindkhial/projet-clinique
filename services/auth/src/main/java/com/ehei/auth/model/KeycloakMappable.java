package com.ehei.auth.model;

import java.time.LocalDateTime;

public interface KeycloakMappable {
    String getKeycloakUserId();
    String getEmail();
    void setEmail(String email);
    void setUpdatedAt(LocalDateTime updatedAt);
}