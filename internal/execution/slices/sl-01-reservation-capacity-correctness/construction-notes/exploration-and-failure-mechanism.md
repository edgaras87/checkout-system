# Exploration and Failure Mechanism — SL-01 — Reservation Capacity Correctness

## 1. Purpose

This document records `Run 2 — Exploration + Failure Mechanism` for `SL-01 — Reservation Capacity Correctness`.

It explores the local Inventory Reservation decision world defined by Run 1 and explains how `Overselling` can happen under `Concurrency`.

This is a working construction artifact. It does not define the final invariant, guarantees, implementation requirements, implementation strategy, database design, API design, validation evidence, or completion state.

---

## 2. Source Inputs

This run uses:

- `internal/execution/slices/sl-01-reservation-capacity-correctness/execution-context.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/slice-clarification.md`

Accepted Run 1 framing:

```text
The world to explore is the local Inventory Reservation decision space.

The failure to explore is overselling.

The pressure to explore is concurrency.

The boundary to preserve is local reservation capacity correctness, not full checkout outcome correctness.
```

---

## 3. Framing Recap

`SL-01` is a local correctness slice inside `Inventory Reservation`.

Inventory Reservation owns the local reservation-capacity decision for a purchase attempt. It decides whether requested quantity can be reserved, records accepted or rejected reservation decisions, and treats accepted reservations as local capacity consumption.

For this slice, `Overselling` means:

```text
Committed reserved quantity exceeds available capacity for the relevant reservable inventory capacity.
```

Concurrency matters because multiple purchase attempts can compete for the same available capacity at nearly the same time. The slice must explore how overlapping reservation decisions can allow more quantity to be accepted than the available capacity supports.

This run stays inside local reservation-capacity correctness. Order creation, payment behavior, final outcome composition, reconciliation, reservation release, expiration, external inventory synchronization, API shape, database schema, and implementation strategy remain outside this artifact.

---

## 4. Explored Slice World

Inside this slice, the world contains:

- reservable capacity
- requested reservation quantity
- already accepted reservations
- reserved quantity consumed by accepted reservations
- purchase attempts requesting reservation
- reservation decisions
- accepted decisions
- rejected decisions
- concurrent attempts competing for the same capacity

The central local question is:

```text
Can this requested quantity be accepted without causing committed reserved quantity to exceed available capacity?
```

A reservation request enters the local Inventory Reservation decision space. Inventory Reservation evaluates the request against capacity-relevant state and ends with either an accepted or rejected decision.

An accepted reservation increases committed reserved quantity for the relevant capacity. A rejected reservation does not consume capacity.

The failure surface exists between:

```text
observing capacity-relevant state
    → deciding whether the request can be accepted
    → committing the accepted reservation as capacity-consuming state
```

If two or more reservation attempts overlap in that surface, each may appear individually acceptable while their combined accepted quantity exceeds capacity.

---

## 5. State Surface

The relevant state surface is conceptual only:

```text
available capacity:
    the local capacity limit relevant to the reservation decision

committed reserved quantity:
    quantity already consumed by accepted reservation decisions

requested quantity:
    quantity requested by the current purchase attempt

reservation decision:
    accepted or rejected local result for a purchase attempt

accepted reservation:
    a reservation decision that consumes local reservation capacity

rejected reservation:
    a reservation decision that does not consume local reservation capacity

capacity remainder:
    available capacity minus committed reserved quantity, understood conceptually
```

This state surface becomes failure-prone when the capacity-relevant view used for a decision is not aligned with other accepted reservations being decided at the same time.

This run does not decide how the state is stored, derived, synchronized, locked, queried, updated, or enforced.

---

## 6. Action Surface

The relevant local actions are:

```text
request reservation:
    a purchase attempt asks Inventory Reservation to reserve quantity

observe reservation-capacity state:
    Inventory Reservation considers available capacity and already committed reserved quantity

evaluate request:
    Inventory Reservation compares the requested quantity with the capacity that appears available

accept reservation:
    Inventory Reservation commits the reservation as capacity-consuming local state

reject reservation:
    Inventory Reservation refuses the reservation because accepting it would exceed available capacity

record reservation decision:
    Inventory Reservation preserves the accepted or rejected result as local reservation state
```

The dangerous action sequence is:

```text
observe capacity
    → evaluate request as acceptable
    → accept reservation
```

This sequence becomes unsafe under concurrency when multiple attempts perform it using overlapping or stale capacity views.

---

## 7. Decision Point

The key decision point is the local reservation-capacity decision:

```text
Should this reservation request be accepted or rejected?
```

The decision is capacity-relevant because acceptance consumes local reservation capacity. It is correctness-critical because accepted quantity contributes to committed reserved quantity.

The decision is vulnerable if each request is treated as if it were alone while other concurrent requests are also being accepted against the same available capacity.

This decision point is not downstream order creation, payment interpretation, final outcome resolution, API handling, persistence design, or validation design.

---

## 8. Pressure Expression

The dominant pressure is concurrency.

Concurrency appears when two or more reservation attempts overlap while competing for the same capacity. It can appear as:

- two purchase attempts evaluated at nearly the same time
- several attempts observing the same capacity-relevant state
- decisions made before competing accepted reservations are reflected in the decision view
- accepted reservations being recorded in an order that does not match how capacity was evaluated
- multiple individually valid decisions becoming invalid when combined

The pressure does not require malicious behavior or external system failure. The failure can occur even when each individual request looks valid in isolation. The problem is the combined effect of accepted decisions under overlap.

---

## 9. Failure Mechanism

Overselling happens when Inventory Reservation accepts more total reservation quantity than available capacity allows.

The failure mechanism is:

```text
Available capacity exists.

One or more reservation attempts observe capacity as available.

Each attempt evaluates its own requested quantity against that available capacity.

Each attempt is accepted.

The combined accepted quantity is greater than the capacity that was actually available.

Committed reserved quantity now exceeds available capacity.
```

The core weakness is not that a request asks for too much by itself. The core weakness is that acceptance decisions are made without preserving capacity correctness across overlapping decisions.

In other words:

```text
Each accepted decision can appear locally valid,
while the group of accepted decisions is invalid for the same local capacity.
```

This is the target failure surface for later invariant construction.

---

## 10. Failure Mechanism Variants

Overselling can appear through several conceptual variants inside the same local reservation-capacity boundary.

These variants clarify how the target failure can emerge.

They are exploratory in Run 2.

Later runs may merge, split, refine, replace, or stabilize these variants into correctness attack directions.

They do not define implementation design.

---

### FMV-01 — Shared-Capacity Over-Acceptance

**Meaning:**

Multiple attempts compete for the same remaining capacity and are accepted as if each were the only attempt.

**Core Failure Risk:**

The same capacity is effectively granted more than once.

**Conclusion:**

```text
two requests at the same time can consume the same capacity twice
```

---

### FMV-02 — Stale-Capacity Decision

**Meaning:**

A reservation decision uses a capacity view that does not include another accepted reservation that should affect the decision.

**Core Failure Risk:**

An incomplete capacity view can make an invalid reservation appear valid.

**Conclusion:**

```text
an old capacity view can make an invalid reservation look valid
```

---

### FMV-03 — Split Decision and Commitment

**Meaning:**

A request is evaluated as acceptable, but before its acceptance is reflected as capacity-consuming state, another overlapping request is also evaluated as acceptable.

**Core Failure Risk:**

A gap between checking capacity and recording acceptance can allow multiple accepted reservations to slip through.

**Conclusion:**

```text
a gap between checking capacity and recording acceptance
can allow multiple accepted reservations to slip through
```

---

### FMV-04 — Group Overflow

**Meaning:**

No individual request exceeds remaining capacity, but the combined accepted group exceeds it.

**Core Failure Risk:**

Many individually valid reservations can still overflow capacity together.

**Conclusion:**

```text
many individually valid reservations
can still overflow capacity together
```

---

### Variant Summary

| Variant                                  | Conclusion                                                                                                         |
| ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| FMV-01 — Shared-Capacity Over-Acceptance | Two requests at the same time can consume the same capacity twice.                                                 |
| FMV-02 — Stale-Capacity Decision         | An old capacity view can make an invalid reservation look valid.                                                   |
| FMV-03 — Split Decision and Commitment   | A gap between checking capacity and recording acceptance can allow multiple accepted reservations to slip through. |
| FMV-04 — Group Overflow                  | Many individually valid reservations can still overflow capacity together.                                         |

Together, these variants show that overselling is not only an individual-request problem.

A reservation request can look valid when viewed alone and still contribute to an invalid accepted total.

The correctness problem is collective:

```text
accepted reservations, together,
must not exceed available capacity
```

---

## 11. Concrete Failure Scenarios

The following scenarios illustrate how the failure mechanism variants can appear operationally.

These scenarios are explanatory examples only.

They are not stable correctness attack directions.

One scenario may illustrate multiple failure mechanism variants at the same time.

---

### Scenario 1 — Two Attempts Compete for One Remaining Unit

Initial local capacity state:

```text
Available capacity: 1
Committed reserved quantity: 0
Remaining capacity: 1
```

Concurrent activity:

```text
Purchase attempt A requests quantity 1.
Purchase attempt B requests quantity 1.
```

Failure path:

```text
A observes remaining capacity 1.
B observes remaining capacity 1.

A evaluates quantity 1 as acceptable.
B evaluates quantity 1 as acceptable.

A is accepted.
B is accepted.
```

Result:

```text
Committed reserved quantity: 2
Available capacity: 1
```

Overselling occurred because accepted reserved quantity exceeds available capacity.

Each request looked acceptable alone; together, they exceed capacity.

Illustrates:

- FMV-01 — Shared-Capacity Over-Acceptance
- FMV-04 — Group Overflow

---

### Scenario 2 — Many Small Attempts Exceed Capacity Together

Initial local capacity state:

```text
Available capacity: 10
Committed reserved quantity: 7
Remaining capacity: 3
```

Concurrent activity:

```text
Attempt A requests quantity 1.
Attempt B requests quantity 1.
Attempt C requests quantity 1.
Attempt D requests quantity 1.
```

Failure path:

```text
Each attempt observes remaining capacity 3.
Each attempt evaluates quantity 1 as acceptable.
Each attempt is accepted.
```

Result:

```text
Additional accepted quantity: 4
Remaining capacity before attempts: 3
Committed reserved quantity after attempts: 11
Available capacity: 10
```

Overselling occurred because a group of small requests exceeded the remaining capacity when accepted together.

This scenario shows that overselling is not limited to one large request.

It can emerge from multiple individually small, apparently harmless decisions.

Illustrates:

- FMV-04 — Group Overflow

---

## 12. Concept Clarifications

`Available capacity` is the local capacity limit relevant to the reservation decision. This run does not decide where that capacity comes from, how it is stored, how it is refreshed, or how it relates to external inventory systems.

`Committed reserved quantity` is quantity consumed by accepted reservation decisions inside Inventory Reservation. Only accepted reservations consume capacity. Rejected reservations do not.

`Remaining capacity` is the capacity that appears available after accounting for committed accepted reservations. This is an operational reasoning concept, not a stored-field or calculation decision.

`Overselling` means committed reserved quantity exceeds available capacity for the relevant reservable capacity.

`Concurrency` means overlapping reservation attempts compete for the same local capacity before the combined effect of accepted decisions is safely reflected in the decision result. It is the dominant pressure because it can make individually acceptable decisions collectively unsafe.

---

## 13. Signals for Run 3

Run 3 should account for these signals:

- The correctness risk is caused by accepted reservations as a group, not only by individual requests.
- The important quantity is committed accepted quantity relative to available capacity.
- Rejected reservations should not consume capacity.
- The candidate invariant must survive overlapping decisions against the same capacity.
- The candidate invariant must stay local to Inventory Reservation.
- The candidate invariant should account for shared-capacity over-acceptance, stale-capacity decisions, split decision and commitment, and group overflow.
- The candidate invariant should not expand into order creation, payment interpretation, final outcome composition, reservation expiration, release, external inventory synchronization, API design, or database design.

These are exploration outcomes, not final invariant statements or guarantees. Their purpose is to keep Run 3 from guessing or narrowing the failure too much.

---

## 14. Assumptions

This run assumes:

- The selected slice boundary from Run 1 is accepted.
- Inventory Reservation owns the local reservation-capacity decision.
- Accepted reservations consume local reservation capacity.
- Rejected reservations do not consume local reservation capacity.
- Overselling is evaluated inside local Inventory Reservation capacity correctness.
- The current version focus is `V1 — local correctness`.
- Cross-area final outcome correctness is not required to explain this local failure mechanism.
- Reservation expiration, release, and compensation are not required to explain the current target failure.
- External inventory synchronization is outside this slice.

These assumptions are sufficient for Run 2 exploration.

If later reasoning shows that any assumption affects the current target failure inside the slice boundary, the owning earlier artifact must be revised instead of silently patched later.

---

## 15. Open Questions

The following questions remain open for later runs or downstream work:

- What exact correctness rule must accepted reservations never violate?
- What scope should the candidate invariant use: one capacity item, one reservable capacity unit, one product-like capacity, or another project-specific capacity identity?
- What guarantees are needed so accepted decisions remain safe under concurrency?
- What must be true about rejected decisions so rejected requests do not consume capacity?
- What conceptual state must later implementation requirements require without choosing a schema?
- What observable behavior is needed later without defining an API contract?

These questions do not block Run 2. They are intentionally left for Run 3, Run 4, Run 5, and Run 6.

---

## 16. Out-of-Scope Observations

The following concerns are outside this run and outside the current slice boundary unless later reasoning proves they directly affect local overselling:

```text
Order creation:
    downstream behavior that may depend on reservation, but does not own local reservation-capacity correctness

Order idempotency:
    separate candidate slice for duplicate order creation under retries or duplicate requests

Payment execution and payment interpretation:
    separate responsibility area and later correctness concern

Final outcome uniqueness:
    separate candidate slice

Final outcome composition:
    deferred cross-area correctness concern

Reservation expiration:
    not required to explain immediate overselling under concurrent accepted reservations

Reservation release:
    not required to explain immediate overselling under concurrent accepted reservations

Downstream compensation:
    not required to explain immediate reservation-capacity violation

External inventory synchronization:
    outside local Inventory Reservation correctness

Database schema:
    downstream implementation design concern

API design:
    downstream interface design concern

Test implementation:
    downstream validation implementation concern
```

These observations are separated so Run 3 does not accidentally expand the invariant beyond the selected slice.

---

## 17. Run 2 Result

The slice world is understandable enough to continue to candidate invariant construction.

The explored failure mechanism is:

```text
Concurrent reservation attempts can each appear acceptable against the same available capacity, then all be accepted, causing committed reserved quantity to exceed available capacity.
```

The important failure variants are:

- shared-capacity over-acceptance
- stale-capacity decision
- split decision and commitment
- group overflow

The target failure, dominant pressure, and failure mechanism are clear. Concrete failure scenarios have been recorded, and assumptions, open questions, and out-of-scope observations have been separated.

This artifact is ready for `Run 3 — Candidate Invariant + Invariant Challenge`.
