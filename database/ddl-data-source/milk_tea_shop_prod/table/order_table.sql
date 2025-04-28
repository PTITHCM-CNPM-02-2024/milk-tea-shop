create table if not exists milk_tea_shop_prod.order_table
(
    order_table_id int unsigned auto_increment comment 'Mã đơn hàng và bàn'
        primary key,
    order_id       int unsigned                       not null comment 'Mã đơn hàng',
    table_id       smallint unsigned                  not null comment 'Mã bàn',
    check_in       datetime                           not null comment 'Thời gian vào bàn',
    check_out      datetime                           null comment 'Thời gian rời bàn',
    created_at     datetime default CURRENT_TIMESTAMP null,
    updated_at     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_order_table_order_table
        unique (order_id, table_id),
    constraint fk_order_table_order
        foreign key (order_id) references milk_tea_shop_prod.`order` (order_id)
            on update cascade on delete cascade,
    constraint fk_order_table_service_table
        foreign key (table_id) references milk_tea_shop_prod.service_table (table_id)
            on update cascade on delete cascade
);

