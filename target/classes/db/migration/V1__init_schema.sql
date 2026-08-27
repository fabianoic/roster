CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE store (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(120) NOT NULL,
    address     VARCHAR(255),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE role (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE employee (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(120) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id     UUID NOT NULL REFERENCES role(id),
    store_id    UUID NOT NULL REFERENCES store(id),
    status      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_employee_store ON employee(store_id);
CREATE INDEX idx_employee_role ON employee(role_id);

CREATE TABLE shift (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(id),
    store_id    UUID NOT NULL REFERENCES store(id),
    shift_date  DATE NOT NULL,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED'
                CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELED')),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    CHECK (end_time > start_time)
);

CREATE INDEX idx_shift_employee_date ON shift(employee_id, shift_date);
CREATE INDEX idx_shift_store_date ON shift(store_id, shift_date);

CREATE TABLE availability (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(id),
    weekday     SMALLINT NOT NULL CHECK (weekday BETWEEN 0 AND 6), -- 0=domingo
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    CHECK (end_time > start_time)
);

CREATE INDEX idx_availability_employee ON availability(employee_id);

CREATE TABLE time_off_request (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(id),
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    reason      VARCHAR(255),
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    CHECK (end_date >= start_date)
);

CREATE INDEX idx_time_off_employee ON time_off_request(employee_id);

CREATE TABLE shift_swap_request (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shift_id            UUID NOT NULL REFERENCES shift(id),
    requester_id        UUID NOT NULL REFERENCES employee(id),
    target_employee_id  UUID NOT NULL REFERENCES employee(id),
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                         CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now(),
    CHECK (requester_id <> target_employee_id)
);

CREATE INDEX idx_swap_shift ON shift_swap_request(shift_id);
CREATE INDEX idx_swap_target ON shift_swap_request(target_employee_id);