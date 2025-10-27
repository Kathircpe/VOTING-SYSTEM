package com.kathir.demo.controller;

import com.kathir.demo.models.Voter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kathir.demo.service.AdminService;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Data
@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
public  class AdminController {
        private final static int PAGE_SIZE =10;
        @Autowired
        private final AdminService adminService;

        @PostMapping("/createElection")
        private ResponseEntity<String> createElection(@RequestBody Map<String,String> body) throws Exception {
            return adminService.createElection(body);
        }

        @PatchMapping("/updateElection")
        private String updateElection(@RequestBody Map<String,String> body){
            return adminService.updateElection(body);
        }

        @DeleteMapping("/deleteElection/{id}")
        private String deleteElection(@RequestParam int id){
            return adminService.deleteElection(id);
        }

        @PostMapping("/createCandidate")
        private String createCandidate(@RequestBody Map<String,String> body){
            return adminService.createCandidate(body);
        }

        @PatchMapping("/updateCandidate")
        private String updateCandidate(@RequestBody Map<String,String> body){
            return adminService.updateCandidate(body);
        }

        @DeleteMapping("/deleteCandidate/{id}")
        private String deleteCandidate(@PathVariable int id){
            return adminService.deleteCandidate(id);
        }

        @GetMapping("/getVoters/{page}")
        private Page<Voter> getVoters(@PathVariable int page){
            return adminService.getAllVoters(page, PAGE_SIZE);
        }

        @GetMapping("/getVoter/{id}")
        private Voter getVoterById(@PathVariable long id){
            return adminService.getVoterById(id);
        }

        @GetMapping("/getVotesForAll/{contractAddress}")
        private List<Map<String,String>> getVotesOfAllCandidatesAsync(@PathVariable String contractAddress){
            return adminService.getVotesOfAllCandidatesAsync(contractAddress);
        }

        @GetMapping("/getVotes")
        private Map<String,String> getVotesAsync(@RequestBody Map<String,String> body){
            return adminService.getVotesAsync(body);
        }

        @PatchMapping("/updateAdmin")
        private ResponseEntity<String> updateAdmin(@RequestBody Map<String,String> body){
            return adminService.updateAdmin(body);
        }


}
