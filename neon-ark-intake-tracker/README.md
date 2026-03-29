Neon Ark Intake Tracker
Jessa K. West
28 March 2026

## Project Overview
Neon Ark Intake Tracker is a Spring Boot REST API used to track incoming creatures and assign them to habitats.

The project demonstrates:
- CRUD operations using Spring Boot: specifically create, read
- DTO-based request/response handling
- Validation and error handling
- PostgreSQL plus JPA persistence


## How to run
1. Start PostgreSQL
2. Create database: intake_tracker
3. Run migration -- Flyway runs automatically on startup
4. Start the application: ./gradelw bootRun
5. Server runs on: http://localhost:8080

## API Endpoints

### GET all creatures
GET /api/creatures

### GET creature by ID
GET /api/creatures/{id}

### POST create creature
POST /api/creatures

Required JSON:
{
    "name": "...",
    "species": "...",
    "dangerLevel": "LOW|MEDIUM|HIGH|EXTREME",
    "condition": "STABLE|RECOVERING|AGGRESSIVE|QUARANTINED",
    "habitatID": number
}

## CLI Testing

### Test: Create creature (successfully):

curl -X POST http://localhost:8080/api/creatures \
-H "Content-Type: application/json" \
-d '{
    "name": "Zorg",
    "species": "Alien Beast",
    "dangerLevel": "HIGH",
    "condition": "STABLE",
    "habitatID": 1
}'

Results in:
    HTTP/1.1 201
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Sat, 28 Mar 2026 22:42:35 GMT
    
    {"id":21,"name":"Zorg","species":"Alien Beast","dangerLevel":"HIGH","condition":"STABLE","createdAt":"2026-03-28T22:42:35.383282Z"}%

### Test: Invalid because missing required field
curl -i -X POST http://localhost:8080/api/creatures \
-H "Content-Type: application/json" \
-d '{
"species": "Alien Beast",
"dangerLevel": "HIGH",
"condition": "STABLE",
"habitatId": 1
}'

Results in:
    HTTP/1.1 400
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Sat, 28 Mar 2026 22:44:37 GMT
    Connection: close
    
    {"timestamp":"2026-03-28T22:44:37.448Z","status":400,"error":"Bad Request","path":"/api/creatures"}%

### Test: Invalid enum (dangerLevel)
curl -i -X POST http://localhost:8080/api/creatures \
-H "Content-Type: application/json" \
-d '{
"name": "BadCreature",
"species": "Alien",
"dangerLevel": "SUPERHIGH",
"condition": "STABLE",
"habitatId": 1
}'

Results in:
    HTTP/1.1 400
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Sat, 28 Mar 2026 22:45:15 GMT
    Connection: close
    
    {"timestamp":"2026-03-28T22:45:15.338Z","status":400,"error":"Bad Request","path":"/api/creatures"}%

### Test: Bad Habitat ID
curl -i -X POST http://localhost:8080/api/creatures \
-H "Content-Type: application/json" \
-d '{
"name": "Ghost",
"species": "Phantom",
"dangerLevel": "LOW",
"condition": "STABLE",
"habitatId": 999
}'

Results in:
    HTTP/1.1 400
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Sat, 28 Mar 2026 22:45:22 GMT
    Connection: close
    
    {"timestamp":"2026-03-28T22:45:22.729Z","status":400,"error":"Bad Request","path":"/api/creatures"}%

### Test creature not found with ivalid id
curl -i http://localhost:8080/api/creatures/9999

Results in:
    HTTP/1.1 404
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Sat, 28 Mar 2026 22:45:27 GMT
    
    {"timestamp":"2026-03-28T22:45:27.983Z","status":404,"error":"Not Found","path":"/api/creatures/9999"}%

## Known Errors & Trrroubleshooting

### Error: 500 Internal Server Error (habitat_id null)
Problem: POST request failed with 500 error
Log: null value in column "habitat_id" violates not-null constraint
Cause: CreatureRequest did not include habitatId
Fix: Add "habitatID" to request JSON

### Error: 400 Bad Request (validation)
Problem: Request failed with 400
Cause: DTO validation failed due to missing and/or invalid fields
Example: Missing name, invalid dangerLevel("SUPERDUPERHIGH") 
Fix: Ensure values match required format exactly

### Error: Flyway migration checksum mismatch
Problem: Application Failed to Start
Log: Migration checksum mismatch
Cause: Edited an existing migration file after it was applied
Fix: Option 1) Drop database and rerun, Option 2) run ./gradlew flywayRepair

### Error: 404 Not Found
Problem: request not found
Cause: ID does not exist in the database
Fix: use a valid ID


## Design Notes
- DTOs used to prevent exposing database fields to users
- Service layer handles business logic and mapping
- Controller layer handles HTTP responses only
- Validation is enforced using @Valid and annotations

## Lessons Learned
- Missing foriegn keys will casue database-level failures even if the code compiles
- Validation errors return 400 automatically when using @Valid
- Flyway migrations should never be edited after running
- Debugging logs are critical for identifying real issues and assumptions

## Future Improvements
- Add global exception handlers for better error messages
- Add CRUD operations PUT, PATCH, DELETE
- Add pagination to GET endpoints
- Add authentication
- Improve logging format