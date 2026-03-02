drop table if exists beer_order_line;
drop table if exists beer_order;

create table `beer_order` (
    id varchar(36) not null,
    created_date datetime(6) default null,
    customer_ref varchar(255) default null,
    last_modified_date datetime(6) default null,
    version bigint default null,
    status varchar(255) default null,
    customer_id varchar(36) default null,
    primary key (id),
    foreign key (customer_id) references customer(id)
) engine=InnoDB;

create table `beer_order_line` (
    id varchar(36) not null,
    beer_order_id varchar(36) default null,
    beer_id varchar(36) default null,
    created_date datetime(6) DEFAULT NULL,
    last_modified_date datetime(6) DEFAULT NULL,
    quantity_allocated int default null,
    order_quantity int default null,
    version bigint default null,
    primary key (id),
    foreign key (beer_order_id) references beer_order(id),
    foreign key (beer_id) references beer(id)
) engine=InnoDB;