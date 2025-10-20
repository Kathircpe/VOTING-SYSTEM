package com.kathir.demo.service;

import com.kathir.demo.models.Admin;
import com.kathir.demo.models.Candidate;
import com.kathir.demo.models.Election;
import com.kathir.demo.models.Voter;
import com.kathir.demo.repository.CandidateRepository;
import com.kathir.demo.repository.ElectionRepository;
import com.kathir.demo.utils.JwtUtil;
import com.kathir.demo.utils.OtpUtil;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final OtpUtil otpUtil;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public Page<Voter> getAllVoters(int page,int size){
        Pageable pageable= PageRequest.of(page,size);
        return voterRepository.findAll(pageable);
    }

    public Voter getVoterById(long id) {
        return voterRepository.findById(id).orElseThrow(()-> new IllegalStateException(id+" not found"));
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
        else if(body.containsKey("otp")&&!body.get("otp").equals(admin.getOtp())){
            return new ResponseEntity<>("The wrong otp", HttpStatus.UNAUTHORIZED);
        }

            String token = jwtUtil.generateToken(email);
            return ResponseEntity.ok(Map.of("token", token));



    }

    public void otpSender(String email) {
        Optional<Admin> adminOptional=adminRepository.findByEmail(email);
        if(adminOptional.isPresent()) {
            Admin admin =adminOptional.get();
            String otp = otpUtil.generateOtp();
            otpService.sendOtp(admin.getEmail(), otp);
            admin.setOtp(otp);
            admin.setExpiration(LocalDateTime.now().plusMinutes(15));
            adminRepository.save(admin);
        }

    }

    public String createElection(Map<String, String> body) {

        LocalDateTime from=LocalDateTime.parse(body.get("from"));
        LocalDateTime to=LocalDateTime.parse(body.get("to"));
        String name=body.get("electionName");
        String contractAddress=body.get("contractAddress");
        Election election=new Election();
        election.setEndDate(to);
        election.setStartDate(from);
        election.setElectionName(name);
        election.setContractAddress(contractAddress);
        electionRepository.save(election);
        return "successfully created an election";

    }

    public String deleteElection(int id) {
        Optional<Election> electionOptional=electionRepository.findById(id);
        if(electionOptional.isPresent()){
            electionRepository.deleteById(id);
            return "Successfully deleted the election";
        }
        return "No election found for the provided id";
    }

    public String updateElection(Map<String, String> body) {
        int id=Integer.parseInt(body.getOrDefault("id","0"));
        if(id==0)return "provide id to update the details";
        Optional<Election> electionOptional=electionRepository.findById(id);
        if(electionOptional.isPresent()){
            Election election=electionOptional.get();
            for(String key:body.keySet()){
                switch(key){
                    case "from" -> election.setStartDate(LocalDateTime.parse(body.get(key)));
                    case "to" ->election.setEndDate(LocalDateTime.parse(body.get(key)));
                    case "contractAddress" ->election.setContractAddress(body.get(key));
                    case "electionName" ->election.setElectionName(body.get(key));
                }

            }
            electionRepository.save(election);
            return "Successfully updated the election";
        }
        return "no election found for the provided id";
    }

    public String createCandidate(Map<String, String> body) {
        String partyName=body.get("partyName");
        String name=body.get("name");
        int constituency=Integer.parseInt(body.get("constituency"));
        Candidate candidate=new Candidate();
        candidate.setName(name);
        candidate.setPartyName(partyName);
        candidate.setConstituency(constituency);
        candidateRepository.save(candidate);
        return "Successfully added the candidate";
    }

    public String updateCandidate(Map<String, String> body) {
        int id=Integer.parseInt(body.getOrDefault("id","0"));
        if(id==0)return "provide id to update the details";
        Optional<Candidate> candidateOptional=candidateRepository.findById(id);
        if(candidateOptional.isPresent()){
            Candidate candidate=candidateOptional.get();
            for(String key:body.keySet()){
                switch(key){
                    case "name" ->candidate.setName(body.get(key));
                    case "constituency" ->candidate.setConstituency(Integer.parseInt(body.get(key)));
                    case "partyName" ->candidate.setPartyName(body.get(key));
                }
            }
            candidateRepository.save(candidate);
            return "Successfully updated the candidate";
        }
        return "no candidate found for the provided id";
    }

    public String deleteCandidate(int id) {
        Optional<Candidate> candidateOptional=candidateRepository.findById(id);
        if(candidateOptional.isPresent()){
            candidateRepository.deleteById(id);
            return "Successfully deleted the candidate";
        }
        return "No candidate found for the provided id";
    }
}
