DELETE FROM user_roles
WHERE user_id IN (SELECT id FROM users WHERE username = 'visethsopheach@gmail.com');

DELETE FROM users
WHERE username = 'visethsopheach@gmail.com';

INSERT INTO users (school_id, username, email, password_hash, status)
SELECT s.id, 'visethsopheach@gmail.com', 'visethsopheach@gmail.com',
       '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKNmPeoJ0FoJvSK',
       'ACTIVE'
FROM schools s
WHERE s.code = 'DEFAULT';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'PRINCIPAL'
WHERE u.username = 'visethsopheach@gmail.com';