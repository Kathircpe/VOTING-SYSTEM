package com.kathir.demo.controller;

import com.kathir.demo.service.AdminService;
import com.kathir.demo.service.OtpService;
import com.kathir.demo.service.VoterService;
import lombok.RequiredArgsConstructor;
import com.kathir.demo.modules.Admin;
import com.kathir.demo.modules.Voter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.kathir.demo.repository.AdminRepository;
import com.kathir.demo.repository.VoterRepository;
import com.kathir.demo.utils.JwtUtil;
import com.kathir.demo.utils.OtpUtil;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminService adminService;
    private final VoterService voterService;
    @PostMapping("/ad/request-otp")
    public String adminOtpRequest(@RequestParam String email) {

       return adminService.adminOtpRequest(email);

    }

    @PostMapping("/ad/otpVerification")
    public String adminOtpVerification(@RequestBody Map<String,String> body) {

      return adminService.adminOtpVerification(body);

    }

    @PostMapping("/ad/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> body) {
      return adminService.adminLogin(body);
    }



    @PostMapping("/vo/request-otp")
    public String voterOtpRequest(@RequestParam String email) {

        return voterService.voterOtpRequest(email);
    }

    @PostMapping("/vo/otpVerification")
    public String voterOtpVerification(@RequestBody Map<String,String> body) {

       return voterService.voterOtpVerification(body);
    }

    @PostMapping("/vo/login")
    public ResponseEntity<?> voterLogin(@RequestBody Map<String, String> body) {
      return voterService.voterLogin(body);
    }




}
