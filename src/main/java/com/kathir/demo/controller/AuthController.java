package com.kathir.demo.controller;

import com.kathir.demo.service.AuthService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Data
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
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
    public ResponseEntity<String> otpSenderForVoter(@PathVariable String email) {

        return authService.otpSenderForVoter(email);
    }

    @PostMapping("/ad/{email}")
    public ResponseEntity<String> otpSenderForAdmin(@PathVariable String email) {
        return authService.otpSenderForAdmin(email);
    }
    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        authService.ping();
        return new ResponseEntity<>("DB and backend is awake", HttpStatus.OK);
    }
    


}
