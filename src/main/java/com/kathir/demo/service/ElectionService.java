package com.kathir.demo.service;

import com.kathir.demo.models.Election;
import com.kathir.demo.repository.ElectionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Data
@AllArgsConstructor
@Service
public class ElectionService {

    @Autowired
    private final ElectionRepository electionRepository;


    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public Optional<Election> getElectionById(int id) {
        return electionRepository.findById(id);
    }

    public Election saveElection(Election election) {
        return electionRepository.save(election);
    }

    public void deleteElection(int id) {
        electionRepository.deleteById(id);
    }
}
