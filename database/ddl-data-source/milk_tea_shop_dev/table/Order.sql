create table if not exists milk_tea_shop_dev.`Order`
(
    order_id        int auto_increment comment 'Mã đơn hàng'
        primary key,
    customer_id     int                                      null comment 'Mã khách hàng',
    employee_id     int                                      null comment 'Mã nhân viên',
    payment_id      int                                      null comment 'Mã thanh toán',
    order_time      timestamp      default CURRENT_TIMESTAMP null comment 'Thời gian đặt hàng',
    total_amount    decimal(10, 2)                           not null comment 'Tổng tiền',
    discount_amount decimal(10, 2) default 0.00              null comment 'Số tiền giảm giá',
    final_amount    decimal(10, 2)                           not null comment 'Thành tiền',
    customize_note  text                                     null comment 'Ghi chú tùy chỉnh',
    created_at      datetime       default CURRENT_TIMESTAMP null,
    updated_at      datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Order_ibfk_1
        foreign key (customer_id) references milk_tea_shop_dev.Customer (customer_id),
    constraint Order_ibfk_2
        foreign key (employee_id) references milk_tea_shop_dev.Employee (employee_id)
);

