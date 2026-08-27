INSERT INTO users (id, first_name, last_name, email, password, location, status, profile_picture)
VALUES (1, 'Admin', 'Admin', 'admin@gmail.com', '$2a$12$x/NuM.sSIYNII6GnGh1is.89PgjPZckmfHGyBuOGy6Q.W6Xt8HLza',
        'CLUJ', true, NULL),
       (2, 'Marketing', 'Marketing', 'marketing@gmail.com',
        '$2a$12$Dc1p0yxuPaZ4uGU52H7VUOIG4TMZcDmGbiZIdKFKaWSaEv7i9i8dC',
        'CLUJ', true, NULL),
       (3, 'Hr', 'Hr', 'hr@gmail.com', '$2a$12$wTYjjZyby8DmavhGjBk8O.fdge0IyD5HAMFUTcb0wqhGyoK3unjC2',
        'CLUJ', true, NULL),
       (4, 'Ioan', 'Puiu', 'ionutpuiu010405@gmail.com', '$2a$12$.h6Z1/tpkOv0dnfdZBgyVeTKKAB8uASI2PFZWQZ4X5GQwU70g04..',
        'MURES', true, NULL),
       (5, 'Laura', 'Muresan', 'lauramuresan420@gmail.com',
        '$2a$12$.h6Z1/tpkOv0dnfdZBgyVeTKKAB8uASI2PFZWQZ4X5GQwU70g04..', 'TIMISOARA', true, NULL),
       (6, 'Ioana', 'Marica', 'ioanamarica66@gmail.com', '$2a$12$.h6Z1/tpkOv0dnfdZBgyVeTKKAB8uASI2PFZWQZ4X5GQwU70g04..',
        'CLUJ', true, NULL),
       (7, 'Cristi', 'Czika', 'cristiczika@gmail.com', '$2a$12$.h6Z1/tpkOv0dnfdZBgyVeTKKAB8uASI2PFZWQZ4X5GQwU70g04..',
        'CLUJ', true, NULL)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('users', 'id'), coalesce(max(id), 1))
FROM users;

INSERT INTO user_roles (user_id, role_name)
VALUES (1, 'ADMIN'),
       (2, 'MARKETING'),
       (3, 'HR'),
       (4, 'PARTICIPANT'),
       (5, 'PARTICIPANT'),
       (6, 'PARTICIPANT'),
       (7, 'PARTICIPANT')
ON CONFLICT (user_id, role_name) DO NOTHING;


