# Supply Chain Cargo Management System

![Java](https://img.shields.io/badge/Java-17+-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![Vue.js](https://img.shields.io/badge/Vue.js-3.3.0-brightgreen)
![Kafka](https://img.shields.io/badge/Kafka-3.5.0-orange)

A full-stack application to **manage and track cargo shipments**, coordinate with vendors, and optimize logistics—developed as part of the **Maersk Hackathon**.

---

## Key Features

- **Dashboard:** Overview of all supply chain activities, total shipments, active cargo, and recent activities.  
- **Cargo Management:** CRUD operations for cargo items (value, type, associated shipment).  
- **Shipment Tracking:** Real-time status updates with detailed timelines.  
- **Route Management:** Define and optimize shipping routes.  
- **Vendor Coordination:** Manage logistics partners and assign them to shipments.  
- **Event-Driven Architecture:** **Apache Kafka** for real-time asynchronous updates (Week 3 implementation).

---

## System Architecture

Built on a **3-tier architecture** with an **asynchronous event layer**:

```mermaid
flowchart TB
    subgraph Client [Frontend Tier]
        Vue[Vue.js SPA]
    end

    subgraph Backend [Application Tier]
        Spring[Spring Boot Service]
    end

    subgraph Data [Data Tier]
        PG[(PostgreSQL Database)]
    end

    subgraph Streaming [Asynchronous Event Layer]
        Kafka[(Apache Kafka Cluster)]
    end

    subgraph Cloud [Cloud Platform: AWS / Azure]
        Client -- REST API Calls --> Spring
        Spring -- JDBC / ORM --> PG
        Spring -- Publish Events --> Kafka
        Kafka -- Consume Events --> Spring
        Spring -- WebSockets / APIs --> Vue
    end
````

For more details, see [ARCHITECTURE.md](https://github.com/kalviumcommunity/Supply-Chain-Cargo-Management-Maersk-Hackathon/blob/main/ARCHITECTURE.md).

---

## Tech Stack

| Tier      | Technology                          |
| --------- | ----------------------------------- |
| Frontend  | Vue.js, Vue Router, Tailwind CSS    |
| Backend   | Java 17+, Spring Boot               |
| Database  | PostgreSQL                          |
| Event Bus | Apache Kafka                        |
| Cloud     | AWS or Azure                        |

---

## Getting Started

### Prerequisites

* **Java 17+**
* **Maven** (backend build)
* **Node.js & Bun** (frontend package manager)
* **PostgreSQL** (running instance)
* **Docker** (optional, for Kafka local setup)

### Installation

#### 1. Clone Repository

```bash
git clone https://github.com/kalviumcommunity/Supply-Chain-Cargo-Management-Maersk-Hackathon.git
cd supply-chain-cargo-management-maersk-hackathon
```

#### 2. Backend Setup

**Database:**

```bash
# Create DB
psql -U <user> -c "CREATE DATABASE cargo_db;"

# Run schema
psql -U <user> -d cargo_db -f server/basic-schema.sql
```

**Run Spring Boot App:**

```bash
cd server
mvn clean install
mvn spring-boot:run
```

Backend server: [http://localhost:8080](http://localhost:8080)

#### 3. Frontend Setup

```bash
cd client
bun install
bun run dev
```

Frontend server: [http://localhost:5173](http://localhost:5173)

---

## Project Structure

```
.
├── client/         # Vue.js frontend
├── docs/           # Documentation (HLD, LLD, diagrams)
└── server/         # Spring Boot backend
```

---

## Documentation

* **ARCHITECTURE.md:** System architecture overview
* **docs/:** All HLD, LLD, and planning documents

---

<p align="center"><strong>Developed by</strong></p>

<table align="center">
  <tr>
    <td align="center">
      <a href="https://github.com/dhairyajangir" target="_blank">Dhairya</a>
    </td>
    <td align="center">
      <a href="https://github.com/rahulrr-coder" target="_blank">Rahul</a>
    </td>
    <td align="center">
      <a href="https://github.com/kdinesh24" target="_blank">Dinesh</a>
    </td>
  </tr>
</table>
