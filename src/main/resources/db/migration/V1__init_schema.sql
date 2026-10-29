-- ReTech E-Waste Collection and Recycling Tracker
-- V1 Database Schema Migration

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    role VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_role (role),
    INDEX idx_users_email (email)
);

CREATE TABLE IF NOT EXISTS collection_centres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    supported_categories VARCHAR(500) NOT NULL,
    operating_hours VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_centres_city (city),
    INDEX idx_centres_active (is_active)
);

CREATE TABLE IF NOT EXISTS recycling_facilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    capacity_kg_per_day DECIMAL(10,2) DEFAULT 1000.00,
    supported_materials VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_facilities_city (city),
    INDEX idx_facilities_active (is_active)
);

CREATE TABLE IF NOT EXISTS collection_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tracking_code VARCHAR(30) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    item_name VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    estimated_weight_kg DECIMAL(8,2),
    actual_weight_kg DECIMAL(8,2),
    item_condition VARCHAR(50) NOT NULL,
    pickup_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    preferred_pickup_date DATE,
    actual_pickup_date DATE,
    notes TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'REQUEST_SUBMITTED',
    assigned_agent_id BIGINT,
    assigned_facility_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_req_tracking (tracking_code),
    INDEX idx_req_status (status),
    INDEX idx_req_customer (customer_id),
    INDEX idx_req_agent (assigned_agent_id),
    INDEX idx_req_facility (assigned_facility_id),
    CONSTRAINT fk_req_customer FOREIGN KEY (customer_id) REFERENCES users(id),
    CONSTRAINT fk_req_agent FOREIGN KEY (assigned_agent_id) REFERENCES users(id),
    CONSTRAINT fk_req_facility FOREIGN KEY (assigned_facility_id) REFERENCES recycling_facilities(id)
);

CREATE TABLE IF NOT EXISTS request_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    changed_by_user_id BIGINT,
    changed_by_name VARCHAR(100) NOT NULL,
    remarks VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_history_request (request_id),
    CONSTRAINT fk_history_request FOREIGN KEY (request_id) REFERENCES collection_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_user FOREIGN KEY (changed_by_user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS processing_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    facility_id BIGINT NOT NULL,
    operator_id BIGINT NOT NULL,
    stage VARCHAR(30) NOT NULL,
    quantity_received INT NOT NULL,
    weight_received_kg DECIMAL(8,2) NOT NULL,
    weight_reused_kg DECIMAL(8,2) DEFAULT 0.00,
    weight_recycled_kg DECIMAL(8,2) DEFAULT 0.00,
    weight_disposed_kg DECIMAL(8,2) DEFAULT 0.00,
    outcome VARCHAR(50) NOT NULL,
    verification_notes VARCHAR(500),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_proc_request (request_id),
    INDEX idx_proc_facility (facility_id),
    INDEX idx_proc_operator (operator_id),
    CONSTRAINT fk_proc_request FOREIGN KEY (request_id) REFERENCES collection_requests(id),
    CONSTRAINT fk_proc_facility FOREIGN KEY (facility_id) REFERENCES recycling_facilities(id),
    CONSTRAINT fk_proc_operator FOREIGN KEY (operator_id) REFERENCES users(id)
);
