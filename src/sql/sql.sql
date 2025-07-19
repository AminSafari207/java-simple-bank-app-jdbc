create database bank_app;

set search_path to public;

create table customer(
    id serial primary key,
    name varchar,
    email varchar,
    phone int
);

create table account(
  id serial primary key,
  customer_id int references customer(id),
  account_number int,
  balance decimal
);

create table transaction(
    id serial primary key,
    account_id int references account(id),
    type varchar,
    amount decimal,
    date date,
    details varchar
);