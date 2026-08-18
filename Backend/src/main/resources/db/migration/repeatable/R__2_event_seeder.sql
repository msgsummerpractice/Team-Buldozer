INSERT INTO events (id, name, location, start_date_time, end_date_time, type, status, poster,
                    registration_start_date, registration_end_date, address, description,
                    created_by_id, food_provided, created_at, check_in_code)
VALUES (1, 'Summer Tech Meetup', 'CLUJ',
        '2026-09-15 18:00:00', '2026-09-15 21:00:00',
        'INTERNAL', 'PUBLISHED', NULL,
        '2026-08-15', '2026-09-14',
        'Str. Memorandumului 28, Cluj-Napoca',
        'A casual meetup for engineers to share what they''ve been working on this summer.',
        2, true, '2026-08-14 10:00:00', 'STM001'),
       (2, 'Onboarding Day', 'TIMISOARA',
        '2026-10-01 09:00:00', '2026-10-01 17:00:00',
        'INTERNAL', 'DRAFT', NULL,
        '2026-09-01', '2026-09-28',
        'Bd. Michelangelo 2, Timisoara',
        'Full-day onboarding session for new hires with team introductions and workshops.',
        2, true, '2026-08-14 10:05:00', 'OBD002'),
       (3, 'Open Doors Hackathon', 'ALL',
        '2026-11-20 08:00:00', '2026-11-22 20:00:00',
        'EXTERNAL', 'PUBLISHED', NULL,
        '2026-09-01', '2026-11-15',
        'Piata Trandafirilor 1, Targu Mures',
        'A 48-hour hackathon open to students and professionals across all offices.',
        2, false, '2026-08-14 10:10:00', 'ODH003')
ON CONFLICT (id) DO UPDATE SET check_in_code = EXCLUDED.check_in_code;

SELECT setval(pg_get_serial_sequence('events', 'id'), coalesce(max(id), 1))
FROM events;
