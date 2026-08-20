INSERT INTO attendance_records (id, event_id, user_id)
VALUES (1, 4, 7),
       (2, 4, 8),
       (3, 4, 9),
       (4, 4, 10),
       (5, 4, 11)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('attendance_records', 'id'), coalesce(max(id), 1))
FROM attendance_records;

