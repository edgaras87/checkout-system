# System Structure

---

## **1. Purpose**

Define how the system is internally divided by ownership of decisions and state.

---

## **2. Structural Principle**

The system is divided based on:

```text
ownership of state and decisions required to resolve a purchase attempt into a final outcome
```

---

## **3. Responsibility Areas**

---

### **Inventory Reservation**

#### **Responsibility**

Manage reservation decisions for purchase attempts.

#### **Owned State**

* reservation records
* reserved quantities

#### **Owned Decisions**

* whether items can be reserved

#### **Does Not Own**

* physical inventory
* order creation
* payment interpretation
* final outcome

---

### **Order Management**

#### **Responsibility**

Create and maintain orders for purchase attempts.

#### **Owned State**

* order identity
* order records

#### **Owned Decisions**

* whether an order is created
* ensuring idempotent creation

#### **Does Not Own**

* reservation logic
* payment interpretation
* final outcome

---

### **Payment Outcome Handling**

#### **Responsibility**

Interpret payment signals.

#### **Owned State**

* interpreted payment outcomes

#### **Owned Decisions**

* classification of payment result

#### **Does Not Own**

* payment execution
* order creation
* final outcome

---

### **Checkout Outcome Resolution**

#### **Responsibility**

Produce the final outcome for a purchase attempt.

#### **Owned State**

* final outcome state

#### **Owned Decisions**

* final outcome determination

#### **Does Not Own**

* reservation decisions
* order creation
* payment execution

---

## **4. Ownership Boundaries**

* each decision belongs to exactly one responsibility area
* no shared ownership of state
* responsibility areas may depend on each other but cannot override decisions

---

## **5. Relationship Between Areas**

* reservation decisions influence order creation feasibility
* order existence enables payment association
* interpreted payment outcomes influence final outcome determination

---

## **6. Relationship to Other Documents**

* depends on system-context, because the structure must respect the system boundary
* constrains problem-space, because problems must map to responsibility areas
* constrains interaction-model, because it defines the participants in cross-area interactions
