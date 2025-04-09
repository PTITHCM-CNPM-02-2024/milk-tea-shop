create table milk_tea_shop_prod.ProductPrice
(
    product_price_id int unsigned auto_increment comment 'Mã giá sản phẩm'
        primary key,
    product_id       mediumint unsigned                 not null comment 'Mã sản phẩm',
    size_id          smallint unsigned                  not null comment 'Mã kích thước',
    price            decimal(11, 3)                     not null comment 'Giá',
    created_at       datetime default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint ProductPrice_pk
        unique (product_id, size_id),
    constraint ProductPrice_ibfk_1
        foreign key (product_id) references milk_tea_shop_prod.Product (product_id)
            on update cascade on delete cascade,
    constraint ProductPrice_ibfk_2
        foreign key (size_id) references milk_tea_shop_prod.ProductSize (size_id)
            on update cascade on delete cascade
);

create index product_id
    on milk_tea_shop_prod.ProductPrice (product_id);

create index size_id
    on milk_tea_shop_prod.ProductPrice (size_id);

