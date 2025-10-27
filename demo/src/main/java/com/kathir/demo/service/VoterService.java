package com.kathir.demo.service;

import com.kathir.demo.contracts.VotingContract;
import com.kathir.demo.models.Candidate;
import com.kathir.demo.models.Election;
import com.kathir.demo.models.Voter;
import com.kathir.demo.repository.CandidateRepository;
import com.kathir.demo.repository.ElectionRepository;
import com.kathir.demo.repository.VoterRepository;
import com.kathir.demo.utils.JwtUtil;
import com.kathir.demo.utils.OtpUtil;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Data
@Service
public class VoterService {
    @Autowired
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;

    private final OtpUtil otpUtil;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private final OtpService otpService;
    private final VotingService votingService;

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
        } else if (body.containsKey("otp") && !body.get("otp").equals(voter.getOtp())) {
            return new ResponseEntity<>("The wrong otp", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token));

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
        otpService.sendOtp(voter.getEmail(), otp);
        voter.setOtp(otp);
        voter.setExpiration(LocalDateTime.now().plusMinutes(15));
        voterRepository.save(voter);


    }

    public ResponseEntity<String> updateVoter(Map<String, String> body) {
        long id = Long.parseLong(body.getOrDefault("id", "0"));
        if (id == 0) return new ResponseEntity<>("provide id to update the details", HttpStatus.NOT_ACCEPTABLE);
        Optional<Voter> voterOptional = voterRepository.findById(id);
        if (voterOptional.isPresent()) {
            Voter voter = voterOptional.get();
            for (String key : body.keySet()) {
                switch (key) {
                    case "name" -> voter.setName((body.get(key)));
                    case "email" -> voter.setEmail((body.get(key)));
                    case "age" -> voter.setAge(Integer.parseInt(body.get(key)));
                    case "voterAddress" -> voter.setVoterAddress(body.get(key));
                    case "password" -> voter.setPassword(passwordEncoder.encode(body.get(key)));
                }

            }
            voterRepository.save(voter);
            return new ResponseEntity<>("Successfully updated the voter", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("no voter found for the provided id", HttpStatus.NOT_FOUND);

    }

    public List<Map<String, String>> getVotesOfAllCandidatesAsync(String contractAddress) {
        return votingService.getVotesOfAllCandidatesAsync(contractAddress);
    }

    public void otpSender(String email) {
        Optional<Voter> voterOptional = voterRepository.findByEmail(email);
        if (voterOptional.isPresent()) {
            Voter voter = voterOptional.get();
            String otp = otpUtil.generateOtp();
            otpService.sendOtp(voter.getEmail(), otp);
            voter.setOtp(otp);
            voter.setExpiration(LocalDateTime.now().plusMinutes(15));
            voterRepository.save(voter);
        }

    }

    /**
     * Vote for a candidate asynchronously
     *
     * @param body Address of the voting contract
     *             ID of the candidate to vote for
     * @return CompletableFuture with transaction hash
     */
    public ResponseEntity<CompletableFuture<String>> voteAsync(Map<String, String> body) throws Exception {
        String contractAddress = body.get("contractAddress");
        long candidateId = Long.parseLong(body.get("id"));
        String voterAddress = body.get("voterAddress");

        Optional<Voter> voterOptional = voterRepository.findByVoterAddress(voterAddress);
        Voter voter = voterOptional.get();
        Election election = electionRepository.findByContractAddress(contractAddress).get();

        if (LocalDateTime.now().isAfter(election.getStartDate())
                && LocalDateTime.now().isBefore(election.getEndDate())) {

            if (hasVoted(contractAddress, voterAddress, voter)) {
                return new ResponseEntity<>(CompletableFuture.supplyAsync(() -> {
                    try {
                        voter.setHasVoted(true);
                        voterRepository.save(voter);
                        return vote(contractAddress, candidateId);
                    } catch (Exception e) {
                        throw new RuntimeException("Error casting vote: " + e.getMessage(), e);
                    }
                }),HttpStatus.CREATED);
            }
            return new ResponseEntity<>(CompletableFuture.failedFuture(new RuntimeException("Already voted")),HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(CompletableFuture.failedFuture(new RuntimeException("Currently there is no election scheduled")),HttpStatus.CONFLICT);
    }

    /**
     * Vote for a candidate
     *
     * @param contractAddress Address of the voting contract
     * @param candidateId     ID of the candidate to vote for
     * @return Transaction hash
     * @throws Exception if voting fails
     */
    public String vote(String contractAddress, long candidateId) throws Exception {
        VotingContract contract = votingService.load(contractAddress);
        TransactionReceipt receipt = contract.vote(BigInteger.valueOf(candidateId)).send();
        return receipt.getTransactionHash();
    }


    /**
     * Check if an address has already voted
     *
     * @param contractAddress Address of the voting contract
     * @param voterAddress    Address of the voter
     * @return True if voter has already voted, false otherwise
     * @throws Exception if checking vote status fails
     */
    private boolean hasVoted(String contractAddress, String voterAddress, Voter voter) throws Exception {
        VotingContract contract = votingService.load(contractAddress);

        return !voter.isHasVoted() && !contract.hasVoted(voterAddress).send();
    }

}
