# IT3130-RideLink-Microservices

RideLink is a fictional ride-sharing platform built as a **backend-only microservices system** for the IT3130 – Application Development group assignment. The system is decomposed into four independently deployable services, each owned by a member of the group.

---

## Team & Service Ownership

| # | Microservice | Primary Owner | Student ID | Port | Directory |
|---|---|---|---|---|---|
| 1 | Ride Management Service | Bandara A.R.I.T.A. | IT24100976 | `8083` | `ride-management-service/` |
| 2 | Driver & Vehicle Service | Wewage H.M.C.D. | IT24101040 | `8082` | `driver-vehicle-service/` |
| 3 | Fare & Payment Service | Dharmasiri W.K.Y.D. | IT24101183 | `8084` | `fare-payment-service/` |
| 4 | Account Service | Disanayaka D.M.R.H. | IT24103006 | `8081` | `account-service/` |


All members participate in architecture, API-contract, and integration decisions.

---
## Prerequisites
 
- Java 17+
- Maven
- MongoDB (local instance, or shared connection string) - one database per service
- Postman
---
 
## Configuration
 
Each service reads config from environment variables only - no secrets are committed to this repo.
 
```bash
cp <service-directory>/.env.example <service-directory>/.env
```
 
Repeat for all four services. Set the following in each `.env`:
 
```
PORT=<see port table above>
DATABASE_URL=mongodb://localhost:27017/<service-db-name>
JWT_SECRET=<same value across all 4 services>
```
 
Ride Management Service additionally needs:
 
```
DRIVER_SERVICE_URL=http://localhost:8082
FARE_SERVICE_URL=http://localhost:8084
```
 
**Databases:** `ridelink_account_db`, `ridelink_driver_db`, `ridelink_ride_db`, `ridelink_fare_db`
 
---
 
## Start-up Order
 
Ride Management depends on the other three, so start in this order:
 
1. Account Service (`8081`)
2. Driver & Vehicle Service (`8082`)
3. Fare & Payment Service (`8084`)
4. Ride Management Service (`8083`)
---
 
## Commands
 
Run a service:
```bash
cd <service-directory>
mvn spring-boot:run
```
 
Check a service is up:
```bash
GET http://localhost:<port>/actuator/health
```
 
---
 
## Test Instructions
 
**Unit tests** (per service):
```bash
cd <service-directory>
mvn test
```
 
**Integrated workflow tests:** import the Postman collection and environment, then run the full workflows (registration → ride request → assignment → completion → payment) including the negative scenarios (e.g. no available driver, invalid status transition, unauthorised access).
 
```
postman/RideLink.postman_collection.json
postman/RideLink.postman_environment.json
```
 
---
 
## Endpoint Locations
 
Base pattern: `http://localhost:<port>/api/v1/<resource>`
 
| Service | Base URL | Swagger UI |
|---|---|---|
| Account Service | `http://localhost:8081/api/v1` | `http://localhost:8081/swagger-ui.html` |
| Driver & Vehicle Service | `http://localhost:8082/api/v1` | `http://localhost:8082/swagger-ui.html` |
| Ride Management Service | `http://localhost:8083/api/v1` | `http://localhost:8083/swagger-ui.html` |
| Fare & Payment Service | `http://localhost:8084/api/v1` | `http://localhost:8084/swagger-ui.html` |
 
Example endpoints:
```
POST /api/v1/accounts/register
GET  /api/v1/drivers/available?city=Kandy
POST /api/v1/rides
GET  /api/v1/fares/estimate
```
 
---
 
## Sample Credentials / Test Data
 
| Role | Email | Password | Notes |
|---|---|---|---|
| Passenger | `passenger1@ridelink.test` | `Test@1234` | Pre-seeded test account |
| Driver | `driver1@ridelink.test` | `Test@1234` | Pre-seeded, available status |
| Admin | `admin@ridelink.test` | `Test@1234` | Role management access |
 
