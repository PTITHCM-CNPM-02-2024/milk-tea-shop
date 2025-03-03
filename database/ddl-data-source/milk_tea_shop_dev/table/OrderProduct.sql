create table if not exists milk_tea_shop_dev.OrderProduct
(
    order_product_id        int unsigned auto_increment comment 'Mã chi tiết đơn hàng'
        primary key,
    order_id                int unsigned                       not null comment 'Mã đơn hàng',
    product_price_id        int unsigned                       not null comment 'Mã giá sản phẩm',
    parent_order_product_id int unsigned                       null comment 'Mã đặt hàng sản phẩm gốc, khi sản phẩm ở hàng này được đặt là Topping',
    quantity                smallint unsigned                  not null comment 'Số lượng',
    `option`                varchar(500)                       null comment 'Tùy chọn cho việc lựa chọn lượng đá, đường ',
    created_at              datetime default CURRENT_TIMESTAMP null,
    updated_at              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderProduct_pk
        unique (order_id, product_price_id),
    constraint OrderProduct_ibfk_1
        foreign key (order_id) references milk_tea_shop_dev.`Order` (order_id),
    constraint OrderProduct_ibfk_2
        foreign key (product_price_id) references milk_tea_shop_dev.ProductPrice (product_price_id),
    constraint OrderProduct_ibfk_3
        foreign key (parent_order_product_id) references milk_tea_shop_dev.OrderProduct (order_product_id)
);

