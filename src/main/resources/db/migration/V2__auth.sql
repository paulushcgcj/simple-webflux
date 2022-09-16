CREATE SCHEMA security;

create table security.users(
	username varchar(50) not null primary key,
	password varchar(500) not null,
	enabled boolean not null,
	expiration timestamp with time zone NULL,
    locked bool NOT NULL DEFAULT false,
    credential_expiration timestamp with time zone NOT NULL DEFAULT (now() + '1 mon'::interval)
);

create table security.authority_list (
	authority varchar(50) not null primary key
);

insert into security.authority_list(authority) values
('CAN_READ'),
('CAN_WRITE'),
('CAN_REMOVE'),
('CAN_ENABLE'),
('CAN_IMPERSONATE');

create table security.authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references security.users(username),
	constraint fk_authorities_authority foreign key(authority) references security.authority_list(authority)
);

create unique index ix_auth_username on security.authorities (username,authority);

create table security.groups (
	id bigint generated by default as identity(start with 1) primary key,
	group_name varchar(50) not null
);

insert into security.groups(group_name) values
('ADMIN'),
('USER'),
('READER');

create table security.group_authorities (
	group_id bigint not null,
	authority varchar(50) not null,
	constraint fk_group_authorities_group foreign key(group_id) references security.groups(id),
	constraint fk_group_authorities_authority foreign key(authority) references security.authority_list(authority)
);

insert into security.group_authorities(group_id,authority) values
(1,'CAN_READ'),
(1,'CAN_WRITE'),
(1,'CAN_REMOVE'),
(1,'CAN_ENABLE'),
(1,'CAN_IMPERSONATE'),
(2,'CAN_READ'),
(2,'CAN_WRITE'),
(3,'CAN_READ');

create table security.group_members (
	id bigint generated by default as identity(start with 1) primary key,
	username varchar(50) not null,
	group_id bigint not null,
	constraint fk_group_members_group foreign key(group_id) references security.groups(id)
);