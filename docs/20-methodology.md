# **Methodology**

---

## **Approach**

The system is approached by **defining correctness explicitly before any implementation concerns**.

Work begins from a bounded problem, where:

* a specific failure is identified
* the conditions that make it difficult are made explicit

From there, the approach focuses on:

* defining what must always hold so that failure cannot occur
* validating that definition under real-world conditions
* translating it into obligations the system must enforce at defined decision points
* expressing what the system must provide to make that enforcement possible

This creates a strict separation between:

* **what must be true**
* **how that truth is enforced**

The approach operates independently of implementation and serves as the definition that implementation must follow and cannot redefine.

---

## **Core Idea**

> Correctness must be defined explicitly, validated under real conditions, and only then translated into system requirements.

This means:

* correctness is not assumed or derived from design
* correctness is defined in a precise, testable form
* correctness must hold under concurrency, retries, delays, and partial failures

The result is a definition of correctness that is:

* **unambiguous** — expressed in terms that can be tested
* **validated** — able to withstand real-world pressure
* **enforceable** — expressed in terms the system can uphold

---

## **Construction Phases**

The approach is structured into **distinct conceptual phases**, each responsible for a single concern.

* **Understanding the Problem**
  Defines the bounded problem space: what exists, what actions occur, where decisions are made, and how failure emerges under pressure.

* **Defining Correctness**
  Establishes what must always be true so that the defined failure cannot occur.

* **Validating Correctness**
  Applies real-world pressure, such as concurrency, retries, delays, and partial failures, to test whether the definition holds.

* **Defining System Guarantees**
  Specifies what the system must ensure at specific points of decision so that correctness cannot be violated.

* **Defining System Requirements**
  Specifies what must exist in the system so that those guarantees can be enforced.

* **Refining the Result**
  Improves clarity, structure, and traceability without changing the defined correctness.

Each phase is isolated.

Mixing responsibilities weakens correctness and is not allowed.

---

## **Principles**

* **Correctness is defined before implementation**
  The system does not determine correctness; it must follow it.

* **Each concern is handled in isolation**
  Understanding, defining, validating, and translating correctness are separate responsibilities.

* **Correctness must be validated under pressure**
  Concurrency, retries, delays, and partial failures are treated as standard conditions.

* **All correctness must be explicit and testable**
  Nothing is left implied or dependent on interpretation.

* **Traceability is required**
  Defined failures must connect directly to correctness definitions, guarantees, and requirements.

* **Scope is strictly controlled**
  Work is limited to a bounded problem; additional concerns are recorded and deferred.

* **Enforcement has a defined point of control**
  Decisions that affect correctness must be governed at a specific point where they are enforced.

* **No bypass of enforcement**
  All state changes that affect correctness must pass through defined enforcement points.

---

## **What This Means in Practice**

This approach enforces a different way of reasoning about systems:

* work does not proceed without an explicit and validated definition of correctness
* design decisions are constrained by what must be true, not by convenience or structure
* failures are addressed by defining and enforcing correctness, not by patching behavior
* system responsibilities are expressed through what must be guaranteed, not how components are organized

As a result:

* correctness is established before implementation begins
* assumptions are replaced with explicit definitions
* correctness is validated against real-world pressure before implementation begins
* the system can be explained in terms of what it must ensure, not how it happens internally

The system is therefore shaped by **defined and validated correctness**, which guides and constrains all further work.
