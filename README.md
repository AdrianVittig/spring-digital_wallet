# Spring Digital Wallet

Spring Digital Wallet is a backend REST API for a digital wallet system built with Spring Boot, Java 21, Spring Security, JPA/Hibernate, MySQL, and Docker.

The project focuses on backend development concepts such as authentication, transactional consistency, concurrent balance updates, REST API design, validation, testing, and containerization.

## Features

- User registration and login
- JWT-based authentication
- BCrypt password hashing
- Automatic wallet creation after registration
- Public IBAN-like wallet identifier
- Wallet balance retrieval
- Wallet top-up
- Transfers between wallets by IBAN
- Self-transfer prevention
- Insufficient funds validation
- Incoming and outgoing wallet ledger entries
- Transfer history
- Filtering transfers by amount and date
- Pagination for transfer history
- Transactional balance updates
- Pessimistic locking for concurrent transfers
- Deterministic wallet locking order to reduce deadlock risk
- Global exception handling
- Bean Validation with `@Valid`
- Swagger / OpenAPI documentation
- Dockerized Spring Boot application and MySQL database
- Unit tests with JUnit 5 and Mockito

## Tech Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Hibernate
- Spring Security
- OAuth2 Resource Server
- JWT
- MySQL 8
- Gradle
- JUnit 5
- Mockito
- ModelMapper
- Lombok
- SpringDoc OpenAPI / Swagger UI
- Docker
- Docker Compose

## Architecture

The application follows a layered architecture:

Controller  
↓  
Service  
↓  
Repository  
↓  
Database

Main packages:

- `web/controller` — REST controllers and API endpoints
- `service/contract` — service interfaces
- `service/impl` — business logic
- `data/repository` — Spring Data JPA repositories
- `data/entity` — JPA entities
- `dto` — request and response models
- `config` — security, JWT, ModelMapper, and OpenAPI configuration
- `exception` — custom application exceptions
- `web/exception` — global exception handling

## Authentication

Authentication is stateless and based on JWT tokens.

After successful registration or login, the API returns a JWT token.

Protected endpoints require the following header:

    Authorization: Bearer <token>

The authenticated user's email is stored as the JWT subject and is used to identify the current user.

## Wallets

A wallet is automatically created when a user registers.

Each wallet contains:

- Current balance
- Public IBAN-like identifier
- Owner
- Wallet ledger entries

The public wallet identifier is derived from the database-generated wallet ID.

Example:

    Wallet ID: 1
    IBAN: BG10000

Because database IDs are unique, the generated wallet identifiers are also unique and do not depend on random generation.

The internal database ID is not used by clients when sending money. Transfers are performed using the wallet's public IBAN-like identifier.

## Transfers

Authenticated users can transfer money to another wallet by providing the recipient's IBAN.

Before executing a transfer, the application validates that:

- The transfer amount is greater than zero
- The recipient wallet exists
- The sender is not transferring money to their own wallet
- The sender has sufficient funds

A successful transfer updates both wallet balances and creates the corresponding ledger entries.

## Transaction and Concurrency Handling

Transfers are executed inside a database transaction.

Before wallet balances are modified, both participating wallets are locked using `PESSIMISTIC_WRITE`.

To reduce the risk of deadlocks when two users transfer money in opposite directions at the same time, wallets are always locked in deterministic ID order:

smaller wallet ID  
↓  
larger wallet ID

After both locks are acquired:

1. The sender's balance is validated
2. The transfer amount is subtracted from the sender
3. The transfer amount is added to the recipient
4. Wallet ledger entries are created

Because the operation is transactional, a failure causes the entire transfer operation to roll back.

## Transfer History

Authenticated users can retrieve transfers where their wallet is either the sender or the recipient.

Supported filters include:

- Minimum amount
- Maximum amount
- From date
- To date

Transfer history also supports pagination.

Example request:

    GET /api/transfers?page=0&size=10&minAmount=10&maxAmount=500

Example response:

    {
      "content": [],
      "page": 0,
      "size": 10,
      "totalElements": 0,
      "totalPages": 0,
      "last": true
    }

Pagination metadata is returned through a custom `PageResponseDto`.

## API Endpoints

### Authentication

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user and return a JWT | No |
| POST | `/api/auth/login` | Authenticate a user and return a JWT | No |

### Wallet

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| GET | `/api/wallet` | Get the authenticated user's wallet | Yes |
| POST | `/api/wallet/top-up` | Add funds to the authenticated user's wallet | Yes |

### Transfers

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| GET | `/api/transfers` | Get filtered and paginated transfer history | Yes |
| POST | `/api/transfers` | Transfer money to another wallet by IBAN | Yes |

## Error Handling

The application uses a global exception handler and returns consistent error responses.

Example:

    {
      "status": 400,
      "message": "Amount must be greater than 0!"
    }

Handled cases include:

- Invalid request data
- Bean validation errors
- Invalid authentication
- Missing users or wallets
- Insufficient funds
- Invalid transfer operations

## Swagger / OpenAPI

The API is documented using SpringDoc OpenAPI.

When the application is running, Swagger UI is available at:

    http://localhost:8084/swagger-ui/index.html

The OpenAPI specification is available at:

    http://localhost:8084/v3/api-docs

Swagger can be used to inspect and test the available API endpoints.

## Running with Docker

### Requirements

- Docker Desktop or Docker Engine
- Docker Compose

Create a `.env` file in the project root.

Example:

    MYSQL_DATABASE=spring_digital_wallet
    MYSQL_USER=wallet_user
    MYSQL_PASSWORD=your_mysql_password
    MYSQL_ROOT_PASSWORD=your_mysql_root_password
    JWT_SECRET=your_secure_jwt_secret

The `.env` file is ignored by Git and should not be committed.

Start the application with:

    docker compose up --build

The application will be available at:

    http://localhost:8084

The MySQL container is exposed locally on:

    localhost:3307

Inside the Docker network, the Spring Boot application connects to MySQL using:

    mysql:3306

The Docker Compose setup includes:

- Spring Boot application container
- MySQL 8 container
- Persistent MySQL volume
- MySQL health check
- Startup dependency between the application and database

Stop the containers with:

    docker compose down

To stop the containers and remove the MySQL volume:

    docker compose down -v

## Running Locally

The application can also be started directly from the IDE using the `local` Spring profile.

Local database configuration is stored in `application-local.properties`, which is excluded from Git.

Example local configuration:

    DB_USERNAME=your_username
    DB_PASSWORD=your_password

The JWT secret should also be provided through local environment configuration.

The application runs on:

    http://localhost:8084

## Tests

The project contains unit tests for the main service-layer functionality.

Covered areas include:

- Authentication
- Wallet operations
- Wallet creation
- Wallet top-up
- Transfers
- Transfer balance updates

Run all tests with:

    ./gradlew test

A successful test run ends with:

    BUILD SUCCESSFUL

Tests are implemented using JUnit 5 and Mockito.

## Domain Model

The main entities are:

- `User` — application user
- `Wallet` — stores the public identifier and current balance
- `Transfer` — represents a transfer between two wallets
- `WalletEntry` — represents a ledger entry associated with a wallet and transfer

Relationships conceptually look like this:

    User
      |
      | owns
      v
    Wallet
      |
      | has
      v
    WalletEntry
      |
      | references
      v
    Transfer

A transfer produces ledger entries for both participating wallets:

    Sender wallet    -> OUTCOMING
    Recipient wallet -> INCOMING

The transfer also stores sender and recipient IBAN information together with the amount and creation time.

## Security

Security-related functionality includes:

- BCrypt password hashing
- Stateless authentication
- JWT-based authorization
- Protected API endpoints
- Public authentication endpoints
- Pessimistic locking for balance updates
- Environment-based database credentials
- Environment-based JWT secret
- Sensitive configuration files excluded from Git

## Project Structure

    src/main/java
    └── com.vittig.spring_digital_wallet
        ├── config
        ├── data
        │   ├── entity
        │   ├── repository
        │   └── util
        ├── dto
        ├── exception
        ├── service
        │   ├── contract
        │   └── impl
        └── web
            ├── controller
            └── exception

    src/test/java
    └── com.vittig.spring_digital_wallet
        └── service
            └── impl

## Project Status

The backend is implemented and can be run either locally or through Docker Compose.

The project demonstrates practical experience with:

- Spring Boot backend development
- REST API design
- JWT authentication
- Spring Security
- JPA / Hibernate
- Database transactions
- Concurrency control
- Pessimistic locking
- Validation and exception handling
- Pagination and filtering
- Unit testing
- Swagger / OpenAPI
- Docker and Docker Compose