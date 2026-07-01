# Bank API

A RESTful banking API built with Spring Boot, featuring JWT authentication, account management, and fund transfers.

## Tech Stack

- Java 21 + Spring Boot 4
- Spring Security + JWT
- PostgreSQL + Liquibase
- Redis
- Docker + Docker Compose
- Swagger UI

## Features

- User registration and login with JWT authentication
- Password validation (uppercase, lowercase, digits, special characters)
- PIN code support with secure change flow (requires password confirmation)
- Password reset via phone verification code (Redis-backed, 3-minute expiry)
- Multiple accounts per user (one per currency)
- Fund transfers between accounts with pessimistic locking to prevent double-spending
- Cross-currency transfers with live exchange rates (Frankfurter API)
- Transaction history
- Input validation
- Global exception handling

## Getting Started

### Prerequisites

- Docker and Docker Compose

### Run with Docker

1. Clone the repository
2. Create `.env` file in the root directory:

```
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
```

3. Run:

```
docker-compose up --build
```

The API will be available at `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Testing Password Reset

Since this is a demo project without a real SMS provider, the verification code
is logged to the application console instead of being sent via SMS.

1. Call `POST /api/auth/password-reset/request` with your phone number
2. Check the application logs (or `docker-compose` output) for a line like:
   `Password reset code for +79991234567: 482913`
3. Use that code in `PATCH /api/auth/password-reset/confirm` along with your new password

## Testing

Run tests with:     ./gradlew test

Includes unit tests for transaction service and DTO validation.

## API Endpoints

### Auth
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Login and receive JWT token
- `POST /api/auth/password-reset/request` — Request a password reset code via phone
- `PATCH /api/auth/password-reset/confirm` — Confirm reset code and set a new password

### Users
- `PATCH /api/users/change/pin` — Change PIN code (requires password confirmation)

### Accounts
- `POST /api/accounts` — Create a new account (requires auth)
- `GET /api/accounts` — Get all accounts (requires auth)

### Transactions
- `POST /api/transactions/transfer` — Transfer funds between accounts (requires auth)
- `GET /api/transactions` — Get transaction history (requires auth)
