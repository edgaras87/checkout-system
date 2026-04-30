# 📘 **Correctness Construction Method (Reference Guide)**

---

## **1. What This Method Is**

The Correctness Construction Method is a structured way to build correctness for a specific part of a system (a *slice*).

Instead of jumping straight into code or design, it follows a sequence:

```text
understand the problem
    → define correctness
    → test correctness
    → turn it into enforceable rules
    → turn those rules into system requirements
    → clean it up for use
```

The result is a **clear, validated, and implementation-ready correctness package**.

---

## **2. Why This Exists**

Most systems fail not because of code—but because correctness was never clearly defined.

This method helps:

* avoid vague “it should work” thinking
* handle concurrency, retries, and real-world pressure properly
* separate **what must be true** from **how it is implemented**
* build something that can be confidently implemented and explained

---

## **3. Where It Fits**

The method operates within a two-part execution model:

```text
System 1 — Correctness Construction
    defines what must be true

System 2 — Implementation
    enforces that truth in code
```

This method belongs to **System 1 (Correctness Construction)**.

```text
Selection → Clarification → Correctness Construction → Implementation
```

Where:

* **Correctness Construction (System 1)** produces:

  * invariant
  * guarantees
  * requirements

* **Implementation (System 2)**:

  * must enforce these outputs
  * must not redefine or weaken them

---

## **4. What It Starts With**

The method does not start from nothing—it starts from a **clarified slice**:

```text
- what failure is being targeted
- what pressure makes it hard (e.g. concurrency)
- what is included
- what is explicitly excluded
```

That slice becomes the working unit.

---

## **5. What It Produces**

At the end, the method produces:

```text
- a clear model of the slice
- a validated invariant (what must always hold)
- guarantees (what the system must enforce)
- requirements (what the system must provide)
- a clean, structured artifact ready for implementation
```

This output is what System 2 (implementation) should follow.

---

## **6. The Big Picture Flow**

```text
Phase 0 → understand the world
Phase 1 → define correctness
Phase 2 → try to break it
Phase 3 → define what must be enforced
Phase 4 → define what must exist in the system
Phase 5 → clean it up without changing it
```

---

## **7. The Phases (Practical View)**

---

### 🔹 **Phase 0 — Exploration**

**Goal:** Understand the slice as a working system.

This phase identifies:

* what exists (state)
* what happens (actions)
* where decisions are made
* who has authority over those decisions
* how failure occurs
* how pressure (e.g. concurrency) affects it

No correctness is defined at this stage.

👉 Output: a **clear model of the slice**

---

### 🔹 **Phase 1 — Invariant Construction**

**Goal:** Define correctness.

Core question:

```text
What must always be true so failure is impossible?
```

Output:

* invariant (plain, precise, falsifiable)

Invariant must:

```text
- be testable against system state
- not depend on timing or interpretation
```

👉 This is the **core correctness truth**

---

### 🔹 **Phase 2 — Proof**

**Goal:** Attempt to break the invariant.

This phase includes:

* adversarial scenarios
* explicit pressure application (concurrency, retries, delays, partial failures)
* violation detection

Outcomes:

```text
✔ survives → validated
✖ breaks → return to invariant construction
```

👉 Weak correctness definitions are exposed here

---

### 🔹 **Phase 3 — Guarantee Planning**

**Goal:** Turn correctness into enforceable obligations.

Core question:

```text
What must the system guarantee so the invariant never breaks?
```

Defines:

* guarantees (“the system must ensure…”)
* enforcement points (where correctness is enforced)

Each guarantee must:

```text
- be enforceable at a single logical point
- define what is protected
- define where enforcement happens
- have a single enforcing authority
```

👉 Correctness becomes **operational**

---

### 🔹 **Phase 4 — Implementation Requirements**

**Goal:** Define what the system must have to enforce guarantees.

Core question:

```text
What must exist in the system so guarantees are enforceable?
```

Defines:

* required state
* decision points
* enforcement rules
* rejection rules
* consistency expectations
* conflict handling
* idempotency
* verification needs

Additionally define:

```text
- authority boundary (where final decisions are made)
- atomicity expectations (what must happen as one unit)
- visibility/ordering guarantees (who sees what and when)
```

Each failure condition must define:

```text
- whether it is rejected or retried
```

Also define:

```text
- observable signals (logs, metrics, events) that confirm guarantees hold
```

👉 This is the **bridge to real implementation**

---

### 🔹 **Phase 5 — Polish**

**Goal:** Make the artifact clear and usable.

This phase includes:

* wording cleanup
* terminology alignment
* structural improvements
* traceability exposure

⚠️ Important:

```text
No change may alter invariant, guarantees, or requirements.
```

👉 This is strictly **presentation and clarity**

---

## **8. How the Pieces Connect**

All elements must be traceable:

```text
Failure
    ↓
Invariant
    ↓
Guarantees
    ↓
Requirements
```

If traceability is missing, correctness is incomplete.

---

## **9. Key Rules to Respect**

---

### ⚠️ **1. Don’t skip layers**

It is not valid to:

* define guarantees without an invariant
* define requirements without guarantees
* implement without requirements

---

### ⚠️ **2. Don’t mix phases**

Each phase has a single responsibility:

```text
Phase 0 → understand
Phase 1 → define
Phase 2 → test
Phase 3 → operationalize
Phase 4 → prepare for implementation
Phase 5 → refine
```

Mixing phases leads to weak correctness.

---

### ⚠️ **3. Don’t silently fix issues**

If a problem is discovered:

```text
return to the phase responsible for it
```

Late-stage patching is not allowed.

---

### ⚠️ **4. Pressure is always real**

Assume:

* concurrency
* retries
* delays
* partial failures

Pressure must be applied explicitly during Phase 2 (Proof).

If pressure is not tested, correctness is not validated.

---

### ⚠️ **5. Stay within the slice**

Additional problems will be observed.

These must not be solved within the current slice:

```text
record → defer → continue
```

---

### ⚠️ **6. No-bypass enforcement**

All state changes that affect guarantees must pass through the defined enforcement points.

No alternate paths are allowed.

---

## **10. Completion Criteria**

A slice is considered complete when:

* invariant is defined and validated
* guarantees are complete
* requirements are implementable
* full traceability exists
* artifact is clear and stable

---

## **11. Outcomes**

Proper application of this method results in:

* systems that remain correct under pressure
* clear reasoning that can be explained externally
* implementation driven by correctness rather than assumptions
* controlled scaling of system complexity

---

## **12. Mental Model (Simple Version)**

```text
Understand the world
    → define what must never break
        → try to break it
            → define what must be enforced
                → define what must exist
                    → make it clear
```

---

## **13. Example (Minimal)**

```text
Example: Inventory Reservation

Failure:
    accepted reservations exceed available stock

Invariant:
    total reserved ≤ available stock

Guarantee:
    reservation acceptance must verify capacity at the point of decision

Requirement:
    capacity check and reservation commit must occur atomically within a single decision authority
```

---

## **14. One-Line Summary**

> Build correctness step by step, validate it under pressure, and only then translate it into implementation-ready form.
