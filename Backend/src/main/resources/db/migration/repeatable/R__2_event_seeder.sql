INSERT INTO events (id, name, location, start_date_time, end_date_time, type, status, poster,
                    registration_start_date, registration_end_date, address, description,
                    created_by_id, food_provided, created_at, check_in_code)
VALUES (1, 'msg Romania Christmas Party', 'ALL',
        TIMESTAMP '2025-12-05 19:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2025-12-06 03:30:00' AT TIME ZONE 'Europe/Bucharest',
        'INTERNAL', 'COMPLETED', NULL,
        TIMESTAMP '2025-11-12 12:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2026-12-01 23:59:59' AT TIME ZONE 'Europe/Bucharest',
        'Strada Orăștiei 10, Cluj-Napoca',
        'Curatorul galeriei are plăcerea de a vă invita la .msg Romania Christmas Party 2025: pe data de 5 decembrie, începând cu ora 19:00, vă invităm la MINA (Museum of Immersive New Art), la o seară dedicată petrecerii și artei.
Tema petrecerii, „A Night at the Gallery” propune o experiență imersivă, unde spațiul galeriei devine o scenă a dialogului, a inspirației și a conexiunii autentice. Totul va fi acompaniat bineînțeles de muzică, dans, mâncare și open bar! 🕺
',
        2, true,
        TIMESTAMP '2025-11-05 10:00:00' AT TIME ZONE 'Europe/Bucharest',
        '100001'),

       (2, '.msg Charity Treat Sale', 'CLUJ',
        TIMESTAMP '2025-12-18 12:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2025-12-18 14:00:00' AT TIME ZONE 'Europe/Bucharest',
        'LOCAL', 'COMPLETED', NULL,
        TIMESTAMP '2025-12-01 00:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2025-12-15 23:59:59' AT TIME ZONE 'Europe/Bucharest',
        'Strada Samuel Brassai 9, Cluj-Napoca',
        'Joi, 18 decembrie, vă invităm la un ultim exercițiu de generozitate pentru acest sezon, iar #CharityTreatSale este ocazia perfectă să facem asta! Anul acesta, ne-am parteneriat cu Rotaract SAMVS și strângem bani pentru copiii de la Casa Aksza și cursurile lor de formare profesională.',
        2, false,
        TIMESTAMP '2026-11-30 10:00:00' AT TIME ZONE 'Europe/Bucharest',
        '100002'),

       (3, 'Backyard get-together', 'ALL',
        TIMESTAMP '2026-06-19 18:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2026-06-20 02:00:00' AT TIME ZONE 'Europe/Bucharest',
        'INTERNAL', 'COMPLETED', NULL,
        TIMESTAMP '2026-06-01 00:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2026-06-12 23:59:59' AT TIME ZONE 'Europe/Bucharest',
        'The Barn, sat Gheorgheni',
        'It`s official: am început înscrierile pentru #Backyard, iar noi abia așteptăm să ne revedem pentru o seară relaxată petrecută împreună, în grădină 🌿
Și anul acesta, evenimentul va avea loc separat în fiecare dintre orașele în care #msgRomania are birouri: Cluj-Napoca, Timișoara și Târgu-Mureș.
Ce v-am pregătit?
🔸 O atmosferă relaxată, perfectă pentru socializare, cu DJ, muzică, dans, jocuri, food & drinks 🥂
🔸 Ne întâlnim la The Barn, în satul Gheorgheni
🔸 Asigurăm transport dus–întors din Cluj-Napoca către locație — revenim în curând cu programul exact pentru cei înscriși
🔸 Dress code: casual & comfy 👟 Recomandăm haine lejere și încălțăminte confortabilă, iar pentru seară poate fi utilă și o jachetă mai groasă, just in case
',
        2, false,
        TIMESTAMP '2026-05-20 12:00:00' AT TIME ZONE 'Europe/Bucharest',
        '100003'),

       (4, '#msgSummerNights', 'CLUJ',
        TIMESTAMP '2026-09-02 18:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2026-09-02 23:00:00' AT TIME ZONE 'Europe/Bucharest',
        'LOCAL', 'PUBLISHED', NULL,
        TIMESTAMP '2026-08-01 00:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2026-08-30 23:59:59' AT TIME ZONE 'Europe/Bucharest',
        'Strada Samuel Brassai 9, Cluj-Napoca',
        'Vara nu s-a terminat... încă. Așa că pe 2 septembrie ne mai întâlnim o dată pe terasă, pentru One Last Cheer to Summer, ultima ediție #msgSummerNights din acest sezon. ☀️
Începând cu ora 18:00, vă așteptăm cu:
🍹 două cocktailuri de vară
🍴 mâncare bună
🎶 muzică și socializare până la 23:00
👕 Dress code: cu cât mai colorat, cu atât mai bine
',
        2, true,
        TIMESTAMP '2026-07-30 09:00:00' AT TIME ZONE 'Europe/Bucharest',
        '100004'),

       (5, '9 ani de #msgTimisoara', 'TIMISOARA',
        TIMESTAMP '2025-03-03 18:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2025-03-03 23:00:00' AT TIME ZONE 'Europe/Bucharest',
        'LOCAL', 'DRAFT', NULL,
        TIMESTAMP '2025-02-01 00:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2025-02-30 23:59:59' AT TIME ZONE 'Europe/Bucharest',
        'Bulevardul Cetatii 93, Timisoara',
        'De aproape un deceniu, scriem cod, creăm și ne distrăm împreună. Inovația este la ea acasă în orașul în care 59 de colegi gestionează 25 de proiecte, zilnic. Să fie încă de 9 x pe atât! 🚀🎊',
        2, false,
        TIMESTAMP '2025-01-30 09:00:00' AT TIME ZONE 'Europe/Bucharest',
        '100005'),

       (6, '10 ani de #msgTarguMures', 'MURES',
        TIMESTAMP '2024-09-02 18:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2024-09-02 23:00:00' AT TIME ZONE 'Europe/Bucharest',
        'LOCAL', 'DRAFT', NULL,
        TIMESTAMP '2024-08-01 00:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2024-08-30 23:59:59' AT TIME ZONE 'Europe/Bucharest',
        'Str. Poligrafiei 67, Targu Mures',
        'La doar 100 km de Cluj, o lume tech total diferită! Ne bucurăm că am putut contribui la evoluției industriei IT din Târgu Mureș și am creat aici o super echipă! Povestea celor 10 ani de #msgTarguMures o puteți descoperi în 🔗 link-ul din bio.',
        2, true,
        TIMESTAMP '2024-07-30 10:00:00' AT TIME ZONE 'Europe/Bucharest',
        '100006'),

       (7, 'Hackathon', NULL,
        TIMESTAMP '2026-01-15 15:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2026-01-15 23:00:00' AT TIME ZONE 'Europe/Bucharest',
        'EXTERNAL', 'COMPLETED', NULL,
        TIMESTAMP '2026-01-01 00:00:00' AT TIME ZONE 'Europe/Bucharest',
        TIMESTAMP '2026-01-14 23:59:59' AT TIME ZONE 'Europe/Bucharest',
        'TBD',
        'Formează o echipă, alege una dintre provocările lansate de partenerii noștri și dezvoltă un prototip funcțional în doar un weekend. Fie că vrei să rezolvi o problemă reală din comunitate, să experimentezi cu tehnologii noi sau să îți demonstrezi skill-urile în fața experților din industrie, acesta este locul potrivit pentru tine.',
        2, true,
        TIMESTAMP '2026-01-01 09:00:00' AT TIME ZONE 'Europe/Bucharest',
        '100007')
ON CONFLICT (id) DO NOTHING;

INSERT INTO registrations (id, event_id, user_id, registration_date, gdpr_consent, photo_consent,
                           food_preference, transport_needed, driver_name, driver_phone_number,
                           accommodation_needed, accommodation_days, status)
VALUES (1, 1, 4, TIMESTAMP '2025-11-14 09:15:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'NONE', true, 'Ioan Puiu',
        '0722334455', true, 1, 'CONFIRMED'),
       (2, 1, 5, TIMESTAMP '2025-11-15 14:20:00' AT TIME ZONE 'Europe/Bucharest', true, false, 'VEGETARIAN', true,
        'Laura Muresan', '0744556677', true, 1, 'CONFIRMED'),
       (3, 1, 6, TIMESTAMP '2025-11-16 08:30:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'VEGAN', false, NULL,
        NULL, false, NULL, 'CONFIRMED'),
       (4, 1, 7, TIMESTAMP '2025-11-18 17:45:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'NONE', false, NULL, NULL,
        false, NULL, 'CONFIRMED'),

       (5, 2, 6, TIMESTAMP '2025-12-02 09:00:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'NONE', false, NULL, NULL,
        false, NULL, 'CONFIRMED'),
       (6, 2, 7, TIMESTAMP '2025-12-03 10:30:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'NONE', false, NULL, NULL,
        false, NULL, 'CONFIRMED'),
       (7, 2, 4, TIMESTAMP '2025-12-05 11:00:00' AT TIME ZONE 'Europe/Bucharest', true, false, 'VEGETARIAN', false,
        NULL, NULL, false, NULL, 'WITHDRAWN'),

       (8, 3, 4, TIMESTAMP '2026-06-02 09:00:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'NONE', true, 'Ioan Puiu',
        '0722334455', true, 1, 'CONFIRMED'),
       (9, 3, 5, TIMESTAMP '2026-06-03 12:00:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'VEGETARIAN', true,
        'Laura Muresan', '0744556677', true, 1, 'CONFIRMED'),
       (10, 3, 6, TIMESTAMP '2026-06-04 15:00:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'VEGAN', false, NULL,
        NULL, false, NULL, 'CONFIRMED'),
       (11, 3, 7, TIMESTAMP '2026-06-05 08:30:00' AT TIME ZONE 'Europe/Bucharest', true, false, 'NONE', false, NULL,
        NULL, false, NULL, 'CONFIRMED'),

       (12, 4, 6, TIMESTAMP '2026-08-05 09:00:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'VEGAN', false, NULL,
        NULL, false, NULL, 'CONFIRMED'),
       (13, 4, 7, TIMESTAMP '2026-08-08 14:00:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'NONE', false, NULL,
        NULL, false, NULL, 'CONFIRMED'),
       (14, 4, 4, TIMESTAMP '2026-08-12 11:00:00' AT TIME ZONE 'Europe/Bucharest', true, false, 'VEGETARIAN', true,
        'Ioan Puiu', '0722334455', true, 1, 'CONFIRMED'),
       (15, 4, 5, TIMESTAMP '2026-08-15 16:00:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'NONE', true,
        'Laura Muresan', '0744556677', true, 1, 'WITHDRAWN'),

       (16, 7, 4, TIMESTAMP '2026-01-03 09:00:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'NONE', true,
        'Ioan Puiu', '0722334455', true, 1, 'CONFIRMED'),
       (17, 7, 5, TIMESTAMP '2026-01-04 10:30:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'VEGETARIAN', true,
        'Laura Muresan', '0744556677', true, 1, 'CONFIRMED'),
       (18, 7, 6, TIMESTAMP '2026-01-05 13:20:00' AT TIME ZONE 'Europe/Bucharest', true, true, 'VEGAN', false, NULL,
        NULL, true, 1, 'CONFIRMED'),
       (19, 7, 7, TIMESTAMP '2026-01-07 18:00:00' AT TIME ZONE 'Europe/Bucharest', true, false, 'NONE', false, NULL,
        NULL, false, NULL, 'WITHDRAWN')
ON CONFLICT (event_id, user_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('registrations', 'id'), coalesce(max(id), 1))
FROM registrations;

INSERT INTO attendance_records (id, event_id, user_id, checked_in, checked_in_time)
VALUES (1, 1, 4, true, TIMESTAMP '2025-12-05 19:12:44' AT TIME ZONE 'Europe/Bucharest'),
       (2, 1, 5, true, TIMESTAMP '2025-12-05 19:18:05' AT TIME ZONE 'Europe/Bucharest'),
       (3, 1, 6, true, TIMESTAMP '2025-12-05 19:05:33' AT TIME ZONE 'Europe/Bucharest'),
       (4, 1, 7, false, NULL),

       (5, 2, 6, true, TIMESTAMP '2025-12-18 12:04:11' AT TIME ZONE 'Europe/Bucharest'),
       (6, 2, 7, true, TIMESTAMP '2025-12-18 12:22:37' AT TIME ZONE 'Europe/Bucharest'),

       (7, 3, 4, true, TIMESTAMP '2026-06-19 18:14:02' AT TIME ZONE 'Europe/Bucharest'),
       (8, 3, 5, true, TIMESTAMP '2026-06-19 18:21:47' AT TIME ZONE 'Europe/Bucharest'),
       (9, 3, 6, true, TIMESTAMP '2026-06-19 18:07:19' AT TIME ZONE 'Europe/Bucharest'),
       (10, 3, 7, false, NULL),

       (11, 7, 4, true, TIMESTAMP '2026-01-15 14:52:11' AT TIME ZONE 'Europe/Bucharest'),
       (12, 7, 5, true, TIMESTAMP '2026-01-15 14:58:03' AT TIME ZONE 'Europe/Bucharest'),
       (13, 7, 6, true, TIMESTAMP '2026-01-15 14:47:42' AT TIME ZONE 'Europe/Bucharest')
ON CONFLICT (event_id, user_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('attendance_records', 'id'), coalesce(max(id), 1))
FROM attendance_records;