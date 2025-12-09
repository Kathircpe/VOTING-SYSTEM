package com.kathir.demo.repository;

import com.kathir.demo.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {

     Optional<Admin> findByEmail(String email);


     @Query(value="SELECT 1",nativeQuery=true)
    Integer ping();
}
