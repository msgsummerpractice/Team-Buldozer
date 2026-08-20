ALTER TABLE events
    ADD COLUMN check_in_code VARCHAR(6),
    ADD COLUMN qr_code BYTEA,
    ADD CONSTRAINT uq_events_check_in_code UNIQUE (check_in_code),
    ADD CONSTRAINT chk_events_check_in_code CHECK (check_in_code ~ '^[0-9]{6}$');
