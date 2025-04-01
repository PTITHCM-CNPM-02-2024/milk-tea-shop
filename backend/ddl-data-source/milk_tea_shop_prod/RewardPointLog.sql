create table milk_tea_shop_prod.RewardPointLog
(
    reward_point_log_id int unsigned auto_increment comment 'Mã lịch sử điểm thưởng'
        primary key,
    customer_id         int unsigned                       not null comment 'Mã khách hàng',
    order_id            int unsigned                       not null,
    reward_point        int                                not null comment 'Số điểm thưởng',
    created_at          datetime default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint RewardPointLog_pk
        unique (order_id, customer_id),
    constraint RewardPointLog_ibfk_1
        foreign key (customer_id) references milk_tea_shop_prod.Customer (customer_id),
    constraint RewardPointLog_ibfk_2
        foreign key (order_id) references milk_tea_shop_prod.`Order` (order_id)
);

