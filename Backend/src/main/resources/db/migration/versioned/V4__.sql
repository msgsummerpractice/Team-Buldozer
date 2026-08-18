ALTER TABLE events
    ADD COLUMN check_in_code VARCHAR(6);

UPDATE events
SET check_in_code = upper(left(md5(random()::text), 6))
WHERE check_in_code IS NULL;

ALTER TABLE events
    ALTER COLUMN check_in_code SET NOT NULL;

ALTER TABLE events
    ADD CONSTRAINT uq_events_check_in_code UNIQUE (check_in_code);
