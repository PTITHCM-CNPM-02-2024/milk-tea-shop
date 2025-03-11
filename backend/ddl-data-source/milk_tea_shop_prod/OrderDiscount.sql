create table milk_tea_shop_prod.OrderDiscount
(
    order_discount_id int unsigned auto_increment comment 'Mã giảm giá đơn hàng'
        primary key,
    order_product_id  int unsigned                       not null comment 'Mã chi tiết đơn hàng (nếu giảm giá áp dụng cho sản phẩm cụ thể)',
    discount_id       int unsigned                       not null comment 'Mã chương trình giảm giá được áp dụng',
    discount_amount   decimal(11, 3)                     not null comment 'Số tiền giảm giá được áp dụng',
    created_at        datetime default CURRENT_TIMESTAMP null,
    updated_at        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderDiscount_pk
        unique (order_product_id, discount_id),
    constraint OrderDiscount_ibfk_2
        foreign key (discount_id) references milk_tea_shop_prod.Discount (discount_id),
    constraint OrderDiscount_ibfk_3
        foreign key (order_product_id) references milk_tea_shop_prod.OrderProduct (order_product_id)
);

