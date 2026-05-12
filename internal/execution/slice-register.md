# Slice Register

---

## 1. Purpose

Construction execution register for checkout-system.

This document tracks:

```text
- executable correctness slices
- current execution focus
- slice status
- deferred work
- discovered future work
- per-slice execution locations
```

---

## 2. Status Model

Slices may use the following statuses:

```text
candidate
selected
in-progress
complete
deferred
superseded
```

Status meanings:

```text
candidate:
    identified as possible future work, but not currently selected

selected:
    chosen as the next Work Unit

in-progress:
    currently moving through the Construction Phase execution cycle

complete:
    implemented, validated, and recorded

deferred:
    intentionally postponed because required foundation is missing
    or because the slice belongs to a later project version

superseded:
    replaced by a clearer or more accurate slice
```

A slice must not be marked `complete` until:

```text
- its scope has been clarified
- correctness has been constructed
- implementation enforces the constructed correctness
- validation targets the slice failure under the defined pressure
- completion has been recorded
```

---

## 3. Slice Register

### SL-01 — Reservation Capacity Correctness

```text
Source: Reservation Capacity Violation
Responsibility area: Inventory Reservation
Target failure: Overselling
Dominant pressure: Concurrency
Status: selected
Execution location:
    internal/execution/slices/sl-01-reservation-capacity-correctness/
```

Selection rationale:

```text
Foundational correctness requirement for all purchase attempts.
```

---

### SL-02 — Order Idempotency

```text
Source: Duplicate Order Creation
Responsibility area: Order Management
Target failure: Multiple orders are created for the same purchase attempt.
Dominant pressure: Retries / duplicate requests
Status: candidate
```

---

### SL-03 — Payment Interpretation Under Conflict

```text
Source: Conflicting Payment Signals
Responsibility area: Payment Outcome Handling
Target failure: Conflicting payment signals produce inconsistent interpreted outcomes.
Dominant pressure: Out-of-order delivery and duplication
Status: candidate
```

---

### SL-04 — Final Outcome State Uniqueness

```text
Source: Multiple Final Outcomes
Responsibility area: Checkout Outcome Resolution
Target failure: More than one final outcome state exists for the same purchase attempt.
Dominant pressure: Retries / duplicate resolution attempts / repeated completion signals
Status: candidate
```

Boundary note:

```text
This slice addresses uniqueness of final outcome authority only.

Cross-area semantic correctness is deferred to V3.
```

---

### SL-V3-01 — Final Outcome Composition Correctness

```text
Source: Interaction Model — Final Outcome Composition
Responsibility area / interaction: Cross-area interaction
Target failure: Final outcome contradicts reservation, order, or payment decisions.
Dominant pressure: Partial information, delayed signals, and non-atomic coordination
Status: deferred
```

Deferral note:

```text
Requires foundational local guarantees from V1 first.
```

---

## 4. Current Execution Focus

```text
Current slice:
    SL-01 — Reservation Capacity Correctness

Current status:
    selected

Current version focus:
    V1 — local correctness
```

---

## 5. Discovery Register

```text
- late-arriving signal handling
- partial information handling
- reconciliation strategies
```

Current decision:

```text
Deferred to future slices or later project versions.
```

---

## 6. Per-Slice Artifact Pattern

```text
internal/execution/slices/
    <slice-id>-<slice-name>/
        execution-context.md
        slice-clarification.md

        construction-notes/
            exploration-and-failure-mechanism.md
            invariant-challenge.md
            guarantee-challenge.md

        correctness-construction.md
        implementation-requirements.md
        implementation-plan.md

        validation-record.md
        completion-record.md
```

Artifact responsibilities:

```text
execution-context.md
    → defines the execution context

slice-clarification.md
    → defines slice boundary

construction-notes/
    → records construction exploration and challenges

correctness-construction.md
    → constructs invariant and guarantees

implementation-requirements.md
    → translates correctness into implementation constraints

implementation-plan.md
    → records selected implementation strategy and execution plan

validation-record.md
    → records validation evidence

completion-record.md
    → records completed slice state
```
