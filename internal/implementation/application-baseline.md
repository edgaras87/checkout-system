# Application Baseline

Status: accepted  
Version: v2  
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

v2:
    Clarified application port structure, application port naming, application service rules,
    application runtime wiring boundary, and application layer change boundary for Construction implementation execution.
```

---

## 1. Purpose

This document records the current application baseline that implementation planning must preserve.

It defines the established Java, Spring Boot, Maven, package, layer, structure, application port, application service, and runtime wiring constraints for the project.

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
- application layer responsibility
- domain layer responsibility
- infra layer responsibility
- web layer responsibility
- application port structure
- application port naming
- application service rules
- application runtime wiring boundary
- application layer change boundary
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
- final port method names
- final adapter implementation details
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

## 12. Application Port Structure

Application ports belong under:

```text
src/main/java/com/edge/checkout/application/port/
```

Application ports define capabilities the application needs from outside itself.

Default rule:

```text
application/port/
    application-owned port interfaces
```

When multiple ports exist or grouping improves clarity, ports may be grouped by stable application responsibility:

```text
application/port/<area>/
```

Rules:

```text
- application ports are owned by the application layer
- application ports define what the application needs, not how the need is technically fulfilled
- infra implements application ports
- application ports must not expose infra, database, SQL, JPA, HTTP, or framework-specific mechanisms
- port packages must use lowercase names
- port area names should represent stable application or business responsibility
- port area names must not use slice IDs, correctness artifact names, or technical mechanism names
```

Allowed examples:

```text
application/port/
application/port/reservation/
application/port/order/
application/port/payment/
```

Not allowed examples:

```text
application/port/sl01/
application/port/postgres/
application/port/jpa/
application/port/correctness/
```

Implementation planning may state that a port is needed.

Implementation execution decides the exact package and class names when the accepted implementation responsibility requires them.

---

## 13. Application Port Naming

Application port names should describe the capability needed by the application.

Preferred pattern:

```text
<Capability>Port
```

The capability name should come from the application perspective, not from the technical implementation.

Rules:

```text
- use capability-based names
- avoid technology names in application port names
- avoid database, SQL, JPA, HTTP, or framework terms in application port names
- avoid naming ports after infra adapters
- avoid naming ports after slice IDs
- use Repository naming only when the application intentionally needs a collection-like persistence abstraction
- do not use Repository merely because the infra adapter stores data
```

Preferred style:

```text
<BusinessCapability>Port
<BusinessLookup>Port
<BusinessDecision>Port

OrderLookupPort
PaymentOutcomePort
CapacityReservationPort
```

Avoid by default:

```text
PostgresCapacityPort
JpaOrderRepositoryPort
SqlPaymentStorePort
SL01ReservationPort
```

Application port method names should describe the capability requested by the application without exposing SQL, locking, transaction, adapter, or framework mechanics.

Exact method names belong to implementation execution unless already accepted by an owning contract.

---

## 14. Application Service Rules

Application services own use-case orchestration.

Application services may:

```text
- receive application commands
- coordinate domain behavior
- call application-owned ports
- coordinate transaction boundary placement when required by the accepted implementation strategy
- return application results
```

Application services must not:

```text
- contain SQL
- depend on infra adapters
- depend on web controllers
- depend on HTTP DTOs
- perform database schema changes
- bypass domain meaning when domain meaning exists
- hide application decisions inside technical adapter calls
```

Rules:

```text
- application services express application decision flow
- domain meaning should remain in domain when reusable business meaning exists
- technical persistence mechanics belong behind application ports
- HTTP concerns belong in web
- database access belongs in infra
```

Implementation planning may identify that an application service is needed.

Implementation execution decides exact service names, command names, result names, and method signatures.

---

## 15. Application Runtime Wiring Boundary

Runtime wiring means framework-managed binding of real runtime components.

Runtime wiring may include:

```text
- Spring component registration
- dependency injection configuration
- binding real infra adapters to application ports
- transaction configuration
- framework annotations that register components or define transaction participation
```

Examples:

```text
@Service
@Component
@Configuration
@Bean
@Transactional
```

Rules:

```text
- runtime wiring must not change business meaning
- runtime wiring must not redefine application port contracts
- runtime wiring must not make application depend on infra
- runtime wiring must not make domain depend on Spring, infra, or web
- runtime wiring should connect already-defined application and infra responsibilities
- application code must not manually instantiate infra adapters
- infra must not bypass application use cases
```

Application boundary work may define commands, results, use cases, services, and ports without wiring them into the real Spring runtime.

Runtime wiring should be introduced only when the selected implementation responsibility explicitly owns wiring or executable integration behavior.

---

## 16. Application Layer Change Boundary

Application-layer changes should preserve architectural responsibility boundaries.

An application-layer change may introduce:

```text
- application commands
- application results
- application use-case boundaries
- application services
- application-owned ports
- focused application tests using controlled fakes or stubs
```

An application-layer change must not introduce, unless the selected implementation responsibility explicitly owns it:

```text
- Flyway migrations
- database tables
- persistence adapters
- JPA repositories
- SQL
- HTTP controllers
- request DTOs
- response DTOs
- runtime wiring
- transaction enforcement
```

Rules:

```text
- application ports may be introduced before adapters exist
- adapter changes should implement existing application ports instead of redefining application behavior
- wiring changes should connect existing application and infra pieces instead of adding new business rules
- application tests with fakes or stubs may prove orchestration, but must not claim to prove persistence, durability, transaction, or concurrency behavior
```

---

## 17. Current Bootstrap Features

The application currently includes:

```text
- Spring Boot application entry point
- Maven build baseline
- dev/test profile configuration
- datasource and Flyway configuration
- baseline migration lifecycle
- PostgreSQL Testcontainers integration test baseline
- full web integration test baseline with Testcontainers-backed application context
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

## 18. Constraints for Implementation Planning

Implementation planning must preserve:

```text
- Java 21 baseline
- Maven build model
- Spring Boot application model
- base package com.edge.checkout
- architecture-first root packages
- dependency direction
- separation between application, domain, infra, and web
- application-owned port boundaries
- capability-based application port naming
- application service responsibility boundaries
- runtime wiring boundaries
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
- application ports should expose infra, SQL, JPA, HTTP, or framework mechanics
- every application-layer change should also introduce runtime wiring
- focused application tests prove persistence, durability, transaction, or concurrency behavior
```

---

## 19. Must Preserve

```text
Application structure expresses stable architectural ownership.

Use cases organize behavior inside layers.

Slices are represented by implementation work, tests, and git history.

Slices must not become Java root packages.

Correctness artifacts guide implementation but do not become runtime package roots.

Application/domain decisions must remain independent from infra/web implementations.

Application ports describe needed capabilities without leaking technical implementation.

Application services orchestrate use-case behavior without owning technical persistence mechanics.

Runtime wiring connects already-defined responsibilities without redefining business meaning.
```

---

## 20. Must Not Assume

```text
Do not assume every slice needs a controller.

Do not assume every slice needs a new root package.

Do not assume every correctness concept needs a Java package.

Do not assume exact class names during planning.

Do not assume exact application port method names during planning.

Do not assume business modules exist before code pressure proves they are needed.

Do not assume implementation planning should design every class.

Do not assume application ports should be named after technologies, adapters, or slice IDs.

Do not assume runtime wiring belongs in the same change as application boundary definition.

Do not assume application-layer tests with fakes or stubs prove database-backed correctness.
```
