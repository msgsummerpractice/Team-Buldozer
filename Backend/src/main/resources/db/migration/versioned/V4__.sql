ALTER TABLE events
    ADD COLUMN check_in_code VARCHAR(6);

ALTER TABLE events
    ADD CONSTRAINT uq_events_check_in_code UNIQUE (check_in_code);
