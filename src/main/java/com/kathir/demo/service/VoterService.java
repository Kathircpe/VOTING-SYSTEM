package com.kathir.demo.service;

import com.kathir.demo.contracts.VotingContract;
import com.kathir.demo.models.Candidate;
import com.kathir.demo.models.Election;
import com.kathir.demo.models.Voter;
import com.kathir.demo.repository.CandidateRepository;
import com.kathir.demo.repository.ElectionRepository;
import com.kathir.demo.repository.VoterRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Data
@AllArgsConstructor
@Service
public class VoterService {
    @Autowired
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;

    private final PasswordEncoder passwordEncoder;

    private final VotingService votingService;

    public ResponseEntity<?> getVoterById(Long id) {
        Optional<Voter> voterOptional = voterRepository.findById(id);

        if (voterOptional.isPresent()) {
            Voter voter = voterOptional.get();
            return new ResponseEntity<>(Map.of("id", voter.getId(), "name", voter.getName()), HttpStatus.FOUND);
        }
        return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
    }
    public List<Election> getAllElection() {
        return electionRepository.findAll();
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
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
                    case "email" -> {
                        String email = body.get(key);
                        Optional<Voter> voterOptionalForNewEmail = voterRepository.findByEmail(email);
                        if (voterOptionalForNewEmail.isPresent()) {
                            return new ResponseEntity<>("Email already registered another account", HttpStatus.NOT_ACCEPTABLE);
                        }
                        voter.setEmail(email);
                    }
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
                }), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(CompletableFuture.failedFuture(new RuntimeException("Already voted")), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(CompletableFuture.failedFuture(new RuntimeException("Currently there is no election scheduled")), HttpStatus.CONFLICT);
    }

    /**
     * Vote for a candidate
     *
     * @param contractAddress Address of the voting contract
     * @param candidateId     ID of the candidate to vote for
     * @return Transaction hash
     * @throws Exception if voting fails
     */
    private String vote(String contractAddress, long candidateId) throws Exception {
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
