# Baseline Docs Reference

Status: accepted  
Version: v1  
Scope: checkout-system Preparation exit artifact

---

## 1. Purpose

This document explains the role of implementation baseline docs in `checkout-system`.

Baseline docs record project-wide implementation constraints that exist after Preparation and must be preserved during Construction implementation planning.

They answer:

```text
What implementation constraints already exist before a slice is planned or implemented?
```

This document explains how baseline docs are created, used, and maintained.

It does not define application behavior.

It does not define slice-specific design.

It does not replace `project-state.md`.

---

## 2. What Baseline Docs Are

Baseline docs are Preparation exit artifacts.

They record established project-wide constraints for implementation planning.

They are created after Application Bootstrap because only then the project has a real application, runtime, persistence, testing, and API baseline.

Current baseline docs:

```text
application-baseline.md
persistence-baseline.md
testing-baseline.md
api-baseline.md
local-runtime-baseline.md
```

---

## 3. Purpose of Baseline Docs

Baseline docs exist to prevent implementation planning from guessing.

They give Construction a stable starting point.

They define:

```text
- what stack exists
- what structure exists
- what runtime model exists
- what persistence authority exists
- what testing boundaries exist
- what API behavior exists
- what must be preserved by future slices
```

They do not define:

```text
- final business design
- slice-specific implementation
- final database schema
- final API contracts
- exact classes or methods
- exact tests
```

---

## 4. When Baseline Docs Are Created

Baseline docs are created after Stratum 6 — Application Bootstrap.

They are created before the first Construction slice implementation plan.

Correct timing:

```text
Preparation Stratum 6 completed
    ↓
baseline docs created
    ↓
Preparation exit state recorded
    ↓
Construction implementation planning may begin
```

---

## 5. Why They Are Created After Stratum 6

Baseline docs are created after Application Bootstrap because earlier strata do not yet establish the full implementation baseline.

Before Stratum 6, the project may know:

```text
- system intent
- environment model
- infrastructure service
- service constraints
```

But it does not yet fully know:

```text
- application structure
- actual Spring Boot baseline
- profile behavior
- datasource wiring
- Flyway behavior
- automated verification shape
- web integration boundary
- API error handling baseline
```

Creating baseline docs earlier would force the project to invent constraints before they exist.

---

## 6. Relationship to project-state.md

`project-state.md` records where the project is.

Baseline docs record what implementation constraints exist.

Difference:

```text
project-state.md:
    current execution position and readiness

baseline docs:
    reusable implementation constraints that future slices must preserve
```

`project-state.md` may reference baseline docs after Preparation is complete.

Baseline docs may cite `project-state.md` as a source.

But baseline docs must not duplicate the full project state.

---

## 7. Relationship to Implementation Planning

Implementation planning consumes baseline docs.

For each Construction slice, implementation planning should read:

```text
- project-state.md
- slice correctness artifacts
- implementation-requirements.md
- relevant baseline docs
```

Baseline docs constrain planning.

They help answer:

```text
- where should this behavior live?
- does this require persistence?
- does this require an API?
- what test boundary is appropriate?
- what runtime assumptions must be preserved?
- what must not be accidentally changed?
```

Implementation planning must not violate baseline docs without explicitly recording a project-wide baseline change.

---

## 8. How to Create Baseline Docs

Create one baseline doc per stable implementation area.

Recommended areas:

```text
application-baseline.md
persistence-baseline.md
testing-baseline.md
api-baseline.md
local-runtime-baseline.md
```

Each baseline doc should include:

```text
- status
- version
- scope
- baseline change rule
- purpose
- scope
- current baseline
- constraints for implementation planning
- must preserve
- must not assume
- source references
- final rule
```

A baseline doc should record only established constraints.

It should not invent slice-specific design.

---

## 9. How to Use Baseline Docs

Before implementation planning, read only the baseline docs relevant to the slice.

Examples:

```text
Persistence-sensitive slice:
    read persistence-baseline.md
    read testing-baseline.md
    read local-runtime-baseline.md

HTTP/API slice:
    read api-baseline.md
    read application-baseline.md
    read testing-baseline.md

Application orchestration slice:
    read application-baseline.md
    read testing-baseline.md
```

Do not read every baseline doc mechanically if the slice does not need it.

Use the baseline docs to constrain planning, not to replace reasoning.

---

## 10. How to Maintain Baseline Docs

Baseline docs are maintained only when project-wide constraints change.

They should remain stable during normal slice work.

A baseline doc may be updated when a completed slice establishes a reusable rule that future slices must preserve.

Examples:

```text
- a reusable API error catalog is introduced
- a new project-wide package rule is accepted
- a new persistence authority rule is established
- a new test base becomes standard for future slices
- a new runtime requirement becomes project-wide
```

---

## 11. When Baseline Docs Should Be Updated After a Slice

Update a baseline doc after a slice only when the slice changes the project-wide baseline.

A slice may update a baseline when it introduces:

```text
- reusable implementation structure
- reusable validation boundary
- reusable runtime capability
- reusable API rule
- reusable persistence rule
- project-wide dependency or tooling constraint
```

The update should happen after the slice is implemented, validated, and accepted.

The slice completion record should explain why the baseline changed.

---

## 12. When Baseline Docs Should Not Be Updated

Do not update baseline docs merely because a slice adds normal implementation details.

Do not update baseline docs for:

```text
- one new controller
- one new service
- one new table
- one new migration
- one new repository
- one new test class
- one slice-specific validation decision
- temporary implementation mechanics
```

Those belong in:

```text
implementation-plan.md
validation-record.md
completion-record.md
```

Baseline docs are not a log of all implementation work.

---

## 13. Authority Rule

Baseline docs are project-specific truth.

They belong in the project repository.

Reusable baseline-doc structure may later be extracted into the conventions repository, but the actual baseline values belong in `checkout-system`.

The conventions repository may define how baseline docs should work.

It must not define the concrete baseline state of this project.

---

## 14. Final Rule

```text
Baseline docs record established project-wide implementation constraints after Application Bootstrap so Construction slices can be planned without guessing, over-designing, or accidentally violating the prepared foundation.
```
