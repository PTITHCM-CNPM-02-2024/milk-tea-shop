create table milk_tea_shop_prod.Payment
(
    payment_id        int unsigned auto_increment comment 'Mã thanh toán'
        primary key,
    order_id          int unsigned                             not null comment 'Mã đơn hàng',
    payment_method_id tinyint unsigned                         not null comment 'Mã phương thức thanh toán',
    status            enum ('PROCESSING', 'CANCELLED', 'PAID') null comment 'Trạng thái thanh toán',
    amount_paid       decimal(11, 3)                           null comment 'Số tiền đã trả',
    change_amount     decimal(11, 3) default 0.000             null comment 'Tiền thừa',
    payment_time      timestamp      default CURRENT_TIMESTAMP null comment 'Thời gian thanh toán',
    created_at        datetime       default CURRENT_TIMESTAMP null,
    updated_at        datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Payment_ibfk_1
        foreign key (order_id) references milk_tea_shop_prod.`Order` (order_id)
            on update cascade on delete cascade,
    constraint Payment_ibfk_2
        foreign key (payment_method_id) references milk_tea_shop_prod.PaymentMethod (payment_method_id)
            on update cascade
);

create index payment_method_id
    on milk_tea_shop_prod.Payment (payment_method_id);

grant insert, update on table milk_tea_shop_prod.Payment to thanhanh_staff@localhost;

grant insert, update on table milk_tea_shop_prod.Payment to vanphong_staff@localhost;

grant insert, update on table milk_tea_shop_prod.Payment to thanhphuong_staff;

grant insert, update on table milk_tea_shop_prod.Payment to thanhanh2_staff;

grant insert, update on table milk_tea_shop_prod.Payment to test_staff_2_staff@localhost;

grant insert, update on table milk_tea_shop_prod.Payment to thanhphuong_staff@localhost;

grant insert, update on table milk_tea_shop_prod.Payment to vanphong_staff;

grant insert, update on table milk_tea_shop_prod.Payment to thanhanh_staff;

grant insert, update on table milk_tea_shop_prod.Payment to test_staff_2_staff;

