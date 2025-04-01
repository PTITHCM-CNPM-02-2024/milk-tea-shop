create table milk_tea_shop_prod.OrderProduct
(
    order_product_id int unsigned auto_increment comment 'Mã chi tiết đơn hàng'
        primary key,
    order_id         int unsigned                       not null comment 'Mã đơn hàng',
    product_price_id int unsigned                       not null comment 'Mã giá sản phẩm',
    quantity         smallint unsigned                  not null comment 'Số lượng',
    `option`         varchar(500)                       null comment 'Tùy chọn cho việc lựa chọn lượng đá, đường ',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderProduct_pk
        unique (order_id, product_price_id),
    constraint OrderProduct_ibfk_1
        foreign key (order_id) references milk_tea_shop_prod.`Order` (order_id),
    constraint OrderProduct_ibfk_2
        foreign key (product_price_id) references milk_tea_shop_prod.ProductPrice (product_price_id)
);

create index order_id
    on milk_tea_shop_prod.OrderProduct (order_id);

create index product_price_id
    on milk_tea_shop_prod.OrderProduct (product_price_id);

