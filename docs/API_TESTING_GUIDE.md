# Cargo Management System - API Testing Guide

## 🚀 Base URL
```
http://localhost:8080
```

## 📋 Testing Order
1. **Vendors** (No dependencies)
2. **Routes** (No dependencies)
3. **Shipments** (Depends on Vendors & Routes)
4. **Cargo** (Depends on Shipments)
5. **Deliveries** (Depends on Shipments)

---

## 🏢 1. VENDOR CONTROLLER

### Base URL: `/api/vendors`

#### **POST** - Create Vendor
**Endpoint:** `POST /api/vendors`

**Sample Request 1:**
```json
{
  "name": "Maersk Line",
  "contactInfo": "contact@maersk.com, +45-33-63-33-63",
  "serviceType": "Ocean Freight",
  "isActive": true
}
```

**Sample Request 2:**
```json
{
  "name": "DHL Express",
  "contactInfo": "info@dhl.com, +1-800-225-5345",
  "serviceType": "Express Delivery",
  "isActive": true
}
```

**Sample Request 3:**
```json
{
  "name": "FedEx Corporation",
  "contactInfo": "support@fedex.com, +1-800-463-3339",
  "serviceType": "Air Freight",
  "isActive": true
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/vendors \
  -H "Content-Type: application/json" \
  -d '{"name": "Maersk Line", "contactInfo": "contact@maersk.com", "serviceType": "Ocean Freight", "isActive": true}'
```

#### **GET** - Retrieve Vendors

**Get All Vendors:**
```
GET /api/vendors
```

**Get Vendor by ID:**
```
GET /api/vendors/{id}
```
Example: `GET /api/vendors/1`

**Get Vendors by Service Type:**
```
GET /api/vendors/service-type/{serviceType}
```
Example: `GET /api/vendors/service-type/Ocean Freight`

**Get Active Vendors:**
```
GET /api/vendors/active
```

#### **PUT** - Update Vendor
**Endpoint:** `PUT /api/vendors/{id}`

**Sample Request:**
```json
{
  "name": "Maersk Line Updated",
  "contactInfo": "newcontact@maersk.com, +45-33-63-33-64",
  "serviceType": "Ocean & Air Freight",
  "isActive": true
}
```

**Update Vendor Status:**
```
PUT /api/vendors/{id}/status?isActive=false
```

#### **DELETE** - Delete Vendor
```
DELETE /api/vendors/{id}
```
Example: `DELETE /api/vendors/1`

---

## 🛣️ 2. ROUTE CONTROLLER

### Base URL: `/api/routes`

#### **POST** - Create Route
**Endpoint:** `POST /api/routes`

**Sample Request 1:**
```json
{
  "originPort": "Port of Hamburg",
  "destinationPort": "Port of Shanghai",
  "duration": 28,
  "status": "Active",
  "distance": 11500.5,
  "transportationMode": "Sea",
  "cost": 2500.00
}
```

**Sample Request 2:**
```json
{
  "originPort": "Port of Los Angeles",
  "destinationPort": "Port of Mumbai",
  "duration": 22,
  "status": "Active",
  "distance": 8900.3,
  "transportationMode": "Sea",
  "cost": 3200.00
}
```

**Sample Request 3:**
```json
{
  "originPort": "Frankfurt Airport",
  "destinationPort": "Dubai Airport",
  "duration": 1,
  "status": "Active",
  "distance": 4800.0,
  "transportationMode": "Air",
  "cost": 5500.00
}
```

#### **GET** - Retrieve Routes

**Get All Routes:**
```
GET /api/routes
```

**Get Route by ID:**
```
GET /api/routes/{id}
```

**Get Routes by Status:**
```
GET /api/routes/status/{status}
```
Example: `GET /api/routes/status/Active`

**Search Routes by Origin and Destination:**
```
GET /api/routes/search?origin={origin}&destination={destination}
```
Example: `GET /api/routes/search?origin=Port of Hamburg&destination=Port of Shanghai`

**Get Routes by Transportation Mode:**
```
GET /api/routes/transport-mode/{mode}
```
Example: `GET /api/routes/transport-mode/Sea`

#### **PUT** - Update Route
**Endpoint:** `PUT /api/routes/{id}`

**Sample Request:**
```json
{
  "originPort": "Port of Hamburg",
  "destinationPort": "Port of Shanghai",
  "duration": 30,
  "status": "Active",
  "distance": 11500.5,
  "transportationMode": "Sea",
  "cost": 2700.00
}
```

**Update Route Status:**
```
PUT /api/routes/{id}/status?status=Delayed
```

#### **DELETE** - Delete Route
```
DELETE /api/routes/{id}
```

---

## 📦 3. SHIPMENT CONTROLLER

### Base URL: `/api/shipments`

#### **POST** - Create Shipment
**Endpoint:** `POST /api/shipments`

**Sample Request 1:**
```json
{
  "origin": "Hamburg, Germany",
  "destination": "Shanghai, China",
  "status": "Created",
  "estimatedDelivery": "2025-11-05",
  "assignedRoute": {
    "routeId": 1
  },
  "assignedVendor": {
    "vendorId": 1
  },
  "shipmentCode": "SH2025001"
}
```

**Sample Request 2:**
```json
{
  "origin": "Los Angeles, USA",
  "destination": "Mumbai, India",
  "status": "In Transit",
  "estimatedDelivery": "2025-10-30",
  "assignedRoute": {
    "routeId": 2
  },
  "assignedVendor": {
    "vendorId": 1
  },
  "shipmentCode": "SH2025002"
}
```

**Sample Request 3:**
```json
{
  "origin": "Frankfurt, Germany",
  "destination": "Dubai, UAE",
  "status": "Pending",
  "estimatedDelivery": "2025-10-15",
  "assignedRoute": {
    "routeId": 3
  },
  "assignedVendor": {
    "vendorId": 3
  },
  "shipmentCode": "SH2025003"
}
```

#### **GET** - Retrieve Shipments

**Get All Shipments:**
```
GET /api/shipments
```

**Get Shipment by ID:**
```
GET /api/shipments/{id}
```

**Get Shipments by Status:**
```
GET /api/shipments/status/{status}
```
Example: `GET /api/shipments/status/Created`

#### **PUT** - Update Shipment
**Endpoint:** `PUT /api/shipments/{id}`

**Sample Request:**
```json
{
  "origin": "Hamburg, Germany",
  "destination": "Shanghai, China",
  "status": "In Transit",
  "estimatedDelivery": "2025-11-03",
  "assignedRoute": {
    "routeId": 1
  },
  "assignedVendor": {
    "vendorId": 1
  },
  "shipmentCode": "SH2025001"
}
```

#### **DELETE** - Delete Shipment
```
DELETE /api/shipments/{id}
```

---

## 📋 4. CARGO CONTROLLER

### Base URL: `/api/cargo`

#### **POST** - Create Cargo
**Endpoint:** `POST /api/cargo`

**Sample Request 1:**
```json
{
  "shipment": {
    "shipmentId": 1
  },
  "type": "Electronics",
  "value": 125000.50,
  "description": "High-end smartphones and laptops for retail distribution",
  "weight": 2500.75,
  "volume": 45.20,
  "weightUnit": "kg"
}
```

**Sample Request 2:**
```json
{
  "shipment": {
    "shipmentId": 1
  },
  "type": "Textiles",
  "value": 35000.00,
  "description": "Cotton fabrics and garments for fashion industry",
  "weight": 1200.00,
  "volume": 80.50,
  "weightUnit": "kg"
}
```

**Sample Request 3:**
```json
{
  "shipment": {
    "shipmentId": 2
  },
  "type": "Automotive Parts",
  "value": 89500.25,
  "description": "Engine components and spare parts for car manufacturing",
  "weight": 3200.00,
  "volume": 25.75,
  "weightUnit": "kg"
}
```

**Sample Request 4:**
```json
{
  "shipment": {
    "shipmentId": 3
  },
  "type": "Pharmaceuticals",
  "value": 250000.00,
  "description": "Temperature-controlled medical supplies and vaccines",
  "weight": 500.25,
  "volume": 12.80,
  "weightUnit": "kg"
}
```

#### **GET** - Retrieve Cargo

**Get All Cargo:**
```
GET /api/cargo
```

**Get Cargo by ID:**
```
GET /api/cargo/{id}
```

**Get Cargo by Shipment ID:**
```
GET /api/cargo/shipment/{shipmentId}
```
Example: `GET /api/cargo/shipment/1`

**Get Cargo by Type:**
```
GET /api/cargo/type/{type}
```
Example: `GET /api/cargo/type/Electronics`

#### **PUT** - Update Cargo
**Endpoint:** `PUT /api/cargo/{id}`

**Sample Request:**
```json
{
  "shipment": {
    "shipmentId": 1
  },
  "type": "Electronics",
  "value": 130000.00,
  "description": "Updated high-end smartphones and laptops for retail distribution",
  "weight": 2600.00,
  "volume": 47.50,
  "weightUnit": "kg"
}
```

#### **DELETE** - Delete Cargo
```
DELETE /api/cargo/{id}
```

---

## 🚚 5. DELIVERY CONTROLLER

### Base URL: `/api/deliveries`

#### **POST** - Create Delivery
**Endpoint:** `POST /api/deliveries`

**Sample Request 1:**
```json
{
  "shipment": {
    "shipmentId": 1
  },
  "actualDeliveryDate": null,
  "recipient": "Shanghai Electronics Co. Ltd",
  "status": "Pending"
}
```

**Sample Request 2:**
```json
{
  "shipment": {
    "shipmentId": 2
  },
  "actualDeliveryDate": "2025-10-07T14:30:00",
  "recipient": "Mumbai Auto Industries",
  "status": "Delivered"
}
```

**Sample Request 3:**
```json
{
  "shipment": {
    "shipmentId": 3
  },
  "actualDeliveryDate": null,
  "recipient": "Dubai Medical Center",
  "status": "Pending"
}
```

#### **GET** - Retrieve Deliveries

**Get All Deliveries:**
```
GET /api/deliveries
```

**Get Delivery by ID:**
```
GET /api/deliveries/{id}
```

**Get Delivery by Shipment ID:**
```
GET /api/deliveries/shipment/{shipmentId}
```
Example: `GET /api/deliveries/shipment/1`

**Get Deliveries by Status:**
```
GET /api/deliveries/status/{status}
```
Example: `GET /api/deliveries/status/Pending`

#### **PUT** - Update Delivery
**Endpoint:** `PUT /api/deliveries/{id}`

**Sample Request:**
```json
{
  "shipment": {
    "shipmentId": 1
  },
  "actualDeliveryDate": "2025-10-07T16:45:00",
  "recipient": "Shanghai Electronics Co. Ltd",
  "status": "Delivered"
}
```

#### **DELETE** - Delete Delivery
```
DELETE /api/deliveries/{id}
```

---

## 🏠 6. HOME CONTROLLER

### **GET** - Health Check
```
GET /
```

**Response:**
```
"Server is running"
```

---

## 🧪 TESTING WORKFLOW

### Step 1: Test Basic Connectivity
```bash
curl http://localhost:8080/
```

### Step 2: Create Vendors
```bash
curl -X POST http://localhost:8080/api/vendors \
  -H "Content-Type: application/json" \
  -d '{"name": "Maersk Line", "contactInfo": "contact@maersk.com", "serviceType": "Ocean Freight", "isActive": true}'

curl -X POST http://localhost:8080/api/vendors \
  -H "Content-Type: application/json" \
  -d '{"name": "DHL Express", "contactInfo": "info@dhl.com", "serviceType": "Express Delivery", "isActive": true}'
```

### Step 3: Create Routes
```bash
curl -X POST http://localhost:8080/api/routes \
  -H "Content-Type: application/json" \
  -d '{"originPort": "Port of Hamburg", "destinationPort": "Port of Shanghai", "duration": 28, "status": "Active", "distance": 11500.5, "transportationMode": "Sea", "cost": 2500.00}'

curl -X POST http://localhost:8080/api/routes \
  -H "Content-Type: application/json" \
  -d '{"originPort": "Frankfurt Airport", "destinationPort": "Dubai Airport", "duration": 1, "status": "Active", "distance": 4800.0, "transportationMode": "Air", "cost": 5500.00}'
```

### Step 4: Create Shipments
```bash
curl -X POST http://localhost:8080/api/shipments \
  -H "Content-Type: application/json" \
  -d '{"origin": "Hamburg, Germany", "destination": "Shanghai, China", "status": "Created", "estimatedDelivery": "2025-11-05", "assignedRoute": {"routeId": 1}, "assignedVendor": {"vendorId": 1}, "shipmentCode": "SH2025001"}'
```

### Step 5: Create Cargo
```bash
curl -X POST http://localhost:8080/api/cargo \
  -H "Content-Type: application/json" \
  -d '{"shipment": {"shipmentId": 1}, "type": "Electronics", "value": 125000.50, "description": "High-end smartphones", "weight": 2500.75, "volume": 45.20, "weightUnit": "kg"}'
```

### Step 6: Create Deliveries
```bash
curl -X POST http://localhost:8080/api/deliveries \
  -H "Content-Type: application/json" \
  -d '{"shipment": {"shipmentId": 1}, "recipient": "Shanghai Electronics Co. Ltd", "status": "Pending"}'
```

### Step 7: Verify All Data
```bash
curl http://localhost:8080/api/vendors
curl http://localhost:8080/api/routes
curl http://localhost:8080/api/shipments
curl http://localhost:8080/api/cargo
curl http://localhost:8080/api/deliveries
```

---

## 🔍 VERIFICATION QUERIES

### Check Relationships
```bash
# Get cargo for specific shipment
curl http://localhost:8080/api/cargo/shipment/1

# Get delivery for specific shipment
curl http://localhost:8080/api/deliveries/shipment/1

# Get shipments by status
curl http://localhost:8080/api/shipments/status/Created

# Get active vendors
curl http://localhost:8080/api/vendors/active

# Search routes
curl "http://localhost:8080/api/routes/search?origin=Port%20of%20Hamburg&destination=Port%20of%20Shanghai"
```

---

## 📊 BRUNO COLLECTION SETUP

### Collection Structure:
```
Cargo Management API/
├── 01. Home/
│   └── Health Check (GET /)
├── 02. Vendors/
│   ├── Create Vendor (POST)
│   ├── Get All Vendors (GET)
│   ├── Get Vendor by ID (GET)
│   ├── Get Active Vendors (GET)
│   ├── Get by Service Type (GET)
│   ├── Update Vendor (PUT)
│   ├── Update Status (PUT)
│   └── Delete Vendor (DELETE)
├── 03. Routes/
│   ├── Create Route (POST)
│   ├── Get All Routes (GET)
│   ├── Get Route by ID (GET)
│   ├── Get by Status (GET)
│   ├── Search Routes (GET)
│   ├── Get by Transport Mode (GET)
│   ├── Update Route (PUT)
│   ├── Update Status (PUT)
│   └── Delete Route (DELETE)
├── 04. Shipments/
│   ├── Create Shipment (POST)
│   ├── Get All Shipments (GET)
│   ├── Get Shipment by ID (GET)
│   ├── Get by Status (GET)
│   ├── Update Shipment (PUT)
│   └── Delete Shipment (DELETE)
├── 05. Cargo/
│   ├── Create Cargo (POST)
│   ├── Get All Cargo (GET)
│   ├── Get Cargo by ID (GET)
│   ├── Get by Shipment ID (GET)
│   ├── Get by Type (GET)
│   ├── Update Cargo (PUT)
│   └── Delete Cargo (DELETE)
└── 06. Deliveries/
    ├── Create Delivery (POST)
    ├── Get All Deliveries (GET)
    ├── Get Delivery by ID (GET)
    ├── Get by Shipment ID (GET)
    ├── Get by Status (GET)
    ├── Update Delivery (PUT)
    └── Delete Delivery (DELETE)
```

### Environment Variables:
```
baseUrl: http://localhost:8080
```

---

## ⚠️ IMPORTANT NOTES

1. **Dependencies**: Always create Vendors and Routes before Shipments
2. **Foreign Keys**: Use actual IDs returned from previous requests
3. **Date Format**: Use ISO format for dates (YYYY-MM-DD or YYYY-MM-DDTHH:mm:ss)
4. **Status Values**: 
   - Shipment: "Created", "In Transit", "Delivered", "Pending"
   - Route: "Active", "Delayed", "Closed"
   - Delivery: "Pending", "Delivered", "Failed"
5. **Error Handling**: All endpoints include proper error responses with validation messages

---

## 🚀 READY FOR PRODUCTION

After successful testing, commit and push your changes:

```bash
git add .
git commit -m "feat: Add complete CRUD API endpoints for all controllers"
git push origin feat/api-endpoints
```

Happy Testing! 🎉