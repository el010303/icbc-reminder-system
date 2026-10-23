# ICBC Reminder System - Backend Infrastructure

Backend reliability components I built during my Software Engineering internship at ICBC (Jun–Aug 2026), on top of an existing internal reminder system: a cached monitoring API, a scheduled email-retry workflow with idempotency, a query-index optimization, audit logging, and API-key authentication.

> **Note:** This repository contains only the code I wrote. It includes no production data, credentials, customer information, or ICBC proprietary business logic. Table names are kept so the queries show how the components integrate with the existing schema.

## Tech Stack

- **Java 17** + **Spring Boot 3.5**
- **MySQL** - Primary database
- **Redis** - Caching layer
- **MyBatis** - ORM framework
- **JUnit 5** + **Mockito** - Unit testing
- **Swagger/OpenAPI 3** - API documentation

## Features

### 1. Real-time Monitoring Dashboard
- REST API returning live system metrics
- One endpoint aggregates eight operational metrics: total / pending / completed reminders, active users, emails sent today, new reminders this month, email send success rate, and failed emails
- Redis cache-aside with a 60-second TTL, so the 30-second dashboard poll does not re-run eight aggregation queries every time; manual invalidation endpoint

### 2. Email Reliability Engineering
- Scheduled retry job (`@Scheduled(fixedDelay = 30s)`) picks up `PENDING` rows and resends them through Gmail SMTP (JavaMailSender)
- After 3 failed attempts a row is marked `FAILED` and is never retried again
- Idempotency key `remindId_email_date` + a unique constraint + `INSERT IGNORE`: reporting the same failure twice on the same day creates only one retry row
- Every retry attempt (success or failure) is written to the audit log

### 3. Database Performance Optimization
- Identified full table scan bottleneck using EXPLAIN
- Added composite index on (status, retry_count)
- Estimated query cost (MySQL `EXPLAIN`) reduced by 58%, from 1.70 to 0.71, for the retry job's `WHERE status = 'PENDING' AND retry_count <= 3` query

### 4. Idempotency + Audit Log
- Unique idempotency key prevents duplicate email processing
- Audit trail for every retry: operator, action, target, detail, result, timestamp

### 5. Redis Caching Layer
- Cache-aside pattern for dashboard stats
- 60-second TTL with manual invalidation
- Reduces database load for frequent queries

### 6. Unit Testing
- JUnit 5 + Mockito unit tests with every external dependency (MySQL, Redis, SMTP) mocked
- Covered: cache hit / miss, every retry-decision branch (success → `SUCCESS`, failure below the limit → stays `PENDING`, third failure → `FAILED`), idempotency-key format, and the API-key filter (valid / wrong / missing key, public Swagger paths)
- The Spring context smoke test needs a running MySQL and Redis, so it is disabled in the default build

### 7. API Key Authentication
- Custom Spring Security filter (`X-API-KEY` header) on all API endpoints
- The key is read from the `API_KEY` environment variable; the app fails to start if it is missing
- Keys are compared in constant time (`MessageDigest.isEqual`) to avoid leaking information through timing
- Swagger UI and the static dashboard page are public; API calls return 401 with a JSON error for an invalid or missing key

### 8. Frontend Dashboard
- Single-page monitoring dashboard built with vanilla HTML/JavaScript
- Auto-refreshes every 30 seconds
- Email retry test: trigger a test email directly from the browser
- Asks for the API key once per browser session (kept in `sessionStorage`, never in source code)
- All server-provided values are HTML-escaped before rendering (audit-log details contain user input)
- No frontend framework
- Access at `http://localhost:8080`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /dashboard/stats | Get real-time system metrics |
| POST | /dashboard/refresh | Invalidate Redis cache |
| GET | /audit/logs | Get recent 100 audit log entries |
| POST | /email/test | Queue a test email for the retry job |

Full API documentation available at `/swagger-ui.html`

## Frontend

Access the monitoring dashboard at `http://localhost:8080`

Features:
- Real-time system metrics display
- Audit log viewer
- Email retry test (trigger test emails from browser)
- Cache refresh button

## Getting Started

### Prerequisites
- Java 17
- MySQL 8.0+
- Redis

### Setup

1. Clone the repository
2. Create the MySQL database (the reminder tables come from the existing internal system and are not included)
3. Put local settings (DB password, Gmail address and app password) in `src/main/resources/application-local.properties`, which is gitignored
4. Run the application

```bash
./mvnw spring-boot:run
```
### Environment Variables
```bash
export API_KEY=your-api-key-here
```

### Running Tests
```bash
./mvnw test
```

### Calling the API
```bash
curl -H "X-API-KEY: your-api-key" http://localhost:8080/dashboard/stats
```

## Architecture

```
HTTP Request
     ↓
Controller Layer
     ↓
Service Layer (Business Logic)
     ↓          ↓
Redis Cache   Database (MySQL)
              via MyBatis Mapper
```

## Known Limitations

- **Single instance only.** The retry job does not claim rows before sending, so two running instances could both send the same email. A conditional update (`UPDATE ... SET status = 'SENDING' WHERE id = ? AND status = 'PENDING'`) or a distributed lock would fix this.
- **At-least-once sending.** The email is sent before the database row is updated; a crash in between means the email is sent again on the next run.
- **Per-day idempotency.** Because the key includes the date, a second failure report for the same reminder on the same day is silently ignored by `INSERT IGNORE`.