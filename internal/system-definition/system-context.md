# System Context

---

## **1. System of Interest**

The Checkout System is a backend system responsible for resolving purchase attempts into final outcomes.

---

## **2. System Boundary**

### **Inside the system**

* logic and state required to evaluate purchase attempts
* decision-making required to determine outcomes
* mechanisms to produce and expose final outcomes

---

### **Outside the system**

* payment execution systems
* external inventory systems
* user interfaces and clients

---

## **3. Inside the System**

The system owns:

* state required to support decision-making
* decision logic for purchase attempts
* final outcome representation

---

## **4. Outside the System**

External entities include:

* payment providers
* inventory or warehouse systems
* users and client applications

---

## **5. External Interactions**

Inputs:

* purchase requests
* payment signals
* external constraints and signals

Outputs:

* final outcomes
* externally observable system decisions

---

## **6. Authority Boundaries**

The system:

* **can decide**

  * outcomes of purchase attempts

* **can interpret**

  * external signals

* **cannot control**

  * external system behavior
  * execution of payment or inventory operations

---

## **7. Assumptions About Environment**

* external systems may send duplicate, delayed, or conflicting signals
* inputs may arrive out of order
* external systems are not fully reliable

---

## **8. Constraints from Environment**

* no global transactions across systems
* eventual consistency of external signals
* unreliable communication

---

## **9. Relationship to Other Documents**

This document:

* constrains system-structure, because ownership must fit within boundary
* constrains problem-space, because problems must exist within boundary
* constrains interaction-model, because interactions must respect authority limits
