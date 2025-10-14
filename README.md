#  URL Shortener

A scalable, containerized URL shortener built using Kotlin, Spring Boot, PostgreSQL, and Redis caching. It includes integration tests for all API endpoints and is deployable on Kubernetes (K8s). The system supports multiple short-code generation strategies, a scalable microservice architecture, and integrates with Redis and load balancing. It is designed for high availability, low collision risk, and a clean separation of concerns.

##  Core Features

###  Functional
- Allow users to shorten long URLs.
- Redirect short links to their original destinations.
- Validate and reject invalid URLs.
- Support optional link expiration and categories.
- Provide clear and consistent API responses.

###  Non-Functional
- Handle many requests efficiently and reliably.
- Stay responsive even when under heavy load.
- Ensure data accuracy and avoid broken links.
- Be easy to maintain, extend, and monitor.
- Follow clean design and coding principles.

---

##  Tech Stack

| Layer | Technology |
|--------|-------------|
| Language | Kotlin |
| Framework | Spring Boot |
| Database | PostgreSQL |
| Cache | Redis |
|Containerization | Docker
| Deployment | Kubernetes (K8s) |
| Build Tool | Maven |

---

# URL Shortener Service

A Spring Boot URL shortener with multiple algorithms for generating unique short codes.

## Features

- **Three Generation Algorithms**: Choose from different approaches
- **REST API**: Simple endpoints for shortening and redirecting
- **Validation**: URL format validation and error handling
- **Caching**: Redis and in-memory caching options

## Algorithms

### 1. Random Base62 with Database Check
- Generates random codes using Base62 characters (0-9, a-z, A-Z)
- Checks database for uniqueness
- Automatically increases code length if collisions occur
- Simple and reliable with no external dependencies

### 2. In-Memory Cached Generator
- Extends the random Base62 approach
- Uses Caffeine cache to reduce database queries
- 20,000 entry cache with 60-minute TTL
- Better performance for repeated operations

### 3. HMAC + Base62 with Redis
- Deterministic: same URL always produces same short code
- Uses HMAC-SHA256 cryptographic hashing
- Redis-based reservation system prevents collisions
- Best for high-concurrency environments
  Currently, the service uses the HMAC + Base62 with Redis Checking algorithm by default. To switch algorithms, modify the UrlService class:
## API Usage

### Create Short URL
POST /api/url/shorten
Request Body:

```http

{
  "originalUrl": "http://localhost:57942/api/url/info/QMd2UHc"
}
```
Response: Status: 201
```http
{
    "shortCode": "https://short.ly/NgonJTPa",
    "originalUrl": "http://localhost:57942/api/url/info/QMd2UHc"
}
```
### Redirect to original URL

```http
GET /api/url/{shortCode}
```
Response Returns: 302 Redirect to original URL

### Get URL Info
```http
GET /api/url/info/{shortCode}
```

### Get URL Info
GET /api/url/info/{shortCode}

Response: Status: 200
```
json
{
    "id": 7,
    "originalUrl": "http://localhost:57942/api/url/info/:aaxMAhu",
    "shortCode": "QMd2UHc",
    "status": "ACTIVE",
    "category": null,
    "createdAt": "2025-10-14T05:32:19.942339Z",
    "expirationTime": null
}
```
Status Codes
201 - URL created

302 - Redirect

400 - Invalid URL

404 - Short code not found
Testing
Run the test suite:

```bash
./mvnw test
```
### The test suite includes:

- Integration tests for all API endpoints

- Validation tests for URL format

- Error handling tests

- Algorithm-specific tests

## JMeter Test Results


# Future Enhancements
- Algorithm Selection: Configurable algorithm choice via api

- Custom Code Support: Allow users to specify custom short codes

- Analytics: Track click statistics and usage patterns

- Bulk Operations: Support for batch URL shortening

- QR Code Generation: Generate QR codes for shortened URLs
