create table if not exists milk_tea_shop_prod.ProductPrice
(
    product_price_id int unsigned auto_increment comment 'Mã giá sản phẩm'
        primary key,
    product_id       mediumint unsigned                 null comment 'Mã sản phẩm',
    size_id          smallint unsigned                  null comment 'Mã kích thước',
    price            decimal(10, 2)                     not null comment 'Giá',
    created_at       datetime default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint ProductPrice_pk
        unique (product_id, size_id),
    constraint ProductPrice_ibfk_1
        foreign key (product_id) references milk_tea_shop_prod.Product (product_id),
    constraint ProductPrice_ibfk_2
        foreign key (size_id) references milk_tea_shop_prod.ProductSize (size_id)
);

