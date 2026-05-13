# Project State

## 1. Purpose

This document records the current execution state of `checkout-system`.

It answers:

```text
What is true about the project state right now?
```

It does not define system behavior, project navigation, public explanation, implementation design, correctness construction, or validation evidence.

## 2. Project Identity

```text
Project name: checkout-system
Repository name: checkout-system
Local folder: checkout-system
```

Project description:

```text
Correctness-driven checkout backend for resolving purchase attempts into one consistent final outcome under concurrency, retries, and unreliable external signals.
```

## 3. Repository

```text
Provider: GitHub
Remote URL: https://github.com/edgaras87/checkout-system
Visibility: public
Default branch: main
```

Repository description:

```text
Correctness-driven checkout backend for resolving purchase attempts into one consistent final outcome under concurrency, retries, and unreliable external signals.
```

## 4. Current Execution Position

```text
Phase: Preparation
Current stratum: Stratum 4 — Infrastructure Services completed
Status: ready to begin Stratum 5 — Service Constraints
Construction status: not started
```

## 5. Completed Preparation Work

```text
Completed strata:
    - Stratum 1 — Project Workspace & History
    - Stratum 2 — System Definition
    - Stratum 3 — Execution Environment
    - Stratum 4 — Infrastructure Services
```

Stratum 1 established:

```text
- controlled repository workspace
- Git text normalization
- repository ignore rules
- minimal repository entry point
- initial project state record
```

Stratum 2 established:

```text
- project description
- project intent
- system context and boundary
- responsibility structure
- problem space
- interaction model
- initial slice register
- internal System Definition navigation
- public system overview
- public methodology explanation
- README alignment
- internal project navigation map
```

Stratum 3 established:

```text
- Docker-compatible local container runtime expectation
- Compose as local orchestration mechanism
- compose.yaml as project-level orchestration entry point
- setup documentation for local container usage
- setup/local environment reading path in the project map
```

Stratum 4 established:

```text
- PostgreSQL as the local infrastructure service
- PostgreSQL as the persistent state service
- PostgreSQL service definition in compose.yaml
- service-level PostgreSQL environment variables
- example local environment file
- persistent named volume for local PostgreSQL data
- local PostgreSQL setup documentation
- setup/local environment reading path updated for PostgreSQL service usage
```

## 6. Active Constraints

```text
- System Definition is the current source of system truth.
- Public documentation is a projection of internal truth.
- README is an entry point, not a project-state record.
- project-map.md routes reading and does not define project truth.
- slice-register.md is an execution bridge and does not solve Work Units.
- The execution environment defines how local infrastructure runs.
- Infrastructure services define which local service capabilities exist.
- PostgreSQL exists only as a local infrastructure service at this stage.
- PostgreSQL bootstrap variables are container initialization settings, not the final database authority model.
- compose.yaml is the local infrastructure orchestration entry point.
- .env.example defines example service-level local configuration.
- docs/setup/postgres-local.md explains local PostgreSQL service usage.
- Service constraints are deferred to Stratum 5.
- Application bootstrap is deferred to Stratum 6.
- Construction has not started.
- Later strata must align with System Definition.
```

Not yet defined:

```text
- database role model
- schemas
- privileges
- migrations
- application bootstrap
- baseline runtime
- baseline verification
```

## 7. Readiness / Next Transition

```text
Stratum 4 — Infrastructure Services is complete.

The project is ready to begin Stratum 5 — Service Constraints.
```

Next expected work:

```text
Define the internal rules and constraints required for PostgreSQL service usage, including database authority, role separation, schema ownership, and privilege boundaries.
```

## 8. Deferred or Unresolved Work

Deferred to later Preparation strata:

```text
- service constraints
- application bootstrap
```

Deferred to Construction:

```text
- Reservation Capacity Correctness
- Order Idempotency
- Payment Interpretation Under Conflict
- Final Outcome State Uniqueness
- Final Outcome Composition Correctness
```

## 9. Last Updated Rule

Update this document when:

```text
- a preparation stratum starts or completes
- lifecycle position changes
- Construction begins
- readiness changes
- major active constraints change
- deferred or unresolved work changes materially
```

Do not update this document for:

```text
- ordinary wording changes
- small documentation edits
- navigation-only project-map changes
- minor formatting cleanup
```

## 10. One-Line State

```text
Stratum 4 — Infrastructure Services is complete; the project is ready to begin Stratum 5 — Service Constraints.
```
