# Repository Layer Documentation

## Overview

The repository layer provides data access objects (DAOs) for interacting with the PostgreSQL database. These repositories extend Spring Data JPA interfaces to provide CRUD operations and custom queries for each entity.

## Repositories

### VoterRepository

Provides data access for Voter entities.

#### Standard CRUD Operations
- `save(Voter voter)`: Save or update a voter
- `findById(Long id)`: Find a voter by ID
- `findAll()`: Retrieve all voters
- `deleteById(Long id)`: Delete a voter by ID

#### Custom Queries
- `findByEmail(String email)`: Find a voter by email address
- `findByVoterAddress(String voterAddress)`: Find a voter by Ethereum wallet address

### CandidateRepository

Provides data access for Candidate entities.

#### Standard CRUD Operations
- `save(Candidate candidate)`: Save or update a candidate
- `findById(Integer id)`: Find a candidate by ID
- `findAll()`: Retrieve all candidates
- `deleteById(Integer id)`: Delete a candidate by ID

### ElectionRepository

Provides data access for Election entities.

#### Standard CRUD Operations
- `save(Election election)`: Save or update an election
- `findById(Integer id)`: Find an election by ID
- `findAll()`: Retrieve all elections
- `deleteById(Integer id)`: Delete an election by ID

#### Custom Queries
- `findByContractAddress(String contractAddress)`: Find an election by blockchain contract address

### AdminRepository

Provides data access for Admin entities.

#### Standard CRUD Operations
- `save(Admin admin)`: Save or update an admin
- `findById(Long id)`: Find an admin by ID
- `findAll()`: Retrieve all admins
- `deleteById(Long id)`: Delete an admin by ID

#### Custom Queries
- `findByEmail(String email)`: Find an admin by email address

## Pagination Support

The VoterRepository extends `PagingAndSortingRepository` to provide pagination support for retrieving large lists of voters:

- `findAll(Pageable pageable)`: Retrieve voters with pagination

## Integration with Services

Repositories are injected into service classes using `@Autowired` and provide the data access layer for all database operations:

1. **VoterService**: Uses VoterRepository, CandidateRepository, and ElectionRepository
2. **AdminService**: Uses all repositories for administrative operations
3. **ElectionService**: Uses ElectionRepository for election management

## Transaction Management

Database operations are transactional through Spring's `@Transactional` annotation, primarily used in service classes:

- Operations are automatically committed or rolled back
- Maintains data consistency across related operations

## Query Methods

Spring Data JPA automatically implements query methods based on method names:

- `findBy{FieldName}`: Find entities by specific field values
- `findAll()`: Retrieve all entities
- `save()`: Save or update entities
- `deleteById()`: Delete entities by ID

## Security Considerations

1. **Data Access Control**: Repositories are only accessed through service layers, not directly from controllers
2. **Parameterized Queries**: All queries use parameterized statements to prevent SQL injection
3. **Entity Validation**: Entities are validated before saving to ensure data integrity
