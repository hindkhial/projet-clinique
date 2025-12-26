package com.ehei.staff.repositorys;

import com.ehei.staff.models.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {
}
