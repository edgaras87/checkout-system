# Implementation Requirements — SL-01 — Reservation Capacity Correctness

## 1. Purpose

This document records `Run 6 — Implementation Requirements` for `SL-01 — Reservation Capacity Correctness`.

It translates the accepted correctness construction into enforceable implementation requirements.

It answers:

```text
What must the implementation provide so the final invariant, CAD protections, and guarantees can be enforced?
```

This artifact defines implementation requirements only.

It does not choose implementation strategy, database schema, migration design, SQL strategy, API endpoint design, request or response body design, class design, method design, repository design, framework design, concrete test implementation, validation evidence, or completion state.

Implementation planning may later decide how these requirements are enforced.

Implementation planning must not silently redefine the invariant, correctness attack directions, guarantees, slice boundary, or target failure from the correctness construction.

---

## 2. Correctness Inputs

This document uses:

- `internal/execution/slices/sl-01-reservation-capacity-correctness/execution-context.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/slice-clarification.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/correctness-construction.md`

Selected slice:

```text
Slice ID:
    SL-01

Slice name:
    Reservation Capacity Correctness

Source failure:
    Reservation Capacity Violation

Responsibility area:
    Inventory Reservation

Target failure:
    Overselling

Dominant pressure:
    Concurrency

Status:
    in-progress

Execution location:
    internal/execution/slices/sl-01-reservation-capacity-correctness/

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

Where:

```text
X = the stable reservable capacity identity being protected from overselling.

All quantities and limits in this invariant must be scoped to the same X.
```

Accepted correctness attack directions:

- CAD-01 — Scope Corruption
- CAD-02 — Quantity Miscalculation
- CAD-03 — Collective Over-Acceptance
- CAD-04 — Stale Capacity View Acceptance
- CAD-05 — Evaluation-to-Commit Gap
- CAD-06 — Rejection Consumption
- CAD-07 — Responsibility Boundary Escape

Accepted guarantees:

- G1 — Same-Capacity Scope Guarantee
- G2 — Quantity-Based Acceptance Safety Guarantee
- G3 — Combined Committed Quantity Guarantee
- G4 — Concurrent Acceptance Integrity Guarantee
- G5 — Capacity View Freshness Guarantee
- G6 — Acceptance Transition Visibility Guarantee
- G7 — Rejection Non-Consumption Guarantee
- G8 — Local Responsibility Boundary Guarantee

The implementation requirements below must preserve the accepted invariant, protect against the accepted correctness attack directions, and enforce the accepted guarantees.

---

## 3. Required State

The implementation must represent enough conceptual state to enforce local reservation-capacity correctness inside `Inventory Reservation`.

Required conceptual state:

```text
reservable capacity identity:
    stable identity defining the capacity scope being protected from overselling

local reservation capacity limit:
    the maximum total quantity that may be accepted
    for a reservable capacity

requested reservation quantity:
    the quantity requested by a purchase attempt
    for a reservable capacity

reservation decision:
    the accepted or rejected result of a reservation request

committed accepted reservation quantity:
    the total quantity consumed by accepted reservation decisions
    for the same reservable capacity

remaining capacity:
    the quantity still available for safe acceptance
    for the same reservable capacity

accepted reservation:
    a reservation decision that consumes local reservation capacity

rejected reservation:
    a reservation decision that does not consume local reservation capacity
```

The implementation must preserve the relationship:

```text
For each reservable capacity X:

committed accepted reservation quantity for X
    <=
local reservation capacity limit for X
```

The implementation must preserve stable same-capacity comparison for:

```text
requested quantity for X
committed accepted reservation quantity for X
local reservation capacity limit for X
remaining capacity for X
```

The implementation must be able to distinguish:

- accepted reservation decisions from rejected reservation decisions
- requested quantity from attempt count
- one reservable capacity from another reservable capacity
- capacity-consuming state from non-consuming decision records

This section defines required conceptual state only.

It does not define tables, columns, indexes, constraints, entities, DTOs, repositories, storage layout, calculation strategy, or persistence mechanism.

---

## 4. Required Decision Flow

This section shows how the required conceptual state participates in one reservation-capacity decision.

It does not define implementation order, transaction design, method design, database schema, API flow, or code structure.

It explains which concepts must be available to enforce the requirements.

```text
1. Identify reservable capacity identity X
       Why:
           The system must know which capacity is being protected.

       Required state:
           reservable capacity identity

       Supports:
           R1 — Same-Capacity Scope Enforcement
           CAD-01 — Scope Corruption

2. Read or determine local reservation capacity limit for X
       Why:
           The system must know the maximum quantity that may be accepted for X.

       Required state:
           local reservation capacity limit

       Supports:
           R1 — Same-Capacity Scope Enforcement
           R2 — Quantity-Based Acceptance Safety Enforcement
           CAD-01 — Scope Corruption
           CAD-02 — Quantity Miscalculation

3. Determine committed accepted reservation quantity for X
       Why:
           The system must know how much capacity has already been consumed by accepted reservations.

       Required state:
           committed accepted reservation quantity
           accepted reservation

       Supports:
           R2 — Quantity-Based Acceptance Safety Enforcement
           R3 — Combined Committed Quantity Enforcement
           R5 — Capacity View Freshness Enforcement
           R6 — Acceptance Transition Visibility Enforcement
           CAD-02 — Quantity Miscalculation
           CAD-03 — Collective Over-Acceptance
           CAD-04 — Stale Capacity View Acceptance
           CAD-05 — Evaluation-to-Commit Gap

4. Receive requested reservation quantity for X
       Why:
           The system must know how much new capacity the request wants to consume.

       Required state:
           requested reservation quantity

       Supports:
           R2 — Quantity-Based Acceptance Safety Enforcement
           R3 — Combined Committed Quantity Enforcement
           CAD-02 — Quantity Miscalculation

5. Compare committed accepted quantity plus requested quantity against the capacity limit
       Why:
           The system must decide whether accepting this request would preserve the invariant.

       Conceptual check:
           committed accepted reservation quantity for X
               + requested quantity for X
           <=
           local reservation capacity limit for X

       Supports:
           R2 — Quantity-Based Acceptance Safety Enforcement
           R3 — Combined Committed Quantity Enforcement
           R4 — Concurrent Acceptance Integrity Enforcement
           CAD-02 — Quantity Miscalculation
           CAD-03 — Collective Over-Acceptance

6. Produce exactly one decision result
       Why:
           The request must become either accepted or rejected.

       Required state:
           reservation decision

       Supports:
           R7 — Rejection Non-Consumption Enforcement
           R8 — Local Responsibility Boundary Enforcement
           CAD-06 — Rejection Consumption
           CAD-07 — Responsibility Boundary Escape

7. If accepted, make requested quantity count as committed accepted quantity for X
       Why:
           Future affected decisions must not ignore this accepted reservation.

       Required state:
           accepted reservation
           committed accepted reservation quantity

       Supports:
           R4 — Concurrent Acceptance Integrity Enforcement
           R6 — Acceptance Transition Visibility Enforcement
           Atomicity Conditions
           CAD-03 — Collective Over-Acceptance
           CAD-05 — Evaluation-to-Commit Gap

8. If rejected, do not increase committed accepted quantity for X
       Why:
           Rejected reservations must not consume capacity.

       Required state:
           rejected reservation

       Supports:
           R7 — Rejection Non-Consumption Enforcement
           CAD-06 — Rejection Consumption
```

This flow is conceptual.

It shows what must be protected.

It does not choose how the project will enforce it.

---

## 5. Authoritative Decision Point

The authoritative decision point is the local Inventory Reservation decision:

```text
Should this reservation request be accepted or rejected?
```

This section defines where reservation-capacity authority lives.

It establishes which responsibility area owns the accept-or-reject reservation decision before enforcement requirements are defined.

The following section defines what the implementation must enforce to preserve the invariant once this authority boundary is established.

For a reservation request, the decision must be made inside the `Inventory Reservation` responsibility area.

The decision must evaluate:

```text
reservable capacity identity X
requested quantity for X
current committed accepted reservation quantity for X
local reservation capacity limit for X
```

The decision must produce exactly one local decision result for the request:

```text
accepted
or
rejected
```

An accepted decision means the requested quantity becomes part of committed accepted reservation quantity for the same reservable capacity identity.

A rejected decision means the requested quantity does not become part of committed accepted reservation quantity.

The authoritative decision point must not be delegated to:

- order creation
- payment interpretation
- final outcome composition
- downstream compensation
- external inventory behavior
- reconciliation
- reservation expiration
- reservation release

Those concerns may exist in later slices or later project versions, but they must not be required to preserve this slice’s local capacity invariant.

Trace contribution:

```text
This section helps preserve the guarantees by defining:

- which part of the system is responsible for reservation decisions
- where the accept-or-reject decision happens
- the difference between accepted and rejected reservations
- why downstream systems must not repair local overselling after the fact
```

Trace:

```text
CAD-06 — Rejection Consumption
CAD-07 — Responsibility Boundary Escape

G7 — Rejection Non-Consumption Guarantee
G8 — Local Responsibility Boundary Guarantee
```

---

## 6. Direct Enforcement Requirements

These requirements define the direct implementation obligations derived from the accepted invariant, CADs, and guarantees.

A requirement may protect one CAD, multiple CADs, one guarantee, or part of a guarantee.

A guarantee states what correctness protection must hold.

A requirement states what implementation must enforce so that protection can hold.

The mapping is traceable but not necessarily one-to-one.

### R1 — Same-Capacity Scope Enforcement

For every reservation-capacity decision, the implementation must enforce that the requested quantity, committed accepted reservation quantity, and local reservation capacity limit all refer to the same reservable capacity.

A decision for capacity identity `X` must not use committed accepted quantity or a capacity limit belonging to another capacity.

Protects:

```text
CAD-01 — Scope Corruption
```

Trace:

```text
G1 — Same-Capacity Scope Guarantee
```

---

### R2 — Quantity-Based Acceptance Safety Enforcement

The implementation must accept a reservation request only when accepting it would preserve the invariant.

Required decision rule:

```text
For reservable capacity X:

committed accepted reservation quantity for X
    + requested quantity being accepted for X
must be less than or equal to
local reservation capacity limit for X.
```

If accepting the request would cause committed accepted reservation quantity to exceed the local reservation capacity limit for the same reservable capacity, the request must not be accepted.

Protects:

```text
CAD-02 — Quantity Miscalculation
```

Trace:

```text
G2 — Quantity-Based Acceptance Safety Guarantee
```

---

### R3 — Combined Committed Quantity Enforcement

The implementation must evaluate reservation-capacity correctness against the combined committed accepted reservation quantity for a reservable capacity.

It must not treat one request as safe merely because that request appears valid in isolation.

It must protect against the combined accepted group exceeding the capacity limit.

It must enforce correctness by quantity, not by request count, accepted-attempt count, or reservation-record count.

Protects:

```text
CAD-02 — Quantity Miscalculation
CAD-03 — Collective Over-Acceptance
```

Trace:

```text
G3 — Combined Committed Quantity Guarantee
```

---

### R4 — Concurrent Acceptance Integrity Enforcement

The implementation must ensure that concurrent or overlapping reservation decisions for the same reservable capacity cannot produce a committed accepted group whose total quantity exceeds the local reservation capacity limit.

The implementation must preserve the invariant after overlapping decisions complete.

It is not enough for each decision to appear valid when considered alone.

This requirement protects the committed result, not only the evaluation moment.

The implementation must ensure that an unsafe acceptance does not commit. It may later enforce this through rejection, delay, retry, serialization, conflict handling, or another strategy selected during implementation planning.

This requirement defines the required correctness outcome only.

It does not choose the enforcement mechanism.

Protects:

```text
CAD-03 — Collective Over-Acceptance
CAD-05 — Evaluation-to-Commit Gap
```

Trace:

```text
G4 — Concurrent Acceptance Integrity Guarantee
```

---

### R5 — Capacity View Freshness Enforcement

The implementation must not accept a reservation from a capacity view that omits committed accepted reservations relevant to the same reservable capacity.

A decision view used for acceptance must include all capacity-consuming accepted reservations that are relevant to whether the current request can be safely accepted.

A stale or incomplete view must not justify acceptance.

This requirement defines the required correctness property only.

It does not define how freshness is achieved.

Protects:

```text
CAD-04 — Stale Capacity View Acceptance
```

Trace:

```text
G5 — Capacity View Freshness Guarantee
```

---

### R6 — Acceptance Transition Visibility Enforcement

After a reservation is accepted, its accepted quantity must participate in committed accepted reservation quantity for reservation-capacity decisions that could otherwise over-accept the same reservable capacity.

An accepted reservation must not remain invisible to affected reservation-capacity decisions in a way that allows over-acceptance.

Acceptance must become capacity-consuming in the correctness model.

This requirement does not define transaction boundaries, locking, ordering mechanism, storage mechanism, or persistence strategy.

Protects:

```text
CAD-05 — Evaluation-to-Commit Gap
```

Trace:

```text
G6 — Acceptance Transition Visibility Guarantee
```

---

### R7 — Rejection Non-Consumption Enforcement

The implementation must ensure that rejected reservation decisions do not increase committed accepted reservation quantity.

Rejected decisions may be recorded as local decisions, but they must not consume local reservation capacity.

Protects:

```text
CAD-06 — Rejection Consumption
```

Trace:

```text
G7 — Rejection Non-Consumption Guarantee
```

---

### R8 — Local Responsibility Boundary Enforcement

The implementation must preserve the invariant inside `Inventory Reservation`.

The implementation must not rely on downstream areas to repair overselling after the local reservation-capacity invariant has already been violated.

Order creation, payment interpretation, final outcome composition, external inventory behavior, reservation expiration, reservation release, reconciliation, or downstream compensation must not be required to preserve this slice’s local invariant.

Protects:

```text
CAD-07 — Responsibility Boundary Escape
```

Trace:

```text
G8 — Local Responsibility Boundary Guarantee
```

---

## 7. Enforcement-Supporting System Conditions

Direct enforcement requirements alone are insufficient.

Additional supporting conditions are required so the enforcement requirements remain reliable under the dominant pressure.

These supporting conditions are derived from earlier discovered failure mechanisms, correctness attack directions, and guarantee protections.

Without these supporting conditions:

- enforcement requirements may become operationally unsafe
- correctness guarantees may become unverifiable
- accepted reservations may become invisible or stale
- concurrent behavior may violate the invariant despite local checks appearing correct

The following sections define these supporting correctness conditions.

---

### 7.1 Atomicity Conditions

The implementation must make the reservation acceptance decision and the capacity-consuming effect of that decision indivisible from a correctness perspective.

For an accepted reservation, the following must be protected as one correctness unit:

```text
evaluate requested quantity against same-capacity committed accepted quantity and capacity limit
    → decide accepted
    → make accepted quantity count as committed accepted reservation quantity
```

The implementation must not allow a request to be accepted while its accepted quantity is absent from the capacity-consuming state used by affected reservation-capacity decisions.

The implementation must prevent this unsafe split:

```text
request A is evaluated as acceptable
request A is accepted
request A is not yet visible as capacity-consuming state
request B is evaluated as acceptable against the same capacity
request B is accepted
combined accepted quantity exceeds the capacity limit
```

The atomicity requirement is conceptual.

It does not choose:

- transaction strategy
- locking strategy
- optimistic or pessimistic coordination
- conditional update strategy
- versioning strategy
- database schema
- SQL statement shape
- framework mechanism

Protects:

```text
CAD-03 — Collective Over-Acceptance
CAD-05 — Evaluation-to-Commit Gap
```

Trace:

```text
G2 — Quantity-Based Acceptance Safety Guarantee
G4 — Concurrent Acceptance Integrity Guarantee
G6 — Acceptance Transition Visibility Guarantee
```

---

### 7.2 Dominant-Pressure Handling Conditions

The dominant pressure is `Concurrency`.

The implementation must handle concurrent reservation attempts that compete for the same reservable capacity.

Under concurrency, the implementation must ensure:

- concurrent accepted decisions for the same capacity preserve the invariant
- individually acceptable requests cannot become collectively invalid when accepted together
- stale or incomplete capacity views cannot justify acceptance
- accepted quantities become visible to affected capacity decisions
- accepted quantity is evaluated by quantity, not by attempt count
- decisions for one capacity do not consume or compare against another capacity

The implementation must ensure that an unsafe acceptance does not commit. It may later enforce this through rejection, delay, retry, serialization, conflict handling, or another strategy selected during implementation planning.

This section defines required survivability under the dominant pressure only.

It does not choose which enforcement strategy is used.

Protects:

```text
CAD-01 — Scope Corruption
CAD-02 — Quantity Miscalculation
CAD-03 — Collective Over-Acceptance
CAD-04 — Stale Capacity View Acceptance
CAD-05 — Evaluation-to-Commit Gap
```

Trace:

```text
G1 — Same-Capacity Scope Guarantee
G2 — Quantity-Based Acceptance Safety Guarantee
G3 — Combined Committed Quantity Guarantee
G4 — Concurrent Acceptance Integrity Guarantee
G5 — Capacity View Freshness Guarantee
G6 — Acceptance Transition Visibility Guarantee
```

---

### 7.3 Rejection Behavior Conditions

A reservation request must be rejected when accepting it would violate the local reservation capacity invariant.

Required rejection condition:

```text
For reservable capacity X:

committed accepted reservation quantity for X
    + requested quantity for X
would be greater than
local reservation capacity limit for X.
```

A rejected reservation decision must:

- preserve the fact that the request was not accepted
- not increase committed accepted reservation quantity
- not consume local reservation capacity
- not require downstream compensation to repair local correctness

The implementation must keep accepted and rejected decisions semantically distinct.

Protects:

```text
CAD-06 — Rejection Consumption
CAD-07 — Responsibility Boundary Escape
```

Trace:

```text
G2 — Quantity-Based Acceptance Safety Guarantee
G7 — Rejection Non-Consumption Guarantee
G8 — Local Responsibility Boundary Guarantee
```

---

### 7.4 Durable State Conditions

If durable state is used for reservation-capacity correctness, it must preserve enough information to enforce the invariant.

Durable state must support the following conceptual needs:

- represent reservable capacity identity
- represent local reservation capacity limit
- represent accepted reservation decisions
- determine committed accepted reservation quantity for a reservable capacity
- preserve accepted-versus-rejected distinction
- support correctness under concurrent access

If durable state is used for committed reservation state, the stored state must be sufficient to reconstruct or determine:

```text
committed accepted reservation quantity for X
```

for the same reservable capacity `X` being evaluated.

Durable state must not allow runtime behavior to bypass the invariant.

This section defines durable-state expectations only.

It does not define:

- database tables
- columns
- constraints
- indexes
- migrations
- SQL strategy
- entity mappings
- repository structure
- transaction boundaries
- database locking strategy

Protects:

```text
CAD-01 — Scope Corruption
CAD-03 — Collective Over-Acceptance
CAD-04 — Stale Capacity View Acceptance
CAD-05 — Evaluation-to-Commit Gap
CAD-06 — Rejection Consumption
```

Trace:

```text
G1 — Same-Capacity Scope Guarantee
G3 — Combined Committed Quantity Guarantee
G5 — Capacity View Freshness Guarantee
G6 — Acceptance Transition Visibility Guarantee
G7 — Rejection Non-Consumption Guarantee
```

---

### 7.5 Observable Behavior Conditions

The implementation must produce observable reservation decision behavior at the conceptual level.

For each reservation request, the observable result must distinguish:

```text
accepted
or
rejected
```

Observable acceptance means:

```text
The requested quantity has been accepted as capacity-consuming reservation state
for the same reservable capacity.
```

Observable rejection means:

```text
The requested quantity was not accepted
and does not consume local reservation capacity.
```

When multiple reservation requests compete for the same capacity, the observable results must not show more accepted quantity than the local reservation capacity limit allows.

The implementation must make capacity rejection observable enough that downstream behavior does not need to guess whether reservation capacity was accepted.

This section does not define:

- endpoint path
- HTTP method
- request body
- response body
- status code
- error format
- DTO shape
- API contract

Protects:

```text
CAD-02 — Quantity Miscalculation
CAD-03 — Collective Over-Acceptance
CAD-06 — Rejection Consumption
CAD-07 — Responsibility Boundary Escape
```

Trace:

```text
G2 — Quantity-Based Acceptance Safety Guarantee
G4 — Concurrent Acceptance Integrity Guarantee
G7 — Rejection Non-Consumption Guarantee
G8 — Local Responsibility Boundary Guarantee
```

---

### 7.6 Validation Conditions

Validation must show that the implementation enforces the accepted invariant, CAD protections, and guarantees.

Validation must show coverage for each CAD either directly or through the requirements that protect it.

Validation must cover:

- acceptance within capacity limit
- rejection when capacity would overflow
- quantity-based evaluation
- combined committed quantity correctness
- concurrent competing reservations
- stale or incomplete capacity views
- acceptance transition visibility
- same-capacity correctness
- rejection non-consumption
- local responsibility boundary preservation

Validation must demonstrate that the following cannot happen:

```text
For any reservable capacity X:

committed accepted reservation quantity for X
    >
local reservation capacity limit for X.
```

Validation must target the dominant pressure:

```text
Concurrency
```

Validation must not become concrete test implementation in this artifact.

This section does not define:

- test class names
- test method names
- fixtures
- database schema
- API calls
- thread orchestration details
- framework-specific test setup
- exact test data
- exact assertions

Protects:

```text
CAD-01 — Scope Corruption
CAD-02 — Quantity Miscalculation
CAD-03 — Collective Over-Acceptance
CAD-04 — Stale Capacity View Acceptance
CAD-05 — Evaluation-to-Commit Gap
CAD-06 — Rejection Consumption
CAD-07 — Responsibility Boundary Escape
```

Trace:

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

---

## 8. Requirement Traceability

### Requirement to Guarantee Traceability

| Requirement                           | Requirement Name                              | Traces To                                       |
| ------------------------------------- | --------------------------------------------- | ----------------------------------------------- |
| R1                                    | Same-Capacity Scope Enforcement               | G1 — Same-Capacity Scope Guarantee              |
| R2                                    | Quantity-Based Acceptance Safety Enforcement  | G2 — Quantity-Based Acceptance Safety Guarantee |
| R3                                    | Combined Committed Quantity Enforcement       | G3 — Combined Committed Quantity Guarantee      |
| R4                                    | Concurrent Acceptance Integrity Enforcement   | G4 — Concurrent Acceptance Integrity Guarantee  |
| R5                                    | Capacity View Freshness Enforcement           | G5 — Capacity View Freshness Guarantee          |
| R6                                    | Acceptance Transition Visibility Enforcement  | G6 — Acceptance Transition Visibility Guarantee |
| R7                                    | Rejection Non-Consumption Enforcement         | G7 — Rejection Non-Consumption Guarantee        |
| R8                                    | Local Responsibility Boundary Enforcement     | G8 — Local Responsibility Boundary Guarantee    |
| Atomicity Conditions                  | Decision and capacity-consuming effect safety | G2, G4, G6                                      |
| Dominant-Pressure Handling Conditions | Concurrency handling                          | G1, G2, G3, G4, G5, G6                          |
| Rejection Behavior Conditions         | Rejection behavior under overflow             | G2, G7, G8                                      |
| Durable State Conditions              | Durable capacity correctness expectations     | G1, G3, G5, G6, G7                              |
| Observable Behavior Conditions        | Accepted/rejected decision behavior           | G2, G4, G7, G8                                  |
| Validation Conditions                 | Evidence needed before completion             | G1, G2, G3, G4, G5, G6, G7, G8                  |

### Correctness Attack Direction to Requirement Protection

| CAD                                     | Protected By                                                                                                                                         |
| --------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| CAD-01 — Scope Corruption               | R1, Dominant-Pressure Handling Conditions, Durable State Conditions, Validation Conditions                                                           |
| CAD-02 — Quantity Miscalculation        | R2, R3, Dominant-Pressure Handling Conditions, Observable Behavior Conditions, Validation Conditions                                                 |
| CAD-03 — Collective Over-Acceptance     | R3, R4, Atomicity Conditions, Dominant-Pressure Handling Conditions, Durable State Conditions, Observable Behavior Conditions, Validation Conditions |
| CAD-04 — Stale Capacity View Acceptance | R5, Dominant-Pressure Handling Conditions, Durable State Conditions, Validation Conditions                                                           |
| CAD-05 — Evaluation-to-Commit Gap       | R4, R6, Atomicity Conditions, Dominant-Pressure Handling Conditions, Durable State Conditions, Validation Conditions                                 |
| CAD-06 — Rejection Consumption          | R7, Rejection Behavior Conditions, Durable State Conditions, Observable Behavior Conditions, Validation Conditions                                   |
| CAD-07 — Responsibility Boundary Escape | R8, Rejection Behavior Conditions, Observable Behavior Conditions, Validation Conditions                                                             |

### Requirement to Invariant Traceability

| Requirement                                       | Invariant Contribution                                                                           |
| ------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| R1 — Same-Capacity Scope Enforcement              | Ensures committed quantity and capacity limit are compared for the same `X`.                     |
| R2 — Quantity-Based Acceptance Safety Enforcement | Prevents accepting a request whose requested quantity would exceed the capacity limit.           |
| R3 — Combined Committed Quantity Enforcement      | Ensures correctness is evaluated against total committed accepted quantity.                      |
| R4 — Concurrent Acceptance Integrity Enforcement  | Prevents overlapping accepted decisions from jointly violating the invariant.                    |
| R5 — Capacity View Freshness Enforcement          | Prevents acceptance from views that omit relevant committed accepted quantity.                   |
| R6 — Acceptance Transition Visibility Enforcement | Ensures accepted reservations participate in committed accepted quantity for affected decisions. |
| R7 — Rejection Non-Consumption Enforcement        | Ensures rejected reservations do not increase committed accepted quantity.                       |
| R8 — Local Responsibility Boundary Enforcement    | Keeps responsibility for preserving the invariant inside Inventory Reservation.                  |

---

## 9. Deferred Concerns

The following concerns remain outside this artifact.

### Implementation Strategy

Reason deferred:

```text
This document defines what implementation must enforce, not how the project will enforce it.
```

Possible future owner:

```text
implementation-plan.md
```

Effect on current requirements:

```text
Does not weaken current requirements.
```

---

### Database Schema

Reason deferred:

```text
This document requires conceptual durable-state expectations only.
It does not define tables, columns, constraints, indexes, migrations, SQL, or mappings.
```

Possible future owner:

```text
implementation planning and implementation execution
```

Effect on current requirements:

```text
Does not weaken current requirements.
```

---

### API Design

Reason deferred:

```text
This document defines conceptual observable behavior only.
It does not define endpoint paths, HTTP methods, request bodies, response bodies, status codes, or DTOs.
```

Possible future owner:

```text
implementation planning and implementation execution
```

Effect on current requirements:

```text
Does not weaken current requirements.
```

---

### Concrete Test Implementation

Reason deferred:

```text
This document defines validation needs only.
It does not define concrete tests, fixtures, class names, method names, or framework-specific setup.
```

Possible future owner:

```text
implementation planning
implementation execution
validation-record.md
```

Effect on current requirements:

```text
Does not weaken current requirements.
```

---

### Reservation Expiration and Release

Reason deferred:

```text
Expiration and release may affect future capacity reuse,
but they are not required to enforce immediate local reservation-capacity correctness
for accepted reservations under concurrency.
```

Possible future owner:

```text
future reservation lifecycle slice
```

Effect on current requirements:

```text
Does not weaken current requirements.
```

---

### Cross-Area Checkout Correctness

Reason deferred:

```text
Order creation, payment interpretation, final outcome uniqueness,
and final outcome composition are outside local Inventory Reservation correctness.
```

Possible future owner:

```text
SL-02 — Order Idempotency
SL-03 — Payment Interpretation Under Conflict
SL-04 — Final Outcome State Uniqueness
SL-V3-01 — Final Outcome Composition Correctness
```

Effect on current requirements:

```text
Does not weaken current requirements.
```

---

## 10. Handoff Result

`SL-01 — Reservation Capacity Correctness` has implementation-ready requirements.

The implementation must enforce:

```text
For each reservable capacity X:

committed accepted reservation quantity for X
    <=
local reservation capacity limit for X.
```

The implementation must provide:

- conceptual state sufficient to evaluate capacity correctness
- a local authoritative reservation decision point
- same-capacity scope enforcement
- quantity-based acceptance safety
- combined committed quantity enforcement
- concurrent acceptance integrity
- capacity view freshness
- acceptance transition visibility
- rejection non-consumption
- local responsibility boundary preservation
- conceptual durable-state support
- conceptual observable accepted/rejected behavior
- validation coverage for the invariant, CADs, and guarantees

This artifact completes Run 6.

Downstream implementation planning may now decide how to enforce these requirements.

Downstream work must not silently redefine the invariant, correctness attack directions, guarantees, slice boundary, or target failure.
