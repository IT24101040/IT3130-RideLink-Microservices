# Ride Management Service

Owner: A.R.I.T.A. Bandara (IT24100976) — RideLink, IT3130 Group Assignment

## Responsibility
Ride request creation, driver assignment, ride status lifecycle (REQUESTED → ASSIGNED → ACCEPTED → IN_PROGRESS → COMPLETED, with CANCELLED as a side-branch), and ride retrieval. Owns its own MongoDB database exclusively — no other service reads or writes it directly.

## Prerequisites
- Java 17+
- Maven (or use the included `mvnw` wrapper if added)
- MongoDB running locally (e.g. via MongoDB Compass / `mongod`)

## Configuration
Copy the example env file and fill in local values:
```bash
cp .env.example .env
```

| Variable | Purpose | Default (local) |
|---|---|---|
| `PORT` | This service's port | `8083` |
| `DATABASE_URL` | MongoDB connection string | `mongodb://localhost:27017/ridelink_ride_db` |
| `JWT_SECRET` | Shared secret used to validate JWTs issued by Account Service | *(must match the group's agreed secret)* |
| `DRIVER_SERVICE_URL` | Base URL of Driver & Vehicle Service | `http://localhost:8082` |
| `FARE_SERVICE_URL` | Base URL of Fare & Payment Service | `http://localhost:8084` |

IntelliJ: Run → Edit Configurations → Environment variables, and paste these in (or use the EnvFile plugin to load `.env` automatically).

## Running locally
```bash
mvn spring-boot:run
```
Or run `RideManagementServiceApplication` directly from IntelliJ.

## Endpoints
Base URL: `http://localhost:8083/api/v1/rides`

| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/rides` | Create a ride request |
| GET | `/api/v1/rides/{rideId}` | Get a ride by id |
| GET | `/api/v1/rides?customerId=` | List rides for a customer |
| GET | `/api/v1/rides?driverId=` | List rides for a driver |
| PATCH | `/api/v1/rides/{rideId}/assign` | Assign a driver (REQUESTED → ASSIGNED) |
| PATCH | `/api/v1/rides/{rideId}/status` | Update status per the lifecycle rules |
| DELETE | `/api/v1/rides/{rideId}` | Cancel a ride |

All endpoints require `Authorization: Bearer <JWT>` except `/actuator/health` and Swagger.

## API Documentation
- Swagger UI: `http://localhost:8083/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8083/v3/api-docs`

## Health check
`GET http://localhost:8083/actuator/health`

## Tests
```bash
mvn test
```
Covers: ride creation, valid status transition, invalid status transition rejection, ride-not-found handling.

## Notes for integration
- `customerId` and `driverId` are UUID strings from Account Service and Driver & Vehicle Service respectively — this service never queries their databases.
- Interservice calls (fetching eligible drivers from Driver Service, notifying Fare Service on ride completion) are scaffolded in `config/WebClientConfig.java` and will be implemented in the interservice-communication phase.
