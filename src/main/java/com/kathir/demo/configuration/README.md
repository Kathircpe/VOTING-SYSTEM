# Configuration Layer Documentation

## Overview

The configuration layer contains classes that configure various components of the blockchain-based voting application. These configurations are loaded at application startup and provide the necessary beans for the application to function.

## Architecture

The application follows a layered architecture:

1. **Frontend**: Deployed separately, interacts with the REST API
2. **REST API Layer**: Spring Boot controllers handle HTTP requests
3. **Service Layer**: Business logic implementation
4. **Data Access Layer**: JPA repositories for database operations
5. **Blockchain Layer**: Ethereum smart contracts via Web3j
6. **Database**: PostgreSQL for user and election data
7. **Authentication**: JWT tokens with OTP verification

## Configuration Classes

### Web3jConfiguration

This configuration class sets up the Web3j components needed to interact with the Ethereum blockchain.

#### Beans Provided

- `web3j`: Configures the Web3j instance with the RPC URL from application.properties
- `credentials`: Creates Ethereum credentials from the private key in application.properties
- `gasProvider`: Provides default gas pricing for Ethereum transactions

#### Properties Used

- `web3.rpc-url`: The Ethereum node RPC URL (e.g., Infura endpoint)
- `web3.private-key`: The private key for the Ethereum account that deploys contracts and interacts with them

### SecurityConfiguration

This configuration class sets up Spring Security for the application.

#### Features

- Configures CORS (Cross-Origin Resource Sharing) policies
- Sets up security rules for different endpoints
- Configures password encoder for secure password hashing
- Sets up JWT filter for token-based authentication

#### Security Rules

- `/auth/**` endpoints are publicly accessible (authentication endpoints)
- `/api/v1/admin/**` endpoints require admin role
- `/api/v1/voter/**` endpoints require voter role
- All other endpoints require authentication

### SwaggerConfig

This configuration class sets up Swagger/OpenAPI documentation for the REST API.

#### Features

- Configures API information (title, description, version)
- Sets up security schemes for JWT token authentication
- Provides API documentation accessible at `/swagger-ui.html`

## Application Properties

The main configuration file `application.properties` contains:

### Database Configuration

- `spring.datasource.url`: PostgreSQL database connection URL
- `spring.datasource.username`: Database username
- `spring.datasource.password`: Database password (loaded from environment variable)
- `spring.datasource.driver-class-name`: PostgreSQL driver class

### JPA/Hibernate Configuration

- `spring.jpa.hibernate.ddl-auto`: Database schema generation strategy (update)
- `spring.properties.hibernate.dialect`: PostgreSQL dialect
- `spring.properties.hibernate.format_sql`: SQL formatting
- `spring.jpa.show-sql`: SQL query logging

### Web3j Configuration

- `web3.rpc-url`: Ethereum node RPC URL
- `web3.private-key`: Ethereum account private key (loaded from environment variable)

### Email Configuration

- `spring.mail.host`: SMTP server host (Gmail)
- `spring.mail.port`: SMTP server port
- `spring.mail.properties.mail.smtp.auth`: SMTP authentication enabled
- `spring.mail.properties.mail.smtp.starttls.enable`: TLS encryption enabled
- `spring.mail.username`: Gmail username
- `spring.mail.password`: Gmail app password (loaded from environment variable)

## Environment Variables

Sensitive configuration values are loaded from environment variables:

- `POSTGRES_NEON_PASSWORD`: PostgreSQL database password
- `PRIVATE_KEY`: Ethereum account private key
- `GMAIL_APP_PASSWORD`: Gmail app password for sending OTP emails

These can be set in the `passwords.env` file or through system environment variables.

## Hikari Connection Pool

The application uses HikariCP for database connection pooling with specific configuration:

- `spring.datasource.hikari.maximum-pool-size`: Maximum 5 connections
- `spring.datasource.hikari.minimum-idle`: Minimum 2 idle connections
- `spring.datasource.hikari.idle-timeout`: 300000ms (5 minutes)
- `spring.datasource.hikari.connection-timeout`: 30000ms (30 seconds)
- `spring.datasource.hikari.max-lifetime`: 1200000ms (20 minutes)

## JWT Configuration

JWT tokens are configured through the JwtUtil class (not in configuration package):

- Tokens are generated with user email as subject
- Tokens include issued and expiration timestamps
- Token validation checks signature and expiration

## Blockchain Network

The application is configured to connect to the Sepolia test network through Infura:

- Uses HTTPS RPC endpoint
- Requires a valid Infura project ID
- Can be changed to connect to other Ethereum networks or nodes

## Update History

- Updated README.md file to include more details about the configuration classes and properties used.
