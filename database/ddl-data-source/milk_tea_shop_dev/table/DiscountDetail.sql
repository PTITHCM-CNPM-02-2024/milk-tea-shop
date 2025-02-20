create table if not exists milk_tea_shop_dev.DiscountDetail
(
    discount_details_id     int auto_increment comment 'Mã chi tiết giảm giá'
        primary key,
    discount_type_id        int                                  null comment 'Mã loại giảm giá',
    apply_type              enum ('BEST', 'COMBINE')             not null comment 'Kiểu áp dụng (BEST, COMBINE)',
    discount_value          decimal(10, 2)                       not null comment 'Giá trị giảm giá',
    max_discount_amount     decimal(10, 2)                       null comment 'Số tiền giảm giá tối đa',
    is_allowed_reedem_point tinyint(1) default 0                 null comment 'Cho phép đổi điểm (1: Có, 0: Không)',
    created_at              datetime   default CURRENT_TIMESTAMP null,
    updated_at              datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint DiscountDetail_ibfk_1
        foreign key (discount_type_id) references milk_tea_shop_dev.DiscountType (discount_type_id)
);

