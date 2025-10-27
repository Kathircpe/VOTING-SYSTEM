package com.kathir.demo.service;

import com.kathir.demo.contracts.VotingContract;
import com.kathir.demo.models.Candidate;
import com.kathir.demo.repository.CandidateRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Data
public class VotingService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;
    private final CandidateRepository candidateRepository;

    /**
     * Load an existing contract
     * @param address Contract address
     * @return SimpleVote contract instance
     */
    public VotingContract load(String address) {
        return VotingContract.load(address, web3j, credentials, gasProvider);
    }

    /**
     * Get vote count for a candidate asynchronously
     * @param contractAddress Address of the voting contract
     * @param candidateId ID of the candidate
     * @return CompletableFuture with vote count
     */
    public CompletableFuture<BigInteger> getVotesAsync(String contractAddress, long candidateId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getVotes(contractAddress, candidateId);
            } catch (Exception e) {
                throw new RuntimeException("Error getting vote count: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Get vote count for a candidate
     * @param contractAddress Address of the voting contract
     * @param candidateId ID of the candidate
     * @return Vote count
     * @throws Exception if getting votes fails
     */
    public BigInteger getVotes(String contractAddress, long candidateId) throws Exception {
        VotingContract contract = load(contractAddress);
        return contract.getVotes(BigInteger.valueOf(candidateId)).send();
    }


    public Map<String,String> getVotesAsync(Map<String,String> body){
        String contractAddress=body.get("contractAddress");
        int candidateId=Integer.parseInt(body.get("candidateId"));
        String name=candidateRepository.findById(candidateId).get().getName();
        CompletableFuture<BigInteger> future=getVotesAsync(contractAddress,candidateId);

        return Map.of(name,future.join().toString());
    }
    public List<Map<String,String>> getVotesOfAllCandidatesAsync(String contractAddress ){
        List<Map<String,String>> list= new ArrayList<>();
        List<Candidate> candidates=candidateRepository.findAll();

        for(Candidate candidate:candidates){
            int id=candidate.getId();
            list.add(getVotesAsync(Map.of("contractAddress",contractAddress,"candidateId",""+id)));
        }

        return list;
    }


}
