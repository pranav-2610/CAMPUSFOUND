# Campus Found — Lost & Found Management System

A full-stack university lost-and-found application built with Java, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, HTML, CSS and vanilla JavaScript. The landing page is public; registered community members can create reports, explore matches, and submit claims. Staff and administrators can review claims.

## Current scope

Implemented: BCrypt accounts, session-cookie authentication with CSRF tokens, role-gated admin claim review, lost/found reports with JPA `JOINED` inheritance, public report search/filter, weighted persisted match suggestions, private claim evidence, claim review and handover lifecycle, user dashboard, notifications, and a responsive landing page. The core entity classes map through repositories to PostgreSQL.

Remaining limitations: private evidence image uploads, full audit coverage for every business action, complete analytics, broad workflow/security integration tests, and a few requested UML diagram types. Item detail editing, image uploads, report deactivation, profile management, paginated admin lists, role controls, audit history, and demo seed data are implemented.

## Requirements

- Java 17+ (Java 21 recommended)
- Maven 3.8+
- PostgreSQL 14+

Docker Compose is not included because Docker is not available in the development environment.

## Database and configuration

Create a database and set environment variables:

```sh
createdb campus_lost_found
export DATABASE_URL=jdbc:postgresql://localhost:5432/campus_lost_found
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=your-local-password
```

Hibernate creates/updates tables using `ddl-auto=update` for local development. Use a migration tool and `validate` for deployment. Defaults target a local database with username/password `postgres`.

## Run

```sh
mvn spring-boot:run
```

Visit [http://localhost:8080](http://localhost:8080). Sign up through the UI. Public registration grants STUDENT only. Assign STAFF or ADMIN roles through a trusted database operator; never accept role assignment from public signup. The app does not ship demo credentials in the default profile. To load development sample data into an empty database, run `mvn spring-boot:run -Dspring-boot.run.profiles=demo` (or run the packaged JAR with `--spring.profiles.active=demo`); demo accounts use `demo1@campus.example` through `demo8@campus.example`, all with password `CampusDemo2026!`. The eighth account is the administrator, and accounts 6–7 are staff. Never use the demo profile for production.

## API overview

- `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/logout`, `GET /api/auth/me`
- `GET /api/public/stats`, `GET /api/public/reports?type=FOUND&q=&category=`
- `POST /api/reports/lost`, `POST /api/reports/found`, `GET /api/reports/mine`, `GET /api/reports/{id}`, `PUT /api/reports/{id}`, `PUT /api/reports/{id}/deactivate`
- `GET /api/matches`, `POST /api/claims`, `GET /api/claims/my`
- `GET /api/notifications`, `GET /api/dashboard`, `PUT /api/users/me`, `PUT /api/users/me/password`
- Staff/Admin: `GET /api/admin/dashboard`, `GET /api/admin/claims`, `POST /api/admin/claims/{id}/review`, `GET /api/admin/reports`, `GET /api/admin/users`, `GET /api/admin/audit`

Authentication is session-based and uses an HTTP-only servlet session cookie. CSRF protection uses a cookie token submitted through the `X-XSRF-TOKEN` header. Do not expose server-private claim evidence in non-admin DTOs.

## Domain and database design

`ItemReport` is abstract and uses JPA `JOINED` inheritance. Common report properties are stored in `item_reports`; lost-only and found-only properties are stored in `lost_reports` and `found_reports`, linked by shared primary keys. `User` is the reporter on a many-to-one association. `MatchSuggestion` links one lost and one found report and enforces a unique pair. `Claim` associates one claimant to one found report and optionally records an authorized reviewer. Notification records belong to their recipient. Lazy relationships and hand-built DTOs avoid exposing entity graphs or serializing private found details.

OOP principles: encapsulated private entity state, an abstract common report with specialized subclasses, enum-driven role/type/status state, and domain methods for controlled claim review/status transitions. Staff and admin use role-based authorization rather than a second user inheritance hierarchy.

## Matching

Potential matches combine category (30%), token Jaccard item-name similarity (25%), location token similarity (20%), date proximity (15% over 30 days), and description token similarity (10%). Scores at or above 50 are persisted once per lost/found pair. Suggestions never approve claims automatically.

## Tests

```sh
mvn test
```

Tests cover matching-score examples, handover state transitions, and JPA persistence/retrieval of both joined report subtypes including lazy reporter access. Full authentication, authorization, claim workflow, and PostgreSQL-specific integration coverage is still recommended.

## Documentation

See `docs/` for software design, relational mapping, requirements, and editable Mermaid diagrams. This repository is a functional foundation; the checklist above identifies incomplete acceptance criteria honestly.

Staff workflows include claim approval/rejection, scheduling a handover, claimant confirmation, and staff completion. An item is marked returned only after the claimant confirms and staff records completion. Staff review is available at `/admin.html`, including claims, handovers, reports, user roles, and dashboard totals.

## Uploads and account management
Authenticated users can upload JPG, PNG, and WebP images up to 5 MB through `POST /api/uploads`; files are stored under `uploads/` by generated UUID names and served from `/media/`. Set `-Dcampus.upload.dir=/absolute/private/path` to move storage. Only item images are supported; private claim evidence uploads are not implemented. Profile name/department and password updates are available under `/api/users/me` and `/api/users/me/password`. Owners can deactivate reports. Notification records can be individually marked read.
