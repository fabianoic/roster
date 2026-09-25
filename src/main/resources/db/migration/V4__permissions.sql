-- Permissao granular (acao que um cargo pode executar)
CREATE TABLE permission (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Permissoes concedidas a cada cargo
CREATE TABLE role_permission (
    role_id       UUID NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permission_permission ON role_permission(permission_id);

INSERT INTO permission (name, description) VALUES
    ('ROLE_MANAGE',            'Manage roles and their permissions'),
    ('STORE_READ',             'View stores'),
    ('STORE_WRITE',            'Create, update and delete stores'),
    ('EMPLOYEE_READ_SELF',     'View own employee profile'),
    ('EMPLOYEE_READ_ANY',      'View any employee profile and list employees'),
    ('EMPLOYEE_WRITE',         'Create and update employees and change their status'),
    ('EMPLOYEE_PASSWORD_SELF', 'Change own password'),
    ('EMPLOYEE_PASSWORD_ANY',  'Change any employee password'),
    ('SHIFT_READ',             'View shifts'),
    ('SHIFT_WRITE',            'Create and update shifts'),
    ('SWAP_REQUEST_SELF',      'Create and manage own shift swap requests'),
    ('SWAP_REQUEST_ANY',       'Manage any shift swap request'),
    ('TIME_OFF_SELF',          'Create and manage own time off requests'),
    ('TIME_OFF_ANY',           'View and manage any time off request'),
    ('TIME_OFF_REVIEW',        'Update (approve/reject) time off requests'),
    ('AVAILABILITY_SELF',      'Create and manage own availabilities'),
    ('AVAILABILITY_ANY',       'View and manage any availability');

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r CROSS JOIN permission p
WHERE r.name = 'MANAGER';

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.name IN (
    'STORE_READ', 'EMPLOYEE_READ_SELF', 'EMPLOYEE_READ_ANY', 'EMPLOYEE_PASSWORD_SELF',
    'SHIFT_READ', 'SHIFT_WRITE', 'SWAP_REQUEST_SELF', 'SWAP_REQUEST_ANY',
    'TIME_OFF_SELF', 'TIME_OFF_ANY', 'TIME_OFF_REVIEW', 'AVAILABILITY_SELF', 'AVAILABILITY_ANY')
WHERE r.name = 'SUPERVISOR';

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.name IN (
    'STORE_READ', 'EMPLOYEE_READ_SELF', 'EMPLOYEE_PASSWORD_SELF', 'SHIFT_READ',
    'SWAP_REQUEST_SELF', 'TIME_OFF_SELF', 'AVAILABILITY_SELF')
WHERE r.name = 'STAFF';
