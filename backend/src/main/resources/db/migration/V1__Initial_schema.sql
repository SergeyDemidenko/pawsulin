-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'PET_OWNER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    is_active BOOLEAN DEFAULT true
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Create pets table
CREATE TABLE pets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    age_years INTEGER,
    weight_kg DECIMAL(5, 2),
    diabetes_type VARCHAR(50) NOT NULL,
    medical_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    CONSTRAINT fk_pets_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index on user_id for faster lookups
CREATE INDEX idx_pets_user_id ON pets(user_id);
CREATE INDEX idx_pets_is_active ON pets(is_active);

-- Create glucose_readings table
CREATE TABLE glucose_readings (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    glucose_value DECIMAL(6, 2) NOT NULL,
    glucose_level VARCHAR(50) NOT NULL,
    reading_time TIMESTAMP NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    CONSTRAINT fk_glucose_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_glucose_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for glucose_readings
CREATE INDEX idx_glucose_pet_id ON glucose_readings(pet_id);
CREATE INDEX idx_glucose_user_id ON glucose_readings(user_id);
CREATE INDEX idx_glucose_reading_time ON glucose_readings(reading_time);
CREATE INDEX idx_glucose_pet_time ON glucose_readings(pet_id, reading_time);

-- Create insulin_logs table
CREATE TABLE insulin_logs (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    insulin_type VARCHAR(100) NOT NULL,
    amount_units DECIMAL(6, 2) NOT NULL,
    injection_time TIMESTAMP NOT NULL,
    batch_number VARCHAR(100),
    expiration_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    CONSTRAINT fk_insulin_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_insulin_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for insulin_logs
CREATE INDEX idx_insulin_pet_id ON insulin_logs(pet_id);
CREATE INDEX idx_insulin_user_id ON insulin_logs(user_id);
CREATE INDEX idx_insulin_injection_time ON insulin_logs(injection_time);
CREATE INDEX idx_insulin_pet_time ON insulin_logs(pet_id, injection_time);

-- Create user_pet_access table for shared pet access
CREATE TABLE user_pet_access (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    access_level VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    CONSTRAINT fk_access_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_access_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_pet_access UNIQUE(user_id, pet_id)
);

-- Create indexes for user_pet_access
CREATE INDEX idx_user_pet_access_user ON user_pet_access(user_id);
CREATE INDEX idx_user_pet_access_pet ON user_pet_access(pet_id);

-- Create glucose_alerts table
CREATE TABLE glucose_alerts (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    low_threshold DECIMAL(6, 2),
    high_threshold DECIMAL(6, 2),
    is_enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_alert_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_alert_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for glucose_alerts
CREATE INDEX idx_glucose_alert_pet ON glucose_alerts(pet_id);
CREATE INDEX idx_glucose_alert_user ON glucose_alerts(user_id);
