# Bank API

A RESTful banking API built with Spring Boot, featuring JWT authentication, account management, and fund transfers.

## Tech Stack

- Java 21 + Spring Boot 4
- Spring Security + JWT
- PostgreSQL + Liquibase
- Docker + Docker Compose
- Swagger UI

## Features

- User registration and login with JWT authentication
- Password validation (uppercase, lowercase, digits, special characters)
- PIN code support
- Multiple accounts per user (one per currency)
- Fund transfers between accounts with pessimistic locking to prevent double-spending
- Transaction history
- Input validation

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

## API Endpoints

### Auth
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Login and receive JWT token

### Accounts
- `POST /api/accounts` — Create a new account (requires auth)
- `GET /api/accounts` — Get all accounts (requires auth)

### Transactions
- `POST /api/transactions/transfer` — Transfer funds between accounts (requires auth)
- `GET /api/transactions` — Get transaction history (requires auth)
