create table if not exists milk_tea_shop_prod.order_product
(
    order_product_id int unsigned auto_increment comment 'Mã chi tiết đơn hàng'
        primary key,
    order_id         int unsigned                       not null comment 'Mã đơn hàng',
    product_price_id int unsigned                       not null comment 'Mã giá sản phẩm',
    quantity         smallint unsigned                  not null comment 'Số lượng',
    `option`         varchar(500)                       null comment 'Tùy chọn cho việc lựa chọn lượng đá, đường ',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_order_product_order_product_price
        unique (order_id, product_price_id),
    constraint fk_order_product_order
        foreign key (order_id) references milk_tea_shop_prod.`order` (order_id)
            on update cascade on delete cascade,
    constraint fk_order_product_product_price
        foreign key (product_price_id) references milk_tea_shop_prod.product_price (product_price_id)
            on update cascade on delete cascade
)
    auto_increment = 1395124916;

