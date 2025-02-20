create table if not exists milk_tea_shop_prod.OrderTopping
(
    order_topping_id int auto_increment
        primary key,
    order_product_id int                                null,
    topping_id       int                                null comment 'Mã topping',
    quantity         int                                not null comment 'Số lượng',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderTopping_pk
        unique (order_product_id, topping_id),
    constraint OrderTopping_ibfk_1
        foreign key (order_product_id) references milk_tea_shop_prod.OrderProduct (order_product_id),
    constraint OrderTopping_ibfk_2
        foreign key (topping_id) references milk_tea_shop_prod.Topping (topping_id)
);

