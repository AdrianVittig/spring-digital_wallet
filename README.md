# Spring Digital Wallet

A backend REST API for a digital wallet system built with **Java 21, Spring Boot, Spring Security, JPA/Hibernate, MySQL, and Docker**.

The project demonstrates practical backend engineering concepts, including secure authentication, transactional money transfers, concurrency control, relational database design, schema migrations, and REST API development.

## Features

### Authentication & Security
- User registration and login
- Stateless JWT authentication
- BCrypt password hashing
- Protected REST endpoints
- Automatic wallet creation upon registration

### Wallet Management
- Unique public IBAN-like wallet identifiers
- Wallet balance retrieval
- Wallet top-up functionality
- Transfer funds between wallets
- Prevention of self-transfers
- Insufficient funds validation

### Transfers & Transaction Management
- Atomic transfers using database transactions
- Pessimistic locking for concurrent balance updates
- Deterministic wallet locking order to reduce deadlock risk
- Automatic rollback on transactional failures
- Incoming and outgoing wallet ledger entries
- Transfer categories and optional descriptions

### Transfer History
- Paginated transfer history
- Incoming and outgoing transfers
- Filtering by minimum and maximum amount
- Filtering by date range
- Filtering by transfer category
- Sorting transfers by creation date

### Development & Infrastructure
- Database migrations with Flyway
- Global exception handling
- Request validation using Jakarta Bean Validation
- Swagger / OpenAPI documentation
- Dockerized application and MySQL database
- Unit testing with JUnit 5 and Mockito

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.1 | Application framework |
| Spring Web MVC | REST API development |
| Spring Security | Authentication and authorization |
| Spring OAuth2 Resource Server | JWT validation |
| Spring Data JPA / Hibernate | Data persistence |
| MySQL 8 | Relational database |
| Flyway | Database versioning and migrations |
| Gradle | Build and dependency management |
| Jakarta Bean Validation | Request validation |
| ModelMapper | Entity and DTO mapping |
| Lombok | Boilerplate reduction |
| SpringDoc OpenAPI | API documentation |
| JUnit 5 / Mockito | Unit testing |
| Docker / Docker Compose | Containerization |

## Architecture

The project follows a layered architecture that separates HTTP handling, business logic, and data persistence.

```text
Client
   |
   v
REST Controllers
   |
   v
Service Layer
   |
   v
Spring Data JPA Repositories
   |
   v
MySQL Database
```

### Main Packages

```text
src/main/java/com/vittig/spring_digital_wallet/
├── config/
├── data/
│   ├── entity/
│   ├── repository/
│   └── util/
├── dto/
├── exception/
├── service/
│   ├── contract/
│   └── impl/
└── web/
    ├── controller/
    └── exception/
```

The controller layer handles HTTP requests and responses, while the service layer contains the business logic. Repositories provide database access through Spring Data JPA.

## Domain Model

The application uses four main entities:

- **User** — represents a registered user.
- **Wallet** — stores the current balance, owner, and public wallet identifier.
- **Transfer** — represents a transfer between wallets, including its amount, category, description, and creation timestamp.
- **WalletEntry** — represents an incoming or outgoing ledger entry associated with a transfer.

A successful transfer creates two wallet entries:

- `OUTCOMING` — associated with the sender's wallet.
- `INCOMING` — associated with the recipient's wallet.

Transfers reference wallets using public IBAN-like identifiers rather than exposing internal wallet IDs in transfer requests.

These identifiers are application-generated and are not real banking IBANs.

## Authentication

Authentication is stateless and uses JWT tokens.

Successful registration or login returns a token that must be included in subsequent protected API requests.

```http
Authorization: Bearer <jwt_token>
```

### Registration Example

**POST** `/api/auth/register`

```json
{
  "email": "user@example.com",
  "password": "password123",
  "confirmPassword": "password123"
}
```

### Login Example

**POST** `/api/auth/login`

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Example authentication response:

```json
{
  "token": "<jwt_token>"
}
```

## Wallet Transfers

Authenticated users can transfer funds to another wallet by providing its public IBAN-like identifier.

Before a transfer is completed, the application validates the recipient, amount, wallet ownership, and available funds.

### Create Transfer

**POST** `/api/transfers`

```json
{
  "toIban": "BG10001",
  "amount": 75.50,
  "category": "SHOPPING",
  "description": "New headphones"
}
```

The `category` field is required, while `description` is optional and limited to 255 characters.

### Available Transfer Categories

- `FOOD`
- `TRANSPORT`
- `BILLS`
- `ENTERTAINMENT`
- `SHOPPING`
- `HEALTH`
- `EDUCATION`
- `OTHER`

The transfer response includes the sender and recipient identifiers, amount, category, description, and creation timestamp.

## Transaction Management & Concurrency

Money transfers are executed within a database transaction using Spring's `@Transactional` annotation.

To prevent inconsistent balance updates, the application uses JPA pessimistic locking with `PESSIMISTIC_WRITE`.

Both participating wallets are locked in a deterministic order based on their database IDs.

```text
Transfer Request
       |
       v
Validate Sender & Recipient
       |
       v
Lock Wallets in ID Order
       |
       v
Validate Available Balance
       |
       v
Update Both Wallet Balances
       |
       v
Create Incoming & Outgoing Entries
       |
       v
Commit Transaction
```

If an operation fails, the transaction is rolled back to avoid partially completed transfers.

Deterministic lock ordering reduces the risk of deadlocks when concurrent transfers involve the same wallets.

## Transfer History & Filtering

Authenticated users can retrieve transfers associated with their wallets.

### Supported Query Parameters

| Parameter | Description |
|---|---|
| `minAmount` | Minimum transfer amount |
| `maxAmount` | Maximum transfer amount |
| `fromDate` | Start of date range |
| `toDate` | End of date range |
| `category` | Filter by transfer category |
| `page` | Zero-based page index |
| `size` | Number of results per page |

### Example Request

```http
GET /api/transfers?category=FOOD&minAmount=10&maxAmount=200&page=0&size=10
```

Date filters accept date-time values, for example:

```text
fromDate=2026-10-01T00:00:00
```

### Pagination Response

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "last": true
}
```

Pagination metadata is returned using a custom `PageResponseDto`.

## API Endpoints

### Authentication

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | Public |
| POST | `/api/auth/login` | Authenticate and obtain JWT | Public |

### Wallets

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| GET | `/api/wallet` | Retrieve current user's wallet | JWT |
| POST | `/api/wallet/top-up` | Add funds to current wallet | JWT |

### Transfers

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| GET | `/api/transfers` | Retrieve paginated and filtered transfers | JWT |
| POST | `/api/transfers` | Create a wallet-to-wallet transfer | JWT |

## Database Migrations with Flyway

Database schema changes are managed using **Flyway**, rather than Hibernate automatically modifying database tables.

Migration scripts are stored in:

```text
src/main/resources/db/migration/
├── V001__create_initial_schema.sql
└── V002__add_category_and_description_to_transfers.sql
```

### Migration History

| Version | Description |
|---|---|
| V001 | Creates the initial database schema, including users, wallets, transfers, and wallet entries |
| V002 | Adds transfer categories and optional descriptions |

Flyway automatically executes pending migrations during application startup.

It also maintains a `flyway_schema_history` table to track migration versions and execution status.

The application uses:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Hibernate validates entity mappings against the existing schema without creating or modifying tables.

### Adding Future Migrations

New database changes should be introduced through additional versioned SQL files.

For example:

```text
V003__add_new_feature.sql
```

Previously applied migrations should remain unchanged. Flyway tracks their checksums and applies new migrations in version order.

This allows database schemas to evolve while preserving existing data.

## Running with Docker

### Requirements

- Docker Desktop or Docker Engine
- Docker Compose

### 1. Configure Environment Variables

Create a `.env` file in the project root:

```dotenv
MYSQL_DATABASE=spring_digital_wallet
MYSQL_USER=wallet_user
MYSQL_PASSWORD=your_mysql_password
MYSQL_ROOT_PASSWORD=your_mysql_root_password
JWT_SECRET=replace_with_a_long_random_secret
```

Use a strong random JWT secret of at least 32 bytes for HS256.

The `.env` file is excluded from Git and must not be committed.

### 2. Build and Start

```bash
docker compose up --build
```

Docker Compose starts:

- MySQL 8 with a persistent database volume
- Spring Boot REST API
- Database health checking and startup dependencies

Flyway automatically applies pending migrations when Spring Boot starts.

### 3. Access the Application

| Service | URL |
|---|---|
| REST API | `http://localhost:8084` |
| Swagger UI | `http://localhost:8084/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8084/v3/api-docs` |
| MySQL (from host) | `localhost:3307` |

### 4. Stop Containers

```bash
docker compose down
```

This preserves the MySQL data volume.

To remove containers and database volumes:

```bash
docker compose down -v
```

**Warning:** The `-v` option deletes the persistent database volume and its stored data.

## Running Locally

### Requirements

- Java 21
- MySQL 8
- Gradle wrapper (included in the repository)

Configure the following environment variables:

```dotenv
DB_URL=jdbc:mysql://localhost:3306/digital_wallet?createDatabaseIfNotExist=true
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_secure_jwt_secret
```

If using the MySQL container from Docker Compose, connect through `localhost:3307` instead of port `3306`.

Run the application:

```bash
./gradlew bootRun
```

On Windows:

```powershell
.\gradlew.bat bootRun
```

Alternatively, start the application from an IDE using the `local` Spring profile and local configuration.

The Docker Compose `.env` file is not automatically loaded as Spring Boot environment variables when running the application outside Docker.

## Testing

The project includes unit tests for the service layer using JUnit 5 and Mockito.

The existing test suite covers core scenarios involving:

- User registration and authentication
- Wallet creation and retrieval
- Wallet top-up
- Transfer execution
- Balance updates
- Transfer business rules

Run the tests with:

```bash
./gradlew test
```

On Windows:

```powershell
.\gradlew.bat test
```

## Error Handling

The application uses centralized exception handling through `@RestControllerAdvice`.

Handled scenarios include invalid input, authentication failures, missing resources, insufficient funds, and invalid transfer operations.

Example response:

```json
{
  "status": 400,
  "message": "Insufficient funds!"
}
```

Request DTOs use Jakarta Bean Validation to enforce input constraints.

## Swagger / OpenAPI

Interactive API documentation is available through Swagger UI:

**http://localhost:8084/swagger-ui/index.html**

OpenAPI specification:

**http://localhost:8084/v3/api-docs**

Swagger UI can be used to explore endpoints, inspect request and response models, authorize with JWT, and test API operations.

## Project Scope

Spring Digital Wallet is a backend-focused educational and portfolio project designed to demonstrate the implementation of secure APIs, relational persistence, and transactional business logic.

The application simulates wallet balances and internal transfers. It does not integrate with real banking systems or payment providers.

### Future Direction

The backend is intended to serve as the foundation for a separate Python-based AI application that can interact with wallet data through authenticated REST APIs.

Potential capabilities include financial data analysis, automated insights, reporting, and task orchestration using specialized AI agents.

The AI application will be developed as a separate component, keeping financial operations and business rules within the Spring Boot backend.
