INSERT INTO attendance_records (id, event_id, user_id, checked_in)
VALUES (1, 4, 7, false),
       (2, 4, 8, false),
       (3, 4, 9, false),
       (4, 4, 10, false),
       (5, 4, 11, false)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('attendance_records', 'id'), coalesce(max(id), 1))
FROM attendance_records;

