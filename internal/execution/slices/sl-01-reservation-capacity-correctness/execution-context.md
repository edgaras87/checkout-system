# Execution Context — SL-01 — Reservation Capacity Correctness

## 1. Purpose

This document collects the source context needed before executing `SL-01 — Reservation Capacity Correctness`.

It prepares the accepted incoming context for slice execution and constrains Run 1 — Clarification + Direct Framing.

This artifact does not define the final slice boundary, invariant, guarantees, implementation requirements, implementation strategy, database schema, API design, test strategy, validation plan, or completion criteria.

---

## 2. Selected Slice

Slice ID: `SL-01`  
Slice name: `Reservation Capacity Correctness`  
Status: `in-progress`  
Execution location: `internal/execution/slices/sl-01-reservation-capacity-correctness/`

From the slice register:

- Source: `Reservation Capacity Violation`
- Responsibility area: `Inventory Reservation`
- Target failure: `Overselling`
- Dominant pressure: `Concurrency`
- Selection rationale: foundational correctness requirement for all purchase attempts
- Current version focus: `V1 — local correctness`

---

## 3. Responsibility Area Context

`Inventory Reservation` manages reservation decisions for purchase attempts.

It owns:

- reservation records
- reserved quantities
- decisions about whether requested items can be reserved

It does not own:

- physical inventory
- order creation
- payment interpretation
- final outcome

Each decision belongs to exactly one responsibility area. Responsibility areas may depend on one another, but they cannot override one another’s decisions.

At the system boundary, the Checkout System owns the state and decision logic required to support purchase attempts and represent final outcomes. It does not control external inventory systems or external system behavior.

---

## 4. State Context

Relevant state concepts for this slice are:

- reservation records
- reserved quantities
- available capacity relevant to reservation decisions
- purchase attempts evaluated for reservation

This is conceptual state only. It does not define tables, columns, indexes, entities, DTOs, repositories, locking fields, or any other implementation structure.

---

## 5. Decision Context

Relevant decisions are:

- whether requested items can be reserved for a purchase attempt
- whether a reservation decision is accepted or rejected

The key decision pressure is that multiple purchase attempts may try to reserve against the same available capacity at the same time.

Reservation decisions influence whether order creation is feasible. Order creation depends on reservation and must reflect a valid reservation decision.

This context identifies decision relationships only. It does not define decision algorithms, invariants, guarantees, or enforcement strategy.

---

## 6. Boundary Exclusions

This slice does not own:

- physical inventory management
- external inventory system correctness
- external system behavior
- order creation
- order idempotency
- payment execution
- payment interpretation
- final outcome determination
- final outcome uniqueness
- cross-area outcome composition
- reconciliation strategy
- late-arriving signal handling
- partial-information coordination

Cross-area coordination and composition concerns are deferred to later slices or later project versions unless they are directly required by the selected target failure.

---

## 7. Failure Context

The target failure is `Overselling`.

For this slice, overselling means reserved quantity exceeds available capacity.

The dominant pressure is concurrency: concurrent reservation attempts may compete for the same available capacity.

The source failure is `Reservation Capacity Violation`. Future correctness work must address how committed reservations avoid exceeding available capacity.

This failure context does not include expiration or cross-area coordination, and it does not define the invariant, guarantees, proof, or enforcement strategy.

---

## 8. Version Scope

The current version focus is `V1 — local correctness`.

Current execution is limited to bounded local correctness inside `Inventory Reservation`.

Deferred to future slices or later project versions:

- duplicate order creation
- conflicting payment interpretation
- multiple final outcomes
- final outcome composition correctness
- reconciliation strategies
- delayed-signal coordination
- partial-information coordination

Cross-area composition work remains deferred until bounded local correctness exists for participating responsibility areas.

---

## 9. Context Sources

- `internal/project-state.md`
- `internal/execution/slice-register.md`
- `internal/system-definition/project-intent.md`
- `internal/system-definition/system-context.md`
- `internal/system-definition/system-structure.md`
- `internal/system-definition/problem-space.md`
- `internal/system-definition/interaction-model.md`
- `docs/methodology.md`

---

## 10. Context Use Rule

This document is accepted incoming context for slice execution.

It constrains Run 1 — Clarification + Direct Framing, but it does not replace slice clarification.

Slice boundary, invariant, guarantees, implementation requirements, implementation planning, validation, and completion are defined in later artifacts.

