package com.kathir.demo.service;

import com.kathir.demo.modules.Admin;
import com.kathir.demo.modules.Voter;
import com.kathir.demo.utils.JwtUtil;
import com.kathir.demo.utils.OtpUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.kathir.demo.repository.AdminRepository;
import com.kathir.demo.repository.VoterRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Data
@Service
public class AdminService {

    @Autowired
    private final AdminRepository adminRepository;
    private final VoterRepository voterRepository;
    private final OtpUtil otpUtil;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public Page<Admin> getAllAdmins(int page,int size){
        Pageable pageable= PageRequest.of(page,size);
        return adminRepository.findAll(pageable);
    }
    public Page<Voter> getAllVoters(int page,int size){
        Pageable pageable= PageRequest.of(page,size);
        return voterRepository.findAll(pageable);
    }

    public Voter getVoterById(long id) {
        return voterRepository.findById(id).orElseThrow(()-> new IllegalStateException(id+" not found"));
    }
    public Admin getAdminById(long id) {
        return adminRepository.findById(id).orElseThrow(()-> new IllegalStateException(id+" not found"));
    }

    public void updateVoter(long id,Voter voter) {
        voterRepository.save(voter);

    }

    public void addNewVoter(Voter voter) {
        voterRepository.save(voter);
    }

    public void deleteVoter(long id) {
        voterRepository.delete(getVoterById(id));
    }

    public String adminOtpRequest(String email) {
        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent()) {
            Admin admin=adminOptional.get();
            if (admin.getPassword() != null) {
                return "email already registered";
            }
            String otp=otpUtil.generateOtp();
            otpService.sendOtp(email,otp);
            admin.setOtp(otp);
            admin.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
            return "otp has been sent";
        }
        return "Admin is not found in the Data Base";
    }

    public String adminOtpVerification(Map<String, String> body) {
        String email=body.get("email");
        String otp=body.get("otp");
        String newPassword=body.get("password");

        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        //optional get method is used because already isPresent is checked in req otp method;
        Admin admin=adminOptional.get();

        if (otp.equals(admin.getOtp())&&LocalDateTime.now().isBefore(admin.getOtpExpiry())) {
            admin.setPassword(newPassword);
            return "Successfully registered";
        } else return "Wrong Otp.Registration failed";
    }

    public ResponseEntity<?> adminLogin(Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isEmpty()) {
            return new ResponseEntity<>("The provided user detail is not registered", HttpStatus.UNAUTHORIZED);
        }
        Admin admin = adminOptional.get();
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            return new ResponseEntity<>("The wrong password", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token));

    }
}
