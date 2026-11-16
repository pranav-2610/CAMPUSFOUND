# Campus Lost & Found Management System

A full-stack campus platform for reporting lost and found belongings, discovering potential matches, submitting private ownership claims, and coordinating verified returns. The system replaces scattered informal messages with a searchable, accountable workflow for students and campus staff.

> **Academic project:** Java / Spring Boot backend, PostgreSQL persistence, and a responsive HTML, CSS, and vanilla JavaScript frontend. See [`docs/`](docs/) for requirements and editable system diagrams.

## Project at a glance

- **Backend:** Java 17+, Spring Boot 3.2, Spring MVC, Spring Security, Spring Data JPA, Hibernate, Bean Validation
- **Frontend:** HTML5, CSS3, vanilla JavaScript (ES6), Fetch API, Lucide icons
- **Database:** PostgreSQL; H2 is used only for repository tests
- **Build and tests:** Maven, JUnit 5
- **Authentication:** Server-side session cookie, BCrypt password hashing, CSRF token protection

## Features

### Campus members

- Register as a student and sign in with a campus email address.
- Create lost and found reports with descriptions, category, location, date, and optional photo.
- Browse active reports and search by keyword, category, and report type.
- View candidate lost/found matches calculated by the Java matching service.
- Submit private ownership claims for found reports and track their status.
- View notifications, update profile details, change password, edit or close their reports.
- Confirm a scheduled handover after meeting with campus staff.

### Staff and administrators

- Review private claim evidence and approve or reject claims with a review note.
- Schedule a handover, record owner confirmation, and complete the handover.
- Browse and deactivate reports, inspect users, view dashboard totals, and review audit history.
- Assign roles from the administration screen; role management is restricted to administrators.

### Privacy and security

- Passwords are stored using BCrypt hashes.
- Public registration always creates a `STUDENT` account; it cannot self-assign staff privileges.
- Staff-only operations are checked on the server, independent of frontend visibility.
- Sensitive found-item details and private claim evidence are omitted from public and claimant responses.
- Uploaded item photos are limited to JPEG, PNG, or WebP, capped at 5 MB, and stored with generated filenames.
- CSRF protection is enabled for state-changing requests.

## Application workflow

1. A member creates a lost or found report.
2. The application compares it with active reports of the opposite type and stores suggestions scoring at least 50%.
3. A member submits a claim with private verification details.
4. Authorized staff reviews the evidence and records a decision.
5. For an approved claim, staff schedules a handover. The member confirms it, then staff completes it and the item is marked returned.

Matching is advisory only. It does not approve ownership or change item status by itself.

## Object-oriented and database design

`ItemReport` is an abstract JPA entity with `JOINED` inheritance. `LostReport` and `FoundReport` extend the common report model and store their subtype-specific fields in separate tables. Other core entities include `User`, `MatchSuggestion`, `Claim`, `Handover`, `AppNotification`, and `AuditLog`.

Key associations include a user-to-reports one-to-many relationship, a user-to-claims one-to-many relationship, found-report-to-claims one-to-many, match suggestions connecting one lost and one found report, and handovers linked one-to-one with claims. JPA entities use private fields and controlled state-changing methods. REST responses are assembled as DTO-shaped maps rather than serializing entity graphs.

The matcher uses weighted similarity:

| Signal | Weight |
| --- | ---: |
| Category match | 30% |
| Item name token similarity | 25% |
| Location token similarity | 20% |
| Date proximity | 15% |
| Description token similarity | 10% |

The total score is stored as a `MatchSuggestion`; duplicate lost/found pairs are constrained in the database.

## Requirements

- Java 17 or newer
- Maven 3.8 or newer
- PostgreSQL 14 or newer

## Database setup

Create a local PostgreSQL database:

```sh
createdb campus_lost_found
```

Configure the connection in your shell (adjust the username and password to your local PostgreSQL setup):

```sh
export DATABASE_URL=jdbc:postgresql://localhost:5432/campus_lost_found
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=your-local-password
```

The application defaults to the same local database name and `postgres` credentials. Hibernate uses `ddl-auto=update` for convenient development setup. For a production deployment, use database migrations and schema validation instead.

## Build, test, and run

Run tests and build the executable Spring Boot JAR:

```sh
mvn test
mvn package
```

Start the application:

```sh
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). The landing page and community board are public. Sign up to submit reports and claims. The staff interface is at [http://localhost:8080/admin.html](http://localhost:8080/admin.html) and requires a STAFF or ADMIN account.

## Development sample data

Sample data is opt-in and only inserted if the configured database has no users. Start a fresh development database with:

```sh
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

The demo profile creates eight accounts, 15 lost reports, 15 found reports, match suggestions, pending claims, and a completed handover.

| Account | Login | Password | Role |
| --- | --- | --- | --- |
| Student | `demo1@campus.example` | `CampusDemo2026!` | STUDENT |
| Staff | `demo6@campus.example` | `CampusDemo2026!` | STAFF |
| Administrator | `demo8@campus.example` | `CampusDemo2026!` | ADMIN |

These shared credentials are for local development only. **Never enable the demo profile in production.**

## API overview

All endpoints are rooted at `/api`. Session authentication is used for member and staff operations.

| Area | Endpoints |
| --- | --- |
| Authentication | `POST /auth/register`, `POST /auth/login`, `POST /auth/logout`, `GET /auth/me` |
| Public discovery | `GET /public/stats`, `GET /public/reports?type=&q=&category=` |
| Reports | `POST /reports/lost`, `POST /reports/found`, `GET /reports/{id}`, `GET /reports/mine`, `PUT /reports/{id}`, `PUT /reports/{id}/deactivate` |
| Matching | `GET /matches` |
| Claims | `POST /claims`, `GET /claims/my` |
| Handovers | `POST /handovers/{id}/confirm`, `GET /handovers/my` |
| Member account | `GET /users/me`, `PUT /users/me`, `PUT /users/me/password` |
| Notifications | `GET /notifications`, `PUT /notifications/{id}/read`, `PUT /notifications/read-all` |
| Staff/admin | `GET /admin/dashboard`, `GET /admin/claims`, `POST /admin/claims/{id}/review`, `GET /admin/handovers`, `POST /admin/handovers`, `POST /admin/handovers/{id}/complete`, `GET /admin/reports`, `GET /admin/users`, `PUT /admin/users/{id}/role`, `GET /admin/audit` |
| Uploads | `POST /uploads` (multipart form field `file`; JPEG/PNG/WebP, maximum 5 MB) |

## Documentation

- [`docs/requirements.md`](docs/requirements.md): problem statement, actors, functional and non-functional requirements, use cases, and workflow/security constraints.
- [`docs/diagrams.md`](docs/diagrams.md): editable Mermaid class, ER, use-case, sequence, activity, component, and deployment diagrams.

## Tests

The automated suite currently covers representative matching scores, handover state transitions, and JPA persistence/visibility for both report subtypes, including reporter access. Run it with `mvn test`. A live PostgreSQL environment is recommended for final deployment-level integration checks.

## Known limitations

- Claim evidence is text-only; private evidence image uploads are not included.
- Audit history records key administrative operations, not every read or member action.
- Analytics are limited to dashboard totals.
- The project does not include a production database migration, email delivery, or external file-storage provider.
- Additional end-to-end security and workflow tests would strengthen production readiness.

## Repository structure

```text
src/main/java/com/campus/lostfound/   Spring Boot controllers, domain, repositories, security, services
src/main/resources/static/            HTML pages, CSS, and vanilla JavaScript
src/test/java/                         JUnit tests for domain and persistence behavior
docs/                                  Requirements and editable Mermaid diagrams
```
