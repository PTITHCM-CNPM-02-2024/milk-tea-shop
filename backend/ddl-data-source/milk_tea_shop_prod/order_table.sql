create table milk_tea_shop_prod.order_table
(
    order_table_id int unsigned auto_increment comment 'Mã đơn hàng và bàn'
        primary key,
    order_id       int unsigned                       not null comment 'Mã đơn hàng',
    table_id       smallint unsigned                  not null comment 'Mã bàn',
    check_in       datetime                           not null comment 'Thời gian vào bàn',
    check_out      datetime                           null comment 'Thời gian rời bàn',
    created_at     datetime default CURRENT_TIMESTAMP null,
    updated_at     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint order_table_pk
        unique (order_id, table_id),
    constraint order_table_ibfk_1
        foreign key (order_id) references milk_tea_shop_prod.`order` (order_id)
            on update cascade on delete cascade,
    constraint order_table_ibfk_2
        foreign key (table_id) references milk_tea_shop_prod.service_table (table_id)
            on update cascade on delete cascade
);

create index order_table_order_idx
    on milk_tea_shop_prod.order_table (order_id);

create index order_table_table_idx
    on milk_tea_shop_prod.order_table (table_id);

grant insert, update on table milk_tea_shop_prod.order_table to vanphong_staff;

grant insert, update on table milk_tea_shop_prod.order_table to thanhphuong_staff;

grant insert, update on table milk_tea_shop_prod.order_table to vanphong_staff@localhost;

grant insert, update on table milk_tea_shop_prod.order_table to thanhphuong_staff@localhost;

