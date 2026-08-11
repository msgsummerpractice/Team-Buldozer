INSERT INTO users (id, first_name, last_name, email, password, location, status)
VALUES (1, 'Admin', 'Admin', 'admin@gmail.com', 'admin123', 'CLUJ', true),
       (2, 'Ion', 'Popescu', 'ion@gmail.com', 'ion123', 'TIMISOARA', true),
       (3, 'Maria', 'Ionescu', 'maria@gmail.com', 'maria123', 'TIMISOARA', true),
       (4, 'Andrei', 'Muresan', 'andrei@gmail.com', 'andrei123', 'CLUJ', true),
       (5, 'George', 'Dobre', 'george@gmail.com', 'george123', 'MURES', true);

SELECT setval(pg_get_serial_sequence('users', 'id'), coalesce(max(id), 1))
FROM users;

INSERT INTO user_roles (user_id, role_name)
VALUES (1, 'ADMIN'),
       (2, 'MARKETING'),
       (2, 'HR'),
       (3, 'HR'),
       (4, 'PARTICIPANT'),
       (5, 'PARTICIPANT');
