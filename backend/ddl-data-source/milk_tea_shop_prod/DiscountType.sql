create table milk_tea_shop_prod.DiscountType
(
    discount_type_id smallint unsigned auto_increment comment 'Mã loại giảm giá'
        primary key,
    promotion_name   varchar(100)                       not null comment 'Tên loại giảm giá',
    description      varchar(1000)                      null comment 'Mô tả',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint DiscountType_pk
        unique (promotion_name)
);

