ALTER TABLE employee ADD account_locked BOOLEAN;
ALTER TABLE employee ADD failed_attempt INTEGER;
ALTER TABLE employee ADD lock_time TIMESTAMP;

UPDATE employee
SET account_locked = false, failed_attempt = 0;

