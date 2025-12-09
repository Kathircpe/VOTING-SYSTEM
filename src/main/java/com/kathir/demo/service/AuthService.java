package com.kathir.demo.service;

import com.kathir.demo.DTO.*;
import com.kathir.demo.models.Admin;
import com.kathir.demo.models.Voter;
import com.kathir.demo.repository.AdminRepository;
import com.kathir.demo.repository.VoterRepository;
import com.kathir.demo.utils.JwtUtil;
import com.kathir.demo.utils.OtpUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@AllArgsConstructor
@Service
public class AuthService {

    @Autowired
    private final AdminRepository adminRepository;
    private final VoterRepository voterRepository;
    private final OtpUtil otpUtil;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final int EXPIRATION = 15;
    private static final int OTP_TIMER = 120;


    public ResponseEntity<?> login(Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String role = body.get("role");
        Map<String, Object> map = new HashMap<>();

        if (role.equals("admin")) {
            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isEmpty()) {
                return new ResponseEntity<>("The provided user is not registered", HttpStatus.UNAUTHORIZED);
            }

            Admin admin = adminOptional.get();
            if (!passwordEncoder.matches(password, admin.getPassword())) {
                return new ResponseEntity<>("The wrong password", HttpStatus.UNAUTHORIZED);
            }
            userAdmin user = new userAdmin();
            user.id = admin.getId();
            user.name = admin.getName();
            user.email = admin.getEmail();
            user.phoneNumber = admin.getPhoneNumber();
            map.put("user", user);

        } else if (role.equals("voter")) {
            Optional<Voter> voterOptional = voterRepository.findByEmail(email);
            if (voterOptional.isEmpty()) {
                return new ResponseEntity<>("The provided user detail is not registered", HttpStatus.UNAUTHORIZED);
            }
            Voter voter = voterOptional.get();

            if (!voter.isEnabled()) {
                return new ResponseEntity<>("Your account is not verified, verify it to login", HttpStatus.UNAUTHORIZED);
            }

            if (!passwordEncoder.matches(password, voter.getPassword())) {
                return new ResponseEntity<>("Wrong password", HttpStatus.UNAUTHORIZED);
            }

            userVoter user = new userVoter();

            user.id = voter.getId();
            user.name = voter.getName();
            user.email = voter.getEmail();
            user.phoneNumber = voter.getPhoneNumber();
            user.age = voter.getAge();
            user.hasVoted = voter.isHasVoted();
            user.isEnabled = voter.isEnabled();
            user.voterAddress = voter.getVoterAddress();
            map.put("user", user);

        } else {
            return new ResponseEntity<>("role is not defined", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtil.generateToken(email);

        map.put("token", token);

        return ResponseEntity.ok(map);

    }

    public ResponseEntity<String> otpSenderForAdmin(String email) {
        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            if (LocalDateTime.now().isBefore(admin.getOtpTimer()) && admin.getOtpTimer() != null) {
                return new ResponseEntity<>("Wait for " + (admin.getOtpTimer().getSecond() - LocalDateTime.now().getSecond()) + " seconds to resend otp", HttpStatus.NOT_ACCEPTABLE);
            }
            String otp = otpUtil.generateOtp();
            emailService.sendOtpEmail(admin.getEmail(), otp);
            admin.setOtp(otp);
            admin.setExpiration(LocalDateTime.now().plusMinutes(EXPIRATION));
            admin.setOtpTimer(LocalDateTime.now().plusSeconds(OTP_TIMER));
            adminRepository.save(admin);
            return new ResponseEntity<>("otp has been sent", HttpStatus.OK);

        }
        return new ResponseEntity<>("user did not exist", HttpStatus.UNAUTHORIZED);

    }

    public ResponseEntity<String> forgotPassword(Map<String, String> body) {
        String email = body.get("email");
        String role = body.get("role");
        String otp = body.get("otp");
        String password = body.get("password");
        if (role.equals("admin")) {
            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                if (otp.equals(admin.getOtp())) {
                    if (admin.getExpiration().isBefore(LocalDateTime.now())) {
                        return new ResponseEntity<>("otp expired", HttpStatus.UNAUTHORIZED);

                    }
                    admin.setPassword(passwordEncoder.encode(password));
                    adminRepository.save(admin);
                    return new ResponseEntity<>("Password has been successfully changed", HttpStatus.CREATED);
                }
                return new ResponseEntity<>("otp is wrong", HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        } else if (role.equals("voter")) {
            Optional<Voter> voterOptional = voterRepository.findByEmail(email);
            if (voterOptional.isPresent()) {
                Voter voter = voterOptional.get();

                if (otp.equals(voter.getOtp())) {
                    if (voter.getExpiration().isBefore(LocalDateTime.now())) {
                        return new ResponseEntity<>("otp expired", HttpStatus.UNAUTHORIZED);

                    }
                    voter.setPassword(passwordEncoder.encode(password));
                    voterRepository.save(voter);
                    return new ResponseEntity<>("Password has been successfully changed", HttpStatus.CREATED);
                }
                return new ResponseEntity<>("otp is wrong", HttpStatus.UNAUTHORIZED);

            }
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);

        } else {
            return new ResponseEntity<>("role is not defined", HttpStatus.UNAUTHORIZED);
        }


    }

    public ResponseEntity<String> voterRegistration(Map<String, String> body) {
        String email = body.get("email");
        Optional<Voter> VoterOptional = voterRepository.findByEmail(email);
        if (VoterOptional.isPresent()) {
            return new ResponseEntity<>("email has been already registered", HttpStatus.UNAUTHORIZED);
        }
        int age = Integer.parseInt(body.get("age"));
        if (age < 18) return new ResponseEntity<>("Minors can't vote", HttpStatus.NOT_ACCEPTABLE);
        String name = body.get("name");
        String phoneNum = body.get("phoneNumber");
        String voterAddress = body.get("voterAddress");
        String password = body.get("password");

        addNewVoter(email, name, phoneNum, voterAddress, password, age);

        return new ResponseEntity<>("Successfully registered", HttpStatus.CREATED);

    }


    public ResponseEntity<String> voterVerification(Map<String, String> body) {

        String email = body.get("email");
        String otp = body.get("otp");

        Optional<Voter> voterOptional = voterRepository.findByEmail(email);
        if (voterOptional.isPresent()) {
            Voter voter = voterOptional.get();
            if (!otp.equals(voter.getOtp())) {
                return new ResponseEntity<>("otp is not matching", HttpStatus.UNAUTHORIZED);
            }
            if (LocalDateTime.now().isAfter(voter.getExpiration())) {
                return new ResponseEntity<>("otp expired", HttpStatus.NOT_ACCEPTABLE);
            }
            voter.setEnabled(true);
            voterRepository.save(voter);
            return new ResponseEntity<>("Successfully verified", HttpStatus.OK);

        }
        return new ResponseEntity<>("You need register first to verify", HttpStatus.UNAUTHORIZED);

    }


    public void addNewVoter(String email, String name, String phoneNum, String voterAddress, String password, int age) {
        Voter voter = new Voter();
        voter.setEmail(email);
        voter.setName(name);
        voter.setPhoneNumber(phoneNum);
        voter.setVoterAddress(voterAddress);
        voter.setAge(age);
        voter.setPassword(passwordEncoder.encode(password));
        String otp = otpUtil.generateOtp();
        emailService.sendOtpEmail(voter.getEmail(), otp);
        voter.setOtp(otp);
        voter.setExpiration(LocalDateTime.now().plusMinutes(EXPIRATION));
        voter.setOtpTimer(LocalDateTime.now().plusSeconds(OTP_TIMER));
        voterRepository.save(voter);


    }

    public ResponseEntity<String> otpSenderForVoter(String email) {
        Optional<Voter> voterOptional = voterRepository.findByEmail(email);
        if (voterOptional.isPresent()) {
            Voter voter = voterOptional.get();
            if (LocalDateTime.now().isBefore(voter.getOtpTimer()) && voter.getOtpTimer() != null) {
                return new ResponseEntity<>("Wait for " + (voter.getOtpTimer().getSecond() - LocalDateTime.now().getSecond()) + " seconds to resend otp", HttpStatus.NOT_ACCEPTABLE);
            }

            String otp = otpUtil.generateOtp();
            emailService.sendOtpEmail(voter.getEmail(), otp);
            voter.setOtp(otp);
            voter.setExpiration(LocalDateTime.now().plusMinutes(EXPIRATION));
            voter.setOtpTimer(LocalDateTime.now().plusSeconds(OTP_TIMER));
            voterRepository.save(voter);
            return new ResponseEntity<>("otp has been sent", HttpStatus.OK);
        }
        return new ResponseEntity<>("user did not exist", HttpStatus.UNAUTHORIZED);

    }

    public void ping() {
        adminRepository.ping();
    }


}
