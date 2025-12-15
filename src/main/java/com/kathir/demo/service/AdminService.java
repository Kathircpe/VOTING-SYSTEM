package com.kathir.demo.service;

import com.kathir.demo.contracts.VotingContract;
import com.kathir.demo.models.Admin;
import com.kathir.demo.models.Candidate;
import com.kathir.demo.models.Election;
import com.kathir.demo.models.Voter;
import com.kathir.demo.repository.CandidateRepository;
import com.kathir.demo.repository.ElectionRepository;
import lombok.AllArgsConstructor;
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
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Data
@AllArgsConstructor
@Service
public class AdminService {

    @Autowired
    private final AdminRepository adminRepository;
    private final VoterRepository voterRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;

    private final PasswordEncoder passwordEncoder;
    private final VotingService votingService;

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;

    public Page<Voter> getAllVoters(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voterRepository.findAll(pageable);
    }

    public ResponseEntity<?> getVoterById(long id) {
        Optional<Voter> voterOptional = voterRepository.findById(id);
        if (voterOptional.isPresent()) {
            return new ResponseEntity<>(voterOptional.get(), HttpStatus.FOUND);
        }
        return new ResponseEntity<>(id + " not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> createElection(Map<String, String> body) throws Exception {

        try {
            String name = body.get("electionName");
            LocalDateTime startDate = LocalDateTime.parse(body.get("startDate"));
            LocalDateTime endDate = LocalDateTime.parse(body.get("endDate"));

            Optional<Election> electionOptional = electionRepository.finActiveElection();
            if (electionOptional.isPresent())
                return new ResponseEntity<>("already there is an active election", HttpStatus.CONFLICT);

            Election election = new Election();
            election.setEndDate(endDate);
            election.setStartDate(startDate);
            election.setElectionName(name);
            election.setContractAddress(deploy());
            electionRepository.save(election);

            //marking hasVoted as false for all voters
            voterRepository.updateAllHasVotedToFalse();

            return new ResponseEntity<>("successfully created an election", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("error can't create an election", HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<String> deleteElection(int id) {
        Optional<Election> electionOptional = electionRepository.findById(id);
        if (electionOptional.isPresent()) {
            electionRepository.deleteById(id);
            return new ResponseEntity<>("Successfully deleted the election", HttpStatus.OK);
        }
        return new ResponseEntity<>("No election found for the provided id", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> updateElection(Map<String, String> body) {
        int id = Integer.parseInt(body.getOrDefault("id", "0"));
        if (id == 0) return new ResponseEntity<>("provide id to update the details", HttpStatus.CONFLICT);
        Optional<Election> electionOptional = electionRepository.findById(id);
        if (electionOptional.isPresent()) {
            Election election = electionOptional.get();
            for (String key : body.keySet()) {
                switch (key) {
                    case "from" -> election.setStartDate(LocalDateTime.parse(body.get(key)));
                    case "to" -> election.setEndDate(LocalDateTime.parse(body.get(key)));
                    case "contractAddress" -> election.setContractAddress(body.get(key));
                    case "electionName" -> election.setElectionName(body.get(key));
                }

            }
            electionRepository.save(election);
            return new ResponseEntity<>("Successfully updated the election", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("no election found for the provided id", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> createCandidate(Map<String, String> body) {
        String partyName = body.get("partyName");
        String name = body.get("name");
        String constituency = body.get("constituency");
        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setPartyName(partyName);
        candidate.setConstituency(constituency);
        candidateRepository.save(candidate);
        return new ResponseEntity<>("Successfully added the candidate", HttpStatus.CREATED);
    }

    public ResponseEntity<String> updateCandidate(Map<String, String> body) {
        int id = Integer.parseInt(body.getOrDefault("id", "0"));
        if (id == 0) return new ResponseEntity<>("provide id to update the details", HttpStatus.CONFLICT);
        Optional<Candidate> candidateOptional = candidateRepository.findById(id);
        if (candidateOptional.isPresent()) {
            Candidate candidate = candidateOptional.get();
            for (String key : body.keySet()) {
                switch (key) {
                    case "name" -> candidate.setName(body.get(key));
                    case "constituency" -> candidate.setConstituency(body.get(key));
                    case "partyName" -> candidate.setPartyName(body.get(key));
                }
            }
            candidateRepository.save(candidate);
            return new ResponseEntity<>("Successfully updated the candidate", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("no candidate found for the provided id", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> deleteCandidate(int id) {
        Optional<Candidate> candidateOptional = candidateRepository.findById(id);
        if (candidateOptional.isPresent()) {
            candidateRepository.deleteById(id);
            return new ResponseEntity<>("Successfully deleted the candidate", HttpStatus.OK);
        }
        return new ResponseEntity<>("No candidate found for the provided id", HttpStatus.NOT_FOUND);
    }

    /**
     * Deploy a new voting contract asynchronously
     *
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
     *
     * @return Contract address
     * @throws Exception if deployment fails
     */
    private String deploy() throws Exception {
        VotingContract contract = VotingContract.deploy(web3j, credentials, gasProvider).send();
        return contract.getContractAddress();
    }

    public ResponseEntity<?> getVotesAsync(Map<String, String> body) {
       Optional<Election> electionOptional= electionRepository.findByContractAddress(body.get("id"));
        if(electionOptional.isEmpty())return new ResponseEntity<>("no election found for the provided id",HttpStatus.NOT_FOUND);
        String contractAddress=electionOptional.get().getContractAddress();
        body.put("contractAddress",contractAddress);
        return new ResponseEntity<>(votingService.getVotesAsync(body,false),HttpStatus.FOUND);
    }

    public ResponseEntity<?> getVotesOfAllCandidatesAsync(int id) {
        Optional<Election> electionOptional=electionRepository.findById(id);
        if(electionOptional.isEmpty())return new ResponseEntity<>("no election found for the provided id",HttpStatus.NOT_FOUND);
        String contractAddress =electionOptional.get().getContractAddress();

        return new ResponseEntity<>(votingService.getVotesOfAllCandidatesAsync(contractAddress),HttpStatus.FOUND);
    }


    public ResponseEntity<String> updateAdmin(Map<String, String> body) {
        long id = Long.parseLong(body.get("id"));
        Optional<Admin> adminOptional = adminRepository.findById(id);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            for (String key : body.keySet()) {
                switch (key) {
                    case "name" -> admin.setName(body.get(key));
                    case "email" -> {
                        String email = body.get(key);
                        Optional<Admin> adminOptionalForNewEmail = adminRepository.findByEmail(email);
                        if (adminOptionalForNewEmail.isPresent()) {
                            return new ResponseEntity<>("Email already registered another account", HttpStatus.NOT_ACCEPTABLE);
                        }
                        admin.setEmail(email);
                    }
                    case "phoneNumber" -> admin.setPhoneNumber(body.get(key));
                    case "password" -> admin.setPassword(passwordEncoder.encode(body.get(key)));
                }
            }
            adminRepository.save(admin);
            return new ResponseEntity<>("Successfully updated admin credentials", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("admin not found", HttpStatus.NOT_FOUND);
    }


}
