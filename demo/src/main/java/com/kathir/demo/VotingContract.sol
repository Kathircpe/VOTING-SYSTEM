// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract VotingContract {
    mapping(uint256 => uint256) public voteCount;
    mapping(address => bool) public hasVoted;

    event Voted(address indexed voter, uint256 indexed candidateId, uint256 newCount);

    function vote(uint256 candidateId) external {
        require(!hasVoted[msg.sender], "Already voted");
        hasVoted[msg.sender] = true;
        voteCount[candidateId] += 1;
        emit Voted(msg.sender, candidateId, voteCount[candidateId]);
    }

    function getVotes(uint256 candidateId) external view returns (uint256) {
        return voteCount[candidateId];
    }
}
