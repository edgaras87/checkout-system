# Implementation Plan — SL-01 — Reservation Capacity Correctness

Status: accepted
Work Unit: SL-01 — Reservation Capacity Correctness

---

## 1. Purpose

This document translates accepted construction reasoning and implementation requirements for `SL-01 — Reservation Capacity Correctness` into a project-specific implementation plan.

It prepares implementation execution.

It does not define final code, final schema, migration scripts, endpoint contracts, DTOs, or test code.

---

## 2. Source Inputs

Accepted Work Unit inputs:

```text
- internal/execution/slices/sl-01-reservation-capacity-correctness/execution-context.md
- internal/execution/slices/sl-01-reservation-capacity-correctness/slice-clarification.md
- internal/execution/slices/sl-01-reservation-capacity-correctness/correctness-construction.md
- internal/execution/slices/sl-01-reservation-capacity-correctness/implementation-requirements.md
```

Relevant project baseline inputs:

```text
- internal/implementation/application-baseline.md
- internal/implementation/persistence-baseline.md
- internal/implementation/testing-baseline.md
- internal/implementation/api-baseline.md
- internal/implementation/local-runtime-baseline.md
```

Other project inputs used:

```text
- internal/project-state.md
- internal/project-map.md
- internal/execution/slice-register.md
```

Traceability rule:

```text
This plan preserves the accepted slice boundary, accepted correctness construction, accepted implementation requirements, and relevant project baseline constraints.
```

---

## 3. Planning Readiness Check

Required upstream artifacts are present and planning-ready:

```text
execution-context.md:
    present
    accepted incoming context is available

slice-clarification.md:
    present
    slice boundary is stable

correctness-construction.md:
    present
    invariant, correctness attack directions, and guarantees are defined

implementation-requirements.md:
    present
    requirements are enforceable and traceable

project implementation baselines:
    present
    relevant application, persistence, testing, API, and runtime constraints are available
```

Planning is not blocked.

No upstream artifact needs revision before implementation planning.

---

## 4. Accepted Construction Summary

```text
Work Unit:
    SL-01 — Reservation Capacity Correctness

Responsibility area:
    Inventory Reservation

Target failure:
    Overselling

Dominant pressure:
    Concurrency

Current version focus:
    V1 — local correctness
```

Accepted invariant:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
must be less than or equal to
the local reservation capacity limit for X.
```

Operational form:

```text
committed accepted reservation quantity for X
    <=
local reservation capacity limit for X
```

Reservable capacity identity note:

```text
In this plan, X means the stable local Inventory Reservation capacity identity being protected from overselling.

The final concrete representation of X belongs to implementation execution.

Whatever representation is chosen, requested quantity, committed accepted reservation quantity, and local reservation capacity limit must all be scoped to the same X.
```

Accepted correctness attack directions:

```text
CAD-01 — Scope Corruption
CAD-02 — Quantity Miscalculation
CAD-03 — Collective Over-Acceptance
CAD-04 — Stale Capacity View Acceptance
CAD-05 — Evaluation-to-Commit Gap
CAD-06 — Rejection Consumption
CAD-07 — Responsibility Boundary Escape
```

Accepted guarantees:

```text
G1 — Same-Capacity Scope Guarantee
G2 — Quantity-Based Acceptance Safety Guarantee
G3 — Combined Committed Quantity Guarantee
G4 — Concurrent Acceptance Integrity Guarantee
G5 — Capacity View Freshness Guarantee
G6 — Acceptance Transition Visibility Guarantee
G7 — Rejection Non-Consumption Guarantee
G8 — Local Responsibility Boundary Guarantee
```

Implementation requirement summary:

```text
The implementation must provide conceptual state sufficient to enforce local reservation-capacity correctness, a local authoritative Inventory Reservation decision point, same-capacity scope enforcement, quantity-based acceptance safety, combined committed quantity enforcement, concurrent acceptance integrity, capacity view freshness, acceptance transition visibility, rejection non-consumption, local responsibility boundary preservation, durable-state support where required, observable accepted/rejected behavior, and validation coverage for the invariant, correctness attack directions, and guarantees.
```

Summary rule:

```text
This section restates accepted inputs.
It does not rewrite them.
```

---

## 5. Implementation Scope

Inside this plan:

```text
- local Inventory Reservation capacity decision behavior
- accepted/rejected reservation decision result
- quantity-based capacity enforcement
- durable reservation-capacity state needed for enforcement
- concurrency-safe acceptance behavior
- stale capacity view prevention
- acceptance transition visibility
- rejection behavior when capacity would be exceeded
- validation planning for normal, boundary, mixed-quantity, rejected, same-capacity, separate-capacity, and concurrent reservation attempts
```

Outside this plan:

```text
- order creation
- order idempotency
- payment execution
- payment interpretation
- final checkout outcome determination
- final outcome uniqueness
- final outcome composition correctness
- reservation expiration
- reservation release
- downstream compensation
- external inventory synchronization
- public API contract design
- final schema, SQL, entity, repository, class, method, DTO, or test implementation
```

Downstream work:

```text
Implementation execution will change the repository according to the accepted strategy and sequence in this plan.
```

---

## 6. Requirement-to-Project Mapping

### Requirement — Same-Capacity Scope Enforcement

```text
Source requirement:
    R1 — Same-Capacity Scope Enforcement

Accepted reasoning supported:
    G1 — Same-Capacity Scope Guarantee
    CAD-01 — Scope Corruption

Responsible project area:
    domain, application, persistence / migrations, infra, tests

Planning meaning:
    The implementation must represent a stable reservable capacity identity and ensure that requested quantity, committed accepted quantity, and capacity limit are evaluated for the same capacity.
```

### Requirement — Quantity-Based Acceptance Safety

```text
Source requirement:
    R2 — Quantity-Based Acceptance Safety Enforcement

Accepted reasoning supported:
    G2 — Quantity-Based Acceptance Safety Guarantee
    CAD-02 — Quantity Miscalculation

Responsible project area:
    domain, application, persistence / migrations, infra, tests

Planning meaning:
    The implementation must accept a reservation request only when adding its requested quantity to the same-capacity committed accepted quantity preserves the capacity limit.
```

### Requirement — Combined Committed Quantity Enforcement

```text
Source requirement:
    R3 — Combined Committed Quantity Enforcement

Accepted reasoning supported:
    G3 — Combined Committed Quantity Guarantee
    CAD-02 — Quantity Miscalculation
    CAD-03 — Collective Over-Acceptance

Responsible project area:
    domain, application, persistence / migrations, infra, tests

Planning meaning:
    The implementation must evaluate reservation-capacity correctness against total committed accepted quantity for a reservable capacity, not against isolated request validity, request count, accepted-attempt count, or record count.
```

### Requirement — Concurrent Acceptance Integrity

```text
Source requirement:
    R4 — Concurrent Acceptance Integrity Enforcement

Accepted reasoning supported:
    G4 — Concurrent Acceptance Integrity Guarantee
    CAD-03 — Collective Over-Acceptance
    CAD-05 — Evaluation-to-Commit Gap

Responsible project area:
    application, persistence / migrations, infra, tests

Planning meaning:
    Concurrent attempts for the same reservable capacity must not commit accepted quantity above the local capacity limit. The implementation must protect the committed result, not only the evaluation moment.
```

### Requirement — Capacity View Freshness

```text
Source requirement:
    R5 — Capacity View Freshness Enforcement

Accepted reasoning supported:
    G5 — Capacity View Freshness Guarantee
    CAD-04 — Stale Capacity View Acceptance

Responsible project area:
    application, persistence / migrations, infra, tests

Planning meaning:
    Acceptance must not rely on stale or incomplete same-capacity state. A capacity view that omits relevant committed accepted reservations must not justify acceptance.
```

### Requirement — Acceptance Transition Visibility

```text
Source requirement:
    R6 — Acceptance Transition Visibility Enforcement

Accepted reasoning supported:
    G6 — Acceptance Transition Visibility Guarantee
    CAD-05 — Evaluation-to-Commit Gap

Responsible project area:
    application, persistence / migrations, infra, tests

Planning meaning:
    After a reservation is accepted, its accepted quantity must participate in committed accepted reservation quantity for affected same-capacity decisions.
```

### Requirement — Rejection Non-Consumption

```text
Source requirement:
    R7 — Rejection Non-Consumption Enforcement

Accepted reasoning supported:
    G7 — Rejection Non-Consumption Guarantee
    CAD-06 — Rejection Consumption

Responsible project area:
    domain, application, persistence / migrations, infra, tests

Planning meaning:
    Rejected reservation decisions must not increase committed accepted quantity or consume capacity.
```

### Requirement — Local Responsibility Boundary

```text
Source requirement:
    R8 — Local Responsibility Boundary Enforcement

Accepted reasoning supported:
    G8 — Local Responsibility Boundary Guarantee
    CAD-07 — Responsibility Boundary Escape

Responsible project area:
    domain, application, tests

Planning meaning:
    Inventory Reservation must preserve the invariant locally without relying on order, payment, final outcome, external inventory, release, expiration, reconciliation, or compensation behavior.
```

### Requirement — Atomicity Conditions

```text
Source requirement:
    Atomicity Conditions

Accepted reasoning supported:
    G2 — Quantity-Based Acceptance Safety Guarantee
    G4 — Concurrent Acceptance Integrity Guarantee
    G6 — Acceptance Transition Visibility Guarantee
    CAD-03 — Collective Over-Acceptance
    CAD-05 — Evaluation-to-Commit Gap

Responsible project area:
    application, persistence / migrations, infra, tests

Planning meaning:
    For an accepted reservation, the acceptance decision and capacity-consuming effect must be protected as one correctness unit. An accepted reservation must not be recorded as accepted while its accepted quantity remains invisible to affected same-capacity decisions.
```

### Requirement — Dominant-Pressure Handling Conditions

```text
Source requirement:
    Dominant-Pressure Handling Conditions

Accepted reasoning supported:
    G1 through G6
    CAD-01 through CAD-05

Responsible project area:
    application, persistence / migrations, infra, tests

Planning meaning:
    The implementation must preserve local reservation-capacity correctness under concurrent reservation attempts competing for the same reservable capacity.
```

### Requirement — Rejection Behavior Conditions

```text
Source requirement:
    Rejection Behavior Conditions

Accepted reasoning supported:
    G2 — Quantity-Based Acceptance Safety Guarantee
    G7 — Rejection Non-Consumption Guarantee
    G8 — Local Responsibility Boundary Guarantee
    CAD-06 — Rejection Consumption
    CAD-07 — Responsibility Boundary Escape

Responsible project area:
    domain, application, persistence / migrations, infra, tests

Planning meaning:
    A reservation request must be rejected when accepting it would violate the invariant, and the rejected result must remain non-consuming.
```

### Requirement — Durable State Conditions

```text
Source requirement:
    Durable State Conditions

Accepted reasoning supported:
    G1 — Same-Capacity Scope Guarantee
    G3 — Combined Committed Quantity Guarantee
    G5 — Capacity View Freshness Guarantee
    G6 — Acceptance Transition Visibility Guarantee
    G7 — Rejection Non-Consumption Guarantee

Responsible project area:
    persistence / migrations, infra, tests

Planning meaning:
    Business persistence must support authoritative same-capacity committed quantity, capacity limits, accepted reservation distinction, accepted-versus-rejected decision behavior, and correctness under concurrent access.
```

### Requirement — Observable Behavior Conditions

```text
Source requirement:
    Observable Behavior Conditions

Accepted reasoning supported:
    G2 — Quantity-Based Acceptance Safety Guarantee
    G4 — Concurrent Acceptance Integrity Guarantee
    G7 — Rejection Non-Consumption Guarantee
    G8 — Local Responsibility Boundary Guarantee

Responsible project area:
    application, tests
    web only if HTTP exposure is deliberately added later

Planning meaning:
    Each reservation request must produce an observable accepted or rejected result. For this slice, application-level observability and persisted state visibility are enough; public HTTP exposure is not required to prove the invariant.
```

### Requirement — Validation Conditions

```text
Source requirement:
    Validation Conditions

Accepted reasoning supported:
    G1 through G8
    CAD-01 through CAD-07

Responsible project area:
    tests, testsupport, persistence / migrations, infra, application, domain

Planning meaning:
    Validation must prove the invariant under normal, boundary, mixed-quantity, rejected, same-capacity, separate-capacity, and concurrent reservation attempts against real PostgreSQL behavior where persistence and concurrency are part of enforcement.
```

---

## 7. Affected Project Areas

### Domain

```text
Why affected:
    The slice needs business concepts for reservable capacity identity, requested quantity, capacity limit, reservation decision, accepted reservation, rejected reservation, committed accepted quantity, and capacity correctness.

Responsibility:
    Own business meaning and reusable business rules.

Baseline constraints to preserve:
    Domain must not depend on application, infra, web, Spring configuration, HTTP DTOs, or JPA repositories.

Planning meaning:
    Domain should express reservation-capacity meaning without owning technical persistence or HTTP behavior.
```

### Application

```text
Why affected:
    The slice needs one authoritative use-case path for deciding whether a reservation request is accepted or rejected.

Responsibility:
    Own use-case orchestration, decision flow, transaction boundary coordination, and ports.

Baseline constraints to preserve:
    Application may depend on domain and application ports, but not infra or web.

Planning meaning:
    Application coordinates the reservation decision through stable boundaries and returns accepted/rejected behavior to callers.
```

### Persistence / Migrations

```text
Why affected:
    The accepted requirements require durable capacity-consuming state and correctness under concurrent access.

Responsibility:
    Introduce required business persistence through Flyway migrations and preserve PostgreSQL authority boundaries.

Baseline constraints to preserve:
    Schema changes must be migration-driven.
    Business objects belong in the app schema for dev/prod unless a later explicit architectural decision changes this.
    checkout_migrator owns structure.
    checkout_runtime uses data and must not perform DDL.
    Hibernate must not create or update schema.

Planning meaning:
    Persistence must support authoritative capacity state and reservation decision behavior without defining final schema in this plan.
```

### Infra

```text
Why affected:
    The chosen strategy requires a PostgreSQL-backed adapter that enforces atomic capacity commitment behavior.

Responsibility:
    Implement application ports using PostgreSQL and technical transaction/concurrency behavior.

Baseline constraints to preserve:
    Infra may depend on application ports and domain, but must not own application decisions or bypass use cases.

Planning meaning:
    Infra provides the technical enforcement mechanism while application/domain preserve business decision ownership.
```

### Tests / Testsupport

```text
Why affected:
    The slice must be validated under concurrency and durable PostgreSQL behavior.

Responsibility:
    Provide focused validation for domain/application behavior and DB-backed validation for persistence and concurrency behavior.

Baseline constraints to preserve:
    ./mvnw test remains standard verification.
    DB-backed tests use PostgreSQL Testcontainers.
    DB-free web-layer tests use an isolated web-layer boundary instead of DB-disabled full application startup.
    Full application web integration tests use the real infrastructure required by the application context.
    SL-01 does not require full web integration validation unless HTTP exposure is deliberately added.

Planning meaning:
    Validation must use real PostgreSQL for concurrency-sensitive persistence behavior.
    Web validation is not required for this slice unless reservation behavior is deliberately exposed through HTTP.
```

### Web

```text
Why affected:
    Observable behavior is required, but public HTTP exposure is not required for this slice.

Responsibility:
    No required business API responsibility in this plan.

Baseline constraints to preserve:
    Do not add controllers by default.
    Web must not enforce correctness.
    Existing error handling baseline remains unchanged.
    If DB-free web behavior is added later, it should use an isolated web-layer boundary.
    If full application web integration is used later, it should use the real infrastructure required by the application context.

Planning meaning:
    This slice can be implemented and validated through application-level results and persisted state. A public reservation API may be introduced later only if deliberately pulled into scope.
```

### Runtime Configuration

```text
Why affected:
    New persistence must run under existing dev/test profiles and role boundaries.

Responsibility:
    Preserve existing profile and local runtime behavior.

Baseline constraints to preserve:
    Dev uses local Compose PostgreSQL.
    Automated DB-backed tests use Testcontainers.
    Local secrets and machine-specific values must not be committed.

Planning meaning:
    No project-wide runtime model change is planned.
```

### Documentation

```text
Why affected:
    Downstream lifecycle artifacts will need validation and completion records after implementation.

Responsibility:
    Record evidence and completion after implementation, not inside this plan.

Baseline constraints to preserve:
    This plan must not mark the slice complete or record validation evidence before validation happens.

Planning meaning:
    Documentation updates after implementation should be limited to validation-record.md, completion-record.md, slice-register.md, and project-state.md when appropriate.
```

---

## 8. Implementation Strategy Options

### Option — Application-Memory Check with Later Persistence

```text
How it satisfies accepted requirements:
    It could model acceptance and rejection behavior in application code.

Benefits:
    Simple to implement initially.
    Useful for early domain exploration.

Downsides:
    Does not protect durable state under concurrent requests.
    Cannot reliably prevent stale capacity views.
    Does not satisfy PostgreSQL-backed correctness expectations.
    Does not fit the persistence baseline for concurrency-sensitive slices.

Risks:
    High risk of accepting overselling under real concurrent access.
    High risk of validating the wrong behavior if tests use fake repositories.

Complexity:
    Low implementation complexity, high correctness risk.

Fit for current project state:
    Poor. Business persistence does not exist yet, but this slice requires durable capacity correctness.

Fit for relevant project baselines:
    Poor. Persistence and testing baselines require real PostgreSQL behavior for concurrency-sensitive persistence correctness.

Decision:
    reject

Reason:
    It cannot safely satisfy concurrent acceptance integrity, capacity view freshness, acceptance transition visibility, or durable validation requirements.
```

### Option — Pessimistic Locking Around Capacity State

```text
How it satisfies accepted requirements:
    Lock the relevant same-capacity state while evaluating and committing an accepted reservation decision.

Benefits:
    Conceptually clear.
    Strong protection against overlapping same-capacity writes.
    Good fit for PostgreSQL-backed correctness.

Downsides:
    Can reduce concurrency for hot capacities.
    Requires careful transaction boundary planning.
    Can be more blocking than necessary.

Risks:
    Incorrect lock scope could either fail to protect the invariant or block unrelated capacities.
    Deadlock and timeout behavior may need later operational handling.

Complexity:
    Medium.

Fit for current project state:
    Reasonable because PostgreSQL is already the persistence service and may participate in concurrency control.

Fit for relevant project baselines:
    Fits migration-driven persistence and real PostgreSQL validation.

Decision:
    keep as fallback

Reason:
    Strong option, but it may be heavier than necessary for the first local capacity slice if an atomic conditional commitment can enforce the invariant more directly.
```

### Option — Optimistic Versioning with Retry

```text
How it satisfies accepted requirements:
    Detect conflicting capacity changes through versioned state and retry or reject when another transaction changes same-capacity state.

Benefits:
    Can reduce blocking under lower contention.
    Makes conflict detection explicit.

Downsides:
    Requires retry behavior to be carefully bounded.
    More moving parts than needed for the first local correctness slice.
    Validation must cover conflict and retry paths.

Risks:
    Poor retry design could hide rejection behavior or create unstable outcomes under contention.
    More complexity before basic reservation behavior exists.

Complexity:
    Medium to high.

Fit for current project state:
    Possible, but heavier than needed for the first business persistence slice.

Fit for relevant project baselines:
    Can fit PostgreSQL and testing baselines if implemented carefully.

Decision:
    reject for this slice

Reason:
    It can satisfy the requirements, but it introduces retry complexity before the project has basic reservation-capacity behavior.
```

### Option — Append-Only Reservation Ledger with Derived Capacity

```text
How it satisfies accepted requirements:
    Record every reservation decision as an event or ledger entry and derive committed accepted quantity from accepted entries.

Benefits:
    Strong audit trail.
    Natural history of accepted and rejected decisions.
    Useful for future reconciliation or lifecycle concerns.

Downsides:
    Needs additional strategy to prevent concurrent over-acceptance at write time.
    Deriving capacity alone does not solve the atomic acceptance problem.
    More complex than required for V1 local correctness.

Risks:
    If used without a strong write-time guard, overselling can still occur.
    May pull future audit/reconciliation concerns into this slice too early.

Complexity:
    High for current needs.

Fit for current project state:
    Too broad for the first local correctness slice.

Fit for relevant project baselines:
    Could fit later, but it introduces more persistence design than this plan needs.

Decision:
    reject for this slice

Reason:
    It may be useful later, but it does not by itself provide the simplest enforcement of the current invariant under concurrency.
```

### Option — Atomic Conditional Reservation-Capacity Commitment with Decision Recording

```text
How it satisfies accepted requirements:
    Use PostgreSQL as the authoritative concurrency participant. Accept a reservation only through an atomic same-capacity commitment operation that succeeds when capacity remains available and fails when accepting would exceed the limit. Return a reservation decision for every request and record accepted decisions where needed for committed capacity visibility and validation.

Benefits:
    Directly enforces acceptance safety.
    Handles concurrent same-capacity attempts through one authoritative persistence boundary.
    Prevents stale acceptance because acceptance depends on current committed capacity state at commitment time.
    Keeps rejected decisions non-consuming.
    Fits PostgreSQL, Flyway, role separation, and real DB-backed validation.
    Avoids broad ledger or retry complexity for V1.

Downsides:
    Requires carefully planned persistence state.
    Requires DB-backed concurrency validation.
    Does not by itself provide full audit/event-sourcing behavior.
    Must ensure accepted decision recording and capacity-consuming effect stay consistent.

Risks:
    If accepted decision recording is split incorrectly from capacity commitment, acceptance transition visibility could be weakened.
    If same-capacity scope is modeled poorly, unrelated capacities could interfere or the invariant could be checked against the wrong capacity.

Complexity:
    Medium.

Fit for current project state:
    Strong. The project already has PostgreSQL, Flyway, role separation, Testcontainers, and no business persistence yet.

Fit for relevant project baselines:
    Strong. It preserves migration-driven persistence, runtime DML-only access, PostgreSQL-backed concurrency behavior, and DB-backed validation.

Decision:
    choose

Reason:
    It is the most direct strategy for enforcing the accepted invariant under concurrency while staying within current project baselines and V1 local correctness scope.
```

---

## 9. Chosen Implementation Strategy

```text
Selected strategy:
    Atomic conditional reservation-capacity commitment with decision recording.

Reason for selection:
    The slice is dominated by concurrency and requires accepted capacity consumption to be decided and made visible as one correctness unit. PostgreSQL can act as the authoritative concurrency participant, and an atomic same-capacity commitment operation can enforce the invariant directly.

Accepted requirements satisfied:
    R1 — Same-Capacity Scope Enforcement
    R2 — Quantity-Based Acceptance Safety Enforcement
    R3 — Combined Committed Quantity Enforcement
    R4 — Concurrent Acceptance Integrity Enforcement
    R5 — Capacity View Freshness Enforcement
    R6 — Acceptance Transition Visibility Enforcement
    R7 — Rejection Non-Consumption Enforcement
    R8 — Local Responsibility Boundary Enforcement
    Atomicity Conditions
    Dominant-Pressure Handling Conditions
    Rejection Behavior Conditions
    Durable State Conditions
    Observable Behavior Conditions
    Validation Conditions

Accepted reasoning supported:
    The chosen strategy preserves the accepted invariant by allowing accepted quantity to become committed only when the same-capacity capacity limit is not exceeded.

Dominant pressure / constraint handled:
    Concurrent competing reservation attempts for the same reservable capacity.

Trade-offs accepted:
    The first implementation favors direct invariant enforcement over broad audit history, reservation lifecycle behavior, release/expiration, retry/idempotency behavior, and public API exposure.

Alternatives rejected:
    Application-memory check with later persistence.
    Optimistic versioning with retry.
    Append-only ledger with derived capacity.

Fallback retained:
    Pessimistic locking around capacity state.

Reason alternatives were rejected:
    Application-memory enforcement is unsafe under durable concurrent access.
    Optimistic retry adds unnecessary complexity for the first slice.
    Append-only ledger is broader than needed and still needs write-time protection.
    Pessimistic locking is valid but heavier than the chosen atomic conditional commitment path.
```

Planning result:

```text
The selected strategy is the basis for implementation execution unless implementation work exposes a planning weakness.
```

---

## 10. Chosen Strategy Consequence

This plan implies that `SL-01` needs real business persistence in this slice.

The implementation cannot remain domain-only or application-memory-only because the accepted requirements require durable, concurrency-safe capacity enforcement.

Therefore, implementation execution is expected to introduce slice-driven persistence through Flyway migrations and DB-backed validation.

This strategy prevents local overselling.

It does not solve:

```text
- duplicate reservation commands
- order idempotency
- reservation expiration
- reservation release
- external inventory synchronization
- full checkout outcome correctness
- public API contract behavior
```

Those concerns remain outside this slice as defined in the Implementation Scope.

---

## 11. State and Persistence Planning

Required state:

```text
- reservable capacity identity
- local reservation capacity limit
- committed accepted reservation quantity for that capacity
- reservation request quantity
- reservation decision result
- accepted reservation decision as capacity-consuming state
- rejected reservation decision as non-consuming result
```

Reservable capacity identity planning:

```text
For SL-01, reservable capacity X means the stable local Inventory Reservation capacity identity used to group:

- local reservation capacity limit
- committed accepted reservation quantity
- requested reservation quantity being evaluated
- accepted capacity-consuming reservation decisions

The implementation must define one stable project-specific representation of this identity during implementation execution.

This plan does not define whether X is represented as a SKU, product identifier, inventory item identifier, warehouse-specific stock identity, database key, Java value object, or another concrete representation.

Whatever representation is chosen, all capacity comparison and commitment behavior must preserve same-capacity scope for X.
```

Authoritative state:

```text
PostgreSQL-backed Inventory Reservation state is authoritative for capacity-consuming decisions.
```

Durable state:

```text
The state needed to preserve committed accepted reservation quantity and accepted reservation decision outcomes must be durable.
```

State visible to validation:

```text
Validation must be able to observe:
- capacity limit for a reservable capacity
- committed accepted quantity after accepted decisions
- accepted versus rejected decision outcomes
- absence of committed accepted quantity beyond the capacity limit
```

Capacity limit availability:

```text
The implementation must provide a controlled way for reservable capacity limits used in validation and local execution to exist before reservation decisions are made.

For SL-01, capacity limits may be introduced through controlled test setup and local/dev setup needed to exercise the reservation use case.

This plan does not require a public capacity-management API.

Capacity-limit creation must not bypass reservation-capacity correctness because capacity limits define available capacity; they do not consume capacity.
```

Persistence responsibility:

```text
Persistence must support an atomic same-capacity capacity commitment for accepted reservations and reservation decision observability for accepted and rejected outcomes.
```

Accepted decision consistency:

```text
For accepted reservations, the decision result and the capacity-consuming effect must not diverge.

An accepted reservation must not be recorded as accepted unless its accepted quantity becomes committed accepted reservation quantity for the same reservable capacity.

The implementation must choose a transaction boundary that prevents an accepted decision from being recorded while its capacity-consuming effect remains invisible to affected same-capacity decisions.
```

Rejected decision consistency:

```text
Rejected decisions must remain non-consuming.

For SL-01, rejected decisions are required to be observable as application results.

Durable rejected-decision history is not required for this slice unless implementation execution proves it is needed for validation or consistency.

If rejected decisions are durably recorded, they must remain non-consuming and must not increase committed accepted reservation quantity.
```

Persistence constraints to preserve:

```text
- business persistence must be introduced through Flyway migrations
- dev/prod business objects should belong to the app schema
- checkout_migrator owns structural changes
- checkout_runtime uses data but must not perform DDL
- Hibernate must not create or update schema
- PostgreSQL behavior must be used for concurrency-sensitive validation
```

Planning-level rule:

```text
This section describes state and persistence responsibility.
It does not define final schema, SQL, migrations, entity mappings, indexes, or repository implementation.
```

---

## 12. Pressure and Consistency Planning

```text
Dominant pressure / constraint:
    Concurrency.

Consistency risk:
    Multiple same-capacity reservation attempts can each appear acceptable while their combined accepted quantity exceeds the capacity limit.

Authoritative decision / enforcement boundary:
    Inventory Reservation application flow backed by PostgreSQL atomic same-capacity capacity commitment.

Contradictions to prevent:
    - accepted quantity for capacity X exceeding the capacity limit for X
    - acceptance based on stale same-capacity committed quantity
    - accepted reservation decision not becoming visible as capacity-consuming state
    - rejected reservation consuming capacity
    - requested quantity for one capacity being evaluated against another capacity
    - correctness based on attempt count instead of requested quantity

Required protection:
    atomicity:
        Acceptance decision and capacity-consuming effect must be one correctness unit.

    idempotency:
        Not owned by this slice. Duplicate order/request idempotency is deferred to SL-02 unless implementation execution exposes unavoidable local duplicate-decision concerns.

    ordering:
        No cross-area ordering is required. Same-capacity accepted quantity must be serialized, guarded, or atomically constrained enough to preserve the invariant.

    deduplication:
        Not owned by this slice, except that reservation decision recording must not be allowed to bypass capacity correctness.

    ignored / stale input handling:
        Stale capacity views must not justify acceptance.

    retry behavior:
        Not required as the primary strategy. Failed capacity acceptance should produce rejection or controlled non-acceptance behavior rather than hidden unsafe retry.

    reconciliation:
        Deferred. This slice must prevent local overselling, not repair it later.

    rejection behavior:
        Reject when accepting would exceed the same-capacity limit. Rejection must not consume capacity.

How the chosen strategy handles the pressure:
    The atomic conditional capacity commitment makes the current same-capacity committed quantity and the requested quantity meet at one authoritative PostgreSQL-backed decision boundary. Under concurrent attempts, only commitments that preserve the capacity invariant may succeed. Requests that cannot be accepted without violating the invariant produce rejected decisions.
```

---

## 13. Application Flow Planning

```text
Main use case / execution path:
    Reservation request handling inside Inventory Reservation.

Decision or action requested:
    Reserve a requested quantity for a reservable capacity.

State or dependency consulted:
    Authoritative persisted reservation-capacity state for the same reservable capacity.

Decision made:
    Accept if the requested quantity can be committed without exceeding the local capacity limit.
    Reject if accepting would exceed the local capacity limit.

Successful result:
    Accepted reservation decision.
    Requested quantity becomes committed accepted reservation quantity for the same capacity.

Non-successful result:
    Rejected reservation decision.
    Requested quantity does not consume capacity.

Responsibility boundary:
    Application orchestrates the use case.
    Domain expresses reservation-capacity meaning.
    Infra enforces the PostgreSQL-backed atomic persistence behavior through application-defined boundaries.
    Web is not required for this slice.
```

Planning-level rule:

```text
This section explains behavior.
It does not define final controllers, services, repositories, adapters, methods, DTOs, or endpoint paths.
```

---

## 14. Observable Behavior Planning

```text
Successful outcome observable as:
    A reservation request returns an accepted decision and the accepted quantity is reflected in committed accepted quantity for the same reservable capacity.

Non-successful outcome observable as:
    A reservation request returns a rejected decision and does not increase committed accepted quantity.

Contradictions that must not be observable:
    - accepted total quantity greater than the capacity limit for the same reservable capacity
    - rejected decisions consuming capacity
    - accepted decisions invisible to later same-capacity decisions
    - decisions for one capacity affecting another capacity

Observation boundary:
    application result
    persisted state
    DB-backed validation

If no public API is planned, behavior is observed through:
    application-level reservation decision results and persisted reservation-capacity state during validation.
```

Traceability rule:

```text
Observable behavior traces to accepted/rejected decision requirements and the invariant.
```

API exposure decision:

```text
This slice does not require public HTTP exposure.

Reason:
    The accepted requirements require observable reservation decisions, but they do not require a public API contract. Application-level results and persisted state are enough to implement and validate local reservation-capacity correctness.

Effect:
    No business controller, endpoint path, HTTP status mapping, request DTO, or response DTO is planned here.

Future option:
    A later slice or implementation increment may expose reservation behavior through HTTP if external interaction becomes part of accepted scope.
```

---

## 15. Validation Planning

```text
What must be proven:
    For each reservable capacity X, committed accepted reservation quantity for X never exceeds the local reservation capacity limit for X.

Pressure or constraint to simulate:
    Concurrent reservation attempts competing for the same reservable capacity.

Failure to prevent:
    Overselling through individually acceptable but collectively invalid accepted reservations.

Observable confirmation:
    - fitting requests are accepted
    - exceeding requests are rejected
    - accepted requests increase committed accepted quantity
    - rejected requests do not increase committed accepted quantity
    - mixed requested quantities are evaluated by quantity
    - concurrent accepted results do not exceed the capacity limit
    - same-capacity scope is preserved
    - separate capacities do not interfere with each other
    - accepted decision records do not diverge from capacity-consuming state
```

### Validation Boundary Selection

```text
SL-01 validation should use the narrowest boundary that proves the required behavior.

Domain-focused validation:
    Use for reservation-capacity value meaning, quantities, decisions, and non-consuming rejection behavior when no Spring or database behavior is required.

Application-level validation:
    Use for reservation decision flow when use-case orchestration must be verified without HTTP exposure.

DB-backed integration validation:
    Use for persistence behavior, committed accepted quantity, same-capacity state, and accepted-versus-rejected storage effects.

Concurrency-backed PostgreSQL validation:
    Use for the dominant pressure of SL-01: concurrent same-capacity reservation attempts.

Full web integration validation:
    Not required for SL-01 unless public HTTP exposure is deliberately added later.

DB-free web-layer validation:
    If web behavior is added later and does not require the real application context, use an isolated web-layer boundary instead of DB-disabled full application startup.
```

Likely test boundary:

```text
domain-focused validation for business meaning
application-level validation for decision behavior
DB-backed integration validation for persistence behavior
concurrency-backed PostgreSQL validation for dominant pressure
```

Project baseline constraints to preserve:

```text
- ./mvnw test remains the standard verification command
- DB-backed tests use PostgreSQL Testcontainers
- DB-free web-layer tests use an isolated web-layer boundary instead of DB-disabled full application startup
- full application web integration tests use the real infrastructure required by the application context
- SL-01 does not require full web integration validation unless HTTP exposure is deliberately added
- concurrency-sensitive persistence correctness is not proven using mocks or unrelated in-memory databases
```

Planning-level rule:

```text
This section defines what must be validated.
Implementation execution defines exact tests and test mechanics.
```

---

## 16. Implementation Sequence

```text
1. Introduce the minimal domain concepts needed to express reservation-capacity identity, requested quantity, capacity limit, accepted decision, rejected decision, and committed accepted quantity.

2. Define the project-specific representation of reservable capacity identity X at implementation level, preserving the rule that requested quantity, committed accepted quantity, and capacity limit must all be scoped to the same X.

3. Define the application-level reservation decision boundary that accepts a reservation request and returns an accepted or rejected result.

4. Define application-owned persistence boundaries needed by the use case, without making application depend on infra.

5. Add migration-driven business persistence for reservation-capacity state and accepted capacity commitment, preserving app schema usage and database authority separation.

6. Provide a controlled capacity-limit setup mechanism for validation and local execution.

7. Keep rejected decisions as application-result-only for SL-01 unless implementation execution exposes a concrete validation or consistency need for durable rejected-decision records. If rejected decisions are durably recorded, ensure they remain non-consuming.

8. Grant required runtime data privileges for new business persistence through the migration/setup flow, preserving checkout_runtime as DML-only runtime authority.

9. Implement the PostgreSQL-backed infra behavior for atomic conditional capacity commitment and decision observability.

10. Ensure accepted decision recording and the capacity-consuming effect are protected by one correctness-preserving transaction boundary.

11. Connect the application flow to the infra implementation through application-defined boundaries.

12. Add focused validation for domain/application acceptance and rejection behavior.

13. Add DB-backed validation for persistence state, accepted-versus-rejected distinction, same-capacity scope, and quantity-based committed totals.

14. Add concurrency-backed PostgreSQL validation proving concurrent same-capacity reservation attempts cannot oversell.

15. Do not add public HTTP exposure or full web integration validation for SL-01 unless HTTP behavior is deliberately pulled into scope.

16. If future web behavior is added, use an isolated web-layer boundary for DB-free web-layer validation and real required infrastructure for full application web integration validation.

17. Run the standard verification command:

    ./mvnw test

18. Prepare validation-record.md with actual validation evidence after implementation and tests pass.

19. Prepare completion-record.md only after implementation, validation, and slice completion conditions are satisfied.

20. Update slice-register.md and project-state.md only after completion is recorded.
```

Sequence rule:

```text
The sequence is concrete enough to execute.
It does not define final code, final schema, final SQL, final endpoints, final DTOs, or final test implementation.
```

---

## 17. Risks, Trade-offs, and Deferrals

Accepted trade-offs:

```text
- The first slice chooses direct local capacity correctness over broader reservation lifecycle behavior.
- Public HTTP exposure is not included unless later deliberately pulled into scope.
- Full audit/event-sourcing behavior is not introduced in this slice.
- Retry/idempotency behavior is not solved here unless implementation exposes an unavoidable local consistency issue.
- Durable rejected-decision history is not required unless implementation execution proves it is needed for observability, validation, or consistency.
```

Remaining risks:

```text
- The atomic persistence behavior must keep accepted decision recording and capacity-consuming effect consistent.
- Poor same-capacity modeling could weaken the invariant.
- Concurrency tests must validate real PostgreSQL behavior, not only application-level behavior.
- Rejection behavior must remain non-consuming if rejected decisions are durably recorded.
- Implementation execution must not turn the chosen strategy into final schema or SQL design without preserving the accepted invariant.
- Capacity limit initialization must not become an uncontrolled backdoor that bypasses reservation-capacity correctness.
```

Deferred concerns:

```text
Concern:
    Reservation expiration and release.

Reason outside current scope:
    They affect future capacity reuse but are not required to prevent immediate local overselling under concurrent accepted reservations.

Possible future owner:
    Future reservation lifecycle slice.

Effect on current accepted requirements:
    Does not weaken current accepted requirements.
```

```text
Concern:
    Reservation command duplication and order idempotency.

Reason outside current scope:
    SL-01 owns local reservation capacity correctness. Duplicate order/request semantics are identified as a future responsibility, with order idempotency already represented by SL-02.

Possible future owner:
    SL-02 — Order Idempotency, or a future reservation-command idempotency slice if discovered as separate.

Effect on current accepted requirements:
    Does not weaken current accepted requirements as long as SL-01 does not rely on duplicate handling to preserve capacity correctness.
```

```text
Concern:
    Public reservation API.

Reason outside current scope:
    The current accepted requirements require observable accepted/rejected behavior, but not public HTTP exposure.

Possible future owner:
    Future API-focused implementation increment or checkout interaction slice.

Effect on current accepted requirements:
    Does not weaken current accepted requirements because application result and persisted state provide sufficient observability for this slice.
```

```text
Concern:
    External inventory synchronization.

Reason outside current scope:
    This slice protects local Inventory Reservation capacity correctness, not external stock authority.

Possible future owner:
    Future integration or synchronization slice.

Effect on current accepted requirements:
    Does not weaken current accepted requirements.
```

```text
Concern:
    Final checkout outcome composition.

Reason outside current scope:
    Final outcome composition depends on multiple responsibility areas and is deferred to a later version.

Possible future owner:
    SL-V3-01 — Final Outcome Composition Correctness.

Effect on current accepted requirements:
    Does not weaken current accepted requirements.
```

Future Work Unit candidates:

```text
- Reservation lifecycle correctness for expiration and release
- Public reservation API behavior
- Reservation command idempotency if it is not fully owned by order idempotency
- External inventory synchronization correctness
```

Deferral rule:

```text
Each deferred concern is outside the current accepted slice boundary and does not break the current invariant or accepted requirements.
```

---

## 18. Planning Result

```text
Implementation planning status:
    complete

Chosen strategy status:
    accepted

Implementation execution readiness:
    ready

Blocking issues:
    none
```

Result rule:

```text
Implementation execution may begin from this accepted implementation-plan.md.

If implementation work exposes a contradiction, missing requirement, baseline violation, or strategy weakness, work must return to the artifact that owns the issue instead of patching the problem silently in code.
```
