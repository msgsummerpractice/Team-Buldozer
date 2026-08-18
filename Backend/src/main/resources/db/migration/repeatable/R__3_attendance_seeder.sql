INSERT INTO attendance_records (event_id, user_id, checked_in)
VALUES (4, 1, false),
       (4, 2, false),
       (4, 3, false),
       (4, 4, false),
       (4, 5, false)
ON CONFLICT (event_id, user_id) DO NOTHING;
