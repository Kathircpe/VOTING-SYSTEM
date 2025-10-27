package com.kathir.demo.controller;

import com.kathir.demo.models.Candidate;
import com.kathir.demo.service.VoterService;
import com.kathir.demo.service.VotingService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    @PatchMapping("/updateVoter")
    public ResponseEntity<String> updateVoter(@RequestBody Map<String, String> body){
        return voterService.updateVoter(body);
    }
    @PatchMapping("/vote")
    private ResponseEntity<CompletableFuture<String>> vote(@RequestBody Map<String,String> body) throws Exception {
        return voterService.voteAsync(body);
    }
    @GetMapping("/getVotes/{contractAddress}")
    private List<Map<String,String>> getVotesOfAllCandidatesAsync(@PathVariable String contractAddress){
        return voterService.getVotesOfAllCandidatesAsync(contractAddress);
    }







}
