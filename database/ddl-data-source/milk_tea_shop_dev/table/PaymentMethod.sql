create table if not exists milk_tea_shop_dev.PaymentMethod
(
    payment_method_id   tinyint unsigned auto_increment comment 'Mã phương thức thanh toán'
        primary key,
    payment_name        varchar(50)                        not null comment 'Tên phương thức thanh toán',
    payment_description varchar(255)                       null comment 'Mô tả phương thức thanh toán',
    created_at          datetime default CURRENT_TIMESTAMP null,
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint PaymentMethod_pk
        unique (payment_name)
);

