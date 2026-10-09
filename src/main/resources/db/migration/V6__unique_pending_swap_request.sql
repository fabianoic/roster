-- A shift can have only one PENDING swap request at a time. The service already checks this, but two concurrent
-- requests could both pass the check, so the database enforces it with a partial unique index.
-- Duplicates created before this rule existed are resolved by keeping the oldest PENDING request per shift
-- and rejecting the others, otherwise the index could not be created.
UPDATE shift_swap_request s
SET status = 'REJECTED',
    updated_at = now()
WHERE s.status = 'PENDING'
  AND EXISTS (
    SELECT 1
    FROM shift_swap_request older
    WHERE older.shift_id = s.shift_id
      AND older.status = 'PENDING'
      AND (older.created_at, older.id) < (s.created_at, s.id)
);

CREATE UNIQUE INDEX uq_swap_pending_shift ON shift_swap_request(shift_id) WHERE status = 'PENDING';
