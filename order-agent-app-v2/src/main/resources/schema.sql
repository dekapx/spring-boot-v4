create table if not exists orders (
    id bigserial primary key,
    order_number varchar(50) not null unique,
    customer_name varchar(100) not null,
    status varchar(50) not null
    current_location varchar(100) not null,
    estimated_delivery date not null,
    last_updated timestamp not null
);