package com.kathir.demo.service;

import com.kathir.demo.modules.Election;
import com.kathir.demo.repository.ElectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ElectionService {

    @Autowired
    private final ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

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
