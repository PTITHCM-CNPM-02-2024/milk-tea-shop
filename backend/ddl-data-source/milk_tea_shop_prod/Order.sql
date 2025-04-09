create table milk_tea_shop_prod.`Order`
(
    order_id       int unsigned auto_increment comment 'Mã đơn hàng'
        primary key,
    customer_id    int unsigned                                  null comment 'Mã khách hàng',
    employee_id    int unsigned                                  not null comment 'Mã nhân viên',
    order_time     timestamp    default CURRENT_TIMESTAMP        null comment 'Thời gian đặt hàng',
    total_amount   decimal(11, 3)                                null comment 'Tổng tiền',
    final_amount   decimal(11, 3)                                null comment 'Thành tiền',
    status         enum ('PROCESSING', 'CANCELLED', 'COMPLETED') null comment 'Trạng thái đơn hàng',
    customize_note varchar(1000)                                 null comment 'Ghi chú tùy chỉnh',
    point          int unsigned default '1'                      null,
    created_at     datetime     default CURRENT_TIMESTAMP        null,
    updated_at     datetime     default CURRENT_TIMESTAMP        null on update CURRENT_TIMESTAMP,
    constraint Order_ibfk_1
        foreign key (customer_id) references milk_tea_shop_prod.Customer (customer_id)
            on update cascade on delete set null,
    constraint Order_ibfk_2
        foreign key (employee_id) references milk_tea_shop_prod.Employee (employee_id)
            on update cascade on delete cascade
);

create index employee_id
    on milk_tea_shop_prod.`Order` (employee_id);

grant insert, update on table milk_tea_shop_prod.`Order` to test_staff_2_staff;

grant insert, update on table milk_tea_shop_prod.`Order` to vanphong_staff@localhost;

grant insert, update on table milk_tea_shop_prod.`Order` to thanhphuong_staff@localhost;

grant insert, update on table milk_tea_shop_prod.`Order` to vanphong_staff;

grant insert, update on table milk_tea_shop_prod.`Order` to thanhanh2_staff;

grant insert, update on table milk_tea_shop_prod.`Order` to test_staff_2_staff@localhost;

grant insert, update on table milk_tea_shop_prod.`Order` to thanhphuong_staff;

grant insert, update on table milk_tea_shop_prod.`Order` to thanhanh_staff@localhost;

grant insert, update on table milk_tea_shop_prod.`Order` to thanhanh_staff;

