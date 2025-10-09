package com.kathir.demo.repository;

import com.kathir.demo.modules.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {

     Optional<Admin> findByEmail(String email);
    Optional<Admin> findByPhoneNumber(long PhoneNumber);

}
