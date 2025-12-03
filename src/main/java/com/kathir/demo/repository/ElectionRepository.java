package com.kathir.demo.repository;

import com.kathir.demo.models.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ElectionRepository extends JpaRepository<Election,Integer> {
    Optional<Election> findByContractAddress(String contractAddress);

    @Query("SELECT e FROM Election e WHERE e.endDate > CURRENT_TIMESTAMP ORDER BY e.endDate DESC")
    Optional<Election> finActiveElection();
}
