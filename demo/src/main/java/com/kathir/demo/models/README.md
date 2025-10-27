# Model Layer Documentation

## Overview

The model layer represents the data entities in the blockchain-based voting application. These entities are mapped to database tables using JPA annotations and define the structure of the data stored in the system.

## Entities

### Voter

Represents a registered voter in the system.

#### Fields

- `id`: Unique identifier (Primary Key)
- `name`: Voter's full name (Required)
- `age`: Voter's age (Required)
- `hasVoted`: Boolean flag indicating if voter has cast a vote in current election (Default: false)
- `isEnabled`: Boolean flag indicating if voter account is verified (Default: false)
- `email`: Voter's email address (Required, Unique)
- `phoneNumber`: Voter's phone number (Required)
- `voterAddress`: Voter's Ethereum wallet address (Required, Unique)
- `password`: Hashed password for authentication (Required)
- `otp`: One-time password for two-factor authentication
- `expiration`: Expiration time for OTP

#### Validation

- Email must be valid format
- Phone number must be exactly 10 digits
- Age must be provided
- All required fields must be non-blank

### Candidate

Represents a candidate in an election.

#### Fields

- `id`: Unique identifier (Primary Key)
- `name`: Candidate's full name (Required)
- `partyName`: Political party name (Required)
- `constituency`: Geographic area the candidate is running in (Required)

#### Validation

- All required fields must be non-blank

### Election

Represents an election event.

#### Fields

- `Id`: Unique identifier (Primary Key)
- `electionName`: Name of the election (Required)
- `startDate`: Date and time when voting begins (Required)
- `endDate`: Date and time when voting ends (Required)
- `contractAddress`: Address of the Ethereum smart contract for this election (Required, Unique)

#### Validation

- Election name must be provided
- Start and end dates must be provided

### Admin

Represents an administrator of the system.

#### Fields

- `id`: Unique identifier (Primary Key)
- `name`: Admin's full name (Required)
- `email`: Admin's email address (Required, Unique)
- `phoneNumber`: Admin's phone number (Required)
- `password`: Hashed password for authentication (Required)
- `otp`: One-time password for two-factor authentication
- `expiration`: Expiration time for OTP

#### Validation

- Email must be valid format
- Phone number must be exactly 10 digits
- All required fields must be non-blank

## Relationships

There are no explicit JPA relationships defined between entities in this application. Instead, relationships are managed through:
1. The `contractAddress` field in Election linking to the blockchain contract
2. The `voterAddress` field in Voter linking to the Ethereum wallet address
3. Business logic in services that coordinate between entities

## Lombok Annotations

All entities use Lombok annotations to reduce boilerplate code:
- `@Data`: Generates getters, setters, toString, equals, and hashCode methods
- `@Entity`: Marks class as a JPA entity
- `@AllArgsConstructor`: Generates constructor with all fields
- `@NoArgsConstructor`: Generates no-args constructor

## Database Mapping

Entities are mapped to PostgreSQL database tables using JPA annotations:
- `@Entity`: Maps class to database table
- `@Id`: Marks primary key field
- `@GeneratedValue`: Specifies primary key generation strategy
- `@Column`: Maps fields to database columns (when needed)

## Validation Constraints

Entities use Jakarta Bean Validation annotations for data validation:
- `@NotNull`: Field cannot be null
- `@NotBlank`: String field cannot be blank
- `@Email`: String field must be valid email format
- `@Pattern`: String field must match specified regex pattern
- `@DateTimeFormat`: Date/time fields use specified format

## Security Considerations

1. **Password Storage**: Passwords are stored as hashed values, never in plain text
2. **Unique Constraints**: Email addresses and Ethereum wallet addresses are unique
3. **Required Fields**: Critical fields are marked as required to ensure data integrity
4. **OTP Expiration**: OTPs have expiration times to limit their validity period
