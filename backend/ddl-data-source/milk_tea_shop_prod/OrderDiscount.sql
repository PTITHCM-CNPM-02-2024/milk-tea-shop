create table milk_tea_shop_prod.OrderDiscount
(
    order_discount_id int unsigned auto_increment comment 'Mã giảm giá đơn hàng'
        primary key,
    order_id          int unsigned                       not null comment 'Mã đơn  hàng áp dụng giảm giá',
    discount_id       int unsigned                       not null comment 'Mã chương trình giảm giá được áp dụng',
    discount_amount   decimal(11, 3)                     not null comment 'Số tiền giảm giá được áp dụng',
    created_at        datetime default CURRENT_TIMESTAMP null,
    updated_at        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderDiscount_pk
        unique (order_id, discount_id),
    constraint OrderDiscount_ibfk_1
        foreign key (order_id) references milk_tea_shop_prod.`Order` (order_id)
            on update cascade on delete cascade,
    constraint OrderDiscount_ibfk_2
        foreign key (discount_id) references milk_tea_shop_prod.Discount (discount_id)
            on update cascade on delete cascade
);

grant insert, update on table milk_tea_shop_prod.OrderDiscount to thanhanh_staff@localhost;

grant insert, update on table milk_tea_shop_prod.OrderDiscount to thanhphuong_staff;

grant insert, update on table milk_tea_shop_prod.OrderDiscount to thanhanh2_staff;

grant insert, update on table milk_tea_shop_prod.OrderDiscount to vanphong_staff;

grant insert, update on table milk_tea_shop_prod.OrderDiscount to thanhphuong_staff@localhost;

grant insert, update on table milk_tea_shop_prod.OrderDiscount to test_staff_2_staff;

grant insert, update on table milk_tea_shop_prod.OrderDiscount to vanphong_staff@localhost;

grant insert, update on table milk_tea_shop_prod.OrderDiscount to test_staff_2_staff@localhost;

grant insert, update on table milk_tea_shop_prod.OrderDiscount to thanhanh_staff;

