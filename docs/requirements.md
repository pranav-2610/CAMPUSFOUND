# Software Requirements Specification (initial)

## Problem and objectives
Campus members currently depend on informal messages and physical offices to recover belongings. Campus Found centralizes reports, searchable discovery, candidate matching, private proof of ownership, and staff review.

## Actors
- Visitor: views aggregate statistics and active reports.
- Student: manages lost/found reports, sees matching suggestions, submits and tracks claims.
- Staff: reviews ownership claims and coordinates the physical return.
- Administrator: staff-level authorization plus oversight (future user management/audit tools).

## Functional requirements
1. Register with a unique email and BCrypt-protected password; authenticate using a server session.
2. Submit lost and found reports linked to the authenticated reporter.
3. Browse active reports by type, keyword, and category.
4. Persist weighted lost/found match suggestions at a configured minimum score.
5. Submit private ownership claims against active found reports; reject self-claims and duplicate active claims.
6. Permit only STAFF/ADMIN to review pending claims; store reviewer and notify claimant.
7. Provide personal dashboard and notification list.
8. Expose aggregate public statistics and safe report fields.

## Non-functional requirements
Responsive and accessible HTML; server-side authorization; hashed credentials; parameterized persistence; DTO serialization; private evidence access control; maintainable layered Java design; PostgreSQL persistence. For production, configure TLS, rate limits, migrations, backup and retention policies.

## Use cases
- Report an item: member authenticates → enters details → server validates and stores report → matcher evaluates opposite-type active reports → eligible suggestions persist → report appears on board.
- Claim found item: member views safe listing → submits reason and private evidence → server checks ownership/status/duplicates → claim persists pending → authorized staff reviews → status persists and claimant receives a notification.
- Handover: approved claim is physically checked by staff and returned to owner. The digital handover workflow remains future work.

## Security and workflow constraints
- Only active found reports accept claims. Claimants cannot claim their own found report; an active duplicate claim by the same member is rejected.
- Public signup always creates a STUDENT. Only an ADMIN can change roles; STAFF and ADMIN can review claims and manage item returns.
- Found report private identifying information and claim evidence are omitted from public and claimant DTOs; authorized reviewer DTOs include claim evidence.
- Report images are limited to server-validated JPEG, PNG, and WebP content types at 5 MB, and filenames are generated server-side.
- Item handover status moves SCHEDULED → OWNER_CONFIRMED → COMPLETED; only completion marks the found report RETURNED.
- Admin actions write audit records for role changes, claim decisions, report deactivation, and handover schedule/completion.
