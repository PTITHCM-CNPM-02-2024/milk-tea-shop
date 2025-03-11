create table milk_tea_shop_prod.`Order`
(
    order_id       int unsigned auto_increment comment 'Mã đơn hàng'
        primary key,
    customer_id    int unsigned                        null comment 'Mã khách hàng',
    employee_id    int unsigned                        null comment 'Mã nhân viên',
    order_time     timestamp default CURRENT_TIMESTAMP null comment 'Thời gian đặt hàng',
    total_amount   decimal(11, 3)                      not null comment 'Tổng tiền',
    final_amount   decimal(11, 3)                      not null comment 'Thành tiền',
    customize_note varchar(1000)                       null,
    created_at     datetime  default CURRENT_TIMESTAMP null,
    updated_at     datetime  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Order_ibfk_1
        foreign key (customer_id) references milk_tea_shop_prod.Customer (customer_id),
    constraint Order_ibfk_2
        foreign key (employee_id) references milk_tea_shop_prod.Employee (employee_id)
);

create index employee_id
    on milk_tea_shop_prod.`Order` (employee_id);

