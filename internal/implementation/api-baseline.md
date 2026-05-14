# API Baseline

Status: accepted  
Version: v1  
Scope: checkout-system implementation planning baseline

---

## 0. Baseline Change Rule

This document changes only when a project-wide API, HTTP, or error handling planning constraint changes.

It should not be updated merely because a slice adds one controller, endpoint, DTO, or response shape.

Slice-specific API decisions belong in that slice's implementation-plan.md, validation-record.md, or completion-record.md.

A completed slice may update this baseline only when it establishes a reusable API rule, error handling rule, public API convention, or HTTP behavior constraint that future slices must preserve.

---

## 0.1 Change Log

```text
v1:
    Created from Preparation Phase API baseline before first Construction slice.
```

---

## 1. Purpose

This document records the current API and HTTP behavior baseline that implementation planning must preserve.

It defines the established HTTP entry behavior, error handling baseline, and API expansion constraints.

It answers:

```text
What API behavior already exists before slice implementation planning begins?
```

This document does not define business API design.

It does not define reservation endpoints.

It does not define final request or response DTOs.

---

## 2. Scope

This baseline covers:

```text
- current HTTP sanity endpoint
- current API error handling baseline
- current validation error handling
- current malformed request handling
- current business API absence
- API expansion rules for future slices
```

This baseline does not cover:

```text
- reservation API endpoint design
- order API endpoint design
- payment API endpoint design
- checkout outcome API endpoint design
- final URL paths
- final HTTP status mapping for slice behavior
- final request/response DTOs
```

---

## 3. Current API Baseline

Current bootstrap endpoint:

```text
GET /ping
```

Expected response:

```text
pong
```

Purpose:

```text
bootstrap HTTP sanity check
```

The `/ping` endpoint is not business API behavior.

It does not represent reservation, order, payment, or checkout outcome functionality.

---

## 4. Current Error Handling Baseline

The application has an API error handling baseline.

Current automated verification covers:

```text
- request body validation errors
- request parameter validation errors
- malformed JSON errors
- bad request exceptions
- server-side invariant violation handling
```

The response contract currently includes:

```text
- title
- status
- detail
- errors, where applicable
```

Problem Detail type URI catalog:

```text
deferred
```

Rules:

```text
- new API behavior must reuse the existing API error handling baseline
- validation errors must remain consistently shaped
- malformed requests must remain consistently handled
- server-side invariant violations must remain consistently handled
```

---

## 5. Business API Baseline

No public checkout business API has been defined yet.

Current state:

```text
reservation API: not defined
order API: not defined
payment API: not defined
checkout outcome API: not defined
```

Implementation planning must not assume that a slice requires HTTP exposure by default.

A slice may add public API behavior only when its implementation requirements require observable external behavior through HTTP.

---

## 6. API Expansion Rule

Implementation planning must explicitly decide whether a slice needs API exposure.

Allowed planning statement:

```text
This slice requires observable acceptance/rejection behavior, but implementation planning must decide whether that observability is exposed through HTTP in this slice or validated at application level only.
```

Allowed planning statement:

```text
This slice adds an HTTP entry point because the accepted implementation requirements require externally observable reservation decisions.
```

Not allowed:

```text
Every slice automatically gets a controller.
```

Not allowed:

```text
Create this exact endpoint path and DTO shape during baseline planning.
```

Rule:

```text
API exposure must be justified by slice requirements, not by habit.
```

---

## 7. Web Layer Responsibility

HTTP/API behavior belongs in:

```text
web/
```

Rules:

```text
- web receives HTTP input
- web performs HTTP-specific validation
- web maps HTTP requests to application use cases
- web maps application results to HTTP responses
- web does not enforce core correctness
- web does not access infra directly
- web does not mutate domain state directly
```

Core correctness enforcement belongs behind the web layer.

---

## 8. Observable Behavior Planning

Implementation planning may describe observable behavior without defining final API design.

Allowed:

```text
Accepted reservation decisions must be observable.

Rejected reservation decisions must be observable.

The system must not expose accepted reservations that contradict capacity.
```

Too low-level:

```text
POST /reservations returns this exact JSON body.
```

Too low-level:

```text
Use this exact DTO class.
```

Rule:

```text
Observable behavior is planning-level.

Endpoint and DTO design belongs to implementation execution unless already required by an accepted contract.
```

---

## 9. Constraints for Implementation Planning

Implementation planning must preserve:

```text
- /ping as bootstrap sanity endpoint
- existing API error handling baseline
- existing validation error response shape
- current absence of public business API
- web/application/infra dependency boundaries
- deferred Problem Detail type URI catalog
```

Implementation planning must decide:

```text
- whether the slice needs HTTP exposure now
- whether application-level observability is enough
- whether existing error handling covers new failure cases
- whether new error types require later catalog work
```

Implementation planning must not assume:

```text
- every slice requires a public endpoint
- /ping is part of business behavior
- API error catalog already exists
- final DTOs should be designed during baseline planning
```

---

## 10. Must Preserve

```text
Business correctness must not be enforced in controllers.

Controllers must delegate to application behavior.

Existing error handling shape must remain consistent.

New API behavior must not bypass application use cases.

Problem Detail type URI catalog remains deferred unless explicitly pulled into scope.
```

---

## 11. Must Not Assume

```text
Do not assume SL-01 automatically needs an HTTP endpoint.

Do not assume accepted/rejected behavior must be exposed publicly in the first implementation increment.

Do not assume final API paths are known.

Do not assume final request/response DTOs are known.

Do not assume error type URIs are already defined.

Do not assume the bootstrap /ping endpoint represents business API structure.
```

---

## 12. Source References

```text
project-state.md
src/main/java/com/edge/checkout/web/
src/test/java/com/edge/checkout/web/
docs/
```

---

## 13. Final Rule

```text
API planning must preserve the existing HTTP and error handling baseline, avoid accidental business API expansion, and require explicit justification before a slice adds public API behavior.
```
