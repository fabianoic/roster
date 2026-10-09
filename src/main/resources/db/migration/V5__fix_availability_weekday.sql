-- availability.weekday is defined as ISO-8601 (1 = Monday ... 7 = Sunday), but the API persisted the Java
-- DayOfWeek ordinal (0 = Monday ... 6 = Sunday) until DayOfWeekConverter was introduced. Monday availabilities
-- could never be saved (0 violates the CHECK), so API rows hold 1..6 and must be shifted by one day.
-- The rows inserted by V2__seed_tables.sql already use ISO values and are kept as they are.
UPDATE availability a
SET weekday = a.weekday + 1
WHERE a.weekday < 7
  AND NOT EXISTS (
    SELECT 1
    FROM employee e
    JOIN (VALUES
        ('bruno.costa@empresa.com', 1, true,  'Disponível turno manhã',         TIME '08:00:00', TIME '16:00:00'),
        ('carla.souza@empresa.com', 2, true,  'Disponível período tarde/noite', TIME '13:00:00', TIME '21:00:00'),
        ('diego.rocha@empresa.com', 5, false, 'Faculdade à noite',              TIME '08:00:00', TIME '12:00:00'),
        ('ana.silva@empresa.com',   6, true,  'Turno integral',                 TIME '08:00:00', TIME '18:00:00')
    ) AS seed (email, weekday, is_available, note, start_time, end_time) ON seed.email = e.email
    WHERE e.id = a.employee_id
      AND a.weekday = seed.weekday
      AND a.is_available = seed.is_available
      AND a.note = seed.note
      AND a.start_time = seed.start_time
      AND a.end_time = seed.end_time
);
