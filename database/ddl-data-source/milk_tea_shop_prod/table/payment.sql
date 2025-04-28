create table if not exists milk_tea_shop_prod.payment
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
    constraint fk_payment_order
        foreign key (order_id) references milk_tea_shop_prod.`order` (order_id)
            on update cascade on delete cascade,
    constraint fk_payment_payment_method
        foreign key (payment_method_id) references milk_tea_shop_prod.payment_method (payment_method_id)
            on update cascade
)
    auto_increment = 1395105136;

