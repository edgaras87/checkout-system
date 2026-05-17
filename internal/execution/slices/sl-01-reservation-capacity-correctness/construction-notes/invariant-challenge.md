# Invariant Challenge — SL-01 — Reservation Capacity Correctness

## 1. Purpose

This document records `Run 3 — Candidate Invariant + Invariant Challenge` for `SL-01 — Reservation Capacity Correctness`.

It proposes a candidate invariant for local reservation capacity correctness and challenges that invariant under the dominant pressure of concurrency.

This is a working construction artifact. It does not define final guarantees, implementation requirements, implementation strategy, database schema, API design, framework design, validation evidence, or completion state.

---

## 2. Source Inputs

This run uses:

- `internal/execution/slices/sl-01-reservation-capacity-correctness/execution-context.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/slice-clarification.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/construction-notes/exploration-and-failure-mechanism.md`

Accepted Run 1 framing:

```text
The world to explore is the local Inventory Reservation decision space.

The failure to explore is overselling.

The pressure to explore is concurrency.

The boundary to preserve is local reservation capacity correctness, not full checkout outcome correctness.
```

Accepted Run 2 result:

```text
Concurrent reservation attempts can each appear acceptable against the same available capacity, then all be accepted, causing committed reserved quantity to exceed available capacity.
```

---

## 3. Framing Recap

`SL-01` is a local correctness slice inside `Inventory Reservation`.

Inventory Reservation owns the local reservation-capacity decision for a purchase attempt. It decides whether requested quantity can be reserved, records accepted or rejected reservation decisions, and treats accepted reservations as local capacity consumption.

The target failure is `Overselling`.

For this slice, overselling means:

```text
Committed accepted reservation quantity exceeds the local reservation capacity limit
for the relevant reservable capacity.
```

Concurrency is the dominant pressure because multiple purchase attempts may overlap while competing for the same capacity. Each attempt may appear acceptable on its own, while the combined committed result is invalid.

The invariant must therefore protect the combined committed effect of accepted reservation decisions, not only the individual validity of one request.

This run stays inside local reservation-capacity correctness. It does not expand into order creation, payment interpretation, final outcome composition, reservation expiration, reservation release, external inventory synchronization, API design, database design, or implementation strategy.

---

## 4. Candidate Invariant

Candidate invariant:

```text
For each relevant reservable capacity, the total committed quantity consumed by accepted reservations must never exceed the local reservation capacity limit for that reservable capacity.
```

Operational form:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
    <=
local reservation capacity limit for X
```

Where:

```text
X = the relevant reservable capacity being protected from overselling
```

This run does not decide the final project-specific representation of `X`.

`X` may later be represented as a product-like capacity, item capacity, stock keeping unit, product variant, warehouse-local capacity, or another project-specific capacity identity.

For Run 3, the important requirement is that both sides of the comparison use the same `X`.

The invariant concerns accepted reservation decisions only. Rejected reservation decisions do not consume capacity and must not increase committed accepted reservation quantity.

---

## 5. Capacity Concepts

This slice uses three capacity concepts.

```text
local reservation capacity limit:
    the total local capacity that accepted reservations must stay within
    for a given reservable capacity

committed accepted reservation quantity:
    the total quantity already consumed by accepted reservation decisions
    for that same reservable capacity

remaining capacity:
    the amount still available for a new reservation request

    remaining capacity =
        local reservation capacity limit
        minus committed accepted reservation quantity
```

Example:

```text
Local reservation capacity limit: 10
Committed accepted reservation quantity: 7
Remaining capacity: 3
```

The invariant protects the total accepted reservation quantity:

```text
committed accepted reservation quantity <= local reservation capacity limit
```

Using the example:

```text
7 <= 10
```

This means the system has not oversold.

`Remaining capacity` is used for evaluating a new reservation request.

For example, if a new request asks for quantity `2`, the request is evaluated against what remains:

```text
requested quantity <= remaining capacity

2 <= 3
```

If the request is accepted, committed accepted reservation quantity becomes:

```text
7 + 2 = 9
```

The invariant must still hold after acceptance:

```text
9 <= 10
```

The important distinction is:

```text
capacity limit:
    the maximum total quantity that may be accepted

remaining capacity:
    the amount still left for the next reservation request
```

This section uses `local reservation capacity limit` instead of vague wording like `available capacity`, because `available capacity` can be confused with `remaining capacity`.

---

## 6. Invariant Scope

The invariant scope is local to `Inventory Reservation`.

It applies to:

- accepted reservation decisions
- committed accepted reservation quantity
- the local reservation capacity limit relevant to those decisions
- concurrent reservation attempts competing for the same reservable capacity
- the relevant reservable capacity identity used by the reservation-capacity decision

It does not apply to:

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

The exact project-specific identity of a `relevant reservable capacity` is not finalized in this run.

For Run 3, this is acceptable because the invariant challenge only needs a consistent comparison scope:

```text
committed accepted reservation quantity for X
    <=
local reservation capacity limit for X
```

where:

```text
X = the same relevant reservable capacity on both sides of the comparison
```

This run does not decide whether `X` is represented later as a product, SKU, item, product variant, warehouse-local capacity, or another project-specific capacity identity.

Before implementation, the project must define a stable capacity identity so accepted reservations and capacity limits are grouped and compared against the same thing.

---

## 7. Decision Model Boundary

This run follows the current slice clarification: a reservation decision is treated as accepted or rejected.

Accepted reservations consume local reservation capacity. Rejected reservations do not.

Partial reservation behavior is not introduced by this invariant. If partial reservation behavior is later introduced, it must be handled explicitly by the artifact that owns that change. It must not be smuggled into this invariant challenge.

---

## 8. Assumptions Behind the Invariant

This candidate invariant assumes:

- Inventory Reservation owns the local reservation-capacity decision.
- Accepted reservations consume local reservation capacity.
- Rejected reservations do not consume local reservation capacity.
- Overselling is evaluated inside local Inventory Reservation capacity correctness.
- The local reservation capacity limit is the capacity limit relevant to the reservation decision.
- Committed accepted reservation quantity is the total quantity consumed by accepted reservation decisions.
- Concurrent reservation attempts may compete for the same local reservation capacity.
- The current version focus is `V1 — local correctness`.
- Cross-area final outcome correctness is not required to state the local reservation-capacity invariant.
- The overselling invariant can be defined before reservation expiration, release, or compensation behavior is designed.
- External inventory synchronization is outside the current slice boundary.

These assumptions are sufficient for candidate invariant construction.

If later reasoning shows that one of these assumptions affects the current target failure inside this slice boundary, the owning earlier artifact must be revised instead of silently patched in a later artifact.

---

## 9. Adversarial Scenarios

### SC-01 — Two Attempts Compete for One Remaining Unit

Tests:

```text
Can the invariant detect direct over-acceptance against the same remaining capacity?
```

Covers:

```text
- FMV-01 — Shared-Capacity Over-Acceptance
- FMV-04 — Group Overflow
```

Initial local capacity state:

```text
Local reservation capacity limit: 1
Committed accepted reservation quantity: 0
Remaining capacity: 1
```

Concurrent activity:

```text
Purchase attempt A requests quantity 1.
Purchase attempt B requests quantity 1.
```

Unsafe path:

```text
A observes remaining capacity 1.
B observes remaining capacity 1.

A is accepted.
B is accepted.
```

Result:

```text
Committed accepted reservation quantity: 2
Local reservation capacity limit: 1
```

Invariant check:

```text
2 <= 1
```

Result:

```text
false
```

The invariant catches the failure because committed accepted reservation quantity exceeds the local reservation capacity limit.

This scenario also shows that the invariant alone is not an enforcement mechanism. Later guarantees must explain what must be true during decision and commitment so two individually acceptable decisions cannot jointly violate the invariant.

---

### SC-02 — Many Small Attempts Overflow Capacity Together

Tests:

```text
Does the invariant protect the combined committed quantity,
not only the validity of individual requests?
```

Covers:

```text
- FMV-04 — Group Overflow
```

Initial local capacity state:

```text
Local reservation capacity limit: 10
Committed accepted reservation quantity: 7
Remaining capacity: 3
```

Concurrent activity:

```text
Attempt A requests quantity 1.
Attempt B requests quantity 1.
Attempt C requests quantity 1.
Attempt D requests quantity 1.
```

Unsafe path:

```text
Each attempt observes remaining capacity 3.
Each attempt is accepted.
```

Result:

```text
Additional accepted quantity: 4
Committed accepted reservation quantity after attempts: 11
Local reservation capacity limit: 10
```

Invariant check:

```text
11 <= 10
```

Result:

```text
false
```

The invariant catches the failure because it evaluates the committed group effect. A weaker rule focused only on individual request size would miss this scenario because each request for quantity 1 appears individually acceptable against remaining capacity 3.

---

### SC-03 — Mixed-Quantity Concurrent Requests

Tests:

```text
Does the invariant protect total quantity,
not merely the number of reservation attempts?
```

Initial local capacity state:

```text
Local reservation capacity limit: 10
Committed accepted reservation quantity: 6
Remaining capacity: 4
```

Concurrent activity:

```text
Attempt A requests quantity 3.
Attempt B requests quantity 3.
```

Unsafe path:

```text
A observes remaining capacity 4.
B observes remaining capacity 4.

A evaluates quantity 3 as acceptable.
B evaluates quantity 3 as acceptable.

A is accepted.
B is accepted.
```

Result:

```text
Additional accepted quantity: 6
Committed accepted reservation quantity after attempts: 12
Local reservation capacity limit: 10
```

Invariant check:

```text
12 <= 10
```

Result:

```text
false
```

The invariant catches the failure because it protects quantity, not merely the number of reservation attempts. Overselling can occur even when no single request consumes the full remaining capacity.

---

### SC-04 — Stale Capacity View

Tests:

```text
Does the invariant still expose the failure
when reservation decisions use outdated capacity information?
```

Covers:

```text
- FMV-02 — Stale-Capacity Decision
```

Initial local capacity state:

```text
Local reservation capacity limit: 5
Committed accepted reservation quantity: 3
Remaining capacity: 2
```

Concurrent activity:

```text
Attempt A requests quantity 2.
Attempt B requests quantity 1.
```

Unsafe path:

```text
A observes remaining capacity 2.
B observes remaining capacity 2.

A is accepted.
B is accepted.
```

Result:

```text
Committed accepted reservation quantity after attempts: 6
Local reservation capacity limit: 5
```

Invariant check:

```text
6 <= 5
```

Result:

```text
false
```

The invariant catches the failure after both decisions are committed.

This scenario exposes an important signal for later runs: a correctness guarantee must prevent acceptance based on stale or incomplete capacity views. The invariant states what must never be false, while later guarantees must protect the decision process under stale-view pressure.

---

### SC-05 — Split Decision and Commitment

Tests:

```text
Does the invariant survive the dangerous gap
between reservation evaluation and committed accepted state?
```

Covers:

```text
- FMV-03 — Split Decision and Commitment
```

Initial local capacity state:

```text
Local reservation capacity limit: 2
Committed accepted reservation quantity: 0
Remaining capacity: 2
```

Concurrent activity:

```text
Attempt A requests quantity 2.
Attempt B requests quantity 1.
```

Unsafe path:

```text
A evaluates quantity 2 as acceptable.
Before A is reflected as capacity-consuming state, B evaluates quantity 1 as acceptable.

A is accepted.
B is accepted.
```

Result:

```text
Committed accepted reservation quantity: 3
Local reservation capacity limit: 2
```

Invariant check:

```text
3 <= 2
```

Result:

```text
false
```

The invariant catches the resulting oversell.

This scenario shows that future guarantees must account for the whole acceptance transition, not only the evaluation step. The invariant must be preserved across the path from evaluation to committed accepted reservation.

---

### SC-06 — Rejected Reservation Accidentally Consumes Capacity

Tests:

```text
Does the invariant clearly distinguish
accepted reservations from rejected reservations?
```

Initial local capacity state:

```text
Local reservation capacity limit: 5
Committed accepted reservation quantity: 5
Remaining capacity: 0
```

Activity:

```text
Attempt A requests quantity 1.
A is rejected.
```

Unsafe interpretation:

```text
Rejected attempt A still increases committed accepted reservation quantity.
```

Result:

```text
Committed accepted reservation quantity: 6
Local reservation capacity limit: 5
```

Invariant check:

```text
6 <= 5
```

Result:

```text
false
```

The invariant catches the failure if rejected reservations are incorrectly counted as committed accepted reservation quantity.

This confirms that the invariant must focus on quantity consumed by accepted reservations. It also signals that later guarantees must preserve rejection behavior: rejected decisions must not consume capacity.

---

### SC-07 — Scope Mismatch

Tests:

```text
Does the invariant compare quantity and capacity
for the same reservable capacity identity?
```

Unsafe interpretation:

```text
Committed accepted reservation quantity is counted for one reservable capacity.
Local reservation capacity limit is checked for a different reservable capacity.
```

Example:

```text
Committed accepted reservation quantity for capacity X is compared against the local reservation capacity limit for capacity Y.
```

The candidate invariant becomes meaningless if the capacity identity is inconsistent.

This does not break the invariant itself, but it exposes a scope weakness:

```text
The invariant must require committed accepted reservation quantity and local reservation capacity limit to refer to the same relevant reservable capacity.
```

Refinement needed:

```text
For each relevant reservable capacity, compare only committed accepted reservation quantity for that same capacity against the local reservation capacity limit for that same capacity.
```

This refinement stays inside the slice boundary and does not define schema or implementation design.

---

## 10. Challenge Findings

The adversarial scenarios showed that the candidate invariant is directionally correct, but it must be interpreted precisely.

Each finding below records:

- the challenge scenario that exposed the weakness
- the exposed weakness
- the refinement pressure created by that weakness
- the protection need passed into Run 4

### Finding 1 — Accepted quantity must be evaluated as a committed total

Exposed by:

```text
- SC-01 — Two Attempts Compete for One Remaining Unit
- SC-02 — Many Small Attempts Overflow Capacity Together
```

Exposed weakness:

```text
Individually acceptable reservation decisions can become collectively invalid when committed together.
```

Refinement pressure:

```text
The invariant must protect committed accepted reservation quantity as a total.
```

Protection need:

```text
PN-01 — Prevent overlapping individually acceptable decisions from becoming collectively invalid after commitment.
```

---

### Finding 2 — Quantity matters, not just reservation count

Exposed by:

```text
- SC-03 — Mixed-Quantity Concurrent Requests
```

Exposed weakness:

```text
Overselling depends on quantity correctness, not on reservation-attempt count.
```

Refinement pressure:

```text
The invariant must compare committed accepted quantity against the local reservation capacity limit.
```

Protection need:

```text
PN-02 — Preserve quantity correctness under mixed request sizes.
```

---

### Finding 3 — Stale capacity views can make unsafe acceptance look valid

Exposed by:

```text
- SC-04 — Stale Capacity View
```

Exposed weakness:

```text
Reservation decisions can appear valid when based on stale or incomplete capacity information.
```

Refinement pressure:

```text
The invariant must survive decisions made under stale-view pressure.
```

Protection need:

```text
PN-03 — Prevent stale or incomplete capacity views from producing invalid accepted reservations.
```

---

### Finding 4 — The evaluation-to-commit transition must be protected

Exposed by:

```text
- SC-05 — Split Decision and Commitment
```

Exposed weakness:

```text
Acceptance evaluation and committed accepted state can diverge during overlapping activity.
```

Refinement pressure:

```text
The invariant must survive the entire acceptance transition, not only evaluation.
```

Protection need:

```text
PN-04 — Protect the transition from evaluation to committed accepted reservation state.
```

---

### Finding 5 — Rejected reservations must not consume capacity

Exposed by:

```text
- SC-06 — Rejected Reservation Accidentally Consumes Capacity
```

Exposed weakness:

```text
Rejected reservations can incorrectly appear as committed accepted capacity consumption.
```

Refinement pressure:

```text
The invariant must apply only to accepted reservations.
```

Protection need:

```text
PN-05 — Preserve rejection behavior so rejected reservations never consume committed accepted capacity.
```

---

### Finding 6 — Capacity scope must remain consistent

Exposed by:

```text
- SC-07 — Scope Mismatch
```

Exposed weakness:

```text
The invariant becomes meaningless if quantity and capacity limit are compared against different reservable-capacity identities.
```

Refinement pressure:

```text
The invariant must use the same reservable capacity identity on both sides of the comparison.
```

Protection need:

```text
PN-06 — Preserve consistent reservable-capacity scope during evaluation and commitment.
```

---

## 11. Refinement Decision

The initial candidate invariant was:

```text
For each relevant reservable capacity, the total committed quantity consumed by accepted reservations must never exceed the local reservation capacity limit for that reservable capacity.
```

The challenge findings showed that the invariant needs to be more explicit about scope, quantity, and accepted decisions.

The refined candidate invariant is:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
must be less than or equal to
the local reservation capacity limit for X.
```

Where:

```text
X = the relevant reservable capacity being protected from overselling
```

This refinement makes five points explicit:

- the comparison is scoped to the same reservable capacity
- `X` must mean the same capacity identity on both sides of the comparison
- the protected quantity is the total committed accepted quantity, not one request in isolation
- the comparison is against the local reservation capacity limit, not remaining capacity
- rejected reservations do not contribute to committed accepted reservation quantity

---

## 12. Challenge Result

The refined candidate invariant survives the Run 3 challenge.

It directly addresses overselling because overselling exists when committed accepted reservation quantity exceeds the local reservation capacity limit.

It survives concurrency as a correctness rule because concurrent acceptance paths are invalid whenever their combined committed result violates the invariant.

The challenge also shows that the invariant alone is not enough to guide implementation.

The invariant says what must never be false.

Run 4 must derive guarantees that explain what must be preserved so the invariant remains true under concurrency.

Those guarantees must address the protection problems exposed by the Run 3 challenge:

- `SC-01`, `SC-02`:
  protection of combined committed quantity so individually acceptable decisions cannot become collectively invalid

- `SC-03`:
  quantity correctness under mixed request sizes

- `SC-04`:
  protection against stale or incomplete capacity views producing invalid accepted reservations

- `SC-05`:
  protection of the evaluation-to-commit transition so overlapping acceptance paths cannot oversell capacity

- `SC-06`:
  preservation of rejection behavior so rejected reservations do not consume capacity

- `SC-07`:
  consistent reservable-capacity scope between committed quantity and capacity limit

---

## 13. Deferred or Later Concerns

The following concerns remain outside Run 3:

```text
Implementation strategy:
    Later work must decide how the invariant is enforced.
    This run does not choose locking, conditional updates, transactions, versioning, retries, schema structure, repositories, or framework mechanisms.

Database schema:
    Later work may need persistence structure.
    This run does not define tables, columns, constraints, indexes, migrations, or SQL behavior.

API design:
    Later work may expose observable behavior.
    This run does not define endpoint paths, HTTP methods, request bodies, or response bodies.

Reservation expiration:
    Expiration may affect future capacity.
    The core overselling invariant can be defined before expiration behavior is designed.

Reservation release:
    Release may affect future capacity reuse.
    The core overselling invariant can be defined before release behavior is designed.

External inventory synchronization:
    External inventory behavior is outside local Inventory Reservation correctness.

Order creation:
    Order behavior may depend on reservation decisions, but it does not own local reservation-capacity correctness.

Payment interpretation:
    Payment behavior is outside this slice.

Final outcome composition:
    Cross-area semantic consistency is deferred to later project versions.
```

None of these deferred concerns weakens the current invariant for local reservation-capacity correctness.

---

## 14. Signals for Run 4

Run 4 should derive guarantees from the protection needs exposed during Run 3.

| Protection Need                                                                                                   | Source Scenario | Exposed Weakness                                                  | Refinement Pressure                                        |
| ----------------------------------------------------------------------------------------------------------------- | --------------- | ----------------------------------------------------------------- | ---------------------------------------------------------- |
| PN-01 — Prevent overlapping individually acceptable decisions from becoming collectively invalid after commitment | SC-01, SC-02    | Individually acceptable decisions can become collectively invalid | Protect committed accepted quantity as a total             |
| PN-02 — Preserve quantity correctness under mixed request sizes                                                   | SC-03           | Overselling depends on quantity, not reservation count            | Compare committed accepted quantity against capacity limit |
| PN-03 — Prevent stale or incomplete capacity views from producing invalid accepted reservations                   | SC-04           | Decisions can use stale or incomplete capacity information        | Survive stale-view pressure                                |
| PN-04 — Protect the transition from evaluation to committed accepted reservation state                            | SC-05           | Evaluation and committed state can diverge                        | Protect full acceptance transition                         |
| PN-05 — Preserve rejection behavior so rejected reservations never consume committed accepted capacity            | SC-06           | Rejected reservations may incorrectly consume capacity            | Preserve accepted/rejected distinction                     |
| PN-06 — Preserve consistent reservable-capacity scope during evaluation and commitment                            | SC-07           | Quantity and limit may use inconsistent capacity identities       | Preserve stable comparison scope                           |

These protection needs are not guarantees.

They are the protection problems that Run 4 must use when deriving and challenging guarantees.

Run 4 may merge, split, refine, rename, or stabilize these protection needs into correctness attack directions and guarantee coverage.

---

## 15. Run 3 Result

Run 3 produces the following refined candidate invariant for use by Run 4:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
must be less than or equal to
the local reservation capacity limit for X.
```

Where:

```text
X = the relevant reservable capacity being protected from overselling
```

The invariant scope is local to Inventory Reservation and applies to accepted reservation decisions under concurrency.

The invariant directly addresses the target failure of overselling.

The invariant has been challenged against these failure-mechanism variants and invariant challenge scenarios:

```text
Failure-Mechanism Variants:
- FMV-01 — Shared-Capacity Over-Acceptance
- FMV-02 — Stale-Capacity Decision
- FMV-03 — Split Decision and Commitment
- FMV-04 — Group Overflow

Invariant Challenge Scenarios:
- SC-01 — Two Attempts Compete for One Remaining Unit
- SC-02 — Many Small Attempts Overflow Capacity Together
- SC-03 — Mixed-Quantity Concurrent Requests
- SC-04 — Stale Capacity View
- SC-05 — Split Decision and Commitment
- SC-06 — Rejected Reservation Accidentally Consumes Capacity
- SC-07 — Scope Mismatch
```

The challenge produced the following protection needs for Run 4:

```text
- PN-01 — Prevent overlapping individually acceptable decisions from becoming collectively invalid after commitment
- PN-02 — Preserve quantity correctness under mixed request sizes
- PN-03 — Prevent stale or incomplete capacity views from producing invalid accepted reservations
- PN-04 — Protect the transition from evaluation to committed accepted reservation state
- PN-05 — Preserve rejection behavior so rejected reservations never consume committed accepted capacity
- PN-06 — Preserve consistent reservable-capacity scope during evaluation and commitment
```

The invariant is stable enough for `Run 4 — Refined Invariant + Candidate Guarantees + Guarantee Challenge`.

Run 4 must derive and challenge guarantees that preserve this invariant under concurrency without introducing implementation design too early.
