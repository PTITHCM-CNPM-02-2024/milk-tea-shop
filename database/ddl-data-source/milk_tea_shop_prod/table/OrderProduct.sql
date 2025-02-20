create table if not exists milk_tea_shop_prod.OrderProduct
(
    order_product_id int auto_increment comment 'Mã chi tiết đơn hàng'
        primary key,
    order_id         int                                null comment 'Mã đơn hàng',
    product_price_id int                                null comment 'Mã giá sản phẩm',
    quantity         int                                not null comment 'Số lượng',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderProduct_pk
        unique (order_id, product_price_id),
    constraint OrderProduct_ibfk_1
        foreign key (order_id) references milk_tea_shop_prod.`Order` (order_id),
    constraint OrderProduct_ibfk_2
        foreign key (product_price_id) references milk_tea_shop_prod.ProductPrice (product_price_id)
);

