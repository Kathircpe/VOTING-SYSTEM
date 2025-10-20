package com.kathir.demo.service;

import com.kathir.demo.models.Candidate;
import com.kathir.demo.models.Voter;
import com.kathir.demo.repository.CandidateRepository;
import com.kathir.demo.repository.VoterRepository;
import com.kathir.demo.utils.JwtUtil;
import com.kathir.demo.utils.OtpUtil;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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

    public String getVoterById(Long id) {
        Voter voter = voterRepository.findById(id).orElseThrow(() -> new IllegalStateException(id + " not found"));
        if (voter != null) {
            return voter.getId() + "\n" + voter.getName();
        }
        return "ID not found";
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public ResponseEntity<String> voterRegistration(Map<String, String> body)  {
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

        return new ResponseEntity<>("Successfully registered", HttpStatus.OK);

    }

    public ResponseEntity<String> voterVerification(Map<String, String> body) {

        String email = body.get("email");
        String otp = body.get("otp");

        Optional<Voter> voterOptional = voterRepository.findByEmail(email);
        if (voterOptional.isPresent()) {
            Voter voter = voterOptional.get();
            if (!otp.equals(voter.getOtp())) {
                return new ResponseEntity<>("otp is not matching",HttpStatus.UNAUTHORIZED);
            }
            if (LocalDateTime.now().isAfter(voter.getExpiration())) {
                return  new ResponseEntity<>("otp expired",HttpStatus.NOT_ACCEPTABLE);
            }
            voter.setEnabled(true);
            voterRepository.save(voter);
            return new ResponseEntity<>("Successfully verified",HttpStatus.OK);

        }
        return  new ResponseEntity<>("You need register first to verify",HttpStatus.UNAUTHORIZED);

    }

    public ResponseEntity<?> voterLogin(Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        Optional<Voter> voterOptional = voterRepository.findByEmail(email);
        if (voterOptional.isEmpty()) {
            return new ResponseEntity<>("The provided user detail is not registered", HttpStatus.UNAUTHORIZED);
        }
        Voter voter = voterOptional.get();

        if (!voter.isEnabled()) {
            return new ResponseEntity<>("Your account is not verified", HttpStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(password, voter.getPassword())) {
            return new ResponseEntity<>("Wrong password", HttpStatus.UNAUTHORIZED);
        }
        else if(body.containsKey("otp")&&!body.get("otp").equals(voter.getOtp())){
            return new ResponseEntity<>("The wrong otp", HttpStatus.UNAUTHORIZED);
        }
            String token = jwtUtil.generateToken(email);
            return ResponseEntity.ok(Map.of("token", token));

    }

    public void addNewVoter(String email, String name, String phoneNum, String voterAddress, String password, int age)  {
        Voter voter = new Voter();
        voter.setEmail(email);
        voter.setName(name);
        voter.setPhoneNumber(phoneNum);
        voter.setVoterAddress(voterAddress);
        voter.setAge(age);
        voter.setPassword(passwordEncoder.encode(password));
        String otp = otpUtil.generateOtp();
        otpService.sendOtp(email, otp);
        voter.setOtp(otp);
        voter.setExpiration(LocalDateTime.now().plusMinutes(15));
        voterRepository.save(voter);
    }


    public void otpSender(String email) {
        Optional<Voter> voterOptional=voterRepository.findByEmail(email);
        if(voterOptional.isPresent()) {
            Voter voter =voterOptional.get();
            String otp = otpUtil.generateOtp();
            otpService.sendOtp(voter.getEmail(), otp);
            voter.setOtp(otp);
            voter.setExpiration(LocalDateTime.now().plusMinutes(15));
            voterRepository.save(voter);
        }

    }


}
