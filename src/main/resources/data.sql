INSERT INTO users (afm, amka, answer, email, first_name, last_name, password, username) VALUES
    ('111111111', '11111111111', null, 'admin@gmail.com', 'Kevin', 'Stana', '$2a$10$VwKas4ss8uuLL.YpbycfXeT52yMjOXMce3OJe9wotGL4MT/Juo7tS', 'admin');

INSERT INTO role (role) VALUES
('ROLE_ADMIN'),
('ROLE_LAWYER'),
('ROLE_NOTARY'),
('ROLE_CLIENT');

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);