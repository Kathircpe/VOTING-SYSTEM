package com.kathir.demo.controller;

import com.kathir.demo.service.AdminService;
import com.kathir.demo.service.VoterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminService adminService;
    private final VoterService voterService;


    @PostMapping("/ad/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> body) {
      return adminService.adminLogin(body);
    }



    @PostMapping("/vo/registration")
    public ResponseEntity<?> voterRegistration(@RequestBody Map<String,String> body) {

        return voterService.voterRegistration(body);
    }

    @PostMapping("/vo/verification")
    public String voterVerification(@RequestBody Map<String,String> body) {

       return voterService.voterVerification(body);
    }

    @PostMapping("/vo/login")
    public ResponseEntity<?> voterLogin(@RequestBody Map<String, String> body) {
      return voterService.voterLogin(body);
    }


}
