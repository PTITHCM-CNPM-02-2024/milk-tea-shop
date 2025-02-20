create table if not exists milk_tea_shop_prod.Payment
(
    payment_id        int auto_increment comment 'Mã thanh toán'
        primary key,
    order_id          int                                      null comment 'Mã đơn hàng',
    payment_method_id int                                      null comment 'Mã phương thức thanh toán',
    amount_paid       decimal(10, 2)                           not null comment 'Số tiền đã trả',
    change_amount     decimal(10, 2) default 0.00              null comment 'Tiền thừa',
    payment_time      timestamp      default CURRENT_TIMESTAMP null comment 'Thời gian thanh toán',
    created_at        datetime       default CURRENT_TIMESTAMP null,
    updated_at        datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint order_id
        unique (order_id),
    constraint Payment_ibfk_1
        foreign key (order_id) references milk_tea_shop_prod.`Order` (order_id),
    constraint Payment_ibfk_2
        foreign key (payment_method_id) references milk_tea_shop_prod.PaymentMethod (payment_method_id)
);

