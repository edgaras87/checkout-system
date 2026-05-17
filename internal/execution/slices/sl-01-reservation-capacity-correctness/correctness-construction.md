# Correctness Construction — SL-01 — Reservation Capacity Correctness

## 1. Purpose

This document records the final correctness construction for `SL-01 — Reservation Capacity Correctness`.

It consolidates the accepted reasoning from Runs 1–4 into the authoritative correctness model for this slice.

It defines:

- the operational framing
- the explored local failure model
- the final failure mechanism
- the final invariant
- the invariant scope
- the final correctness attack directions
- the final guarantees
- traceability from target failure to failure mechanism, invariant, CADs, and guarantees
- deferred concerns outside the current slice

Run 3 protection needs are consolidated through the Run 4 correctness attack directions. They are not repeated as a separate final layer because CADs are the stabilized attack classes used for final correctness construction.

This artifact does not define implementation requirements, implementation strategy, database schema, migration design, API design, framework design, class design, repository design, test implementation, validation evidence, or completion state.

---

## 2. Source Inputs

This construction uses:

- `internal/execution/slices/sl-01-reservation-capacity-correctness/execution-context.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/slice-clarification.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/construction-notes/exploration-and-failure-mechanism.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/construction-notes/invariant-challenge.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/construction-notes/guarantee-challenge.md`

The selected slice is:

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

---

## 3. Operational Framing Recap

`SL-01` is a local correctness slice inside `Inventory Reservation`.

Inventory Reservation owns the local reservation-capacity decision for a purchase attempt. It decides whether requested quantity can be reserved, records accepted or rejected reservation decisions, and treats accepted reservations as consuming local reservation capacity.

The target failure is `Overselling`.

For this slice, overselling means:

```text
Committed accepted reservation quantity exceeds the local reservation capacity limit
for the relevant reservable capacity.
```

The dominant pressure is `Concurrency`.

Concurrency matters because multiple purchase attempts may overlap while competing for the same local reservable capacity. Each attempt may appear acceptable on its own, while the combined accepted quantity exceeds the capacity limit.

This slice is not full checkout correctness. It does not solve order creation, order idempotency, payment interpretation, final outcome determination, final outcome uniqueness, final outcome composition, external inventory correctness, reservation expiration, reservation release, reconciliation, or downstream compensation.

The boundary to preserve is:

```text
local reservation capacity correctness inside Inventory Reservation
```

not:

```text
full checkout outcome correctness
```

---

## 4. Explored Model Summary

Inside this slice, the relevant local model contains:

- reservable capacity
- local reservation capacity limit
- committed accepted reservation quantity
- remaining capacity
- requested reservation quantity
- purchase attempts requesting reservation
- reservation decisions
- accepted reservation decisions
- rejected reservation decisions
- concurrent attempts competing for the same reservable capacity

The central decision is:

```text
Should this reservation request be accepted or rejected?
```

A request is safe to accept only if the accepted result preserves local reservation capacity correctness for the same reservable capacity.

Accepted reservations consume local reservation capacity.

Rejected reservations do not consume local reservation capacity.

The important correctness comparison is not one request in isolation. The required comparison is the total committed accepted reservation quantity for a reservable capacity against the local reservation capacity limit for that same reservable capacity.

The important distinction is:

```text
local reservation capacity limit:
    the maximum total quantity that may be accepted

remaining capacity:
    the amount still available for a new reservation request

committed accepted reservation quantity:
    the quantity already consumed by accepted reservations
```

The exact project-specific representation of a reservable capacity remains downstream. For this correctness construction, the required concept is stable:

```text
For each reservable capacity X,
compare committed accepted reservation quantity for X
against the local reservation capacity limit for X.
```

Before implementation, the project must define a stable project-specific identity for `X` so committed accepted reservation quantity and reservation capacity limits are grouped and compared against the same reservable capacity.

This construction does not decide how capacity is stored, derived, refreshed, locked, queried, updated, persisted, exposed, or validated.

---

## 5. Failure Mechanism

Overselling occurs when Inventory Reservation accepts more total reservation quantity than the local reservation capacity limit allows.

The failure mechanism is:

```text
A local reservation capacity limit exists for a reservable capacity.

One or more reservation attempts observe remaining capacity as sufficient.

Each attempt evaluates its own requested quantity as acceptable.

Each attempt is accepted.

The combined accepted quantity exceeds the local reservation capacity limit.

Committed accepted reservation quantity now exceeds the local reservation capacity limit.
```

The core weakness is not necessarily that one request asks for too much.

The core weakness is:

```text
Each accepted decision can appear valid alone,
while the group of accepted decisions is invalid for the same reservable capacity.
```

The failure can appear through these correctness attack directions:

- scope corruption
- quantity miscalculation
- collective over-acceptance
- stale capacity view acceptance
- evaluation-to-commit gap
- rejection consumption
- responsibility boundary escape

These attack directions all threaten the same local failure:

```text
total committed accepted reservation quantity for X
    >
local reservation capacity limit for X
```

---

## 6. Final Invariant

For each reservable capacity `X`:

```text
total committed accepted reservation quantity for X
must be less than or equal to
the local reservation capacity limit for X
```

Operational form:

```text
committed accepted reservation quantity for X
    <=
local reservation capacity limit for X
```

This invariant directly negates the target failure.

If the invariant holds for a reservable capacity, local overselling has not occurred for that capacity.

If the invariant is violated, local overselling has occurred.

---

## 7. Invariant Scope

The invariant applies inside `Inventory Reservation`.

It applies to:

- local reservation-capacity decisions
- accepted reservation decisions
- committed accepted reservation quantity
- local reservation capacity limits
- requested quantities being accepted
- concurrent reservation attempts competing for the same reservable capacity
- the same-capacity comparison between committed quantity and capacity limit

The invariant requires both sides of the comparison to refer to the same reservable capacity:

```text
committed accepted reservation quantity for X
must be compared against
local reservation capacity limit for X
```

The invariant does not apply to:

- physical inventory management
- external inventory system correctness
- external stock synchronization
- order creation
- order idempotency
- payment execution
- payment interpretation
- final outcome determination
- final outcome uniqueness
- final outcome composition correctness
- reservation expiration
- reservation release
- downstream compensation
- reconciliation strategy
- API shape
- database schema
- implementation strategy
- validation implementation

Rejected reservation decisions are outside committed accepted reservation quantity. They may be recorded as decisions, but they must not consume local reservation capacity.

Partial reservation behavior is not introduced by this construction. If partial reservation behavior is introduced later, the owning artifact must handle it explicitly.

---

## 8. Final Correctness Attack Directions

The following correctness attack directions are the stable classes of threat against the invariant.

They are correctness-level attack classes, not implementation risks.

They describe what the guarantee set must defend against so the invariant remains true under concurrency.

### CAD-01 — Scope Corruption

Source:

```text
PN-06 — Preserve consistent reservable-capacity scope during evaluation and commitment.
```

Committed accepted reservation quantity and local reservation capacity limit may be evaluated against different reservable-capacity identities.

Invariant risk:

```text
The invariant becomes meaningless if both sides of the comparison do not refer to the same X.
```

---

### CAD-02 — Quantity Miscalculation

Source:

```text
PN-02 — Preserve quantity correctness under mixed request sizes.
```

Reservation-capacity correctness may be evaluated by request count, presence of remaining capacity, or isolated request validity instead of accepted quantity.

Invariant risk:

```text
The system may accept a request even though the requested quantity pushes
committed accepted reservation quantity above the local reservation capacity limit.
```

---

### CAD-03 — Collective Over-Acceptance

Source:

```text
PN-01 — Prevent overlapping individually acceptable decisions from becoming collectively invalid after commitment.
```

Multiple individually acceptable reservation decisions may become invalid as a committed group.

Invariant risk:

```text
The combined committed accepted quantity may exceed the local reservation capacity limit.
```

---

### CAD-04 — Stale Capacity View Acceptance

Source:

```text
PN-03 — Prevent stale or incomplete capacity views from producing invalid accepted reservations.
```

A reservation may be accepted using a capacity view that excludes relevant committed accepted reservations.

Invariant risk:

```text
The system may accept capacity that has already been consumed.
```

---

### CAD-05 — Evaluation-to-Commit Gap

Source:

```text
PN-04 — Protect the transition from evaluation to committed accepted reservation state.
```

A reservation may be evaluated as acceptable, but the committed state may change before the reservation becomes capacity-consuming.

Invariant risk:

```text
The decision may be safe at evaluation time but unsafe by the time it is committed.
```

---

### CAD-06 — Rejection Consumption

Source:

```text
PN-05 — Preserve rejection behavior so rejected reservations never consume committed accepted capacity.
```

Rejected reservation decisions may incorrectly increase committed accepted reservation quantity.

Invariant risk:

```text
Capacity can be consumed without an accepted reservation decision.
```

---

### CAD-07 — Responsibility Boundary Escape

Source:

```text
Run 1 boundary and Run 3 invariant scope.
```

Local reservation-capacity correctness may be shifted to downstream systems, external inventory behavior, reconciliation, or future compensation.

Invariant risk:

```text
Inventory Reservation may fail to preserve its own local overselling invariant.
```

`CAD-07 — Responsibility Boundary Escape` is boundary-derived rather than PN-derived, because local correctness cannot be preserved if the slice allows overselling responsibility to escape Inventory Reservation.

---

## 9. Final Guarantees

### G1 — Same-Capacity Scope Guarantee

For every reservation-capacity decision, the requested quantity, committed accepted reservation quantity, and local reservation capacity limit must be evaluated within the same reservable-capacity scope.

A decision for capacity `X` must not compare committed accepted reservation quantity for `X` against the capacity limit for another capacity.

This guarantee protects the invariant from meaningless or unsafe cross-capacity comparison.

Protected attack direction:

```text
CAD-01 — Scope Corruption
```

---

### G2 — Quantity-Based Acceptance Safety Guarantee

A reservation request may be accepted only if adding its requested quantity to the committed accepted reservation quantity for the same reservable capacity keeps the total less than or equal to the local reservation capacity limit.

Conceptual form:

```text
For reservable capacity X:

committed accepted reservation quantity for X
    + requested quantity being accepted for X
must be less than or equal to
local reservation capacity limit for X
```

This guarantee protects the invariant at the acceptance decision point.

Protected attack direction:

```text
CAD-02 — Quantity Miscalculation
```

---

### G3 — Combined Committed Quantity Guarantee

Reservation-capacity correctness must be evaluated against the combined committed accepted quantity for the reservable capacity, including all accepted reservations that consume that capacity.

A request that appears valid alone may still be unsafe if the accepted group would exceed the capacity limit.

This guarantee protects against isolated-request reasoning and collectively invalid committed totals.

Protected attack directions:

```text
CAD-02 — Quantity Miscalculation
CAD-03 — Collective Over-Acceptance
```

---

### G4 — Concurrent Acceptance Integrity Guarantee

Concurrent or overlapping reservation-capacity decisions for the same reservable capacity must not commit accepted reservations whose combined quantity exceeds the local reservation capacity limit.

This guarantee directly addresses the dominant pressure of concurrency.

It requires the invariant to survive overlapping decisions, not only sequential or isolated decisions.

Protected attack directions:

```text
CAD-03 — Collective Over-Acceptance
CAD-05 — Evaluation-to-Commit Gap
```

---

### G5 — Capacity View Freshness Guarantee

A reservation must not be accepted from a capacity view that omits committed accepted reservations relevant to the same reservable capacity.

A stale or incomplete view can make a request appear acceptable even when accepting it would violate the invariant.

This guarantee protects against accepting based on capacity-relevant state that is incomplete for the same reservable capacity.

Protected attack direction:

```text
CAD-04 — Stale Capacity View Acceptance
```

---

### G6 — Acceptance Transition Visibility Guarantee

Once a reservation decision is accepted, its quantity must participate in committed accepted reservation quantity for reservation-capacity decisions that could otherwise over-accept the same reservable capacity.

Acceptance must become capacity-consuming in the correctness model.

This guarantee prevents accepted capacity-consuming decisions from being ignored by affected decisions.

Protected attack direction:

```text
CAD-05 — Evaluation-to-Commit Gap
```

---

### G7 — Rejection Non-Consumption Guarantee

A rejected reservation decision must not increase committed accepted reservation quantity.

Only accepted reservations consume local reservation capacity.

This guarantee preserves the accepted-versus-rejected decision boundary.

Protected attack direction:

```text
CAD-06 — Rejection Consumption
```

---

### G8 — Local Responsibility Boundary Guarantee

The invariant must be preserved inside Inventory Reservation.

It must not rely on order creation, payment interpretation, final outcome composition, external inventory behavior, reservation expiration, reservation release, reconciliation, or downstream compensation.

Downstream behavior may depend on reservation decisions, but it does not repair local overselling after Inventory Reservation has already violated its own capacity invariant.

This guarantee preserves the selected slice boundary.

Protected attack direction:

```text
CAD-07 — Responsibility Boundary Escape
```

---

## 10. Traceability

### Target Failure to Failure Form

```text
Target failure:
    Overselling

Failure form:
    committed accepted reservation quantity exceeds local reservation capacity limit
    for the same reservable capacity
```

### Failure Form to Invariant

```text
Failure:
    total committed accepted reservation quantity for X
        >
    local reservation capacity limit for X

Invariant:
    total committed accepted reservation quantity for X
        <=
    local reservation capacity limit for X
```

The invariant directly negates the target failure.

### Invariant to Correctness Attack Directions

| CAD                                     | Invariant risk                                                                                                   |
| --------------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| CAD-01 — Scope Corruption               | The invariant becomes meaningless if committed quantity and capacity limit do not refer to the same `X`.         |
| CAD-02 — Quantity Miscalculation        | Acceptance may push committed accepted quantity above the capacity limit if quantity is not evaluated correctly. |
| CAD-03 — Collective Over-Acceptance     | Individually acceptable decisions may violate the invariant as a committed group.                                |
| CAD-04 — Stale Capacity View Acceptance | Accepted decisions may be based on views that omit already-consumed capacity.                                    |
| CAD-05 — Evaluation-to-Commit Gap       | The decision may be safe at evaluation time but unsafe by commit time.                                           |
| CAD-06 — Rejection Consumption          | Capacity may be consumed without an accepted reservation decision.                                               |
| CAD-07 — Responsibility Boundary Escape | Inventory Reservation may fail to preserve its own local invariant.                                              |

### Correctness Attack Directions to Guarantees

| CAD                                     | Protected by |
| --------------------------------------- | ------------ |
| CAD-01 — Scope Corruption               | G1           |
| CAD-02 — Quantity Miscalculation        | G2, G3       |
| CAD-03 — Collective Over-Acceptance     | G3, G4       |
| CAD-04 — Stale Capacity View Acceptance | G5           |
| CAD-05 — Evaluation-to-Commit Gap       | G4, G6       |
| CAD-06 — Rejection Consumption          | G7           |
| CAD-07 — Responsibility Boundary Escape | G8           |

### Invariant to Guarantees

| Guarantee                                       | Trace to Invariant                                                                                                     |
| ----------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| G1 — Same-Capacity Scope Guarantee              | Ensures both sides of the invariant refer to the same reservable capacity `X`.                                         |
| G2 — Quantity-Based Acceptance Safety Guarantee | Prevents accepting a request whose requested quantity would push committed accepted quantity above the capacity limit. |
| G3 — Combined Committed Quantity Guarantee      | Ensures the invariant is protected for total committed accepted quantity, not isolated requests.                       |
| G4 — Concurrent Acceptance Integrity Guarantee  | Ensures overlapping accepted decisions do not jointly violate the invariant.                                           |
| G5 — Capacity View Freshness Guarantee          | Prevents acceptance from views that omit relevant committed accepted quantity.                                         |
| G6 — Acceptance Transition Visibility Guarantee | Ensures accepted reservations participate in committed accepted quantity for affected decisions.                       |
| G7 — Rejection Non-Consumption Guarantee        | Ensures rejected decisions do not increase committed accepted reservation quantity.                                    |
| G8 — Local Responsibility Boundary Guarantee    | Keeps preservation of the invariant inside Inventory Reservation.                                                      |

### Challenge Influence

The invariant challenge refined the invariant by making the same-capacity scope explicit:

```text
For each reservable capacity X
```

It also clarified that the invariant protects:

- total committed accepted quantity
- same-capacity comparison
- quantity rather than attempt count
- accepted reservations only
- local Inventory Reservation correctness

The guarantee challenge consolidated Run 3 protection needs into correctness attack directions and strengthened the guarantee set by requiring protection against:

- scope corruption
- quantity miscalculation
- collective over-acceptance
- stale capacity view acceptance
- evaluation-to-commit gaps
- rejection consumption
- responsibility boundary escape

---

## 11. Deferred Concerns

The following concerns are outside the current slice and do not weaken the current correctness result.

### Implementation Strategy

Reason deferred:

```text
This construction defines what must be true, not how it is enforced.
```

Possible future owner:

```text
implementation-requirements.md
implementation-plan.md
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

### Database Schema

Reason deferred:

```text
The invariant and guarantees require conceptual capacity correctness,
but this artifact does not define tables, columns, constraints, indexes, migrations, or SQL behavior.
```

Possible future owner:

```text
implementation planning and implementation execution
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

### API Design

Reason deferred:

```text
The slice may later need observable behavior, but this artifact does not define endpoint paths, HTTP methods, request bodies, response bodies, or error contracts.
```

Possible future owner:

```text
implementation planning and implementation execution
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

### Test Implementation

Reason deferred:

```text
This artifact defines correctness, not concrete test classes, test data, test timing, or test implementation.
```

Possible future owner:

```text
implementation requirements
validation planning
implementation execution
validation record
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

### Reservation Expiration

Reason deferred:

```text
Expiration may affect future capacity reuse, but it is not required to preserve the immediate local invariant for accepted reservations under concurrency.
```

Possible future owner:

```text
future reservation lifecycle slice
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

### Reservation Release

Reason deferred:

```text
Release may affect future capacity reuse, but it is not required to preserve the immediate local invariant for accepted reservations under concurrency.
```

Possible future owner:

```text
future reservation lifecycle slice
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

### Downstream Compensation

Reason deferred:

```text
Downstream compensation may respond to later failures, but it does not preserve local reservation-capacity correctness once overselling has already occurred.
```

Possible future owner:

```text
future cross-area or recovery slice
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

### External Inventory Synchronization

Reason deferred:

```text
External inventory behavior is outside local Inventory Reservation correctness.
This slice protects local reservation capacity decisions, not external stock authority.
```

Possible future owner:

```text
future integration or synchronization slice
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

### Order Creation and Order Idempotency

Reason deferred:

```text
Order behavior may depend on reservation decisions,
but it does not own local reservation-capacity correctness.
```

Possible future owner:

```text
SL-02 — Order Idempotency
future order behavior slices
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

### Payment Interpretation

Reason deferred:

```text
Payment behavior is outside Inventory Reservation and does not define whether local reservation capacity was oversold.
```

Possible future owner:

```text
SL-03 — Payment Interpretation Under Conflict
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

### Final Outcome Uniqueness and Composition

Reason deferred:

```text
Final outcome correctness depends on multiple responsibility areas.
This slice establishes local reservation-capacity correctness first.
```

Possible future owner:

```text
SL-04 — Final Outcome State Uniqueness
SL-V3-01 — Final Outcome Composition Correctness
```

Effect on current correctness:

```text
Does not weaken current correctness.
```

---

## 12. Construction Result

The final correctness model for `SL-01 — Reservation Capacity Correctness` is stable.

The final invariant is:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
must be less than or equal to
the local reservation capacity limit for X.
```

The final correctness attack directions are:

- CAD-01 — Scope Corruption
- CAD-02 — Quantity Miscalculation
- CAD-03 — Collective Over-Acceptance
- CAD-04 — Stale Capacity View Acceptance
- CAD-05 — Evaluation-to-Commit Gap
- CAD-06 — Rejection Consumption
- CAD-07 — Responsibility Boundary Escape

The final guarantees required to preserve that invariant are:

- G1 — Same-Capacity Scope Guarantee
- G2 — Quantity-Based Acceptance Safety Guarantee
- G3 — Combined Committed Quantity Guarantee
- G4 — Concurrent Acceptance Integrity Guarantee
- G5 — Capacity View Freshness Guarantee
- G6 — Acceptance Transition Visibility Guarantee
- G7 — Rejection Non-Consumption Guarantee
- G8 — Local Responsibility Boundary Guarantee

This construction directly addresses the target failure of overselling under concurrency inside Inventory Reservation.

The result is ready for `Run 6 — Implementation Requirements`.

Run 6 must translate the final invariant, CAD protections, and guarantees into enforceable implementation requirements without choosing implementation strategy, database schema, API design, class design, repository design, migration design, or test implementation.
