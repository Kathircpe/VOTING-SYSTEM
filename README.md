# Blockchain-Based Voting Application

## Overview

This is a secure, transparent voting application built on blockchain technology. The system leverages Ethereum smart contracts to ensure vote integrity and prevent tampering, while using a traditional database for user management and election data.

## Architecture

The application follows a layered architecture:

1. **Frontend**: (Not included in current codebase) - Would interact with the REST API
2. **REST API Layer**: Spring Boot controllers handle HTTP requests
3. **Service Layer**: Business logic implementation
4. **Data Access Layer**: JPA repositories for database operations
5. **Blockchain Layer**: Ethereum smart contracts via Web3j
6. **Database**: PostgreSQL for user and election data
7. **Authentication**: JWT tokens with OTP verification

## Key Components

### Models

- **Voter**: Represents a voter with personal information and blockchain wallet address
- **Candidate**: Represents a candidate in an election
- **Election**: Represents an election event with start/end dates and blockchain contract address
- **Admin**: Represents an administrator with management privileges

### Services

- **VoterService**: Handles voter registration, authentication, and voting operations
- **AdminService**: Handles administrative functions like election and candidate management
- **VotingService**: Interfaces with the blockchain voting contract
- **OtpService**: Manages OTP generation and email sending
- **ElectionService**: Manages election data in the database

### Controllers

- **AuthController**: Handles user authentication (voter and admin)
- **VotersController**: Provides endpoints for voters to view candidates and cast votes
- **AdminController**: Provides administrative endpoints for managing elections and candidates

### Blockchain Contract

- **SimpleVote.sol**: Ethereum smart contract that tracks votes and prevents double voting

## API Endpoints

### Authentication

#### Voter Registration
```
POST /auth/vo/registration
Body: {
  "email": "string",
  "name": "string",
  "phoneNumber": "string",
  "voterAddress": "string", // Blockchain wallet address
  "password": "string",
  "age": "integer"
}
```

#### Voter Verification
```
POST /auth/vo/verification
Body: {
  "email": "string",
  "otp": "string"
}
```

#### Voter Login
```
POST /auth/vo/login
Body: {
  "email": "string",
  "password": "string",
  "otp": "string" // Optional
}
```

#### Admin Login
```
POST /auth/ad/login
Body: {
  "email": "string",
  "password": "string",
  "otp": "string" // Optional
}
```

### Voter Operations

#### Get Voter by ID
```
GET /api/v1/voter/{id}
```

#### Get All Candidates
```
GET /api/v1/voter/candidates
```

#### Update Voter Information
```
PATCH /api/v1/voter/updateVoter
Body: {
  "id": "long",
  "email": "string", // Optional
  "name": "string", // Optional
  "phoneNumber": "string", // Optional
  "voterAddress": "string", // Optional
  "password": "string", // Optional
  "age": "integer" // Optional
}
```

#### Cast Vote
```
PATCH /api/v1/voter/vote
Body: {
  "contractAddress": "string",
  "id": "string", // Candidate ID
  "voterAddress": "string"
}
```

#### Get Vote Counts for All Candidates
```
GET /api/v1/voter/getVotes/{contractAddress}
```

### Admin Operations

#### Create Election
```
POST /api/v1/admin/createElection
Body: {
  "electionName": "string",
  "startDate": "datetime",
  "endDate": "datetime"
}
```

#### Update Election
```
PATCH /api/v1/admin/updateElection
Body: {
  "id": "integer",
  "electionName": "string", // Optional
  "from": "datetime", // Optional, updates startDate
  "to": "datetime", // Optional, updates endDate
  "contractAddress": "string" // Optional
}
```

#### Delete Election
```
DELETE /api/v1/admin/deleteElection/{id}
```

#### Create Candidate
```
POST /api/v1/admin/createCandidate
Body: {
  "name": "string",
  "partyName": "string",
  "constituency": "string"
}
```

#### Update Candidate
```
PATCH /api/v1/admin/updateCandidate
Body: {
  "id": "integer",
  "name": "string", // Optional
  "partyName": "string", // Optional
  "constituency": "string" // Optional
}
```

#### Delete Candidate
```
DELETE /api/v1/admin/deleteCandidate/{id}
```

#### Get All Voters (Paginated)
```
GET /api/v1/admin/getVoters/{page}
```

#### Get Voter by ID
```
GET /api/v1/admin/getVoter/{id}
```

#### Get Vote Counts for All Candidates
```
GET /api/v1/admin/getVotesForAll/{contractAddress}
```

#### Get Vote Count for Specific Candidate
```
GET /api/v1/admin/getVotes
Body: {
  "contractAddress": "string",
  "candidateId": "string"
}
```

#### Update Admin Information
```
PATCH /api/v1/admin/updateAdmin
Body: {
  "id": "long",
  "email": "string", // Optional
  "name": "string", // Optional
  "phoneNumber": "string", // Optional
  "password": "string" // Optional
}
```

## Blockchain Integration

The application uses the Web3j library to interact with Ethereum smart contracts. The `SimpleVote.sol` contract provides the core voting functionality:

### Smart Contract Functions

- `vote(uint256 candidateId)`: Cast a vote for a candidate
- `getVotes(uint256 candidateId)`: Get vote count for a candidate
- `hasVoted(address voter)`: Check if an address has already voted

### Key Features

1. **Vote Tracking**: Each candidate's votes are stored in a mapping
2. **Double Voting Prevention**: Voter addresses are tracked to prevent multiple votes
3. **Transparency**: All vote data is stored on the blockchain and can be verified
4. **Immutability**: Once a vote is cast, it cannot be altered or deleted

## Database Schema

The application uses PostgreSQL to store user and election data:

### Voter Table
- id (Primary Key)
- name
- age
- hasVoted (boolean)
- isEnabled (boolean)
- email (unique)
- phoneNumber
- voterAddress (unique, blockchain wallet address)
- password (hashed)
- otp
- expiration

### Candidate Table
- id (Primary Key)
- name
- partyName
- constituency

### Election Table
- id (Primary Key)
- electionName
- startDate
- endDate
- contractAddress (unique)

### Admin Table
- id (Primary Key)
- name
- email (unique)
- phoneNumber
- password (hashed)
- otp
- expiration

## Authentication and Security

### JWT Tokens
- Both voters and admins receive JWT tokens upon successful login
- Tokens are used to authenticate subsequent requests

### OTP Verification
- Two-factor authentication using email-based OTPs
- OTPs expire after 15 minutes
- OTPs are regenerated when needed

### Password Security
- Passwords are hashed using Spring Security's password encoder
- Secure storage in database

### Voting Security
- Voters must be registered and verified
- Voters can only vote once per election
- Blockchain ensures vote integrity and prevents tampering

## Configuration

The application requires several environment variables to be set:

- `POSTGRES_NEON_PASSWORD`: PostgreSQL database password
- `PRIVATE_KEY`: Ethereum wallet private key for contract interactions
- `GMAIL_APP_PASSWORD`: Gmail app password for OTP email sending

These can be set in the `passwords.env` file.

## Deployment

1. Set up a PostgreSQL database
2. Configure environment variables in `passwords.env`
3. Deploy the Ethereum smart contract (if not already deployed)
4. Update `application.properties` with correct RPC URL and contract address
5. Build the application: `./mvnw clean package`
6. Run the application: `java -jar target/demo-0.0.1-SNAPSHOT.jar`

## Dependencies

- Spring Boot
- Web3j (Ethereum integration)
- PostgreSQL driver
- Spring Security
- Spring Data JPA
- Lombok
- Java Mail Sender
