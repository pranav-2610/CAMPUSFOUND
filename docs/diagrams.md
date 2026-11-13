# Editable software design diagrams (Mermaid)

## UML class/domain diagram
```mermaid
classDiagram
class User { UUID id; String fullName; String email; String passwordHash; Role role }
class ItemReport { <<abstract>> UUID id; String title; String description; String category; String location; LocalDate incidentDate; ReportStatus status }
class LostReport { String lastSeenTime }
class FoundReport { String storageLocation; String privateIdentificationDetails }
class MatchSuggestion { UUID id; int score }
class Claim { UUID id; String reason; String privateEvidence; ClaimStatus status }
class AppNotification { UUID id; String message; boolean read }
ItemReport <|-- LostReport
ItemReport <|-- FoundReport
User "1" --> "0..*" ItemReport : reports
User "1" --> "0..*" Claim : submits
FoundReport "1" --> "0..*" Claim : receives
LostReport "1" --> "0..*" MatchSuggestion
FoundReport "1" --> "0..*" MatchSuggestion
User "1" --> "0..*" AppNotification : receives
User "0..1" --> "0..*" Claim : reviews
```

## ER diagram
```mermaid
erDiagram
 CAMPUS_USER ||--o{ ITEM_REPORT : reports
 ITEM_REPORT ||--o| LOST_REPORT : subtype
 ITEM_REPORT ||--o| FOUND_REPORT : subtype
 LOST_REPORT ||--o{ MATCH_SUGGESTION : candidate
 FOUND_REPORT ||--o{ MATCH_SUGGESTION : candidate
 CAMPUS_USER ||--o{ CLAIM : submits
 FOUND_REPORT ||--o{ CLAIM : receives
 CAMPUS_USER ||--o{ CLAIM : reviews
 CAMPUS_USER ||--o{ APP_NOTIFICATION : receives
```

## Report submission sequence
```mermaid
sequenceDiagram
 actor Member
 participant UI
 participant API as Spring controller
 participant Service as Matching service
 participant DB as PostgreSQL/JPA
 Member->>UI: Submit lost/found report
 UI->>API: POST /api/reports/{type}
 API->>DB: Persist report with reporter
 API->>Service: Match opposite active reports
 Service->>DB: Read candidates and persist unique scores >= 50
 API-->>UI: Safe report DTO
```

## Claim review sequence
```mermaid
sequenceDiagram
 actor Claimant
 actor Staff
 participant API
 participant DB
 Claimant->>API: POST /api/claims (private evidence)
 API->>DB: Validate availability and save PENDING
 Staff->>API: POST /api/admin/claims/{id}/review
 API->>DB: Store decision and reviewer
 API->>DB: Store claimant notification
 API-->>Staff: Reviewed claim
```

## Handover activity
```mermaid
flowchart TD
 A[Claim approved] --> B[Arrange campus staff meeting]
 B --> C{Staff verifies identity and item}
 C -->|Verified| D[Record handover]
 D --> E[Owner and staff confirm]
 E --> F[Mark item returned]
 C -->|Not verified| G[Keep item secured and escalate]
```

## Components
```mermaid
flowchart LR
 Browser[Vanilla JS + HTML/CSS] -->|same-origin JSON + session| Spring[Spring MVC controllers]
 Spring --> Security[Spring Security]
 Spring --> Services[Matching and notification services]
 Services --> Repositories[Spring Data JPA]
 Repositories --> Hibernate[Hibernate ORM]
 Hibernate --> PostgreSQL[(PostgreSQL)]
```

## Deployment
```mermaid
flowchart TB
 UserBrowser --> HTTPS[Reverse proxy / TLS]
 HTTPS --> App[Spring Boot application]
 App --> DB[(PostgreSQL private network)]
 App --> Files[Private upload storage: planned]
```

## Use case diagram
```mermaid
flowchart LR
 Visitor((Visitor)) --> Browse[Browse and search reports]
 Student((Student)) --> Register[Register and sign in]
 Student --> Report[Create and manage reports]
 Student --> Matches[Review possible matches]
 Student --> Claim[Submit and track claim]
 Student --> Handover[Confirm scheduled handover]
 Staff((Staff)) --> Review[Review ownership evidence]
 Staff --> HandoverAdmin[Schedule and complete handover]
 Staff --> Moderate[Deactivate reports]
 Admin((Administrator)) --> Review
 Admin --> Users[Manage user roles]
 Admin --> Audit[Review audit history and statistics]
```
