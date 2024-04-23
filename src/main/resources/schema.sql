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