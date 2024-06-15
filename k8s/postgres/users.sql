create table IF NOT exists users (
    id bigserial PRIMARY KEY,
    afm varchar(9) unique not null,
    amka varchar(11) unique not null,
    answer varchar(255),
    email varchar(50) unique not null,
    first_name varchar(20) not null,
    last_name varchar(20) not null,
    password varchar(120) not null,
    username varchar(20) unique not null
);

create table IF NOT exists role (
    id bigserial PRIMARY KEY,
    role varchar(20) unique not null
);

create table if not exists user_role (
    user_id bigint not null,
    role_id bigint not null,
    unique (user_id, role_id),
    constraint fk_roles_user foreign key (user_id) references users(id),
    constraint fk_roles_id foreign key (role_id) references role(id)
);

create table if not exists contract (
    id bigserial primary key ,
    date_created bigint not null ,
    date_approved bigint,
    status varchar(255),
    text varchar(255) not null
);

alter table users add column contract_id bigint;
alter table users add constraint fk_user_contract foreign key (contract_id) references contract(id);

INSERT INTO users (answer, afm, amka, email, first_name, last_name, password, username) VALUES
    (null, '111111111', '11111111111', 'admin@gmail.com', 'Kevin', 'Stana', '$2a$10$VwKas4ss8uuLL.YpbycfXeT52yMjOXMce3OJe9wotGL4MT/Juo7tS', 'admin');

INSERT INTO users (answer, afm, amka, email, first_name, last_name, password, username) VALUES
    (null, '222222222', '22222222222', 'client_one@gmail.com', 'Client', 'One', '$2a$10$VwKas4ss8uuLL.YpbycfXeT52yMjOXMce3OJe9wotGL4MT/Juo7tS', 'client_one');

INSERT INTO users (answer, afm, amka, email, first_name, last_name, password, username) VALUES
    (null, '333333333', '33333333333', 'client_two@gmail.com', 'Client', 'Two', '$2a$10$VwKas4ss8uuLL.YpbycfXeT52yMjOXMce3OJe9wotGL4MT/Juo7tS', 'client_two');

INSERT INTO users (answer, afm, amka, email, first_name, last_name, password, username) VALUES
    (null, '444444444', '44444444444', 'lawyer_one@gmail.com', 'Lawyer', 'One', '$2a$10$VwKas4ss8uuLL.YpbycfXeT52yMjOXMce3OJe9wotGL4MT/Juo7tS', 'lawyer_one');

INSERT INTO users (answer, afm, amka, email, first_name, last_name, password, username) VALUES
    (null, '555555555', '55555555555', 'lawyer_two@gmail.com', 'Lawyer', 'Two', '$2a$10$VwKas4ss8uuLL.YpbycfXeT52yMjOXMce3OJe9wotGL4MT/Juo7tS', 'lawyer_two');

INSERT INTO role (role) VALUES
('ROLE_ADMIN'),
('ROLE_LAWYER'),
('ROLE_NOTARY'),
('ROLE_CLIENT');

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (2, 4);
INSERT INTO user_role (user_id, role_id) VALUES (3, 4);
INSERT INTO user_role (user_id, role_id) VALUES (4, 2);
INSERT INTO user_role (user_id, role_id) VALUES (5, 2);