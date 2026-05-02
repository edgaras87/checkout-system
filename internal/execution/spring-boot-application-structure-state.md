# **📦 Spring Boot Application Structure State — checkout-system**

---

## **0. Artifact Identity**

### Name

```text
Spring Boot Application Structure State — checkout-system
```

### Version

```text
v1
```

### Artifact Type

```text
Project application structure state
```

---

## **1. Project Metadata**

```text
Project: Maven
Language: Java
Spring Boot: latest stable selected in Spring Initializr
Group: com.edge
Artifact: checkout-system
Package Name: com.edge.checkout
Packaging: Jar
Configuration: YAML
Java Version: 21
```

---

## **2. Package State**

```text
Base package: com.edge.checkout
```

```text
Java source path: src/main/java/com/edge/checkout/
```

```text
Java test path: src/test/java/com/edge/checkout/
```

```text
Resource path: src/main/resources/
```

---

## **3. Application Structure Orientation**

```text
src/main/java/com/edge/checkout/
├── CheckoutSystemApplication.java
├── application/
├── domain/
├── infra/
└── web/
```

Status:

```text
minimal application entry point exists

application roots are defined as structural orientation
```

---

## **4. Resource Structure Orientation**

```text
src/main/resources/
├── application.yaml
└── db/
    └── migration/
```

Status:

```text
resource structure is oriented around YAML configuration and Flyway migrations
```

---

## **5. Test Structure Orientation**

```text
src/test/java/com/edge/checkout/
└── testsupport/
```

Status:

```text
test structure is introduced gradually as tests are added
```

---

## **6. Current Implementation Orientation**

```text
Current implementation focus: reservation-related application growth
```

---

## **7. Reservation-Related Structure Orientation**

Reservation-related implementation may grow toward:

```text
src/main/java/com/edge/checkout/
├── CheckoutSystemApplication.java
│
├── application/
│   ├── port/
│   └── reserve/
│
├── domain/
│   └── reservation/
│
├── infra/
│   ├── db/
│   │   └── reservation/
│   └── config/
│
└── web/
    ├── reserve/
    └── ApiExceptionHandler.java
```

Corresponding test orientation:

```text
src/test/java/com/edge/checkout/
├── testsupport/
├── application/
│   └── reserve/
├── domain/
│   └── reservation/
├── db/
│   └── reservation/
└── web/
    └── reserve/
```

---

## **8. Package Meaning**

```text
application/ = use-case flow and application decisions

domain/ = business concepts and rules

infra/ = technical implementation

web/ = HTTP/API entry layer

src/main/resources/ = runtime configuration and migrations

src/test/java/ = verification code
```

---

## **9. Current Growth Orientation**

For the current implementation focus, package growth is oriented around reservation-related implementation:

```text
application/port/
application/reserve/
domain/reservation/
infra/db/reservation/
infra/config/
web/reserve/
```

Test growth is oriented around:

```text
src/test/java/com/edge/checkout/application/reserve/
src/test/java/com/edge/checkout/domain/reservation/
src/test/java/com/edge/checkout/db/reservation/
src/test/java/com/edge/checkout/web/reserve/
```

---

## **10. Deferred Package Areas**

Not introduced yet:

```text
application/place/
application/interpretpayment/
application/resolveoutcome/

domain/order/
domain/payment/
domain/outcome/
domain/purchaseattempt/

infra/db/order/
infra/db/payment/
infra/db/outcome/

web/place/
web/payment/
web/outcome/
```

Reason:

```text
Order Management, Payment Outcome Handling, and Checkout Outcome Resolution
are not the current implementation focus.
```

---

## **11. Boundary State**

The following are not represented as Java root packages:

```text
Inventory Reservation
Order Management
Payment Outcome Handling
Checkout Outcome Resolution
SL-01
invariants
guarantees
proofs
requirements
```

They are represented through:

```text
documents
implementation changes
tests
commits
```

---

## **12. Current Orientation Summary**

```text
checkout-system is one system.

com.edge.checkout is the Java base package.

The Spring Boot application entry point is CheckoutSystemApplication.java.

The application structure is oriented around application/, domain/, infra/, and web/.

The current implementation focus is reservation-related application growth.

Reservation-related packages may appear first.

Order, payment, and final outcome packages are deferred.

Correctness artifacts do not become Java packages.
```
