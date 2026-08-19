INSERT INTO attendance_records (id, event_id, user_id, checked_in)
VALUES (4, 4, 1, false),
       (5, 4, 2, false),
       (6, 4, 3, false),
       (7, 4, 4, false),
       (8, 4, 5, false)
ON CONFLICT (event_id, user_id) DO NOTHING;
