# Supply Chain Cargo Management System 

## Project Overview
Full-stack supply chain management system built for Maersk hackathon using Vue.js frontend, Spring Boot backend, PostgreSQL database, and Apache Kafka for real-time event streaming.

## Architecture & Key Patterns

### Backend Structure (Spring Boot)
- **3-Layer Architecture**: Controller → Service → Repository pattern
- **Controllers**: Handle HTTP requests, live in `server/src/main/java/com/cargomanagement/controller/`
  - All controllers use `@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})` for CORS
  - Standard REST endpoints: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
  - Example: `CargoController` at `/api/cargo`, `ShipmentController` at `/api/shipments`
- **Services**: Business logic layer, includes `KafkaProducerService` for event publishing
- **Repositories**: Spring Data JPA interfaces extending `JpaRepository<Entity, Long>`

### Frontend Structure (Vue.js 3 + Composition API)
- **Components**: Located in `client/src/components/`, use `<script setup>` syntax
- **Routing**: Vue Router with auth guards in `client/src/router/index.js`
- **API Layer**: Centralized in `client/src/services/api.js` with timeout handling and error messages
- **Shared Components**: Reusable components in `client/src/components/shared/`

### Database Schema
Core entities follow this relationship pattern:
- **Vendor** (independent) ← **Shipment** (central) → **Route** (independent)
- **Shipment** → **Cargo** (dependent, cascade delete)
- **Shipment** → **Delivery** (dependent, cascade delete)

See `server/basic-schema.sql` for complete schema with constraints and indexes.

## Development Commands

### Backend (Spring Boot)
```bash
cd server
mvn spring-boot:run  # Start backend on port 8080
```

### Frontend (Vue.js with Bun)
```bash
cd client
bun install     # Install dependencies
bun run dev     # Start dev server on port 5173
```

### Database Setup
```bash
psql -U <user> -c "CREATE DATABASE cargo_db;"
psql -U <user> -d cargo_db -f server/basic-schema.sql
```

## Configuration Requirements

### Backend Configuration
Copy `server/src/main/resources/application.properties.example` to `application.properties` and configure:
- PostgreSQL connection (username/password)
- Kafka bootstrap servers (default: localhost:9092)
- Google OAuth2 credentials for authentication

### Environment Variables
Frontend uses `VITE_API_BASE_URL` (defaults to http://localhost:8080/api)

## Event-Driven Architecture (Kafka)

### Key Integration Points
- **KafkaProducerService**: Publishes events when entities are created/updated
- **Topic Naming**: `route-events`, `shipment-events`, `cargo-events`
- **Message Format**: Simple string messages describing the action
- **Consumer Group**: `supply-chain-group`

### Example Usage in Controllers
```java
kafkaProducerService.sendMessage("shipment-events", 
    "Shipment created: ID=" + shipment.getShipmentId());
```

## API Testing & Documentation

### Bruno API Client
- API collection available in `bruno/` directory
- Organized by entity: `cargos/`, `shipments/`, `routes/`, `vendors/`, `deliveries/`
- Use Bruno for testing all REST endpoints during development

### Standard API Patterns
- **Success Responses**: Return entity data with appropriate HTTP status
- **Error Handling**: Controllers catch exceptions and return structured error responses
- **CRUD Operations**: All entities follow standard REST conventions

## Project-Specific Conventions

### Error Handling
- Controllers use try-catch blocks with meaningful error messages
- Frontend API service has enhanced error handling for connection issues
- Backend connectivity errors suggest starting the server

### Authentication Flow
- OAuth2 with Google integration
- Session-based authentication (not JWT)
- Auth guards in Vue Router check `requiresAuth` meta field
- Login redirects to `/dashboard`, authenticated users redirected from `/login`

### Component Organization
- Feature-based components: `CargoManagement.vue`, `ShipmentTracking.vue`
- Shared components for reusability: `BaseModal.vue`, `RouteMap.vue`
- Authentication components: `Login.vue`, `OAuthCallback.vue`

### Database Constraints
- Entity IDs use `SERIAL PRIMARY KEY`
- Status fields have CHECK constraints for valid values
- Foreign keys use `ON DELETE RESTRICT` (shipment references) or `CASCADE` (cargo/delivery)
- Unique constraints on business keys (vendor names, route combinations)

## Key Files to Reference
- `ARCHITECTURE.md`: System design and component responsibilities  
- `docs/backend-hld-details.md`: Detailed backend architecture
- `docs/lld-api-outline.md`: API specifications and error responses
- `server/basic-schema.sql`: Complete database schema with all constraints

## Development Tips
- Use the layered architecture consistently - keep controllers thin, business logic in services
- Follow the established error handling patterns in existing controllers
- Use the centralized API service in frontend for all HTTP requests
- Test with Bruno collection instead of manual curl commands
- Kafka is configured but can be skipped for basic CRUD operations