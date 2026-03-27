package com.ehei.auth.repository;

import com.ehei.auth.model.StaffKeycloakMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffKeycloakMappingRepository extends JpaRepository<StaffKeycloakMapping, Long> {
    Optional<StaffKeycloakMapping> findByStaffId(Long staffId);
    Optional<StaffKeycloakMapping> findByKeycloakUserId(String keycloakUserId);
    void deleteByStaffId(Long staffId);
}