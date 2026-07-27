![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)
![JWT](https://img.shields.io/badge/Auth-JWT-red)

# 🚀 Distributed Job Scheduler

A production-inspired backend application built with **Spring Boot** that allows users to schedule, execute, and monitor system jobs asynchronously. The project demonstrates authentication, multithreading, scheduling, retry mechanisms, Docker containerization, and clean backend architecture.

---

## ✨ Features

- 🔐 JWT Authentication & Authorization
- 👤 User Registration & Login
- 📅 Schedule jobs for future execution
- ⚡ Execute jobs immediately
- 🔄 Automatic background scheduler
- 🧵 Multi-threaded execution using `ExecutorService`
- 📜 Job execution history
- ♻️ Configurable retry mechanism
- ⏱️ Execution timeout handling
- 🐳 Docker & Docker Compose support
- 🌍 Environment variable configuration
- ⚠️ Global exception handling

---

# 🏗️ Architecture

```text
                  REST Client
                      │
                      ▼
          ┌────────────────────────┐
          │ Spring Boot REST API   │
          └────────────┬───────────┘
                       │
          ┌────────────┴─────────────┐
          │                          │
          ▼                          ▼
 Authentication Service        Job Service
          │                          │
          └────────────┬─────────────┘
                       ▼
                PostgreSQL Database
                       ▲
                       │
              Scheduler Service
                 (@Scheduled)
                       │
                       ▼
              ExecutorService Pool
                       │
                       ▼
             Execute System Commands
```

---

# ⚙️ Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 25 |
| Framework | Spring Boot |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA (Hibernate) |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |
| Concurrency | ExecutorService |
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
├── repository
├── security
├── service
│   ├── impl
│   └── interfaces
├── scheduler
└── util
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
Scheduler Polls Database
     │
     ▼
Claim Job
     │
     ▼
ExecutorService Thread
     │
     ▼
Execute Command
     │
     ▼
Success / Failure
     │
     ▼
Save Execution History
```

---

# 🐳 Running with Docker

## Clone Repository

```bash
git clone https://github.com/Marya45/job-scheduler.git
cd job-scheduler
```

## Create .env

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/job-scheduler
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

JWT_SECRET=your_secret_key
JWT_EXPIRATION=3600000
```

## Start

```bash
docker compose up --build
```

Application:

```
http://localhost:8080
```

---

# 📡 REST APIs

## Authentication

| Method | Endpoint |
|---------|----------|
| POST | /api/auth/register |
| POST | /api/auth/login |

## Jobs

| Method | Endpoint |
|---------|----------|
| GET | /api/jobs |
| GET | /api/jobs/{id} |
| POST | /api/jobs |
| PUT | /api/jobs/{id} |
| DELETE | /api/jobs/{id} |

## Execution

| Method | Endpoint |
|---------|----------|
| POST | /api/jobs/{id}/run |
| GET | /api/jobs/{id}/executions |

---

# 💡 Highlights

- Designed an asynchronous job execution pipeline using Spring Scheduler and ExecutorService.
- Implemented configurable retry logic and timeout handling for failed executions.
- Recorded detailed execution history including output, exit code, duration, and failure reason.
- Secured REST APIs using JWT-based authentication with Spring Security.
- Containerized the application using Docker and Docker Compose.
- Centralized error handling using `@RestControllerAdvice`.

---

# 🚀 Future Improvements

- Cron Expression Support
- Recurring Jobs
- Kafka/RabbitMQ Queue
- Distributed Workers
- Kubernetes Deployment
- Grafana Monitoring
- Worker Heartbeat & Health Checks

---

# 👨‍💻 Author

**Rohan Marya**