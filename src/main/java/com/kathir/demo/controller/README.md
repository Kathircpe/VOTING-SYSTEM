# Controller Layer Documentation

## Overview

The controller layer handles HTTP requests and responses for the blockchain-based voting application. It provides RESTful endpoints for voters and administrators to interact with the system.

## Controllers

### AuthController

This controller handles authentication-related endpoints for both voters and administrators.

#### Endpoints

- `POST /auth/vo/registration`: Register a new voter
- `POST /auth/vo/verification`: Verify a voter's email with OTP
- `POST /auth/vo/{email}`: Send OTP to voter's email
- `POST /auth/vo/login`: Authenticate a voter and generate JWT token
- `POST /auth/ad/login`: Authenticate an admin and generate JWT token
- `POST /auth/ad/{email}`: Send OTP to admin's email

### VotersController

This controller provides endpoints for voters to view candidates and cast votes.

#### Endpoints

- `GET /api/v1/voter/{id}`: Get voter information by ID
- `GET /api/v1/voter/candidates`: Get all candidates
- `PATCH /api/v1/voter/updateVoter`: Update voter information
- `PATCH /api/v1/voter/vote`: Cast a vote for a candidate
- `GET /api/v1/voter/getVotes/{contractAddress}`: Get vote counts for all candidates

### AdminController

This controller provides administrative endpoints for managing elections and candidates.

#### Endpoints

- `POST /api/v1/admin/createElection`: Create a new election
- `PATCH /api/v1/admin/updateElection`: Update election details
- `DELETE /api/v1/admin/deleteElection/{id}`: Delete an election
- `POST /api/v1/admin/createCandidate`: Add a new candidate
- `PATCH /api/v1/admin/updateCandidate`: Update candidate information
- `DELETE /api/v1/admin/deleteCandidate/{id}`: Delete a candidate
- `GET /api/v1/admin/getVoters/{page}`: Get all voters (paginated)
- `GET /api/v1/admin/getVoter/{id}`: Get voter information by ID
- `GET /api/v1/admin/getVotesForAll/{contractAddress}`: Get vote counts for all candidates
- `GET /api/v1/admin/getVotes`: Get vote count for a specific candidate
- `PATCH /api/v1/admin/updateAdmin`: Update admin information

## Request/Response Flow

1. **Incoming Requests**: Controllers receive HTTP requests from clients
2. **Validation**: Basic request validation (e.g., required parameters)
3. **Service Delegation**: Controllers call appropriate service methods to handle business logic
4. **Response**: Controllers return HTTP responses with appropriate status codes and data

## Authentication

All endpoints (except authentication endpoints) require JWT tokens for access:

- Voters use tokens generated from `/auth/vo/login`
- Admins use tokens generated from `/auth/ad/login`

Tokens should be included in the Authorization header as "Bearer {token}".

## Error Handling

Controllers return appropriate HTTP status codes for different scenarios:

- `200 OK`: Successful requests
- `201 CREATED`: Successfully created resources
- `400 BAD REQUEST`: Invalid request data
- `401 UNAUTHORIZED`: Authentication failed
- `404 NOT FOUND`: Resource not found
- `406 NOT ACCEPTABLE`: Request cannot be processed (e.g., voter under 18)
- `409 CONFLICT`: Conflicting state (e.g., already voted)

## Asynchronous Operations

Some operations are performed asynchronously to prevent blocking:

- Voting operations return `CompletableFuture` responses
- Contract deployment operations return `CompletableFuture` responses

Clients should handle these asynchronous responses appropriately.

## Update History

- Updated README.md file to include more details about the controllers and their endpoints.
