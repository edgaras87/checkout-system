# Checkout System

A correctness-driven backend system for resolving each purchase attempt into one consistent and authoritative final outcome under concurrency, retries, and unreliable external signals.

---

## Overview

`checkout-system` explores how backend correctness can be defined, enforced, and validated when real-world conditions make simple request handling unsafe.

The system focuses on checkout outcome correctness, not full e-commerce functionality.

It is designed around clearly separated responsibilities:

```text
Inventory Reservation
Order Management
Payment Outcome Handling
Checkout Outcome Resolution
```

---

## Documentation

* [System Overview](docs/system-overview.md)
* [Methodology](docs/methodology.md)
* [Local Setup](docs/setup/README.md)
