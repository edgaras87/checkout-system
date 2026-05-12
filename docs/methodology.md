# Methodology

## 1. Purpose

`checkout-system` is built using a correctness-first construction approach.

The project treats difficult backend behavior as a sequence of bounded correctness problems rather than as one large implementation task.

Construction work focuses on understanding why failures happen under pressure, defining the guarantees required to prevent them, and then implementing and validating those guarantees explicitly.

This document explains how Construction work is organized inside this repository and how to read the related artifacts.

It is a reader-facing overview, not the source of truth for project state or active slice execution.

---

## 2. Why Slice-Based Construction Exists

Many backend failures are not caused by missing business rules alone.

They emerge under operational pressure such as:

- concurrency
- retries
- duplicate requests
- out-of-order signals
- delayed signals
- partial information
- non-atomic coordination

Instead of implementing the entire checkout flow as one large unit, the project isolates one correctness problem at a time and constructs bounded guarantees around it before moving outward.

This keeps correctness work:

- focused
- pressure-aware
- traceable
- testable
- easier to reason about

The goal is not only to make the system work, but to understand why it remains correct under pressure.

---

## 3. Correctness Slices

The basic unit of Construction is a correctness slice.

A correctness slice is a bounded unit of work focused on:

- one source failure
- one responsibility area
- one target failure
- one dominant pressure

Example:

```text
SL-01 — Reservation Capacity Correctness

Source failure:
    Reservation Capacity Violation

Responsibility area:
    Inventory Reservation

Target failure:
    Overselling

Dominant pressure:
    Concurrency
```

Selected and candidate slices are tracked in:

```text
internal/execution/slice-register.md
```

---

## 4. Construction Flow

Each selected slice moves through this general flow:

```text
selection
    → execution context
    → clarification
    → correctness construction
    → implementation requirements
    → implementation planning
    → implementation
    → validation
    → completion recording
```

The flow intentionally separates:

* context gathering
* slice clarification
* correctness reasoning
* implementation constraints
* implementation strategy
* implementation
* validation
* completion recording

Correctness is constructed before implementation strategy is finalized.

---

## 5. Slice Execution Runs

The reasoning part of slice execution is divided into runs.

A run is a focused reasoning stage with a specific purpose and expected artifact output.

The runs prevent clarification, correctness construction, implementation concerns, and validation concerns from being mixed too early.

### Run 1 — Clarification + Direct Framing

Defines:

* what the slice is solving
* what belongs inside the slice
* what belongs outside the slice
* which concerns are intentionally deferred

Primary artifact:

```text
slice-clarification.md
```

### Run 2 — Exploration + Failure Mechanism

Explores how the target failure can occur under the dominant pressure.

Primary artifact:

```text
construction-notes/exploration-and-failure-mechanism.md
```

### Run 3 — Candidate Invariant + Invariant Challenge

Proposes and challenges a candidate invariant intended to block the target failure.

Primary artifact:

```text
construction-notes/invariant-challenge.md
```

### Run 4 — Refined Invariant + Candidate Guarantees + Guarantee Challenge

Refines the invariant and challenges the guarantees required to preserve it.

Primary artifact:

```text
construction-notes/guarantee-challenge.md
```

### Run 5 — Final Correctness Construction

Consolidates the accepted reasoning into the final correctness artifact.

Primary artifact:

```text
correctness-construction.md
```

This artifact defines:

* final failure framing
* invariant
* required guarantees
* explicit deferrals

### Run 6 — Implementation Requirements

Translates constructed correctness into implementation-facing constraints.

Primary artifact:

```text
implementation-requirements.md
```

This stage defines what implementation must preserve, enforce, or avoid without fully designing the implementation itself.

---

## 6. Artifact Structure

Each slice owns its own execution directory:

```text
internal/execution/slices/
    <slice-id>-<slice-name>/
```

Expected artifact structure:

```text
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
    accepted incoming context

slice-clarification.md
    operational slice boundary

construction-notes/
    reasoning exploration and challenge records

correctness-construction.md
    invariant and guarantees

implementation-requirements.md
    implementation-facing constraints

implementation-plan.md
    implementation strategy and structure

validation-record.md
    validation evidence

completion-record.md
    completed slice state
```

Slice-specific truth belongs inside the slice artifacts themselves.

---

## 7. Validation Philosophy

Correctness is not considered complete until behavior is validated under the pressure that originally created the failure risk.

Validation is intended to answer questions such as:

```text
Does the implementation actually prevent the target failure?

Does it remain correct under the selected dominant pressure?

Do the implemented guarantees hold under realistic execution conditions?
```

Validation evidence is recorded in:

```text
validation-record.md
```

If validation shows the implementation does not satisfy the constructed correctness, the slice returns to the required earlier stage.

---

## 8. Construction Direction

Construction starts with bounded local correctness inside responsibility areas before moving toward broader cross-area composition problems.

The project builds foundational guarantees first, then uses those guarantees as inputs for larger coordination concerns.

---

## 9. Reading Path

For current project position:

```text
internal/project-state.md
```

For selected and candidate slices:

```text
internal/execution/slice-register.md
```

For active slice execution:

```text
internal/execution/slices/<slice-id>-<slice-name>/
```

For system definition and responsibility ownership:

```text
internal/system-definition/
```

---

## 10. One-Line Mental Model

```text
checkout-system builds correctness one bounded failure at a time.
```
