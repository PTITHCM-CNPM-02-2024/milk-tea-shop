create table if not exists milk_tea_shop_prod.Topping
(
    topping_id   int auto_increment comment 'Mã topping'
        primary key,
    name         varchar(50)                          not null comment 'Tên topping',
    description  text                                 null comment 'Mô tả',
    price        decimal(10, 2)                       not null comment 'Giá',
    is_available tinyint(1) default 1                 null comment 'Topping có sẵn (1: Có, 0: Không)',
    image        mediumblob                           null comment 'Hình ảnh',
    created_at   datetime   default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint Topping_pk
        unique (name)
);

