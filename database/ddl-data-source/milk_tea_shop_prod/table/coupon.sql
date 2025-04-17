create table if not exists milk_tea_shop_prod.coupon
(
    coupon_id   int unsigned auto_increment comment 'Mã coupon'
        primary key,
    coupon      varchar(15)                        not null comment 'Mã giảm giá',
    description varchar(1000)                      null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint coupon_uk
        unique (coupon)
)
    auto_increment = 7;

