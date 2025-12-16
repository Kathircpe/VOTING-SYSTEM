package com.kathir.demo.repository;

import com.kathir.demo.models.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface VoterRepository extends JpaRepository<Voter, Long> {

    Optional<Voter> findByEmail(String email);

    Optional<Voter> findByVoterAddress(String voterAddress);

    @Modifying
    @Transactional
    @Query(value = "UPDATE voter SET has_voted = FALSE", nativeQuery = true)
    void updateAllHasVotedToFalse();


}
