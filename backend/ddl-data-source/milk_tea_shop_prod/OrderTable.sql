create table milk_tea_shop_prod.OrderTable
(
    order_table_id int unsigned auto_increment comment 'Mã đơn hàng và bàn'
        primary key,
    order_id       int unsigned                       not null comment 'Mã đơn hàng',
    table_id       smallint unsigned                  not null comment 'Mã bàn',
    check_in       datetime                           not null comment 'Thời gian vào bàn',
    check_out      datetime                           null comment 'Thời gian rời bàn',
    created_at     datetime default CURRENT_TIMESTAMP null,
    updated_at     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderTable_pk
        unique (order_id, table_id),
    constraint OrderTable_ibfk_1
        foreign key (order_id) references milk_tea_shop_prod.`Order` (order_id),
    constraint OrderTable_ibfk_2
        foreign key (table_id) references milk_tea_shop_prod.ServiceTable (table_id)
);

create index order_id
    on milk_tea_shop_prod.OrderTable (order_id);

create index table_id
    on milk_tea_shop_prod.OrderTable (table_id);

