package com.kathir.demo.repository;

import com.kathir.demo.modules.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate,Long> {

}
