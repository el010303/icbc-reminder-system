# ICBC Reminder System - Backend Infrastructure

A production-grade backend reliability infrastructure built on top of ICBC's enterprise reminder system, featuring real-time monitoring, email reliability engineering, database performance optimization, and caching layer.

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
- Tracks total reminders, pending/completed tasks, email delivery rates
- Redis cache with 60-second TTL, manual refresh endpoint

### 2. Email Reliability Engineering
- Automated retry system with exponential backoff
- Idempotency guarantee via unique keys (INSERT IGNORE)
- Real email delivery via Gmail SMTP (JavaMailSender)
- Maximum 3 retry attempts before marking as FAILED
- Audit logging for every retry operation

### 3. Database Performance Optimization
- Identified full table scan bottleneck using EXPLAIN
- Added composite index on (status, retry_count)
- Query cost reduced by 58% (from 1.7 to 0.71)
- Documented in db_optimization.sql

### 4. Idempotency + Audit Log
- Unique idempotency key prevents duplicate email processing
- Complete audit trail: who, when, what, result
- Financial compliance ready

### 5. Redis Caching Layer
- Cache-aside pattern for dashboard stats
- 60-second TTL with manual invalidation
- Reduces database load for frequent queries

### 6. Unit Testing
- 6 unit tests with Mockito mocking
- Tests cover cache hit/miss, retry logic, failure recording
- Zero dependencies on external services in tests

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /dashboard/stats | Get real-time system metrics |
| GET | /dashboard/refresh | Invalidate Redis cache |
| GET | /audit/logs | Get recent 100 audit log entries |

Full API documentation available at `/swagger-ui.html`

## Getting Started

### Prerequisites
- Java 17
- MySQL 8.0+
- Redis

### Setup

1. Clone the repository
2. Create MySQL database
3. Configure `application.properties`
4. Run the application

```bash
./mvnw spring-boot:run
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