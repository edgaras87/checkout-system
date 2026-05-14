# Application Baseline

Status: accepted  
Version: v1  
Scope: checkout-system implementation planning baseline

---

## 0. Baseline Change Rule

This document changes only when a project-wide application structure or stack planning constraint changes.

It should not be updated merely because a slice adds classes, packages, use cases, or adapters.

Slice-specific application decisions belong in that slice's implementation-plan.md, validation-record.md, or completion-record.md.

A completed slice may update this baseline only when it establishes a reusable application structure rule, dependency rule, or stack capability that future slices must preserve.

---

## 0.1 Change Log

```text
v1:
    Created from Preparation Phase application baseline before first Construction slice.
```

---

## 1. Purpose

This document records the current application baseline that implementation planning must preserve.

It defines the established Java, Spring Boot, Maven, package, layer, and structure constraints for the project.

It answers:

```text
What application structure and stack constraints already exist before slice implementation planning begins?
```

This document does not define slice-specific application design.

It does not define final classes, methods, or package additions.

---

## 2. Scope

This baseline covers:

```text
- Java baseline
- Spring Boot application baseline
- Maven build baseline
- packaging model
- base package
- application entry point
- profile model
- architectural root packages
- dependency direction
- current bootstrap application features
```

This baseline does not cover:

```text
- reservation use-case design
- order use-case design
- payment use-case design
- checkout outcome use-case design
- final class names
- final method signatures
- final package additions
```

---

## 3. Current Baseline

```text
Application framework: Spring Boot
Language: Java
Java version: 21
Build tool: Maven
Packaging: jar
Base package: com.edge.checkout
Application entry point: CheckoutSystemApplication
Runtime configuration model: profile-based
Manual local startup profile: dev
Automated test profile: test
```

The application baseline is complete enough to host controlled Construction work.

No checkout business behavior has been implemented yet.

---

## 4. Current Application Entry Point

Application entry point:

```text
src/main/java/com/edge/checkout/CheckoutSystemApplication.java
```

Base package:

```text
com.edge.checkout
```

Rules:

```text
- the base package must remain stable
- package names must remain lowercase
- package names must not contain hyphens
- application package structure must not be reorganized casually
```

---

## 5. Structural Model

The project uses an architecture-first package structure.

Default root packages:

```text
application/
domain/
infra/
web/
```

Meaning:

```text
application/
    use-case orchestration and application decisions

domain/
    business concepts and reusable business rules

infra/
    technical implementations, adapters, database access, configuration

web/
    HTTP/API entry layer
```

Rules:

```text
- slices are not root packages
- correctness artifacts are not root packages
- business areas are not root packages by default
- packages represent stable architectural ownership
```

---

## 6. Dependency Direction

Allowed dependency direction:

```text
web → application

application → domain

application → application/port

infra → application/port

infra → domain
```

Forbidden dependency direction:

```text
application → infra

application → web

domain → application

domain → infra

domain → web

web → infra
```

Core rule:

```text
Application and domain decisions must not depend on technical implementations.
```

---

## 7. Application Layer Baseline

`application/` owns use-case orchestration.

Allowed in `application/`:

```text
- use cases
- application services
- command handling
- query handling
- port interfaces
- transaction boundary coordination
```

Disallowed in `application/`:

```text
- HTTP controllers
- HTTP DTOs
- JPA repositories
- database implementation details
- external client implementations
- framework-heavy infrastructure logic
```

Implementation planning may place slice behavior in the application layer when orchestration or decision flow is required.

Implementation planning must not force exact class names too early.

---

## 8. Domain Layer Baseline

`domain/` owns business meaning.

Allowed in `domain/`:

```text
- business concepts
- business state
- business transitions
- reusable business rules
- domain-specific exceptions
```

Disallowed in `domain/`:

```text
- HTTP controllers
- HTTP DTOs
- JPA repositories by default
- Spring configuration
- external integration code
- application orchestration
```

Implementation planning may identify domain concepts required by a slice.

Implementation planning must not invent broad domain models before the slice needs them.

---

## 9. Infra Layer Baseline

`infra/` owns technical implementation.

Allowed in `infra/`:

```text
- database access
- external integrations
- technical adapters
- framework configuration
- implementations of application ports
```

Rules:

```text
- infra may depend on application ports
- infra may depend on domain
- infra must not own application decisions
- infra must not bypass application use cases
```

Implementation planning may identify infrastructure responsibilities.

Implementation execution decides exact adapter, repository, and persistence implementation.

---

## 10. Web Layer Baseline

`web/` owns HTTP/API entry behavior.

Allowed in `web/`:

```text
- HTTP controllers
- request DTOs
- response DTOs
- HTTP-specific validation
- exception handling
```

Disallowed in `web/`:

```text
- business rules
- database access
- domain state mutation logic
- correctness enforcement logic
- application orchestration beyond calling a use case
```

Rules:

```text
- web calls application
- web does not call infra directly
- web does not enforce core correctness
```

---

## 11. Port and Adapter Baseline

Ports belong in:

```text
application/port/
```

Adapters belong in:

```text
infra/
```

Rule:

```text
application defines ports

infra implements ports
```

Implementation planning may state that a port is needed.

Implementation planning must not require final method signatures unless the contract has already been accepted elsewhere.

---

## 12. Current Bootstrap Features

The application currently includes:

```text
- Spring Boot application entry point
- Maven build baseline
- dev/test profile configuration
- datasource and Flyway configuration
- baseline migration lifecycle
- PostgreSQL Testcontainers integration test baseline
- web smoke test baseline
- bootstrap HTTP sanity endpoint
- Lombok annotation processing support
- API error handling baseline
```

The application currently does not include:

```text
- reservation behavior
- order behavior
- payment behavior
- final outcome behavior
- business database tables
- business API endpoints
```

---

## 13. Constraints for Implementation Planning

Implementation planning must preserve:

```text
- Java 21 baseline
- Maven build model
- Spring Boot application model
- base package com.edge.checkout
- architecture-first root packages
- dependency direction
- separation between application, domain, infra, and web
- profile-based configuration model
- existing bootstrap verification boundaries
```

Implementation planning must not assume:

```text
- reservation packages already exist
- business use cases already exist
- business persistence already exists
- web APIs for business behavior already exist
- package roots should be created per slice
- correctness artifacts should become Java packages
```

---

## 14. Must Preserve

```text
Application structure expresses stable architectural ownership.

Use cases organize behavior inside layers.

Slices are represented by implementation work, tests, and git history.

Slices must not become Java root packages.

Correctness artifacts guide implementation but do not become runtime package roots.

Application/domain decisions must remain independent from infra/web implementations.
```

---

## 15. Must Not Assume

```text
Do not assume every slice needs a controller.

Do not assume every slice needs a new root package.

Do not assume every correctness concept needs a Java package.

Do not assume exact class names during planning.

Do not assume business modules exist before code pressure proves they are needed.

Do not assume implementation planning should design every class.
```

---

## 16. Source References

```text
project-state.md
src/main/java/com/edge/checkout/
src/main/resources/
pom.xml
```

---

## 17. Final Rule

```text
Application planning must preserve the Spring Boot Java 21 architecture-first baseline, keep slices inside stable layers, and delay exact class/package design until implementation execution proves the need.
```
