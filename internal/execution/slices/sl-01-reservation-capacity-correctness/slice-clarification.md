# Slice Clarification — SL-01 — Reservation Capacity Correctness

## 1. Purpose

This document clarifies the boundary and operational framing for `SL-01 — Reservation Capacity Correctness`.

It answers:

- what this slice is solving
- what is inside the slice
- what is outside the slice
- how the slice should be understood operationally

This artifact is produced by `Run 1 — Clarification + Operational Framing`.

It does not define invariants, guarantees, implementation requirements, implementation strategy, database schema, code design, API design, test strategy, validation evidence, or completion state.

---

## 2. Source Inputs

This clarification is based on:

- `internal/project-state.md`
- `internal/execution/slice-register.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/execution-context.md`

The accepted incoming context for this slice is:

```text
internal/execution/slices/sl-01-reservation-capacity-correctness/execution-context.md
```

---

## 3. Selected Slice

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

Selection rationale:

```text
Foundational correctness requirement for all purchase attempts.
```

---

## 4. Framing Mode

This run uses direct framing.

The selected slice has one clear interpretation: Inventory Reservation must clarify the local reservation-capacity decision boundary so overselling can later be explored under concurrent purchase attempts.

No competing interpretation is being selected in this run.

---

## 5. Target Failure

The target failure is `Overselling`.

For this slice, overselling means:

```text
Committed reserved quantity exceeds available capacity for the relevant reservable inventory capacity.
```

This slice is not about every possible reason a purchase attempt may fail. It is specifically about the capacity correctness of reservation decisions.

The failure appears when multiple purchase attempts compete for the same available capacity and reservation decisions allow more quantity to be accepted than the available capacity supports.

Concurrency is the dominant pressure because competing reservation attempts may be evaluated close together, overlap, or race against the same available capacity.

---

## 6. Responsibility Area Authority

The responsibility area is `Inventory Reservation`.

Inventory Reservation owns the local decision about whether requested items can be reserved for a purchase attempt.

Inside this slice, Inventory Reservation has authority over:

- accepting or rejecting reservation decisions
- recording reservation decisions as local reservation state
- treating reserved quantities as local reservation state
- making capacity-relevant reservation decisions

Inventory Reservation does not own:

- physical inventory management
- external inventory system correctness
- order creation
- payment interpretation
- final checkout outcome determination
- cross-area semantic composition

Other responsibility areas may depend on reservation decisions, but they do not override the Inventory Reservation decision inside this slice.

---

## 7. Included Behavior

This slice includes local Inventory Reservation behavior needed to clarify reservation-capacity decisions:

- evaluating whether a purchase attempt can reserve requested quantity
- accepting a reservation decision when capacity is available
- rejecting a reservation decision when capacity is not available
- treating accepted reservations as consuming local reservation capacity
- handling concurrent reservation attempts competing for the same capacity

The included behavior is limited to the reservation-capacity decision.

---

## 8. Excluded Behavior

This slice excludes:

- physical inventory synchronization
- external inventory system behavior
- external stock correction
- order creation
- order idempotency
- payment execution
- payment interpretation
- final outcome determination
- final outcome uniqueness
- final outcome composition correctness
- cross-area reconciliation
- late-arriving signal handling
- partial-information coordination
- reservation expiration
- reservation release
- compensation after failed downstream steps

These concerns are outside the Run 1 boundary unless later reasoning proves that one of them is required to clarify the current target failure.

At this stage, none of the excluded concerns is required to clarify the local overselling boundary.

---

## 9. Relevant State Concepts

The relevant state concepts are:

```text
available capacity:
    the local capacity limit relevant to a reservation decision

reserved quantity:
    quantity already committed by accepted reservation decisions

reservation record:
    local record that a reservation decision exists for a purchase attempt

purchase attempt:
    the attempt requesting reservation capacity

reservation decision:
    the local accepted or rejected result produced by Inventory Reservation
```

These concepts are only operational vocabulary for this clarification. They do not define tables, columns, indexes, entities, repositories, DTOs, locking fields, transaction boundaries, or persistence strategy.

This run does not decide how available capacity is derived, stored, synchronized, locked, calculated, or enforced.

---

## 10. Relevant Actions

The relevant actions are:

```text
request reservation:
    a purchase attempt asks Inventory Reservation to reserve quantity

evaluate reservation capacity:
    Inventory Reservation determines whether local available capacity can support the requested quantity

accept reservation:
    Inventory Reservation decides that the requested quantity is reserved

reject reservation:
    Inventory Reservation decides that the requested quantity is not reserved

record reservation decision:
    Inventory Reservation preserves the local decision result as reservation state
```

These actions describe the slice at the operational level only. They do not define implementation flow, code structure, persistence model, API behavior, or tests.

This run does not decide whether these actions happen through one operation, multiple operations, a transaction, a lock, a conditional update, or any other enforcement mechanism.

---

## 11. Operational Framing

This slice is a local correctness slice.

Inventory Reservation receives competing reservation requests from purchase attempts. Each request asks for quantity against limited available capacity.

Inventory Reservation decides whether each request is accepted or rejected. Accepted reservations are treated as relevant to local reservation capacity consumption. Rejected reservations do not.

The slice boundary is correct only if the local reservation-capacity decision space is clear enough for later reasoning to explain how overselling can happen and what must be prevented.

This slice is not responsible for deciding what happens after reservation acceptance. It is also not responsible for composing reservation, order, payment, and final outcome into a full checkout result.

Its responsibility is to clarify the local reservation-capacity boundary before correctness construction continues.

---

## 12. Clarified Slice Boundary

`SL-01` solves local reservation capacity correctness inside Inventory Reservation.

It clarifies the local decision boundary where accepted reservations must be reasoned against available capacity under concurrent purchase attempts.

Inside the boundary:

- local reservation decision authority
- accepted versus rejected reservation decisions
- reserved quantity as local capacity consumption
- overselling as the target failure
- concurrency as the dominant pressure

Outside the boundary:

- downstream order behavior
- downstream payment behavior
- final outcome behavior
- external inventory behavior
- cross-area semantic consistency
- recovery and reconciliation behavior

This slice does not solve order creation, payment interpretation, final outcome resolution, cross-area composition, external inventory correctness, reconciliation, or downstream compensation.

---

## 13. Readiness for Run 2

This slice is ready for `Run 2 — Exploration + Failure Mechanism` when the following framing is accepted:

```text
The world to explore is the local Inventory Reservation decision space.

The failure to explore is overselling.

The pressure to explore is concurrency.

The boundary to preserve is local reservation capacity correctness, not full checkout outcome correctness.
```

Run 2 may explore how overselling can occur inside this boundary.

Run 2 must not redefine this slice as an order, payment, final outcome, API, database schema, or implementation strategy problem.
