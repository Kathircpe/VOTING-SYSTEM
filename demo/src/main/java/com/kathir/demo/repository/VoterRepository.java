package com.kathir.demo.repository;

import com.kathir.demo.modules.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoterRepository extends JpaRepository<Voter,Long> {

    Optional<Voter> findByEmail(String email);
    Optional<Voter> findByPhoneNumber(long PhoneNumber);


}
