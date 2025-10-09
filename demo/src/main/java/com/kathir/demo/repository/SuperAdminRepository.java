package com.kathir.demo.repository;

import com.kathir.demo.modules.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin,Long> {
    Optional<SuperAdmin> findByEmail(String email);
    Optional<SuperAdmin> findByPhoneNumber(long PhoneNumber);

}
