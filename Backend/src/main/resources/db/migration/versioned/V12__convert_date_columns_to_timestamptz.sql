-- Store all date/time values in UTC (timestamptz).
-- Existing naive TIMESTAMP/DATE values were written in server local time (Europe/Bucharest),
-- so they are reinterpreted as Bucharest local time during the conversion.

ALTER TABLE events
    ALTER COLUMN start_date_time TYPE TIMESTAMPTZ
        USING start_date_time AT TIME ZONE 'Europe/Bucharest',
    ALTER COLUMN end_date_time TYPE TIMESTAMPTZ
        USING end_date_time AT TIME ZONE 'Europe/Bucharest',
    ALTER COLUMN registration_start_date TYPE TIMESTAMPTZ
        USING registration_start_date::timestamp AT TIME ZONE 'Europe/Bucharest',
    ALTER COLUMN registration_end_date TYPE TIMESTAMPTZ
        USING registration_end_date::timestamp AT TIME ZONE 'Europe/Bucharest',
    ALTER COLUMN created_at TYPE TIMESTAMPTZ
        USING created_at AT TIME ZONE 'Europe/Bucharest';

ALTER TABLE attendance_records
    ALTER COLUMN checked_in_time TYPE TIMESTAMPTZ
        USING checked_in_time AT TIME ZONE 'Europe/Bucharest';

ALTER TABLE registrations
    ALTER COLUMN registration_date TYPE TIMESTAMPTZ
        USING registration_date::timestamp AT TIME ZONE 'Europe/Bucharest';

ALTER TABLE password_reset_tokens
    ALTER COLUMN created_at TYPE TIMESTAMPTZ
        USING created_at AT TIME ZONE 'Europe/Bucharest',
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP,
    ALTER COLUMN expires_at TYPE TIMESTAMPTZ
        USING expires_at AT TIME ZONE 'Europe/Bucharest';

ALTER TABLE users
    ALTER COLUMN password_updated_at TYPE TIMESTAMPTZ
        USING password_updated_at AT TIME ZONE 'Europe/Bucharest';
