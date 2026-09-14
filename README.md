![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Message_Broker-FF6600)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)
![JWT](https://img.shields.io/badge/Auth-JWT-red)

# 🚀 Distributed Job Scheduler

A distributed backend job scheduling system built with **Spring Boot, RabbitMQ, PostgreSQL, and Docker**. It supports authenticated job creation, scheduled and recurring execution, multiple worker instances, atomic job claiming, configurable retries, timeout handling, execution history, and dead-letter processing for failed jobs.

The project demonstrates distributed job processing, message-driven architecture, concurrency control, fault handling, containerized deployment, and secure REST API design.

---

## ✨ Features

- 🔐 JWT Authentication & Authorization
- 👤 User Registration & Login
- 📅 Immediate and scheduled job execution
- 🔄 Recurring job execution
- ⏰ Automatic background scheduling using Spring Scheduler
- 📨 RabbitMQ-based asynchronous job distribution
- 👷 Multiple independent worker instances
- 🔒 Atomic database job claiming to prevent duplicate execution
- ♻️ Configurable retry mechanism
- 💀 Dead Letter Queue (DLQ) for permanently failed jobs
- ⏱️ Job execution timeout handling
- 📜 Persistent execution history
- 📤 Standard output, error output, and exit-code tracking
- 🐳 Multi-container deployment using Docker Compose
- 🌍 Environment-based configuration
- ⚠️ Centralized exception handling

---

# 🏗️ Architecture

```text
                         Client
                           │
                           ▼
                  ┌─────────────────┐
                  │ Spring Boot API │
                  │   + Scheduler   │
                  └────────┬────────┘
                           │
                     publish jobId
                           │
                           ▼
                  ┌─────────────────┐
                  │    RabbitMQ     │
                  │    job.queue    │
                  └───────┬─────────┘
                          │
                  ┌───────┴────────┐
                  ▼                ▼
           ┌────────────┐    ┌────────────┐
           │  Worker 1  │    │  Worker 2  │
           └──────┬─────┘    └──────┬─────┘
                  │                  │
                  └────────┬─────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ PostgreSQL  │
                    │ Atomic Claim│
                    └─────────────┘


Failure after retries:

Worker
   │
   ▼
Reject Message
   │
   ▼
 job.dlx
   │
   ▼
 job.dlq
```

---

# ⚙️ Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 25 |
| Framework | Spring Boot 4.x |
| Security | Spring Security + JWT |
| Message Broker | RabbitMQ |
| Database | PostgreSQL 17 |
| ORM | Spring Data JPA / Hibernate |
| Scheduling | Spring Scheduler |
| Concurrency | RabbitMQ Concurrent Consumers |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |
| API Testing | Postman |

---

# 📂 Project Structure

```text
src
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── enums
├── exception
├── queue
├── repository
├── security
├── service
│   ├── impl
│   └── interfaces
├── scheduler
└── worker
```

---

# 🗄️ Database Schema

### User

- id
- username
- email
- password
- role
- createdAt
- updatedAt

### Job

- id
- name
- command
- status
- scheduledAt
- recurrenceType
- maxRetries
- createdBy
- createdAt
- updatedAt

### JobExecution

- id
- job
- status
- output
- errorMessage
- exitCode
- startedAt
- completedAt

---

# 🔄 Execution Flow

```text
User Login
     │
     ▼
Create Job
     │
     ▼
Save as PENDING
     │
     ▼
Scheduler Polls PostgreSQL
     │
     ▼
Publish Job ID to RabbitMQ
     │
     ▼
RabbitMQ job.queue
     │
     ▼
Worker Receives Message
     │
     ▼
Atomic PENDING → RUNNING Claim
     │
     ├──── claim fails ────> Skip Duplicate
     │
     ▼
Execute Job
     │
     ▼
Retry on Failure
     │
     ├──── Success ────────> Save SUCCESS + Execution History
     │
     └──── Retries Exhausted
                    │
                    ▼
               Mark FAILED
                    │
                    ▼
                  DLQ
```

For recurring jobs, a successful execution calculates the next scheduled time and returns the job to `PENDING`.

---

# 🔒 Atomic Job Claiming

Multiple workers can consume jobs concurrently. To prevent the same job from being executed by more than one worker, jobs are claimed atomically in PostgreSQL.

Conceptually:

```sql
UPDATE jobs
SET status = 'RUNNING'
WHERE id = ?
  AND status = 'PENDING';
```

Only the worker that successfully updates the row is allowed to execute the job.

```text
Worker 1 → 1 row updated → Executes Job
Worker 2 → 0 rows updated → Skips Job
```

This allows duplicate message delivery to be tolerated without duplicate job execution.

---

# 💀 Failure Handling & Dead Letter Queue

Each job supports configurable application-level retries.

```text
RabbitMQ
   │
   ▼
Worker
   │
   ├── Attempt 1
   ├── Attempt 2
   ├── ...
   └── Retries Exhausted
            │
            ▼
       Reject Message
            │
            ▼
         job.dlx
            │
            ▼
         job.dlq
```

Messages are configured not to be infinitely requeued after processing failures.

---

# 🐳 Running with Docker

## Clone Repository

```bash
git clone https://github.com/Marya45/job-scheduler.git
cd job-scheduler
```

## Create `.env`

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/job-scheduler
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=jobscheduler
RABBITMQ_PASSWORD=your_password

JWT_SECRET=your_secret_key
JWT_EXPIRATION=3600000
```

## Start all services

```bash
docker compose up --build
```

Docker Compose starts:

```text
Spring Boot API + Scheduler
PostgreSQL
RabbitMQ
Worker 1
Worker 2
```

Application:

```text
http://localhost:8080
```

RabbitMQ Management UI:

```text
http://localhost:15672
```

---

# 📡 REST APIs

## Authentication

| Method | Endpoint |
|---------|----------|
| POST | `/api/auth/register` |
| POST | `/api/auth/login` |

## Jobs

| Method | Endpoint |
|---------|----------|
| GET | `/api/jobs` |
| GET | `/api/jobs/{id}` |
| POST | `/api/jobs` |
| PUT | `/api/jobs/{id}` |
| DELETE | `/api/jobs/{id}` |

## Execution

| Method | Endpoint |
|---------|----------|
| POST | `/api/jobs/{id}/run` |
| GET | `/api/jobs/{id}/executions` |

---

# 💡 Engineering Highlights

- Designed a distributed job-processing architecture using **Spring Boot and RabbitMQ**, decoupling job scheduling from execution.
- Implemented multiple independently running worker containers consuming jobs concurrently from a shared RabbitMQ queue.
- Added **atomic PostgreSQL job claiming** to prevent duplicate execution across competing workers.
- Implemented configurable retries, execution timeouts, and a **Dead Letter Queue** for permanently failed jobs.
- Built persistent execution tracking including status, timestamps, output, errors, and exit codes.
- Added recurring job scheduling and automatic next-run calculation.
- Secured REST APIs using **Spring Security and JWT authentication** with per-user job ownership.
- Containerized the API, PostgreSQL, RabbitMQ, and multiple workers using **Docker Compose**.
- Centralized API error handling using `@RestControllerAdvice`.

---

# 🚀 Future Improvements

- Cron expression support
- Dynamic worker autoscaling
- Worker heartbeat and health monitoring
- Prometheus metrics and Grafana dashboards
- Kubernetes deployment
- Distributed tracing
- Centralized logging
- Retry queues with configurable backoff
- Administrative monitoring dashboard

---

# 👨‍💻 Author

**Rohan Marya**