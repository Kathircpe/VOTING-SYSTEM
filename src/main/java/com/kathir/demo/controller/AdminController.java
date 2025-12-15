package com.kathir.demo.controller;

import com.kathir.demo.models.Voter;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.kathir.demo.service.AdminService;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;


@Data
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final static int PAGE_SIZE = 10;

    @Autowired
    private final AdminService adminService;

    @PostMapping("/createElection")
    private ResponseEntity<String> createElection(@RequestBody Map<String, String> body) throws Exception {
        return adminService.createElection(body);
    }

    @PatchMapping("/updateElection")
    private ResponseEntity<String> updateElection(@RequestBody Map<String, String> body) {
        return adminService.updateElection(body);
    }

    @DeleteMapping("/deleteElection/{id}")
    private ResponseEntity<String> deleteElection(@RequestParam int id) {
        return adminService.deleteElection(id);
    }

    @PostMapping("/createCandidate")
    private ResponseEntity<String> createCandidate(@RequestBody Map<String, String> body) {
        return adminService.createCandidate(body);
    }

    @PatchMapping("/updateCandidate")
    private ResponseEntity<String> updateCandidate(@RequestBody Map<String, String> body) {
        return adminService.updateCandidate(body);
    }

    @DeleteMapping("/deleteCandidate/{id}")
    private ResponseEntity<String> deleteCandidate(@PathVariable int id) {
        return adminService.deleteCandidate(id);
    }

    @GetMapping("/getVoters/{page}")
    private Page<Voter> getVoters(@PathVariable int page) {
        return adminService.getAllVoters(page, PAGE_SIZE);
    }

    @GetMapping("/getVoter/{id}")
    private ResponseEntity<?> getVoterById(@PathVariable long id) {
        return adminService.getVoterById(id);
    }

    @GetMapping("/getVotesForAll/{id}")
    private ResponseEntity<?> getVotesOfAllCandidatesAsync(@PathVariable int id) {
        return adminService.getVotesOfAllCandidatesAsync(id);
    }

    @GetMapping("/getVotes")
    private ResponseEntity<?> getVotesAsync(@RequestBody Map<String, String> body) {
        return adminService.getVotesAsync(body);
    }

    @PatchMapping("/updateAdmin")
    private ResponseEntity<String> updateAdmin(@RequestBody Map<String, String> body) {
        return adminService.updateAdmin(body);
    }


}
