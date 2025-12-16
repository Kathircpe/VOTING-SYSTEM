# Service Layer Documentation

## Overview

The service layer contains the business logic of the blockchain-based voting application. It handles interactions between the controllers, repositories, and the blockchain contract.

## Services

### VoterService

This service handles all voter-related operations including registration, authentication, and voting.

#### Key Functions

- `voterRegistration(Map<String, String> body)`: Registers a new voter with validation for age (must be 18 or older)
- `voterVerification(Map<String, String> body)`: Verifies a voter's email using OTP
- `voterLogin(Map<String, String> body)`: Authenticates a voter and generates JWT token
- `voteAsync(Map<String, String> body)`: Allows a voter to cast their vote on the blockchain
- `getVotesOfAllCandidatesAsync(String contractAddress)`: Retrieves vote counts for all candidates from the blockchain

#### Voting Process

1. Validates that the voter is registered and verified
2. Checks that the election is currently active
3. Verifies that the voter hasn't already voted
4. Calls the blockchain contract to cast the vote
5. Updates the voter's status in the database

### AdminService

This service handles all administrative operations including election and candidate management.

#### Key Functions

- `adminLogin(Map<String, String> body)`: Authenticates an admin and generates JWT token
- `createElection(Map<String, String> body)`: Creates a new election and deploys a blockchain contract
- `updateElection(Map<String, String> body)`: Updates election details
- `deleteElection(int id)`: Deletes an election
- `createCandidate(Map<String, String> body)`: Adds a new candidate to the system
- `updateCandidate(Map<String, String> body)`: Updates candidate information
- `deleteCandidate(int id)`: Removes a candidate from the system
- `deploy()`: Deploys a new instance of the voting contract to the blockchain

#### Election Creation Process

1. Takes election name, start date, and end date as parameters
2. Deploys a new instance of the SimpleVote contract to the blockchain
3. Stores the election details and contract address in the database
4. Resets all voters' "hasVoted" status to false for the new election

### VotingService

This service acts as a bridge between the application and the blockchain contract.

#### Key Functions

- `load(String address)`: Loads an existing voting contract instance
- `getVotes(String contractAddress, long candidateId)`: Retrieves vote count for a candidate from the blockchain
- `getVotesOfAllCandidatesAsync(String contractAddress)`: Retrieves vote counts for all candidates asynchronously

### OtpService

This service handles OTP generation and email sending for two-factor authentication.

#### Key Functions

- `sendOtp(String email, String otp)`: Sends an OTP to the specified email address

### ElectionService

This service handles basic election data management in the database.

#### Key Functions

- `getAllElections()`: Retrieves all elections from the database
- `getElectionById(int id)`: Retrieves a specific election by ID
- `saveElection(Election election)`: Saves an election to the database
- `deleteElection(int id)`: Deletes an election from the database

## Asynchronous Operations

Many blockchain operations are performed asynchronously using `CompletableFuture` to prevent blocking the application while waiting for transactions to be mined:

- `voteAsync()`: Voting operations
- `deployAsync()`: Contract deployment
- `getVotesAsync()`: Vote count retrieval

## Blockchain Integration

The services use Web3j to interact with the Ethereum blockchain:

1. **Contract Loading**: `VotingContract.load()` loads an existing contract instance
2. **Contract Deployment**: `VotingContract.deploy()` deploys a new contract
3. **Function Calls**: Direct calls to contract functions like `vote()` and `getVotes()`
4. **Event Listening**: Monitoring for `Voted` events from the contract

## Security Features

1. **Two-Factor Authentication**: Both voters and admins must verify with OTP
2. **Password Hashing**: All passwords are securely hashed before storage
3. **JWT Tokens**: Secure authentication for API requests
4. **Blockchain Verification**: Vote integrity is ensured through blockchain immutability
5. **Election Validation**: Votes can only be cast during active election periods

Last updated: 12/16/2025
