# Project Map

## 1. Purpose

This document is the internal navigation map for `checkout-system`.

It tells readers where to read for common project work scenarios.

It routes reading.

It does not define:

```text
- project state
- system behavior
- correctness
- implementation
- validation evidence
- reusable convention rules
```

Source documents define truth.

This map points to those source documents.

## 2. Authority Rule

This project map is not the source of project truth.

If this map conflicts with a source document, the source document is authoritative and this map must be updated.

Authority order:

```text
internal/project-state.md
    → current project position

internal/system-definition/
    → system truth

internal/execution/
    → execution bridge and future Work Unit records

docs/
    → public/project-facing explanation

README.md
    → repository entry point
```

Information flows outward:

```text
internal → docs → README.md
```

## 3. Main Reading Entry Points

Use these files as starting points:

```text
README.md
    Public repository entry point.

docs/system-overview.md
    Public explanation of what the system is.

docs/methodology.md
    Public explanation of the construction approach.

docs/setup/README.md
    Setup documentation entry point.

internal/project-state.md
    Current project position and readiness record.

internal/system-definition/README.md
    Navigation for internal System Definition.

internal/execution/slice-register.md
    Candidate Work Unit register and execution bridge.
```

## 4. Current State Reading Path

Use this path when the task is about:

```text
- current phase
- current stratum
- completed preparation work
- readiness
- next work
- active constraints
```

Read:

```text
internal/project-state.md
```

Do not infer current state from README, public docs, or this project map.

## 5. System Definition Reading Path

Use this path when the task is about:

```text
- what the system is
- why it exists
- what it controls
- what it does not control
- responsibility ownership
- problem space
- interaction and composition risks
```

Read in order:

```text
internal/system-definition/README.md
internal/system-definition/project-description.md
internal/system-definition/project-intent.md
internal/system-definition/system-context.md
internal/system-definition/system-structure.md
internal/system-definition/problem-space.md
internal/system-definition/interaction-model.md
```

System Definition source truth lives in:

```text
internal/system-definition/
```

Public projections of this truth live in:

```text
docs/system-overview.md
docs/methodology.md
README.md
```

## 6. Public Documentation Reading Path

Use this path when the task is about:

```text
- public project explanation
- portfolio readability
- external reader orientation
- concise project overview
- public methodology explanation
```

Read:

```text
README.md
docs/system-overview.md
docs/methodology.md
```

Responsibilities:

```text
README.md
    → orients readers and links to public documentation

docs/system-overview.md
    → explains the system publicly

docs/methodology.md
    → explains the construction approach publicly
```

Public documentation explains selected project understanding.

It does not replace internal truth.

## 7. Setup / Local Environment Reading Path

Use this path when the task is about:

```text
- local setup
- container runtime expectations
- Compose usage
- local infrastructure orchestration
```

Read:

```text
docs/setup/README.md
docs/setup/local-containers.md
compose.yaml
```

Responsibilities:

```text
docs/setup/README.md
    → setup documentation navigation

docs/setup/local-containers.md
    → local container runtime and Compose usage

compose.yaml
    → project-level local orchestration entry point
```

This path does not define:

```text
- infrastructure services
- service constraints
- application bootstrap
- system behavior
- project state
```

## 8. Execution Bridge Reading Path

Use this path when the task is about:

```text
- candidate Work Units
- selected Work Unit
- slice status
- deferred correctness work
- future Construction entry
```

Read:

```text
internal/execution/slice-register.md
```

The slice register identifies executable correctness slices.

It does not solve them.

## 9. Construction Reading Path

Use this path when the task is about:

```text
- selected Work Unit
- Work Unit status
- slice clarification
- correctness construction
- implementation requirements
- validation records
- completion records
```

Read:

```text
internal/project-state.md
internal/execution/slice-register.md
internal/execution/slices/<slice-id>-<slice-name>/
```

Expected per-slice artifact location pattern:

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

        validation-record.md
        completion-record.md
```

Detailed slice truth belongs in the slice artifacts, not in this map.

## 10. Consistency Groups

### System Definition Consistency Group

Check together when system definition changes:

```text
internal/system-definition/project-description.md
internal/system-definition/project-intent.md
internal/system-definition/system-context.md
internal/system-definition/system-structure.md
internal/system-definition/problem-space.md
internal/system-definition/interaction-model.md
internal/system-definition/README.md
internal/execution/slice-register.md
docs/system-overview.md
docs/methodology.md
README.md
```

### Public Documentation Consistency Group

Check together when public-facing explanation changes:

```text
README.md
docs/system-overview.md
docs/methodology.md
```

### Setup / Local Environment Consistency Group

Check together when local setup or execution environment changes:

```text
compose.yaml
docs/setup/README.md
docs/setup/local-containers.md
internal/project-map.md
internal/project-state.md
```

### Execution Bridge Consistency Group

Check together when candidate Work Units or execution entry changes:

```text
internal/system-definition/problem-space.md
internal/system-definition/interaction-model.md
internal/execution/slice-register.md
internal/project-state.md
```

## 11. Maintenance Rule

Update this project map when:

```text
- important files are added, renamed, or moved
- a new major project area appears
- a recurring reading scenario appears
- a reading path becomes inaccurate
- a consistency group changes
- convention usage changes
```

Do not update this project map when:

```text
- project state changes but reading paths stay the same
- wording changes inside a source document
- one small implementation file changes
- one test is added
```

Project state changes belong in:

```text
internal/project-state.md
```

This map changes when navigation changes.

## 12. One-Line Mental Model

```text
Use this map to find where to read; do not use it as the source of truth.
```
