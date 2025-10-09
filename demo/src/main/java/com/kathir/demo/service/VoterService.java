package com.kathir.demo.service;

import com.kathir.demo.modules.Candidate;
import com.kathir.demo.modules.Voter;
import com.kathir.demo.repository.CandidateRepository;
import com.kathir.demo.repository.VoterRepository;
import com.kathir.demo.utils.JwtUtil;
import com.kathir.demo.utils.OtpUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Service
public class VoterService {
    @Autowired
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final OtpUtil otpUtil;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

//    need to change the return type because anyone can get a voter's full details with thir id
    public String getVoterById(Long id) {
         Voter voter =voterRepository.findById(id).orElseThrow(()-> new IllegalStateException(id+" not found"));
         if(voter!=null){
             return voter.getId()+"\n"+voter.getName();
         }
         return "ID not found";
    }

    public List<Candidate> getAllCandidates(){
        return candidateRepository.findAll();
    }

    public String voterOtpRequest(String email) {
        Optional<Voter> VoterOptional = voterRepository.findByEmail(email);
        if (VoterOptional.isPresent()) {
            Voter voter=VoterOptional.get();
            if (voter.getPassword() != null) {
                return "email already registered";
            }
            String otp=otpUtil.generateOtp();
            otpService.sendOtp(email,otp);
            voter.setOtp(otp);
            voter.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
            return "otp has been sent";
        }
        return "Voter is not found in the Data Base";

    }

    public String voterOtpVerification(Map<String, String> body) {
        String email=body.get("email");
        String otp=body.get("otp");
        String newPassword=body.get("password");

        Optional<Voter> voterOptional = voterRepository.findByEmail(email);
        //optional get method is used because already isPresent is checked in req otp method;
        Voter voter=voterOptional.get();

        if (otp.equals(voter.getOtp())&&LocalDateTime.now().isBefore(voter.getOtpExpiry())) {
            voter.setPassword(newPassword);
            return "Successfully registered";
        } else return "Wrong Otp.Registration failed";

    }

    public ResponseEntity<?> voterLogin(Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        Optional<Voter> voterOptional = voterRepository.findByEmail(email);
        if (voterOptional.isEmpty()) {
            return new ResponseEntity<>("The provided user detail is not registered", HttpStatus.UNAUTHORIZED);
        }
        Voter voter = voterOptional.get();
        if (!passwordEncoder.matches(password, voter.getPassword())) {
            return new ResponseEntity<>("The wrong password", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
