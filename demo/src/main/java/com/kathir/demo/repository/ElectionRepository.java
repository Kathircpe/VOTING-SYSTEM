package com.kathir.demo.repository;

import com.kathir.demo.models.Election;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<Election,Integer> {

}
