package com.kathir.demo.service;

import com.kathir.demo.contracts.SimpleVote;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Service
@Data
public class VotingService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;



    /**
     * Deploy a new voting contract asynchronously
     * @return CompletableFuture with contract address
     */
    public CompletableFuture<String> deployAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SimpleVote contract = SimpleVote.deploy(web3j, credentials, gasProvider).send();
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
        SimpleVote contract = SimpleVote.deploy(web3j, credentials, gasProvider).send();
        return contract.getContractAddress();
    }

    /**
     * Load an existing contract
     * @param address Contract address
     * @return SimpleVote contract instance
     */
    private SimpleVote load(String address) {
        return SimpleVote.load(address, web3j, credentials, gasProvider);
    }

    /**
     * Vote for a candidate asynchronously
     * @param contractAddress Address of the voting contract
     * @param candidateId ID of the candidate to vote for
     * @return CompletableFuture with transaction hash
     */
    public CompletableFuture<String> voteAsync(String contractAddress, long candidateId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return vote(contractAddress, candidateId);
            } catch (Exception e) {
                throw new RuntimeException("Error casting vote: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Vote for a candidate
     * @param contractAddress Address of the voting contract
     * @param candidateId ID of the candidate to vote for
     * @return Transaction hash
     * @throws Exception if voting fails
     */
    public String vote(String contractAddress, long candidateId) throws Exception {
        SimpleVote contract = load(contractAddress);
        TransactionReceipt receipt = contract.vote(BigInteger.valueOf(candidateId)).send();
        return receipt.getTransactionHash();
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
        SimpleVote contract = load(contractAddress);
        return contract.getVotes(BigInteger.valueOf(candidateId)).send();
    }

    /**
     * Check if an address has already voted
     * @param contractAddress Address of the voting contract
     * @param voterAddress Address of the voter
     * @return True if voter has already voted, false otherwise
     * @throws Exception if checking vote status fails
     */
    public boolean hasVoted(String contractAddress, String voterAddress) throws Exception {
        SimpleVote contract = load(contractAddress);
        return contract.hasVoted(voterAddress).send();
    }
}
