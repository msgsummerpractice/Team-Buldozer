INSERT INTO attendance_records (id, event_id, user_id, checked_in)
VALUES (1.0, 4, 7, false),
       (2.0, 4, 8, false),
       (3.0, 4, 9, false),
       (4.0, 4, 10, false),
       (5.0, 4, 11, false)
ON CONFLICT (id) DO NOTHING;
