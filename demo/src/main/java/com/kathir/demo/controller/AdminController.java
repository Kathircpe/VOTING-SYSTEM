package com.kathir.demo.controller;

import com.kathir.demo.models.Voter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import com.kathir.demo.service.AdminService;

import java.util.Map;

@Data
@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
public  class AdminController {
    private final  int pageSize =10;
        @Autowired
        private final AdminService adminService;

        @GetMapping("/createElection")
        private String createElection(@RequestBody Map<String,String> body){
            return adminService.createElection(body);
        }

        @GetMapping("/updateElection")
        private String updateElection(@RequestBody Map<String,String> body){
            return adminService.updateElection(body);
        }
        @GetMapping("/deleteElection/{id}")
        private String deleteElection(@RequestParam int id){
            return adminService.deleteElection(id);
        }
        @GetMapping("/createCandidate")
        private String createCandidate(@RequestBody Map<String,String> body){
            return adminService.createCandidate(body);
        }

        @GetMapping("/updateCandidate")
        private String updateCandidate(@RequestBody Map<String,String> body){
            return adminService.updateCandidate(body);
        }
        @GetMapping("/deleteCandidate/{id}")
        private String deleteCandidate(@RequestParam int id){
            return adminService.deleteCandidate(id);
        }


        @GetMapping("/getVoters")
        private Page<Voter> getVoters(@RequestParam int page){
            return adminService.getAllVoters(page,pageSize);
        }

        @GetMapping("/getVoter/{id}")
        private Voter getVoterById(@PathVariable long id){
            return adminService.getVoterById(id);
        }



}
