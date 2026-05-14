# 📄 **Testcontainers with Podman — Troubleshooting**

---

## **1. Purpose**

```text
Diagnose local Testcontainers failures when using rootless Podman.
```

This document is only for local Podman troubleshooting.

For the normal setup path, see:

```text
docs/setup/testcontainers-podman.md
```

---

## **2. Common Failure Symptoms**

Testcontainers may fail with:

```text
Could not find a valid Docker environment
```

or:

```text
Could not find unix domain socket
```

It may also show that it tried:

```text
/var/run/docker.sock
```

This means Testcontainers is still looking for Docker’s default socket instead of the Podman socket.

Correct rootless Podman socket usually resolves to:

```text
/run/user/1000/podman/podman.sock
```

Portable form:

```text
${XDG_RUNTIME_DIR}/podman/podman.sock
```

Correct environment variable example:

```text
DOCKER_HOST=unix:///run/user/1000/podman/podman.sock
```

Use the actual Podman socket path if it differs on the local machine.

---

## **3. First Recovery Check**

Before changing test code, verify that the current shell can reach Podman:

```bash
echo $DOCKER_HOST
echo $TESTCONTAINERS_RYUK_DISABLED
podman info
ls -l ${XDG_RUNTIME_DIR}/podman/podman.sock
```

Expected environment values for rootless Podman:

```text
DOCKER_HOST=unix:///run/user/1000/podman/podman.sock
TESTCONTAINERS_RYUK_DISABLED=true
```

Then rerun:

```bash
./mvnw test
```

If this does not resolve the failure, continue with the checks below.

---

## **4. Verify Podman Socket Service**

Check the user socket:

```bash
systemctl --user status podman.socket
```

Expected state:

```text
Active: active (listening)
```

Check the configured socket path:

```bash
systemctl --user show podman.socket -p Listen
```

Expected path usually resolves to:

```text
/run/user/1000/podman/podman.sock
```

Portable form:

```text
${XDG_RUNTIME_DIR}/podman/podman.sock
```

---

## **5. Verify Socket File Exists**

The socket service must be active, but the actual socket file must also exist.

Check:

```bash
ls -l ${XDG_RUNTIME_DIR}/podman/podman.sock
```

Expected result:

```text
srw-rw-rw-... /run/user/1000/podman/podman.sock
```

The exact permissions may differ.

The important part is that the file type starts with:

```text
s
```

That means it is a Unix socket.

---

## **6. Verify Environment Variables**

Check:

```bash
echo $DOCKER_HOST
```

Expected example:

```text
unix:///run/user/1000/podman/podman.sock
```

Check:

```bash
echo $TESTCONTAINERS_RYUK_DISABLED
```

Expected value for rootless Podman:

```text
true
```

If either value is missing, export them again:

```bash
export DOCKER_HOST=unix://${XDG_RUNTIME_DIR}/podman/podman.sock
export TESTCONTAINERS_RYUK_DISABLED=true
```

Then rerun:

```bash
./mvnw test
```

`TESTCONTAINERS_RYUK_DISABLED=true` is a rootless Podman compatibility setting.

It is not a general requirement for Docker users.

---

## **7. IntelliJ Environment Issue**

Tests started from IntelliJ may not inherit shell environment variables.

If tests work from the terminal but fail in IntelliJ, add these variables to the test Run Configuration:

```text
DOCKER_HOST=unix:///run/user/1000/podman/podman.sock
TESTCONTAINERS_RYUK_DISABLED=true
```

Use the actual Podman socket path if it differs on the local machine.

For Maven runs from IntelliJ, also check:

```text
Settings → Build, Execution, Deployment → Build Tools → Maven → Runner
```

Add the same variables there if needed.

---

## **8. Active Socket but Missing Socket File**

Sometimes `podman.socket` may appear active:

```bash
systemctl --user status podman.socket
```

Example:

```text
Active: active (listening)
Listen: /run/user/1000/podman/podman.sock (Stream)
```

but the socket file may still be missing:

```bash
ls -l /run/user/1000/podman/podman.sock
```

Failure:

```text
ls: cannot access '/run/user/1000/podman/podman.sock': No such file or directory
```

This means the systemd socket unit is configured, but the actual Unix socket file is not currently available for Testcontainers.

Recover by restarting the user socket:

```bash
systemctl --user stop podman.socket
systemctl --user stop podman.service
systemctl --user start podman.socket
```

Then verify:

```bash
ls -l ${XDG_RUNTIME_DIR}/podman/podman.sock
```

After the socket file exists, rerun tests from an environment where `DOCKER_HOST` is set:

```bash
./mvnw test
```

Interpretation:

```text
podman.socket active is not enough by itself.
The test JVM must be able to reach the actual socket file.
```

---

## **9. Diagnostic Commands**

Use these commands when Testcontainers cannot find Podman:

```bash
systemctl --user status podman.socket
systemctl --user show podman.socket -p Listen
ls -ld ${XDG_RUNTIME_DIR}
ls -ld ${XDG_RUNTIME_DIR}/podman
ls -la ${XDG_RUNTIME_DIR}/podman
ls -l ${XDG_RUNTIME_DIR}/podman/podman.sock
echo $DOCKER_HOST
echo $TESTCONTAINERS_RYUK_DISABLED
podman info
```

For the usual rootless setup, the socket should resolve to:

```text
/run/user/1000/podman/podman.sock
```

Use the actual Podman socket path if it differs on the local machine.

---

## **10. Project Interpretation**

The project does not require Docker Desktop specifically.

The project requires a Docker-compatible container runtime that Testcontainers can reach.

Valid local runtime paths include:

```text
- Docker, usually auto-detected
- Podman, with socket setup
```

For rootless Podman, the required local setup is:

```text
- Podman installed
- podman.socket active
- Podman socket file exists
- DOCKER_HOST pointing to the Podman socket
- TESTCONTAINERS_RYUK_DISABLED=true for rootless Podman compatibility
```

Once these are set in the environment where the test JVM starts, Testcontainers can start PostgreSQL containers for integration tests.

If tests fail before Spring Boot or PostgreSQL initialization, verify the Podman socket and environment variables before changing test code.
