create table if not exists milk_tea_shop_prod.`Order`
(
    order_id        int unsigned auto_increment comment 'Mã đơn hàng'
        primary key,
    customer_id     int unsigned                             null comment 'Mã khách hàng',
    employee_id     int unsigned                             null comment 'Mã nhân viên',
    payment_id      int unsigned                             null comment 'Mã thanh toán',
    order_time      timestamp      default CURRENT_TIMESTAMP null comment 'Thời gian đặt hàng',
    total_amount    decimal(10, 2)                           not null comment 'Tổng tiền',
    discount_amount decimal(10, 2) default 0.00              null comment 'Số tiền giảm giá',
    final_amount    decimal(10, 2)                           not null comment 'Thành tiền',
    customize_note  text                                     null comment 'Ghi chú tùy chỉnh',
    created_at      datetime       default CURRENT_TIMESTAMP null,
    updated_at      datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Order_pk
        unique (payment_id),
    constraint Order_ibfk_1
        foreign key (customer_id) references milk_tea_shop_prod.Customer (customer_id),
    constraint Order_ibfk_2
        foreign key (employee_id) references milk_tea_shop_prod.Employee (employee_id),
    constraint Order_ibfk_3
        foreign key (payment_id) references milk_tea_shop_prod.Payment (payment_id)
);

