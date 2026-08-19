INSERT INTO users (id, first_name, last_name, email, password, location, status, profile_picture)
VALUES (1, 'Admin', 'Admin', 'admin@gmail.com', '$2a$12$x/NuM.sSIYNII6GnGh1is.89PgjPZckmfHGyBuOGy6Q.W6Xt8HLza',
        'CLUJ', true, NULL),
       (2, 'Ion', 'Popescu', 'ion@gmail.com', '$2a$12$oc1CZXMNywk4ZzHISZLmqumpHXjnFBhuWxz7tZhvTnb86E1k4AUeG',
        'TIMISOARA', true, NULL),
       (3, 'Maria', 'Ionescu', 'maria@gmail.com', '$2a$12$pJNI92kSHeqIiNeL6T3XKO.FEKCy7ql4TQ5YQ93mOifOgPKUrzlze',
        'TIMISOARA', true, NULL),
       (4, 'Andrei', 'Muresan', 'andrei@gmail.com', '$2a$12$BMNTEt0HP4x1OPwfqW94COKIfwXzPHtbKQTTNm2RgY9rLywfNAWcW',
        'CLUJ', true, NULL),
       (5, 'George', 'Dobre', 'george@gmail.com', '$2a$12$nco8Kg9qDJhieMpHdNBuRebcNfuQFiUr6fqQC8iKh7iV6KZZQt78C',
        'MURES', true, NULL),
       (6, 'Elena', 'Radu', 'elena@gmail.com', '$2a$12$nco8Kg9qDJhieMpHdNBuRebcNfuQFiUr6fqQC8iKh7iV6KZZQt78C',
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
       (6, 'MARKETING')
ON CONFLICT (user_id, role_name) DO NOTHING;


