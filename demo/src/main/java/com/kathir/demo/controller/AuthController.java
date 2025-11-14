package com.kathir.demo.controller;

import com.kathir.demo.service.AdminService;
import com.kathir.demo.service.AuthService;
import com.kathir.demo.service.VoterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        return authService.login(body);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        return authService.forgotPassword(body);
    }

    @PostMapping("/registration")
    public ResponseEntity<String> voterRegistration(@RequestBody Map<String, String> body) {

        return authService.voterRegistration(body);
    }

    @PostMapping("/vo/verification")
    public ResponseEntity<String> voterVerification(@RequestBody Map<String, String> body) {

        return authService.voterVerification(body);
    }

    @PostMapping("/vo/{email}")
    public void otpSenderForVoter(@PathVariable String email) {

        authService.otpSenderForVoter(email);
    }

    @PostMapping("/ad/{email}")
    public void otpSenderForAdmin(@PathVariable String email) {
        authService.otpSenderForAdmin(email);
    }


}
