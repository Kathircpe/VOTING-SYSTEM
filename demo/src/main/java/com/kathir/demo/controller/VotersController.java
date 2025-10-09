package com.kathir.demo.controller;

import com.kathir.demo.modules.Candidate;
import com.kathir.demo.service.VoterService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Data
@RestController
@RequestMapping("/api/v1/voter")
public  class VotersController {
    @Autowired
    private final VoterService voterService;

    @GetMapping("{id}")
    private String getVoterById(@PathVariable Long id){
        return voterService.getVoterById(id);
    }
    @GetMapping("/candidates")
    private List<Candidate> getAllCandidates(){
        return voterService.getAllCandidates();
    }






}
