package com.kathir.demo.service;


import com.kathir.demo.contracts.VotingContract;
import com.kathir.demo.models.Admin;
import com.kathir.demo.models.Candidate;
import com.kathir.demo.models.Election;
import com.kathir.demo.models.Voter;
import com.kathir.demo.repository.CandidateRepository;
import com.kathir.demo.repository.ElectionRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    private final VotingService votingService;

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;

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

    public ResponseEntity<String> createElection(Map<String, String> body) throws Exception {

        try{
            LocalDateTime from=LocalDateTime.parse(body.get("from"));
            LocalDateTime to=LocalDateTime.parse(body.get("to"));
            String name=body.get("electionName");
            Election election=new Election();
            election.setEndDate(to);
            election.setStartDate(from);
            election.setElectionName(name);
            election.setContractAddress(deploy());
            electionRepository.save(election);
            //marking hasVoted as false for all voters
            List<Voter> voters=voterRepository.findAll();
            for(Voter voter:voters){
                voter.setHasVoted(false);
                voterRepository.save(voter);
            }
            return new ResponseEntity<>("successfully created an election",HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>("error can't create an election",HttpStatus.BAD_REQUEST);
        }

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
        String constituency=body.get("constituency");
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
                    case "constituency" ->candidate.setConstituency(body.get(key));
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

    /**
     * Deploy a new voting contract asynchronously
     * @return CompletableFuture with contract address
     */
    public CompletableFuture<String> deployAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                VotingContract contract = VotingContract.deploy(web3j, credentials, gasProvider).send();
                return contract.getContractAddress();
            } catch (Exception e) {
                throw new RuntimeException("Error deploying contract: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Deploy a new voting contract
     * @return Contract address
     * @throws Exception if deployment fails
     */
    public String deploy() throws Exception {
        VotingContract contract = VotingContract.deploy(web3j, credentials, gasProvider).send();
        return contract.getContractAddress();
    }

    public CompletableFuture<BigInteger> getVotesAsync( Map<String,String> body){
        return votingService.getVotesAsync(body);
    }

    public List<CompletableFuture<BigInteger>> getVotesOfAllCandidatesAsync(String contractAddress){
        return votingService.getVotesOfAllCandidatesAsync(contractAddress);
    }


}
