create table if not exists milk_tea_shop_prod.RewardPointLog
(
    reward_point_log_id int unsigned auto_increment comment 'Mã lịch sử điểm thưởng'
        primary key,
    customer_id         int unsigned                       null comment 'Mã khách hàng',
    reward_point        int                                not null comment 'Số điểm thưởng',
    operation_type      enum ('EARN', 'REDEEM', 'ADJUST')  not null comment 'Loại thao tác (EARN, REDEEM, ADJUST)',
    created_at          datetime default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint RewardPointLog_ibfk_1
        foreign key (customer_id) references milk_tea_shop_prod.Customer (customer_id)
);

