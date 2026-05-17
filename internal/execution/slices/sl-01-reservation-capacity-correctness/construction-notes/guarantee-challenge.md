# Guarantee Challenge — SL-01 — Reservation Capacity Correctness

## 1. Purpose

This document records `Run 4 — Refined Invariant + Candidate Guarantees + Guarantee Challenge` for `SL-01 — Reservation Capacity Correctness`.

It refines the accepted invariant from Run 3, stabilizes correctness attack directions, derives candidate guarantees, and challenges whether those guarantees are strong enough to preserve the invariant under the dominant pressure of concurrency.

The invariant states what must never become false.

The correctness attack directions identify the stable classes of threat against that invariant.

The guarantees describe what protections must hold so the invariant remains true under concurrency.

This is a working construction artifact. It does not define implementation requirements, implementation strategy, database schema, API design, framework design, repository design, migration design, validation evidence, or completion state.

---

## 2. Source Inputs

This run uses:

- `internal/execution/slices/sl-01-reservation-capacity-correctness/execution-context.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/slice-clarification.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/construction-notes/exploration-and-failure-mechanism.md`
- `internal/execution/slices/sl-01-reservation-capacity-correctness/construction-notes/invariant-challenge.md`

Accepted Run 1 framing:

```text
The world to explore is the local Inventory Reservation decision space.

The failure to explore is overselling.

The pressure to explore is concurrency.

The boundary to preserve is local reservation capacity correctness, not full checkout outcome correctness.
```

Accepted Run 2 result:

```text
Concurrent reservation attempts can each appear acceptable against the same remaining capacity,
then all be accepted, causing committed accepted reservation quantity
to exceed the local reservation capacity limit.
```

Accepted Run 3 invariant:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
must be less than or equal to
the local reservation capacity limit for X.
```

Where:

```text
X = the relevant reservable capacity being protected from overselling
```

Accepted Run 3 protection needs:

```text
PN-01 — Prevent overlapping individually acceptable decisions from becoming collectively invalid after commitment.

PN-02 — Preserve quantity correctness under mixed request sizes.

PN-03 — Prevent stale or incomplete capacity views from producing invalid accepted reservations.

PN-04 — Protect the transition from evaluation to committed accepted reservation state.

PN-05 — Preserve rejection behavior so rejected reservations never consume committed accepted capacity.

PN-06 — Preserve consistent reservable-capacity scope during evaluation and commitment.
```

Run 4 treats these protection needs as the direct handoff from invariant challenge to guarantee construction.

Protection needs are not guarantees.

They identify the protection problems that guarantees must solve.

---

## 3. Refined Invariant

The refined invariant for this run is:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
must be less than or equal to
the local reservation capacity limit for X.
```

Where:

```text
X = the relevant reservable capacity being protected from overselling
```

This invariant means:

- the comparison is scoped to one reservable capacity at a time
- committed accepted reservation quantity and local reservation capacity limit must refer to the same reservable capacity
- the protected quantity is the total committed accepted quantity, not one request in isolation
- rejected reservations do not contribute to committed accepted reservation quantity
- the invariant is violated exactly when local reservation capacity has been oversold

The invariant remains local to Inventory Reservation.

It does not define physical inventory behavior, external inventory synchronization, order creation, payment interpretation, final outcome composition, reservation expiration, reservation release, reconciliation, API shape, database schema, or implementation strategy.

---

## 4. Guarantee Construction Goal

Run 3 attacked the invariant and produced protection needs.

Run 4 must turn those protection needs into a stable protection structure.

This run therefore asks:

```text
What protections must exist so the invariant cannot fail under concurrency?
```

Run 4 builds that protection structure through this flow:

```text
Protection Need
    → Correctness Attack Direction
    → Candidate Guarantee
    → Guarantee Challenge
    → Refined Guarantee
    → CAD Coverage
```

A protection need is the problem exposed by invariant challenge.

A correctness attack direction is the stable class of threat against the invariant.

A guarantee is the correctness protection that must hold against that attack direction.

This run may merge, split, refine, rename, or stabilize Run 3 protection needs into correctness attack directions and guarantees.

This run must not introduce implementation strategy, database schema, API design, framework design, repository design, migration design, or test implementation.

---

## 5. Correctness Attack Directions

Run 3 produced protection needs.

Run 4 stabilizes those protection needs into correctness attack directions.

A correctness attack direction is a stable class of threat against the invariant.

It answers:

```text
What kind of correctness attack must the guarantee set defend against?
```

Correctness attack directions are not implementation risks.

They are correctness-level attack classes.

They sit between protection needs and guarantees:

```text
Protection Need
    → Correctness Attack Direction
    → Guarantee
```

---

### CAD-01 — Scope Corruption

Derived from:

```text
PN-06 — Preserve consistent reservable-capacity scope during evaluation and commitment.
```

Attack direction:

```text
Committed accepted reservation quantity and local reservation capacity limit
may be evaluated against different reservable-capacity identities.
```

Invariant risk:

```text
The invariant becomes meaningless if both sides of the comparison do not refer to the same X.
```

---

### CAD-02 — Quantity Miscalculation

Derived from:

```text
PN-02 — Preserve quantity correctness under mixed request sizes.
```

Attack direction:

```text
Reservation-capacity correctness may be evaluated by request count,
presence of remaining capacity, or isolated request validity instead of accepted quantity.
```

Invariant risk:

```text
The system may accept a request even though the requested quantity pushes
committed accepted reservation quantity above the local reservation capacity limit.
```

---

### CAD-03 — Collective Over-Acceptance

Derived from:

```text
PN-01 — Prevent overlapping individually acceptable decisions from becoming collectively invalid after commitment.
```

Attack direction:

```text
Multiple individually acceptable reservation decisions may become invalid as a committed group.
```

Invariant risk:

```text
The combined committed accepted quantity may exceed the local reservation capacity limit.
```

---

### CAD-04 — Stale Capacity View Acceptance

Derived from:

```text
PN-03 — Prevent stale or incomplete capacity views from producing invalid accepted reservations.
```

Attack direction:

```text
A reservation may be accepted using a capacity view that excludes relevant committed accepted reservations.
```

Invariant risk:

```text
The system may accept capacity that has already been consumed.
```

---

### CAD-05 — Evaluation-to-Commit Gap

Derived from:

```text
PN-04 — Protect the transition from evaluation to committed accepted reservation state.
```

Attack direction:

```text
A reservation may be evaluated as acceptable, but the committed state may change
before the reservation becomes capacity-consuming.
```

Invariant risk:

```text
The decision may be safe at evaluation time but unsafe by the time it is committed.
```

---

### CAD-06 — Rejection Consumption

Derived from:

```text
PN-05 — Preserve rejection behavior so rejected reservations never consume committed accepted capacity.
```

Attack direction:

```text
Rejected reservation decisions may incorrectly increase committed accepted reservation quantity.
```

Invariant risk:

```text
Capacity can be consumed without an accepted reservation decision.
```

---

### CAD-07 — Responsibility Boundary Escape

Derived from:

```text
Run 1 boundary and Run 3 local invariant scope.
```

Boundary note:

```text
CAD-07 is not derived from a Run 3 protection need.

It is stabilized from the accepted slice boundary because boundary escape would weaken every local guarantee.
```

Attack direction:

```text
Local reservation-capacity correctness may be shifted to downstream systems,
external inventory behavior, reconciliation, or future compensation.
```

Invariant risk:

```text
Inventory Reservation may fail to preserve its own local overselling invariant.
```

---

## 6. Candidate Guarantees

### G1 — Same-Capacity Scope Guarantee

Derived from:

```text
CAD-01 — Scope Corruption

PN-06 — Preserve consistent reservable-capacity scope during evaluation and commitment.
```

Protection problem exposed:

```text
Committed accepted quantity and capacity limit become meaningless
if they refer to different reservable capacities.
```

Simple meaning:

```text
The system must compare quantity and capacity for the same thing.
```

Formal guarantee:

```text
For every reservation-capacity decision,
requested quantity,
committed accepted reservation quantity,
and local reservation capacity limit
must be evaluated within the same reservable-capacity scope.
```

Invariant trace:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
    <=
local reservation capacity limit for X
```

Both sides must refer to the same `X`.

---

### G2 — Quantity-Based Acceptance Safety Guarantee

Derived from:

```text
CAD-02 — Quantity Miscalculation

PN-02 — Preserve quantity correctness under mixed request sizes.
```

Protection problem exposed:

```text
Acceptance can be unsafe if it ignores requested quantity
or protects request count instead of committed quantity.
```

Simple meaning:

```text
A reservation must be accepted only if its requested quantity still fits.
```

Formal guarantee:

```text
A reservation request may be accepted only if adding its requested quantity
to the committed accepted reservation quantity for the same reservable capacity
keeps the total less than or equal to the local reservation capacity limit.
```

Conceptual form:

```text
committed accepted reservation quantity for X
    + requested quantity
    <=
local reservation capacity limit for X
```

Invariant trace:

```text
This guarantee preserves the invariant at the acceptance decision point.
```

---

### G3 — Combined Committed Quantity Guarantee

Derived from:

```text
CAD-03 — Collective Over-Acceptance

PN-01 — Prevent overlapping individually acceptable decisions from becoming collectively invalid after commitment.
```

Protection problem exposed:

```text
Multiple requests may each look acceptable in isolation
while their committed group exceeds capacity.
```

Simple meaning:

```text
The system must protect the total accepted result, not isolated requests.
```

Formal guarantee:

```text
Reservation-capacity correctness must be evaluated against
the combined committed accepted quantity for the reservable capacity,
including all accepted reservations that consume that capacity.
```

Invariant trace:

```text
The invariant is about total committed accepted reservation quantity,
so protection must also be total-based.
```

---

### G4 — Concurrent Acceptance Integrity Guarantee

Derived from:

```text
CAD-03 — Collective Over-Acceptance
CAD-05 — Evaluation-to-Commit Gap

PN-01 — Prevent overlapping individually acceptable decisions from becoming collectively invalid after commitment.

PN-04 — Protect the transition from evaluation to committed accepted reservation state.
```

Protection problem exposed:

```text
Overlapping reservation decisions can jointly consume the same remaining capacity.
```

Simple meaning:

```text
Two competing accepted decisions must not both spend the same capacity.
```

Formal guarantee:

```text
Concurrent or overlapping reservation-capacity decisions
for the same reservable capacity
must not commit accepted reservations whose combined quantity
exceeds the local reservation capacity limit.
```

Invariant trace:

```text
This guarantee protects the invariant under concurrency,
where individually safe decisions can become jointly unsafe.
```

---

### G5 — Capacity View Freshness Guarantee

Derived from:

```text
CAD-04 — Stale Capacity View Acceptance

PN-03 — Prevent stale or incomplete capacity views from producing invalid accepted reservations.
```

Protection problem exposed:

```text
A stale or incomplete view can make unsafe acceptance appear valid.
```

Simple meaning:

```text
A reservation must not be accepted using outdated capacity information.
```

Formal guarantee:

```text
A reservation must not be accepted from a capacity view
that excludes committed accepted reservations relevant to
the same reservable-capacity decision.
```

Invariant trace:

```text
The accepted quantity used during the decision must reflect
capacity consumption relevant to the same X.
```

---

### G6 — Acceptance Transition Visibility Guarantee

Derived from:

```text
CAD-05 — Evaluation-to-Commit Gap

PN-04 — Protect the transition from evaluation to committed accepted reservation state.
```

Protection problem exposed:

```text
A reservation can be evaluated as acceptable,
but fail to participate in capacity-consuming committed state
soon enough to protect overlapping decisions.
```

Simple meaning:

```text
Acceptance must become capacity-consuming in the correctness model.
```

Formal guarantee:

```text
Once a reservation decision is accepted,
its quantity must participate in committed accepted reservation quantity
for affected reservation-capacity decisions.
```

Invariant trace:

```text
Accepted reservations must become part of the total protected by the invariant.
```

---

### G7 — Rejection Non-Consumption Guarantee

Derived from:

```text
CAD-06 — Rejection Consumption

PN-05 — Preserve rejection behavior so rejected reservations never consume committed accepted capacity.
```

Protection problem exposed:

```text
Rejected reservations can incorrectly consume capacity.
```

Simple meaning:

```text
Rejected reservations must not reduce capacity.
```

Formal guarantee:

```text
Rejected reservation decisions must not increase
committed accepted reservation quantity.
```

Invariant trace:

```text
Only accepted reservations contribute to the committed accepted reservation quantity
protected by the invariant.
```

---

### G8 — Local Responsibility Boundary Guarantee

Derived from:

```text
CAD-07 — Responsibility Boundary Escape

Run 1 boundary and Run 3 invariant scope.
```

Protection problem exposed:

```text
Local reservation-capacity correctness can incorrectly depend on downstream systems,
external inventory behavior, compensation, or reconciliation.
```

Simple meaning:

```text
Inventory Reservation must prevent overselling locally.
```

Formal guarantee:

```text
The guarantees for this slice must be preserved inside Inventory Reservation
and must not depend on order creation,
payment interpretation,
final outcome composition,
external inventory correctness,
reservation expiration,
reservation release,
or reconciliation.
```

Invariant trace:

```text
The invariant is local to Inventory Reservation,
so preserving it must also be a local Inventory Reservation responsibility.
```

---

## 7. Guarantee-to-Invariant Trace

This section maps each guarantee to the invariant weakness or protection area it exists to preserve.

It answers:

```text
If this guarantee did not exist,
what part of the invariant would become vulnerable?
```

This is not implementation mapping.

It is correctness-protection mapping.

| Guarantee                                       | Protects                             | Why it exists                                                                       |
| ----------------------------------------------- | ------------------------------------ | ----------------------------------------------------------------------------------- |
| G1 — Same-Capacity Scope Guarantee              | consistent reservable-capacity scope | prevents quantity and capacity from being compared against different capacities     |
| G2 — Quantity-Based Acceptance Safety Guarantee | safe quantity-based acceptance       | prevents accepting a request whose quantity would oversell capacity                 |
| G3 — Combined Committed Quantity Guarantee      | group committed quantity             | prevents isolated request reasoning from missing collective overflow                |
| G4 — Concurrent Acceptance Integrity Guarantee  | overlap safety under concurrency     | prevents concurrent decisions from spending the same remaining capacity             |
| G5 — Capacity View Freshness Guarantee          | freshness of capacity-relevant state | prevents stale or incomplete views from allowing invalid acceptance                 |
| G6 — Acceptance Transition Visibility Guarantee | accepted quantity visibility         | ensures accepted reservations participate in committed capacity consumption         |
| G7 — Rejection Non-Consumption Guarantee        | accepted/rejected distinction        | prevents rejected reservations from consuming capacity                              |
| G8 — Local Responsibility Boundary Guarantee    | local slice ownership                | prevents overselling prevention from being pushed to downstream or external systems |

---

## 8. CAD-to-Guarantee Coverage

This section maps each correctness attack direction to the guarantees that protect against it.

It answers:

```text
Which guarantees defend against each stable attack direction?
```

| CAD                                     | Attack Direction                                                          | Protected By |
| --------------------------------------- | ------------------------------------------------------------------------- | ------------ |
| CAD-01 — Scope Corruption               | quantity and limit use different capacity identities                      | G1           |
| CAD-02 — Quantity Miscalculation        | acceptance ignores requested quantity or tracks count instead of quantity | G2, G3       |
| CAD-03 — Collective Over-Acceptance     | individually acceptable decisions become collectively invalid             | G3, G4       |
| CAD-04 — Stale Capacity View Acceptance | unsafe acceptance uses outdated or incomplete capacity view               | G5           |
| CAD-05 — Evaluation-to-Commit Gap       | evaluated acceptance and committed capacity state diverge                 | G4, G6       |
| CAD-06 — Rejection Consumption          | rejected decisions consume capacity                                       | G7           |
| CAD-07 — Responsibility Boundary Escape | local correctness is pushed outside Inventory Reservation                 | G8           |

---

## 9. Guarantee Weakness Challenge

The candidate guarantees are intentionally treated as incomplete protection proposals.

The challenge process attempts to expose weak wording, missing protection, ambiguity, loopholes, and incomplete protection coverage.

When weaknesses are exposed, the guarantees are refined into stronger and more explicit protections.

This section asks:

```text
Can these guarantees still fail?
Are they too weak?
Are they too vague?
Can they be bypassed?
Is any protection still missing?
```

The goal is not to reject the guarantees immediately.

The goal is to expose weak points and strengthen the guarantees before final correctness construction.

---

### Challenge 1 — Scope Corruption Bypass

Tests:

```text
Can the guarantees protect the invariant
if committed quantity and capacity limit use different reservable-capacity scopes?
```

Simple meaning:

```text
A guarantee is useless if it compares quantity for one thing
against capacity for another thing.
```

Weak guarantee:

```text
Check quantity against a capacity limit.
```

Weakness exposed:

```text
The guarantee becomes meaningless
if quantity and limit refer to different reservable capacities.
```

Refinement result:

```text
G1 must explicitly require the same reservable-capacity scope.
```

Protected CAD:

```text
CAD-01 — Scope Corruption
```

---

### Challenge 2 — Requested Quantity Ignored

Tests:

```text
Can the guarantees preserve the invariant
if acceptance ignores the quantity being accepted?
```

Simple meaning:

```text
It is not enough to know that some capacity remains.
The requested quantity must also fit.
```

Weak guarantee:

```text
Accept when some capacity remains.
```

Weakness exposed:

```text
Remaining capacity may exist,
but the requested quantity may still overflow the limit.
```

Refinement result:

```text
G2 must explicitly include requested quantity
inside the acceptance comparison.
```

Protected CAD:

```text
CAD-02 — Quantity Miscalculation
```

---

### Challenge 3 — Isolated Request Reasoning

Tests:

```text
Can the guarantees preserve correctness
if each request is evaluated independently?
```

Simple meaning:

```text
Each request can look safe alone,
while the accepted group becomes unsafe together.
```

Weak guarantee:

```text
Each request must individually fit.
```

Weakness exposed:

```text
Individually acceptable requests
can become collectively invalid.
```

Refinement result:

```text
G3 must explicitly protect the combined committed effect.
```

Protected CAD:

```text
CAD-03 — Collective Over-Acceptance
```

---

### Challenge 4 — Concurrent Double-Spend of Capacity

Tests:

```text
Can concurrent decisions jointly violate the invariant
even if each decision appears valid when evaluated?
```

Simple meaning:

```text
Two overlapping decisions must not both believe
they can safely consume the same capacity.
```

Weak guarantee:

```text
Each decision must be valid during evaluation.
```

Weakness exposed:

```text
Concurrent acceptance paths can jointly oversell capacity.
```

Refinement result:

```text
G4 must explicitly protect overlapping accepted decisions.
```

Protected CAD:

```text
CAD-03 — Collective Over-Acceptance
CAD-05 — Evaluation-to-Commit Gap
```

---

### Challenge 5 — Stale View Trusted

Tests:

```text
Can stale or incomplete capacity views
produce invalid accepted reservations?
```

Simple meaning:

```text
Even a good acceptance rule can fail
if it uses outdated capacity information.
```

Weak guarantee:

```text
Accept when the observed remaining capacity looks sufficient.
```

Weakness exposed:

```text
Observed capacity may already be outdated or incomplete.
```

Refinement result:

```text
G5 must explicitly protect against stale or incomplete capacity views.
```

Protected CAD:

```text
CAD-04 — Stale Capacity View Acceptance
```

---

### Challenge 6 — Acceptance Not Visible as Consumption

Tests:

```text
Can an accepted reservation be missing
from later affected reservation-capacity decisions?
```

Simple meaning:

```text
Once a reservation is accepted,
competing decisions must not behave as if it does not exist.
```

Weak guarantee:

```text
A reservation only needs to be valid when evaluated.
```

Weakness exposed:

```text
Accepted reservations may not participate
in overlapping capacity decisions.
```

Refinement result:

```text
G6 must protect accepted reservation visibility as committed capacity consumption.
```

Protected CAD:

```text
CAD-05 — Evaluation-to-Commit Gap
```

---

### Challenge 7 — Rejection Consumes Capacity

Tests:

```text
Can rejected reservations distort committed accepted quantity?
```

Simple meaning:

```text
A failed reservation must not reduce available capacity.
```

Weak guarantee:

```text
All reservation decisions affect committed quantity.
```

Weakness exposed:

```text
Rejected reservations can incorrectly consume capacity.
```

Refinement result:

```text
G7 must preserve accepted-versus-rejected semantics.
```

Protected CAD:

```text
CAD-06 — Rejection Consumption
```

---

### Challenge 8 — Local Responsibility Escapes the Slice

Tests:

```text
Can local reservation-capacity correctness
depend on downstream or external systems?
```

Simple meaning:

```text
Inventory Reservation must prevent overselling itself.
It must not rely on later order, payment, outcome, reconciliation, or external inventory behavior to repair it.
```

Weak guarantee:

```text
Downstream areas can compensate for overselling later.
```

Weakness exposed:

```text
The local invariant may already be violated
before downstream behavior reacts.
```

Refinement result:

```text
G8 must preserve the local Inventory Reservation boundary.
```

Protected CAD:

```text
CAD-07 — Responsibility Boundary Escape
```

---

## 10. Guarantee Challenge Findings

The guarantee challenge showed that the candidate guarantees were directionally correct, but several protections needed to be made explicit.

Each finding below shows which attack direction was challenged, what could fail, and which guarantee was strengthened.

| Challenge   | What failed or could become weak                                                | CAD            | Guarantee strengthened |
| ----------- | ------------------------------------------------------------------------------- | -------------- | ---------------------- |
| Challenge 1 | quantity and capacity could use different reservable-capacity scopes            | CAD-01         | G1                     |
| Challenge 2 | acceptance could ignore requested quantity                                      | CAD-02         | G2                     |
| Challenge 3 | correctness could be checked request-by-request instead of as a committed group | CAD-03         | G3                     |
| Challenge 4 | overlapping acceptance paths could jointly oversell capacity                    | CAD-03, CAD-05 | G4                     |
| Challenge 5 | decisions could trust stale or incomplete capacity views                        | CAD-04         | G5                     |
| Challenge 6 | accepted reservations could be invisible as capacity consumption                | CAD-05         | G6                     |
| Challenge 7 | rejected reservations could incorrectly consume capacity                        | CAD-06         | G7                     |
| Challenge 8 | local correctness could be pushed onto downstream or external systems           | CAD-07         | G8                     |

The refined guarantees now preserve:

- consistent reservable-capacity scope
- quantity-based acceptance safety
- combined committed quantity correctness
- concurrency safety
- freshness of capacity-relevant decision state
- visibility of accepted capacity consumption
- rejection correctness
- local slice responsibility boundaries

These findings are not implementation decisions.

They are correctness conclusions about what the final guarantee set must protect.

---

## 11. Refined Guarantee Set

The following guarantees survived the Run 4 challenge and are accepted as the protection set for preserving the invariant under concurrency.

The guarantees below are no longer just candidate protections.

They have been challenged, refined, and strengthened against the weaknesses exposed during Section 9.

| Guarantee                                       | What failed or could become weak                                                        | Refined protection                                                                                                              |
| ----------------------------------------------- | --------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| G1 — Same-Capacity Scope Guarantee              | quantity and capacity could be compared against different reservable capacities         | requested quantity, committed accepted quantity, and capacity limit must be evaluated within the same reservable-capacity scope |
| G2 — Quantity-Based Acceptance Safety Guarantee | acceptance could ignore requested quantity or protect request count instead of quantity | acceptance must include requested quantity against committed accepted quantity and capacity limit for the same scope            |
| G3 — Combined Committed Quantity Guarantee      | requests could appear safe individually while becoming unsafe together                  | correctness must protect the combined committed accepted quantity, not isolated requests                                        |
| G4 — Concurrent Acceptance Integrity Guarantee  | overlapping decisions could jointly oversell capacity                                   | concurrent or overlapping decisions must not commit accepted reservations whose combined quantity exceeds the capacity limit    |
| G5 — Capacity View Freshness Guarantee          | outdated or incomplete capacity information could allow invalid acceptance              | accepted decisions must not be based on capacity views that exclude relevant committed accepted reservations                    |
| G6 — Acceptance Transition Visibility Guarantee | accepted reservations could fail to participate in committed capacity consumption       | accepted reservations must participate in committed accepted reservation quantity for affected decisions                        |
| G7 — Rejection Non-Consumption Guarantee        | rejected reservations could incorrectly consume capacity                                | rejected decisions must not increase committed accepted reservation quantity                                                    |
| G8 — Local Responsibility Boundary Guarantee    | local correctness could depend on downstream or external systems                        | Inventory Reservation must preserve local reservation-capacity correctness without relying on downstream correction             |

The refined guarantees for `SL-01 — Reservation Capacity Correctness` are:

- G1 — Same-Capacity Scope Guarantee
- G2 — Quantity-Based Acceptance Safety Guarantee
- G3 — Combined Committed Quantity Guarantee
- G4 — Concurrent Acceptance Integrity Guarantee
- G5 — Capacity View Freshness Guarantee
- G6 — Acceptance Transition Visibility Guarantee
- G7 — Rejection Non-Consumption Guarantee
- G8 — Local Responsibility Boundary Guarantee

Together they preserve the refined invariant:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
must be less than or equal to
the local reservation capacity limit for X.
```

under concurrent reservation pressure.

At this point, the guarantees are accepted as the refined protection set that Run 5 should consolidate into final correctness construction.

---

## 12. Final CAD Coverage Verification

This section verifies that the refined guarantees now protect the correctness attack directions discovered and stabilized during Run 4.

The goal is to confirm that the refined protection set covers the known overselling risks exposed during Run 2, Run 3, and Run 4.

This section does not introduce new guarantees.

It validates that the refined guarantees are now strong enough to cover the discovered attack directions.

| Source | Attack Direction | Protected By |
| --- | --- | --- |
| Run 2 — Failure Mechanism | concurrent reservation attempts can each appear acceptable against the same remaining capacity, then all be accepted, causing committed accepted reservation quantity to exceed the local reservation capacity limit | G2, G3, G4, G5, G6 |
| Run 3 — PN-01 | overlapping individually acceptable decisions become collectively invalid after commitment | G3, G4 |
| CAD-03 — Collective Over-Acceptance | multiple individually acceptable decisions become invalid as a committed group | G3, G4 |
| Run 3 — PN-02 | mixed request sizes overflow capacity if quantity is not protected | G2, G3 |
| CAD-02 — Quantity Miscalculation | acceptance ignores requested quantity or tracks count instead of quantity | G2, G3 |
| Run 3 — PN-03 | stale or incomplete views produce invalid accepted reservations | G5 |
| CAD-04 — Stale Capacity View Acceptance | unsafe acceptance uses outdated or incomplete capacity information | G5 |
| Run 3 — PN-04 | evaluation and committed accepted state diverge | G4, G6 |
| CAD-05 — Evaluation-to-Commit Gap | evaluated acceptance and committed capacity state diverge | G4, G6 |
| Run 3 — PN-05 | rejected reservations consume committed accepted capacity | G7 |
| CAD-06 — Rejection Consumption | rejected decisions consume capacity | G7 |
| Run 3 — PN-06 | quantity and limit use inconsistent capacity identity | G1 |
| CAD-01 — Scope Corruption | quantity and capacity limit use different capacity identities | G1 |
| Run 1 boundary / Run 3 invariant scope | local correctness expands or escapes into downstream/external systems | G8 |
| CAD-07 — Responsibility Boundary Escape | local correctness is pushed outside Inventory Reservation | G8 |

The refined guarantees are now strong enough to protect the invariant against the known overselling attack directions discovered during earlier reasoning.

Together the refined guarantees preserve:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
must be less than or equal to
the local reservation capacity limit for X.
```

under concurrent reservation pressure.

The refined guarantee set is stable enough for final correctness construction.

---

## 13. Deferred or Later Concerns

The following concerns remain outside Run 4 and outside the current slice boundary unless later reasoning proves they directly affect local overselling:

```text
Implementation strategy:
    Later work must decide how these guarantees are enforced.
    This run does not choose locking, conditional updates, transactions, versioning, retries, schema structure, repositories, or framework mechanisms.

Database schema:
    Later work may need persistence structure,
    but this run does not define tables, columns, constraints, indexes, migrations, or SQL behavior.

API design:
    Later work may expose observable behavior,
    but this run does not define endpoint paths, HTTP methods, request bodies, or response bodies.

Reservation expiration:
    Expiration may affect future capacity reuse,
    but it is not required to preserve the immediate local invariant.

Reservation release:
    Release may affect future capacity reuse,
    but it is not required to preserve the immediate local invariant.

External inventory synchronization:
    External inventory behavior is outside local Inventory Reservation correctness.

Order creation:
    Order behavior may depend on reservation decisions,
    but it does not own local reservation-capacity correctness.

Payment interpretation:
    Payment behavior is outside this slice.

Final outcome composition:
    Cross-area semantic consistency is deferred to later project versions.
```

None of these deferred concerns weakens the refined guarantee set for local reservation-capacity correctness.

---

## 14. Signals for Run 5

Run 5 should consolidate the refined invariant, correctness attack directions, and refined guarantee set that survived the Run 4 challenge.

The goal is not to invent new protection.

The goal is to consolidate the accepted protections against the known overselling attack directions discovered during earlier reasoning.

Run 5 should consolidate:

```text
Target failure:
    Overselling

Failure mechanism:
    Concurrent reservation attempts can each appear acceptable against the same remaining capacity,
    then all be accepted, causing committed accepted reservation quantity
    to exceed the local reservation capacity limit.

Invariant:
    committed accepted reservation quantity for X
    must remain <= local reservation capacity limit for X
```

Correctness attack directions:

```text
CAD-01 — Scope Corruption
CAD-02 — Quantity Miscalculation
CAD-03 — Collective Over-Acceptance
CAD-04 — Stale Capacity View Acceptance
CAD-05 — Evaluation-to-Commit Gap
CAD-06 — Rejection Consumption
CAD-07 — Responsibility Boundary Escape
```

Guarantees:

```text
G1 — Same-Capacity Scope Guarantee
    → protects CAD-01 — Scope Corruption

G2 — Quantity-Based Acceptance Safety Guarantee
    → protects CAD-02 — Quantity Miscalculation

G3 — Combined Committed Quantity Guarantee
    → protects CAD-03 — Collective Over-Acceptance

G4 — Concurrent Acceptance Integrity Guarantee
    → protects CAD-03 — Collective Over-Acceptance
    → protects CAD-05 — Evaluation-to-Commit Gap

G5 — Capacity View Freshness Guarantee
    → protects CAD-04 — Stale Capacity View Acceptance

G6 — Acceptance Transition Visibility Guarantee
    → protects CAD-05 — Evaluation-to-Commit Gap

G7 — Rejection Non-Consumption Guarantee
    → protects CAD-06 — Rejection Consumption

G8 — Local Responsibility Boundary Guarantee
    → protects CAD-07 — Responsibility Boundary Escape
```

Run 5 should preserve traceability between:

```text
target failure
    → failure mechanism
    → invariant
    → correctness attack directions
    → guarantees
    → protection responsibility
```

Run 5 should synthesize the refined invariant, CADs, and guarantee set into `correctness-construction.md`.

Run 5 must not introduce implementation strategy, schema design, API design, repository design, migration design, framework design, or test implementation.

---

## 15. Run 4 Result

Run 4 stabilized the Run 3 protection needs into correctness attack directions.

The CADs are:

```text
CAD-01 — Scope Corruption
CAD-02 — Quantity Miscalculation
CAD-03 — Collective Over-Acceptance
CAD-04 — Stale Capacity View Acceptance
CAD-05 — Evaluation-to-Commit Gap
CAD-06 — Rejection Consumption
CAD-07 — Responsibility Boundary Escape
```

`CAD-07 — Responsibility Boundary Escape` is boundary-derived rather than PN-derived, because local correctness cannot be preserved if the slice allows overselling responsibility to escape Inventory Reservation.

Run 4 derived, challenged, and refined guarantees that protect the accepted invariant against those attack directions.

The refined guarantees are:

```text
G1 — Same-Capacity Scope Guarantee
G2 — Quantity-Based Acceptance Safety Guarantee
G3 — Combined Committed Quantity Guarantee
G4 — Concurrent Acceptance Integrity Guarantee
G5 — Capacity View Freshness Guarantee
G6 — Acceptance Transition Visibility Guarantee
G7 — Rejection Non-Consumption Guarantee
G8 — Local Responsibility Boundary Guarantee
```

Together, these guarantees preserve the refined invariant:

```text
For each reservable capacity X:

total committed accepted reservation quantity for X
must be less than or equal to
the local reservation capacity limit for X.
```

The guarantees preserve the refined invariant under concurrency without requiring implementation decisions yet.

The refined guarantees now protect against:

- scope corruption
- quantity miscalculation
- collective over-acceptance
- stale capacity view acceptance
- evaluation-to-commit gaps
- rejection consumption
- responsibility boundary escape

The reasoning is stable enough for `Run 5 — Final Correctness Construction`.

Run 5 should consolidate:

```text
target failure
    → failure mechanism
    → invariant
    → CADs
    → guarantees
    → deferred concerns
```

without introducing implementation strategy prematurely.
