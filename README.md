# Serenia Platform

Serenia Platform is a RESTful API built on the foundations of Domain-Driven Design (DDD). Developed with Java 26, Spring Boot 4, and MySQL, the platform supports the daily wellbeing of older adults and keeps them connected with their distant relatives through daily check-ins, care circles, social companionship, wellbeing monitoring, and safety alerts. It features a modular architecture organized into bounded contexts and adheres to Command Query Responsibility Segregation (CQRS) principles, ensuring a clean separation between data modification and retrieval operations.

## Tech Stack

- **Language:** Java 26
- **Framework:** Spring Boot 4.0.6
- **Database:** MySQL 8+
- **Persistence:** Spring Data JPA / Hibernate
- **Security:** Spring Security with JWT (JSON Web Tokens)
- **Media Storage:** Local file system (development) or Azure Blob Storage (production)
- **Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Build Tool:** Maven 3.9+ (Maven Wrapper included)
- **Containerization:** Docker / Docker Compose

## Prerequisites

- Java 26 JDK
- MySQL 8.0+
- Docker (optional)

## Project Structure

The project is organized into bounded contexts following DDD principles:

```text
src/main/java/com/serenia/platform/
├── iam/                    # Identity and Access Management (Users, Sessions, Roles)
├── carecircle/             # Care circles, family links, invitation codes, care shifts, shared notes
├── dailycheckin/           # Daily check-ins and check-in preferences
├── socialcompanionship/    # Audio and photo messages, social reminders
├── wellbeingmonitoring/    # Small wins and wellbeing suggestions
├── alertsandsafety/        # Emergency and inactivity alerts
└── shared/                 # Shared kernel (Domain bases, Infrastructure, Interfaces)
```

Each bounded context follows the same layered layout: `domain`, `application`, `infrastructure`, and `interfaces` (REST controllers, ACL facades, and scheduled jobs).

## Configuration

The project uses profile-based configuration located in `src/main/resources`:

- `application.properties`: Shared defaults (JWT, schedulers, media storage, i18n).
- `application-dev.properties`: Development settings (default profile). Connects to a local MySQL `serenia` database and creates it if missing.
- `application-prod.properties`: Production settings. All database, JWT, and storage settings are read from environment variables, and media is stored in Azure Blob Storage.

## Getting Started

### 1) Database Setup

Ensure MySQL is running. In the `dev` profile the `serenia` database is created automatically (default credentials `root` / `root`). In the `prod` profile the database must already exist.

### 2) Run the Application

#### Using Maven

```bash
# Run with the default 'dev' profile
./mvnw clean spring-boot:run

# Run with the 'prod' profile
SPRING_PROFILES_ACTIVE=prod \
DATABASE_URL=localhost \
DATABASE_PORT=3306 \
DATABASE_NAME=serenia \
DATABASE_USER=root \
DATABASE_PASSWORD=password \
JWT_SECRET=your-secret \
AZURE_STORAGE_CONNECTION_STRING=your-connection-string \
./mvnw clean spring-boot:run
```

#### Using Docker

The image is built in two stages (JDK 26 for the build, JRE 26 for the runtime) and runs with the `prod` profile.

```bash
# Build and run with Docker Compose, reading the values from an env file kept outside the repository
docker compose --env-file /path/to/serenia.env up --build

# Or build and run the image directly
docker build -t serenia-platform .
docker run -p 8080:8080 \
 -e DATABASE_URL=host.docker.internal \
 -e DATABASE_NAME=serenia \
 -e DATABASE_USER=root \
 -e DATABASE_PASSWORD=password \
 -e JWT_SECRET=your-secret \
 -e AZURE_STORAGE_CONNECTION_STRING=your-connection-string \
 serenia-platform
```

### 3) API Documentation

Once running, access the interactive documentation at:

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## Scripts and Commands

- `./mvnw clean compile`: Compile the project.
- `./mvnw test`: Run the tests.
- `./mvnw package`: Package the application into a JAR file.
- `./mvnw spring-boot:run`: Launch the application.

## API Surface

### IAM (Identity and Access Management)

- `POST /api/v1/users`: Register a new user (older adult or distant relative).
- `POST /api/v1/sessions`: Sign in and get a JWT.
- `DELETE /api/v1/sessions/{sessionId}`: Sign out.
- `/api/v1/users`: Profile data, photo, and password management.

### Care Circle

- `/api/v1/care-circles`: Care circle management.
- `/api/v1/care-circles/{careCircleId}/family-links`: Links between the older adult and their relatives.
- `/api/v1/care-circles/{careCircleId}/invitation-codes`: Invitation codes to join a care circle.
- `/api/v1/care-circles/{careCircleId}/care-shifts`: Care shift scheduling.
- `/api/v1/care-circles/{careCircleId}/shared-notes`: Notes shared within the care circle.

### Daily Check-in

- `/api/v1/check-ins`: Daily check-ins and their answers.
- `/api/v1/check-in-preferences`: Reminder time, simplified mode, and daily pause.

### Social Companionship

- `/api/v1/care-circles/{careCircleId}/audio-messages`: Audio messages (upload, play, share).
- `/api/v1/care-circles/{careCircleId}/photo-messages`: Photo messages (upload, view, share).
- `/api/v1/social-reminders`: Social reminders (complete, postpone, cancel).

### Wellbeing Monitoring

- `/api/v1/small-wins`: Small wins of the older adult.
- `/api/v1/wellbeing-suggestions`: Wellbeing suggestions.

### Alerts and Safety

- `/api/v1/alerts`: Alerts overview.
- `/api/v1/emergency-alerts`: Emergency alerts (acknowledge, resolve).
- `/api/v1/inactivity-alerts`: Inactivity alerts (acknowledge, resolve).

## Security Model

- **Authentication:** Stateless JWT-based, with server-side sessions that can be revoked.
- **Roles:** `OLDER_ADULT` and `DISTANT_RELATIVE`.
- **Password Storage:** BCrypt hashing.
- **Authorization:** Every endpoint except registration, sign-in, and the API documentation requires the token in the header: `Authorization: Bearer <token>`.

## Development Conventions

- **DDD + CQRS:** Write operations use `CommandService`, read operations use `QueryService`.
- **Domain Events:** Aggregates publish domain events through the shared `DomainEventPublisher`.
- **Anti-Corruption Layer:** Bounded contexts communicate through ACL facades instead of accessing each other's domain directly.
- **Scheduled Jobs:** Invitation code expiration, daily check-in lifecycle, and social reminders run as scheduled tasks configured with ISO-8601 durations.
- **Persistence:** Tables use snake_case pluralized names, and every instant is stored in UTC.
- **Lombok:** Used for technical boilerplate; aggregates only use `@Getter` (no setters), so their state changes only through domain methods.
- **Error Handling:** Centralized via `GlobalExceptionHandler` and `ResponseEntityAssembler`.
- **I18n:** Supported via `Accept-Language` header (Default: `en`, also supports `es`).
