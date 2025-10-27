# Blockchain Voting Contract Documentation

## SimpleVote.sol

This is the core Ethereum smart contract that handles the voting functionality for the blockchain-based voting application.

### Contract Overview

The `SimpleVote` contract is a simple implementation of a voting system that:
1. Tracks votes for different candidates
2. Prevents double voting by recording which addresses have already voted
3. Provides transparency through event logging

### Data Structures

#### voteCount
```solidity
mapping(uint256 => uint256) public voteCount;
```
This mapping tracks the number of votes each candidate has received. The key is the candidate ID, and the value is the vote count.

#### hasVoted
```solidity
mapping(address => bool) public hasVoted;
```
This mapping tracks which Ethereum addresses have already voted. It prevents double voting by recording the voting status of each address.

### Events

#### Voted
```solidity
event Voted(address indexed voter, uint256 indexed candidateId, uint256 newCount);
```
This event is emitted whenever a vote is cast. It logs:
- The voter's address
- The candidate ID they voted for
- The new vote count for that candidate

Events are stored on the blockchain and provide a transparent record of all voting activity.

### Functions

#### vote
```solidity
function vote(uint256 candidateId) external
```
This function allows an Ethereum address to cast a vote for a candidate.

**Parameters:**
- `candidateId`: The ID of the candidate to vote for

**Functionality:**
1. Checks if the caller has already voted using the `hasVoted` mapping
2. If not, marks the caller's address as having voted
3. Increments the vote count for the specified candidate
4. Emits a `Voted` event with the details of the vote

**Security Features:**
- Uses `require(!hasVoted[msg.sender], "Already voted")` to prevent double voting
- Only allows external accounts to vote (not other contracts)

#### getVotes
```solidity
function getVotes(uint256 candidateId) external view returns (uint256)
```
This function returns the current vote count for a specific candidate.

**Parameters:**
- `candidateId`: The ID of the candidate to query

**Returns:**
- The number of votes the candidate has received

**Functionality:**
- Provides a read-only view function to query vote counts
- Can be called by anyone without spending gas (when called off-chain)

### Security Considerations

1. **Double Voting Prevention**: The contract uses the voter's Ethereum address to prevent double voting. Each address can only vote once.

2. **Immutability**: Once votes are recorded on the blockchain, they cannot be altered or deleted.

3. **Transparency**: All voting activity is recorded in events that are publicly visible on the blockchain.

4. **Simplicity**: The contract is intentionally simple to minimize potential vulnerabilities.

### Limitations

1. **No Candidate Management**: The contract doesn't manage candidate information. Candidate data is stored off-chain in the application database.

2. **No Election Management**: The contract doesn't handle election start/end times. This is managed by the application layer.

3. **Basic Implementation**: This is a simple voting contract without advanced features like vote delegation or complex voting schemes.

### Deployment

The contract is deployed using the Web3j library from the Java application. The deployment process:
1. Compiles the Solidity contract to bytecode
2. Sends a transaction to deploy the contract to the Ethereum network
3. Returns the contract address for future interactions

### Interaction with Java Application

The Java application uses Web3j to:
1. Deploy new instances of the contract for each election
2. Load existing contracts using their addresses
3. Call contract functions like `vote()` and `getVotes()`
4. Listen for `Voted` events to track voting activity
