create table if not exists milk_tea_shop_prod.Coupon
(
    coupon_id   int unsigned auto_increment comment 'Mã coupon'
        primary key,
    coupon      varchar(15)                          not null comment 'Mã giảm giá',
    description text                                 null comment 'Mô tả',
    is_active   tinyint(1) default 1                 null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at  datetime   default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at  datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint coupon
        unique (coupon)
);

