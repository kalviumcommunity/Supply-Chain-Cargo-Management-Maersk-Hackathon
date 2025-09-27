# Core Database Entities for Supply Chain Cargo Management System

## Project Overview
**Project Name**: Supply Chain Cargo Management System  
**Date**: 2025-09-27  
**Business Domain**: This system manages cargo throughout the supply chain, including creation, tracking, vendor assignments, route planning, and delivery. It simulates Maersk-like operations with real-time updates via Kafka (Week 3). Key modules include Cargo Management (CRUD), Shipment Tracking (lifecycle events), Route Management (path definitions), Vendor Coordination (assignments), and Event Streaming (Kafka integration for events like "cargo picked up" or "delivery delayed").


**Tech Stack**:  
- Backend: Java Spring Boot (for APIs and business logic)  
- Frontend: Vue.js (for dashboards, forms, and real-time UI)  
- Database: PostgreSQL (for schema, entities, and relationships)  
- Deployment: AWS / Azure (e.g., RDS for DB, EC2/App Service for apps)  
- AI Assistant: GitHub Copilot (used for code refactoring, debugging, and auto-generating tests; all generated code will be understood, verified, and tested manually by team members)  


**Research Basis**:  
- Searched "supply chain cargo database entities logistics Maersk" and "database schema for shipment tracking systems."  
- Key Insights: Entities must capture physical/logistical details (e.g., Cargo value for insurance, Route duration for ETAs). Relationships enable workflows like assigning Cargo to Shipment → Vendor → Route → Delivery.  
- Assumptions: IDs auto-generate; timestamps for auditing; expand for Kafka events (e.g., status changes trigger streams). Scalable for 1000+ shipments (indexes on FKs). Total: 5 core entities to cover all modules.  

## Core Entities
Entities are designed for PostgreSQL with standard types (e.g., SERIAL for IDs, DECIMAL for monetary/precise values). Each includes 4-7 key attributes from the project requirements. Relationships are basic (one-to-many/many-to-one via FKs) to support modules like Cargo Management (link to Shipment) and Vendor Coordination (assign to Shipment/Delivery).

### 1. Shipment Entity
Represents a complete shipment batch, central to tracking and lifecycle management (Module: Shipment Tracking). Tracks origin to destination, status updates via Kafka events (e.g., "in transit" → published event).

**Attributes**:
- `shipment_id`: SERIAL PRIMARY KEY (Auto-incrementing unique ID for internal referencing).  
- `origin`: VARCHAR(100) NOT NULL (Starting location/port, e.g., "Mumbai Port" – for route planning).  
- `destination`: VARCHAR(100) NOT NULL (End location/port, e.g., "Singapore Port" – for ETA calculations).  
- `status`: VARCHAR(20) NOT NULL DEFAULT 'Created' (Lifecycle states: e.g., "Created", "In Transit", "Delivered", "Delayed" – updated via CRUD or Kafka).  
- `estimated_delivery`: TIMESTAMP (Expected arrival time; used for delay alerts in tracking dashboard).  
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP (Audit trail for when shipment is created).  
- `vendor_id`: INTEGER REFERENCES vendor(vendor_id) (FK – Assigned vendor for coordination).  
- `route_id`: INTEGER REFERENCES route(route_id) (FK – Linked route for path management).  

**Basic Relationships**:
- Has many Cargos and Deliveries (One Shipment to Many Cargos/Deliveries – groups items for batch tracking).  
- Assigned to one Vendor and one Route (For coordination and path following; supports workflow like "Cargo added → Shipment created → Vendor assigned").

### 2. Cargo Entity
Represents individual cargo items or containers, managed via CRUD (Module: Cargo Management). Includes details for manifests, linking to shipments for tracking.

**Attributes**:
- `cargo_id`: SERIAL PRIMARY KEY (Auto-incrementing unique ID).  
- `shipment_id`: INTEGER NOT NULL REFERENCES shipment(shipment_id) (FK – Links cargo to its parent shipment for manifest grouping).  
- `type`: VARCHAR(50) NOT NULL (Cargo category: e.g., "Electronics", "Perishables", "Hazardous" – determines handling rules).  
- `value`: DECIMAL(12,2) NOT NULL (Monetary value in USD; for insurance and customs calculations).  
- `description`: TEXT (Detailed notes: e.g., "Fragile electronics pallet, 50 units" – for operator reference).  
- `weight`: DECIMAL(10,2) (Optional kg; for shipping fees and load balancing – added from research).  
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP (Audit).  

**Basic Relationships**:
- Belongs to one Shipment (Many Cargos per Shipment – enables manifest creation and updates).  
- Indirectly tied to Route/Delivery via Shipment (Supports workflow: "Create Cargo → Link to Shipment → Track via Route").

### 3. Route Entity
Represents predefined or dynamic paths between ports, for management (Module: Route Management). Used to calculate durations and ETAs in shipments.

**Attributes**:
- `route_id`: SERIAL PRIMARY KEY (Auto-incrementing unique ID).  
- `origin_port`: VARCHAR(100) NOT NULL (Starting port: e.g., "Nhava Sheva, India" – matches Shipment origin).  
- `destination_port`: VARCHAR(100) NOT NULL (Ending port: e.g., "Jebel Ali, UAE" – matches Shipment destination).  
- `duration`: INTEGER NOT NULL (Expected time in days/hours; e.g., 5 for transit calc – research from logistics route models).  
- `status`: VARCHAR(20) DEFAULT 'Active' (e.g., "Active", "Delayed", "Closed" – updated via CRUD or Kafka events).  
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP (Audit).  

**Basic Relationships**:
- Used by many Shipments (One Route to Many Shipments – shared paths for efficiency).  
- Indirectly affects Cargos/Deliveries via Shipment (Workflow: "Define Route → Assign to Shipment → Update status on delays").

### 4. Vendor Entity
Represents external partners (suppliers, carriers, etc.) for coordination (Module: Vendor Coordination). Stores details for assignments and notifications.

**Attributes**:
- `vendor_id`: SERIAL PRIMARY KEY (Auto-incrementing unique ID).  
- `name`: VARCHAR(100) NOT NULL (Company/person: e.g., "Global Logistics Pvt Ltd").  
- `contact_info`: VARCHAR(200) NOT NULL (Email/phone: e.g., "contact@vendor.com, +91-1234567890" – JSON or concatenated for notifications).  
- `service_type`: VARCHAR(50) NOT NULL (e.g., "Supplier", "Carrier", "Customs Broker" – determines role).  
- `is_active`: BOOLEAN DEFAULT TRUE (Flag for availability in assignments).  
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP (Audit).  

**Basic Relationships**:
- Coordinates many Shipments (One Vendor to Many Shipments – for assignments like "Pick up cargo").  
- Indirectly linked to Cargos/Deliveries/Routes via Shipment (Workflow: "Assign Vendor to Shipment → Kafka event for pickup").

### 5. Delivery Entity
Represents the final handover of a shipment, tracking actual vs. estimated (Tied to Shipment Tracking and Cargo Management). Includes recipient for completion.

**Attributes**:
- `delivery_id`: SERIAL PRIMARY KEY (Auto-incrementing unique ID).  
- `shipment_id`: INTEGER NOT NULL REFERENCES shipment(shipment_id) (FK – Links to parent shipment for tracking).  
- `actual_delivery_date`: TIMESTAMP (Real arrival time; compared to estimated for delays – updated via Kafka "delivered" event).  
- `recipient`: VARCHAR(100) NOT NULL (End receiver: e.g., "ABC Importers, Dubai" – for confirmation).  
- `status`: VARCHAR(20) DEFAULT 'Pending' (e.g., "Pending", "Delivered", "Failed" – CRUD updates).  
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP (Audit).  

**Basic Relationships**:
- Belongs to one Shipment (Many Deliveries per Shipment? Typically one-to-one, but many if partial – for full lifecycle closure).  
- Indirectly includes Cargos via Shipment (Workflow: "Shipment in transit → Kafka delivery event → Update actual date → Close manifest").

## Relationships Overview
- **Primary FKs**: ShipmentID (in Cargo/Delivery), VendorID/RouteID (in Shipment) – Ensures data integrity (e.g., CASCADE DELETE if shipment removed).  
- **One-to-Many Patterns**: Shipment → Cargo/Delivery (groups); Vendor/Route → Shipment (reuse).  
- **Many-to-Many Potential**: If vendors share routes, add junction table later (e.g., vendor_route).  
- Indexes: Planned on FKs (e.g., shipment_id) for fast queries in tracking dashboard.  

## Tie to Modules & Features
- **Cargo Management (Week 2 CRUD)**: Cargo attributes enable create/update (e.g., add weight/value); relationships for manifests.  
- **Shipment Tracking (Week 2 CRUD)**: Shipment/Delivery status/ETA updated; Kafka consumes events like "picked up" to trigger DB changes.  
- **Route Management (Week 2 CRUD)**: Route duration feeds Shipment estimated_delivery.  
- **Vendor Coordination (Week 2 CRUD)**: Vendor contact_info for assignments/notifications.  
- **Event Streaming (Week 3 Kafka)**: Hardcoded events (e.g., "Cargo picked up") publish to topics; consumers update entities (e.g., Shipment status).  

**Workflow Example Integration**:  
Cargo created (add attributes) → Link to Shipment (FK) → Assign Vendor/Route → Kafka "picked up" event → Update Delivery actual_date → Dashboard shows real-time status.  

## Next Steps
- Day 2: ERD visual (Dinesh) from these entities for relationships.  
- Week 2: Implement CRUD APIs (Spring Boot entities from these attributes).