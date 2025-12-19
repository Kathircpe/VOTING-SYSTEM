package com.kathir.demo.service;

import com.kathir.demo.DTO.UserVoter;
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

    public ResponseEntity<?> getProfile(long id){
        try{
            Voter voter=voterRepository.findById(id).get();
            UserVoter user = new UserVoter();
            user.id = voter.getId();
            user.name = voter.getName();
            user.email = voter.getEmail();
            user.phoneNumber = voter.getPhoneNumber();
            user.age = voter.getAge();
            user.hasVoted = voter.isHasVoted();
            user.isEnabled = voter.isEnabled();
            user.voterAddress = voter.getVoterAddress();
            return new ResponseEntity<>(Map.of("user", user),HttpStatus.FOUND);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>("something went wrong",HttpStatus.NOT_FOUND);
        }    

    }

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
                    case "phoneNumber" -> voter.setPhoneNumber(body.get(key));
                }

            }
            voterRepository.save(voter);
            return new ResponseEntity<>("Successfully updated the voter", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("no voter found for the provided id", HttpStatus.NOT_FOUND);

    }

    public ResponseEntity<?> getVotesOfAllCandidatesAsync(int id) {
        Optional<Election> electionOptional = electionRepository.findById(id);
        if (electionOptional.isEmpty())
            return new ResponseEntity<>("no election found for the provided id", HttpStatus.NOT_FOUND);
        Election election = electionOptional.get();
        String contractAddress = election.getContractAddress();
        return new ResponseEntity<>(votingService.getVotesOfAllCandidatesAsync(contractAddress), HttpStatus.FOUND);
    }


    /**
     * Vote for a candidate asynchronously
     *
     * @param body Address of the voting contract
     *             ID of the candidate to vote for
     * @return CompletableFuture with transaction hash
     */
    public ResponseEntity<CompletableFuture<String>> voteAsync(Map<String, String> body) throws Exception {
        int id = Integer.parseInt(body.get("id"));
        long candidateId = Long.parseLong(body.get("candidateId"));
        long voterId = Long.parseLong(body.get("voterId"));
System.out.println("1");
        Optional<Voter> voterOptional = voterRepository.findById(voterId);
        if (voterOptional.isEmpty())
            return new ResponseEntity<>(CompletableFuture.failedFuture(new RuntimeException("can't fetch your id, try after sometime")), HttpStatus.CONFLICT);
        Voter voter = voterOptional.get();
System.out.println("2");

        Optional<Election> electionOptional = electionRepository.findById(id);
        if (electionOptional.isEmpty())
            return new ResponseEntity<>(CompletableFuture.failedFuture(new RuntimeException("There is no election with the provided id")), HttpStatus.CONFLICT);
        Election election = electionOptional.get();
System.out.println("3");

        String voterAddress = voter.getVoterAddress();
        String contractAddress = election.getContractAddress();
System.out.println("4");

        if (LocalDateTime.now().isAfter(election.getStartDate())
                && LocalDateTime.now().isBefore(election.getEndDate())) {
        

            if (hasVoted(contractAddress, voterAddress, voter)) {
                return new ResponseEntity<>(CompletableFuture.supplyAsync(() -> {
                    try {
                        voter.setHasVoted(true);
                        voterRepository.save(voter);
System.out.println("going to vote");

                        return vote(contractAddress, candidateId);
                    } catch (Exception e) {
System.out.println("exception"+e.getMessage());

                        throw new RuntimeException("Error casting vote: " + e.getMessage(), e);
                    }
                }), HttpStatus.CREATED);
            }
System.out.println("already voted");

            return new ResponseEntity<>(CompletableFuture.failedFuture(new RuntimeException("Already voted")), HttpStatus.CONFLICT);
        }
System.out.println("no election");

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
System.out.println("contract loaded");        

        TransactionReceipt receipt = contract.vote(BigInteger.valueOf(candidateId)).send();
System.out.println("voted in the contract");        
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
