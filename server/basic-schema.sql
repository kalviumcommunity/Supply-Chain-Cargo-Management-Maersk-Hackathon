
CREATE TABLE route (
    route_id SERIAL PRIMARY KEY,
    origin_port VARCHAR(100) NOT NULL,
    destination_port VARCHAR(100) NOT NULL,
    duration INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'Active' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE vendor (
    vendor_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_info VARCHAR(200) NOT NULL,
    service_type VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE shipment (
    shipment_id SERIAL PRIMARY KEY,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Created',
    estimated_delivery TIMESTAMP,
    vendor_id INTEGER REFERENCES vendor(vendor_id) ON DELETE SET NULL,
    route_id INTEGER REFERENCES route(route_id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE cargo (
    cargo_id SERIAL PRIMARY KEY,
    shipment_id INTEGER NOT NULL REFERENCES shipment(shipment_id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    value DECIMAL(12, 2) NOT NULL,
    description TEXT,
    weight DECIMAL(10, 2),
    volume DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE delivery (
    delivery_id SERIAL PRIMARY KEY,
    shipment_id INTEGER NOT NULL REFERENCES shipment(shipment_id) ON DELETE CASCADE,
    actual_delivery_date TIMESTAMP,
    recipient VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

