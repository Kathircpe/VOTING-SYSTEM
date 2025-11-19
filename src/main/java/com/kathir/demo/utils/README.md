# Utility Classes Documentation

## Overview

The utility classes provide helper functions for common operations in the blockchain-based voting application, including JWT token handling and OTP generation.

## Utility Classes

### JwtUtil

This utility class handles JWT (JSON Web Token) operations for authentication.

#### Key Functions

- `generateToken(String email)`: Generates a new JWT token with the user's email as subject
- `validateToken(String token, String email)`: Validates a JWT token against a user's email
- `extractEmail(String token)`: Extracts the email from a JWT token
- `isTokenExpired(String token)`: Checks if a JWT token has expired

#### Token Configuration

- Tokens are signed using HS256 algorithm
- Secret key is loaded from application.properties
- Tokens expire after 24 hours
- Tokens include issued at (iat) and expiration (exp) claims

### OtpUtil

This utility class handles OTP (One-Time Password) generation.

#### Key Functions

- `generateOtp()`: Generates a random 6-digit OTP

#### OTP Configuration

- OTPs are 6 digits long
- Generated using secure random number generation
- Used for two-factor authentication for both voters and admins

## Integration with Other Layers

### Authentication Flow

1. **Registration**: User registers with email, password, and other details
2. **OTP Generation**: OtpUtil generates a 6-digit OTP
3. **Email Sending**: OtpService sends OTP to user's email
4. **Verification**: User provides OTP to verify account
5. **Login**: User logs in with email, password, and optionally OTP
6. **Token Generation**: JwtUtil generates JWT token
7. **Protected Access**: JWT token is used to access protected endpoints

### Security Features

1. **Secure OTP Generation**: Uses Java's SecureRandom for cryptographically secure OTPs
2. **JWT Signing**: Tokens are signed with a secret key to prevent tampering
3. **Token Expiration**: Tokens automatically expire after 24 hours
4. **Email Validation**: OTPs are sent to verified email addresses only
