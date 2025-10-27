package com.kathir.demo.repository;

import com.kathir.demo.models.Election;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ElectionRepository extends JpaRepository<Election,Integer> {
    Optional<Election> findByContractAddress(String contractAddress);
}
