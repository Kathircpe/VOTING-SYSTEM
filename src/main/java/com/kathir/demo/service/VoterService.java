package com.kathir.demo.service;

import com.kathir.demo.DTO.UserVoter;
import com.kathir.demo.contracts.VotingContract;
import com.kathir.demo.models.Candidate;
import com.kathir.demo.models.Election;
import com.kathir.demo.models.Voter;
import com.kathir.demo.repository.CandidateRepository;
import com.kathir.demo.repository.ElectionRepository;
import com.kathir.demo.repository.VoterRepository;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Credentials;


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
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Data
@AllArgsConstructor
@Service
@Slf4j
public class VoterService {

    private final ZoneId ZONE_ID = ZoneId.of("Asia/Kolkata");

    @Autowired
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;


    private final PasswordEncoder passwordEncoder;

    private final VotingService votingService;

    public ResponseEntity<?> getProfile(long id) {
        try {
            Voter voter = voterRepository.findById(id).get();
            UserVoter user = new UserVoter();
            user.id = voter.getId();
            user.name = voter.getName();
            user.email = voter.getEmail();
            user.phoneNumber = voter.getPhoneNumber();
            user.age = voter.getAge();
            user.hasVoted = voter.isHasVoted();
            user.isEnabled = voter.isEnabled();
            user.privateKey = voter.getPrivateKey();
            return new ResponseEntity<>(Map.of("user", user), HttpStatus.FOUND);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("something went wrong", HttpStatus.NOT_FOUND);
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
                    case "privateKey" -> voter.setPrivateKey(body.get(key));
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
    public ResponseEntity<String> voteAsync(Map<String, String> body) throws ExecutionException, InterruptedException {
        int id = Integer.parseInt(body.get("id"));
        long candidateId = Long.parseLong(body.get("candidateId"));
        long voterId = Long.parseLong(body.get("voterId"));
        Optional<Voter> voterOptional = voterRepository.findById(voterId);
        if (voterOptional.isEmpty())
            return new ResponseEntity<>("can't fetch your id, try after sometime", HttpStatus.CONFLICT);
        Voter voter = voterOptional.get();


        Optional<Election> electionOptional = electionRepository.findById(id);
        if (electionOptional.isEmpty())
            return new ResponseEntity<>("There is no election with the provided id", HttpStatus.CONFLICT);
        Election election = electionOptional.get();

        if (LocalDateTime.now(ZONE_ID).isBefore(election.getStartDate())
                || LocalDateTime.now(ZONE_ID).isAfter(election.getEndDate())) {
            log.warn("no election");

            return new ResponseEntity<>("Currently there is no election scheduled", HttpStatus.CONFLICT);
        }
        int responseIndex = voteAsync(voter, election, candidateId).get();

        final String[] responseArray = {"successfully voted", "Already voted", "something went wrong"};
        String response = responseArray[responseIndex];

        if (responseIndex != 0)
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);


        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }


    private CompletableFuture<Integer> voteAsync(Voter voter, Election election, long candidateId) {


        String voterPrivateKey = voter.getPrivateKey();
        String contractAddress = election.getContractAddress();


        Credentials voterCredentials = Credentials.create(voterPrivateKey);
        try {
            if (voter.isHasVoted() || hasVoted(contractAddress, voterCredentials.getAddress())) {

                log.warn("already voted");
                return CompletableFuture.completedFuture(1);
            }

            return CompletableFuture.supplyAsync(() -> {
                try {
                    voter.setHasVoted(true);
                    voterRepository.save(voter);
                    log.info("going to vote");

                    String receipt = vote(contractAddress, candidateId, voterCredentials);
                    log.info(receipt);
                    return 0;
                } catch (Exception e) {
                    log.error("exception{}", e.getMessage());

                    return 2;
                }
            });
        } catch (Exception e) {
            log.error("error in checking has voted");
            log.error("exception{}", e.getMessage());
            return CompletableFuture.completedFuture(2);

        }


    }

    /**
     * Vote for a candidate
     *
     * @param contractAddress  Address of the voting contract
     * @param candidateId      ID of the candidate to vote for
     * @param voterCredentials
     * @return Transaction hash
     * @throws Exception if voting fails
     */
    private String vote(String contractAddress, long candidateId, Credentials voterCredentials) throws Exception {
        VotingContract contract = votingService.loadForVoting(contractAddress, voterCredentials);
        log.info("contract loaded");

        TransactionReceipt receipt = contract.vote(BigInteger.valueOf(candidateId)).send();
        log.info("voted in the contract");
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
    private boolean hasVoted(String contractAddress, String voterAddress) throws Exception {
        VotingContract contract = votingService.load(contractAddress);

        return contract.hasVoted(voterAddress).send();
    }

}
