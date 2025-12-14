# Security Filter Documentation

## Overview

The securityFilter package provides security features for the application, including JWT token-based authentication and authorization.

## Security Features

* JWT token-based authentication
* Authorization using roles (admin, voter)
* CORS configuration for cross-origin resource sharing

## Configuration

The security configuration is done through the SecurityConfiguration class, which sets up the security rules and JWT filter.

## Usage

To use the securityFilter package, simply add the @EnableWebSecurity annotation to your Spring Boot application configuration class.

## JWT Configuration

The JWT configuration is done through the JwtUtil class, which generates and validates JWT tokens.

## Security Considerations

* The securityFilter package uses HTTPS to encrypt communication between the client and server.
* The JWT tokens are signed with a secret key to prevent tampering.
* The security configuration is customizable to fit the needs of the application.
