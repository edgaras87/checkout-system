# **Methodology**

---

## **Purpose**

This document explains the construction approach used by `checkout-system`.

The project is built by defining the system first, deriving bounded correctness work from that definition, and implementing behavior only after correctness is explicit.

The goal is to prevent implementation details from silently defining system correctness.

---

## **Core Approach**

The project follows this direction:

```text
define system truth
    → identify bounded correctness Work Units
    → construct correctness for one Work Unit
    → translate correctness into implementation requirements
    → implement against those requirements
    → validate under the pressure that made the failure possible
```

The project does not begin from database tables, APIs, packages, or framework structure.

It begins from:

```text
- what the system is responsible for
- what the system controls
- what it does not control
- what failures it must prevent
- what pressures make those failures likely
```

---

## **System Definition First**

Before implementation begins, the project defines the system at the responsibility level.

System Definition establishes:

```text
- system purpose
- system boundary
- external uncertainty
- responsibility ownership
- known failure areas
- interaction and composition risks
- candidate Work Units
```

This internal definition is the source of project truth.

Public documentation summarizes selected parts of that truth, but does not replace it.

---

## **Responsibility-Based Reasoning**

The system is understood through responsibility ownership, not technical components.

Responsibility areas own decisions and state.

They are not treated as:

```text
- packages
- services
- controllers
- database tables
- execution steps
```

For `checkout-system`, the main responsibility areas are:

```text
- Inventory Reservation
- Order Management
- Payment Outcome Handling
- Checkout Outcome Resolution
```

This keeps system reasoning independent from later implementation structure.

---

## **Problem-Driven Work Units**

Construction work is derived from known failures and pressures.

A Work Unit is selected when a specific failure can be bounded clearly enough to reason about and validate.

Examples of pressures include:

```text
- concurrency
- retries
- duplicate requests
- delayed signals
- out-of-order signals
- partial information
- non-atomic coordination
```

Candidate Work Units are tracked in `internal/execution/slice-register.md`.

The register identifies future work.

It does not solve the work.

---

## **Correctness Before Implementation**

For each selected Work Unit, correctness must be constructed before implementation.

This means defining:

```text
- the exact failure being prevented
- the pressure under which the failure appears
- what must always hold
- what the system must guarantee
- what implementation must provide to enforce those guarantees
```

Implementation must follow constructed correctness.

It must not redefine the failure, weaken the guarantees, or hide unresolved correctness questions inside code.

---

## **Validation Under Pressure**

A Work Unit is not complete just because code exists.

Validation must target the pressure that made the failure possible.

For example:

```text
- concurrency problems require concurrency-oriented validation
- retry problems require duplicate-request validation
- delayed-signal problems require delayed or out-of-order scenarios
- composition problems require checks across interacting responsibilities
```

Validation records what was checked and what remains outside the current scope.

---

## **Documentation Separation**

The project separates documentation responsibilities:

```text
internal/
    records construction truth, state, reasoning, and execution records

docs/
    explains selected project understanding for readers

README.md
    orients readers and routes them to documentation
```

Information flows outward:

```text
internal → docs → README.md
```

Public documentation explains the project.

It does not replace internal project truth.

---

## **One-Line Mental Model**

```text
Define the system, derive bounded failures, construct correctness, then implement only what the constructed correctness requires.
```
