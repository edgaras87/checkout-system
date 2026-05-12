# System Definition

## 1. Purpose

This folder contains the internal System Definition for `checkout-system`.

It records the project-specific system truth established during:

```text
Preparation Phase
    → Stratum 2 — System Definition
```

This folder answers:

```text
- what the system is
- why the system exists
- what the system controls
- what the system does not control
- how responsibility ownership is divided
- what failures and pressures are known
- where interaction and composition risks exist
- which candidate Work Units may later be executed
```

This README is navigation only.

It does not define system truth.

System truth remains in the individual System Definition documents.

## 2. Authority Rule

The authority order is:

```text
internal/system-definition/   → system truth
internal/execution/           → execution bridge
docs/                         → public/project-facing projection
README.md                     → repository entry point
```

Information flows outward:

```text
internal → docs → README.md
```

Do not use this README as a replacement for the individual System Definition documents.

Do not use public documentation or the root README as the source of internal system truth.

## 3. Reading Order

Read the System Definition in this order:

```text
1. project-description.md
2. project-intent.md
3. system-context.md
4. system-structure.md
5. problem-space.md
6. interaction-model.md
7. ../execution/slice-register.md
```

## 4. Document Responsibilities

### project-description.md

Seeds the initial project idea.

It records the rough domain direction, high-level system purpose, responsibility areas, challenge, and evolution direction.

It is the starting description, not the final authority for every later decision.

### project-intent.md

Defines why `checkout-system` exists and what it must achieve.

It anchors:

```text
- system purpose
- core goal
- system responsibility
- failure definition
- success criteria
- scope
- non-goals
```

Use this document to understand the identity of the system.

### system-context.md

Defines the System of Interest, its boundary, and its external environment.

It identifies:

```text
- what is inside the system
- what is outside the system
- external entities
- external interactions
- authority boundaries
- environmental assumptions
- environmental constraints
```

Use this document to understand what the system controls and what it must tolerate.

### system-structure.md

Defines internal responsibility ownership.

It identifies responsibility areas as ownership boundaries for state and decisions.

Responsibility areas are not:

```text
- flow steps
- packages
- services
- modules
- database tables
```

Use this document to understand which area owns which decisions and state.

### problem-space.md

Maps known local failures, risks, and pressures.

It identifies where the system can fail under pressure.

It does not define:

```text
- invariants
- guarantees
- proof
- enforcement strategies
- implementation requirements
```

Use this document to understand the problem surface that future Construction work may address.

### interaction-model.md

Defines cross-responsibility behavior and composition risks.

It explains where locally valid decisions may still fail to compose into one consistent final outcome.

It does not replace `system-structure.md`.

Use this document to understand coordination and system-level consistency risks.

### ../execution/slice-register.md

Records candidate Work Units derived from the System Definition.

It is the bridge from Preparation to future Construction.

It does not execute slices.

It does not define:

```text
- final invariants
- final guarantees
- implementation requirements
- validation records
- per-slice reasoning
```

Use this document to see which bounded correctness problems may later become Construction work.

## 5. Relationship to Public Documentation

Public documentation is projected from internal System Definition.

Expected public projections:

```text
../../docs/system-overview.md
../../docs/methodology.md
```

Public documentation explains selected project understanding for readers.

It does not replace internal System Definition.

## 6. Relationship to Future Construction

System Definition prepares future Construction by making the system stable enough to select bounded Work Units.

Construction must not begin by redefining:

```text
- system purpose
- system boundary
- responsibility ownership
- known problem areas
- interaction risks
```

If future work discovers a missing or incorrect system-level decision, update the document that owns that decision.

Do not hide system-definition changes inside slice execution artifacts.

## 7. Boundary

This folder may contain:

```text
- system identity
- system purpose
- system boundary
- responsibility ownership
- known failures and pressures
- interaction and composition risks
```

This folder must not contain:

```text
- implementation code
- database schema
- API contracts
- infrastructure decisions
- final invariants
- final guarantees
- enforcement strategies
- validation evidence
- completed slice reasoning
```

## 8. Maintenance Rule

Update this README only when:

```text
- a System Definition document is added
- a System Definition document is renamed or removed
- the reading order changes
- the relationship to the execution bridge changes
```

Do not update this README to record ordinary project state.

Project state belongs in:

```text
../project-state.md
```

## 9. One-Line Mental Model

```text
Read this folder to understand the system before trying to solve any part of it.
```
