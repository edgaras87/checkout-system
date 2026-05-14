# 📄 **Testcontainers with Podman**

---

## **1. Purpose**

```text
Run integration tests using Testcontainers when the local container runtime is Podman.
```

The project requires a Docker-compatible runtime for tests.

Podman is supported via its Docker-compatible socket.

---

## **2. Default Test Command**

From the project root:

```bash
./mvnw test
```

This is the standard way to run all tests.

---

## **3. Podman Quick Setup (Rootless)**

### **3.1 Enable Podman Socket**

```bash
systemctl --user enable --now podman.socket
```

---

### **3.2 Configure Environment**

```bash
export DOCKER_HOST=unix://${XDG_RUNTIME_DIR}/podman/podman.sock
export TESTCONTAINERS_RYUK_DISABLED=true
```

---

### **3.3 Run Tests**

```bash
./mvnw test
```

---

## **4. IntelliJ Configuration**

Tests started from IntelliJ may not inherit shell environment variables.

Add the following to the Run Configuration:

```text
DOCKER_HOST=unix:///run/user/1000/podman/podman.sock
TESTCONTAINERS_RYUK_DISABLED=true
```

Use the actual Podman socket path if it differs on the local machine.

---

## **5. Project-Scoped Setup (Optional)**

Instead of exporting variables globally, create a local file:

```text
.env.testcontainers.local
```

Content:

```bash
export DOCKER_HOST=unix://${XDG_RUNTIME_DIR}/podman/podman.sock
export TESTCONTAINERS_RYUK_DISABLED=true
```

Run tests with:

```bash
source .env.testcontainers.local
./mvnw test
```

This file is machine-specific and must not be committed.

The repository should ignore it:

```gitignore
.env.testcontainers.local
```

---

## **6. CI Note**

```text
CI runs use the default test command without Podman-specific configuration.
```

---

## **7. Scope**

```text
This document covers only the minimal setup required to run tests with Podman.
```

For failures, socket issues, or diagnostics, see:

```text
docs/setup/testcontainers-podman-troubleshooting.md
```
