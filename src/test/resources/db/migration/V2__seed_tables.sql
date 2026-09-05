INSERT INTO store (id, name, address) VALUES
('8ba57eb8-9345-4068-bc65-d63d9575e4bd', 'Loja Centro', 'Rua das Flores, 123 - Centro'),
(gen_random_uuid(), 'Loja Shopping Plaza', 'Av. Principal, 1000 - Piso 2'),
(gen_random_uuid(), 'Loja Zona Sul', 'Av. Atlântica, 450 - Copacabana'),
(gen_random_uuid(), 'Loja Express Norte', 'Rua do Comércio, 88 - Galpão B');


INSERT INTO role (id, name) VALUES
    ('9aa2510e-feda-4b1f-bffe-21cc9e180e9f', 'MANAGER'),
    (gen_random_uuid(), 'SUPERVISOR'),
    (gen_random_uuid(), 'STAFF');

INSERT INTO employee (id, name, email, password_hash, role_id, status) VALUES
('ebb6ea55-b853-4504-a381-0f59ad2a1659', 'Ana Silva', 'ana.silva@empresa.com', '$2a$10$abcdefghijklmnopqrstuv1', (SELECT id FROM role WHERE name = 'MANAGER' LIMIT 1), 'ACTIVE'),
('8d7ec490-9fdc-4a5f-b261-82e53bc74d7f', 'Bruno Costa', 'bruno.costa@empresa.com', '$2a$10$abcdefghijklmnopqrstuv2', (SELECT id FROM role WHERE name = 'MANAGER' LIMIT 1), 'ACTIVE'),
(gen_random_uuid(), 'Carla Souza', 'carla.souza@empresa.com', '$2a$10$abcdefghijklmnopqrstuv3', (SELECT id FROM role WHERE name = 'STAFF' LIMIT 1), 'ACTIVE'),
(gen_random_uuid(), 'Diego Rocha', 'diego.rocha@empresa.com', '$2a$10$abcdefghijklmnopqrstuv4', (SELECT id FROM role WHERE name = 'SUPERVISOR' LIMIT 1), 'ACTIVE'),
(gen_random_uuid(), 'Eduarda Lima', 'eduarda.lima@empresa.com', '$2a$10$abcdefghijklmnopqrstuv5', (SELECT id FROM role WHERE name = 'SUPERVISOR' LIMIT 1), 'INACTIVE');

INSERT INTO shift (id, employee_id, store_id, shift_date, start_time, end_time, status) VALUES
(
    '4355e858-65ba-4e06-b5fd-87c085a47aab',
    (SELECT id FROM employee WHERE email = 'ana.silva@empresa.com'),
    (SELECT id FROM store WHERE name = 'Loja Centro' LIMIT 1),
    CURRENT_DATE, '08:00:00', '16:00:00', 'SCHEDULED'
),
(
    gen_random_uuid(),
    (SELECT id FROM employee WHERE email = 'carla.souza@empresa.com'),
    (SELECT id FROM store WHERE name = 'Loja Centro' LIMIT 1),
    CURRENT_DATE, '10:00:00', '18:00:00', 'COMPLETED'
),
(
    gen_random_uuid(),
    (SELECT id FROM employee WHERE email = 'diego.rocha@empresa.com'),
    (SELECT id FROM store WHERE name = 'Loja Shopping Plaza' LIMIT 1),
    CURRENT_DATE + INTERVAL '1 day', '12:00:00', '20:00:00', 'SCHEDULED'
),
(
    gen_random_uuid(),
    (SELECT id FROM employee WHERE email = 'ana.silva@empresa.com'),
    (SELECT id FROM store WHERE name = 'Loja Express Norte' LIMIT 1),
    CURRENT_DATE + INTERVAL '2 days', '14:00:00', '22:00:00', 'CANCELED'
);

INSERT INTO availability (id, employee_id, weekday, is_available, note, start_time, end_time) VALUES
(
    'f38d39e2-df1c-4df7-86b6-5cb10571376a',
    (SELECT id FROM employee WHERE email = 'bruno.costa@empresa.com'),
    1, true, 'Disponível turno manhã', '08:00:00', '16:00:00'
),
(
    gen_random_uuid(),
    (SELECT id FROM employee WHERE email = 'carla.souza@empresa.com'),
    2, true, 'Disponível período tarde/noite', '13:00:00', '21:00:00'
),
(
    gen_random_uuid(),
    (SELECT id FROM employee WHERE email = 'diego.rocha@empresa.com'),
    5, false, 'Faculdade à noite', '08:00:00', '12:00:00'
),
(
    gen_random_uuid(),
    (SELECT id FROM employee WHERE email = 'ana.silva@empresa.com'),
    6, true, 'Turno integral', '08:00:00', '18:00:00'
);

INSERT INTO time_off_request (id, employee_id, start_date, end_date, reason, type, status) VALUES
(
    '6df1b206-60aa-4183-8f63-3db9f5a04f77',
    (SELECT id FROM employee WHERE email = 'bruno.costa@empresa.com'),
    CURRENT_DATE + INTERVAL '10 days', CURRENT_DATE + INTERVAL '15 days', 'Consultas médicas agendadas', 'SICK', 'APPROVED'
),
(
    gen_random_uuid(),
    (SELECT id FROM employee WHERE email = 'carla.souza@empresa.com'),
    CURRENT_DATE + INTERVAL '20 days', CURRENT_DATE + INTERVAL '30 days', 'Férias anuais', 'HOLIDAY', 'PENDING'
),
(
    gen_random_uuid(),
    (SELECT id FROM employee WHERE email = 'diego.rocha@empresa.com'),
    CURRENT_DATE + INTERVAL '5 days', CURRENT_DATE + INTERVAL '6 days', 'Assuntos pessoais', 'PERSONAL', 'REJECTED'
),
(
    gen_random_uuid(),
    (SELECT id FROM employee WHERE email = 'ana.silva@empresa.com'),
    CURRENT_DATE + INTERVAL '40 days', CURRENT_DATE + INTERVAL '45 days', 'Viagem de férias', 'HOLIDAY', 'PENDING'
);

INSERT INTO shift_swap_request (id, shift_id, requester_id, target_employee_id, status) VALUES
(
    '78c2dfad-487d-4405-920d-ef89eff01040',
    (SELECT id FROM shift WHERE start_time = '08:00:00' LIMIT 1),
    (SELECT id FROM employee WHERE email = 'bruno.costa@empresa.com'),
    (SELECT id FROM employee WHERE email = 'diego.rocha@empresa.com'),
    'PENDING'
),
(
    gen_random_uuid(),
    (SELECT id FROM shift WHERE start_time = '10:00:00' LIMIT 1),
    (SELECT id FROM employee WHERE email = 'carla.souza@empresa.com'),
    (SELECT id FROM employee WHERE email = 'bruno.costa@empresa.com'),
    'APPROVED'
),
(
    gen_random_uuid(),
    (SELECT id FROM shift WHERE start_time = '12:00:00' LIMIT 1),
    (SELECT id FROM employee WHERE email = 'diego.rocha@empresa.com'),
    (SELECT id FROM employee WHERE email = 'carla.souza@empresa.com'),
    'REJECTED'
);