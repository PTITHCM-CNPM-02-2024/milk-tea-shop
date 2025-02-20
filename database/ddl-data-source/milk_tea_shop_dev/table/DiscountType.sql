create table if not exists milk_tea_shop_dev.DiscountType
(
    discount_type_id int auto_increment comment 'Mã loại giảm giá'
        primary key,
    promotion_name   varchar(100)                       not null comment 'Tên loại giảm giá',
    description      text                               null comment 'Mô tả',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint DiscountType_pk
        unique (promotion_name)
);

