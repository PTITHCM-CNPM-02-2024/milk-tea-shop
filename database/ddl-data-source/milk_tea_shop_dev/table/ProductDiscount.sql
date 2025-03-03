create table if not exists milk_tea_shop_dev.ProductDiscount
(
    product_discount_id int unsigned auto_increment comment 'Mã định danh duy nhất cho mối quan hệ giữa sản phẩm và chương trình giảm giá'
        primary key,
    discount_id         int unsigned                       not null comment 'Liên kết với bảng Discount, xác định chương trình giảm giá áp dụng',
    product_price_id    int unsigned                       not null comment 'Liên kết với bảng ProductPrice, xác định sản phẩm và kích thước được giảm giá',
    min_prod_quantity   tinyint unsigned                   not null comment 'Số lượng tối thiểu của sản phẩm để áp dụng giảm giá',
    created_at          datetime default CURRENT_TIMESTAMP null,
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ProductDiscount_pk
        unique (discount_id, product_price_id),
    constraint ProductDiscount_ibfk_1
        foreign key (discount_id) references milk_tea_shop_dev.Discount (discount_id),
    constraint ProductDiscount_ibfk_2
        foreign key (product_price_id) references milk_tea_shop_dev.ProductPrice (product_price_id)
);

