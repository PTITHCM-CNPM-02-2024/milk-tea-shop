create table if not exists milk_tea_shop_prod.product_price
(
    product_price_id int unsigned auto_increment comment 'Mã giá sản phẩm'
        primary key,
    product_id       mediumint unsigned                 not null comment 'Mã sản phẩm',
    size_id          smallint unsigned                  not null comment 'Mã kích thước',
    price            decimal(11, 3)                     not null comment 'Giá',
    created_at       datetime default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint uk_product_price_product_size
        unique (product_id, size_id),
    constraint fk_product_price_product
        foreign key (product_id) references milk_tea_shop_prod.product (product_id)
            on update cascade on delete cascade,
    constraint fk_product_price_product_size
        foreign key (size_id) references milk_tea_shop_prod.product_size (size_id)
            on update cascade on delete cascade
)
    auto_increment = 85;

