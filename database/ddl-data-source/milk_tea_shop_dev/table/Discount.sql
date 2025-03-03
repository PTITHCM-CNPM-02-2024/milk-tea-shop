create table if not exists milk_tea_shop_dev.Discount
(
    discount_id           int unsigned auto_increment comment 'Mã định danh duy nhất cho chương trình giảm giá'
        primary key,
    discount_type_id      smallint unsigned                      not null comment 'Liên kết với bảng DiscountType, xác định loại giảm giá (ví dụ: giảm giá theo mùa, khuyến mãi đặc biệt, v.v.)',
    coupon_id             int unsigned                           null comment 'Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã giảm giá',
    apply_type            enum ('BEST', 'COMBINE')               not null comment 'Cách áp dụng khi có nhiều giảm giá: BEST - chọn giảm giá tốt nhất, COMBINE - kết hợp các giảm giá',
    discount_value        decimal(10, 2)                         not null comment 'Giá trị giảm giá (phần trăm hoặc số tiền cố định)',
    max_discount_amount   decimal(10, 2)                         null comment 'Giới hạn số tiền giảm giá tối đa, NULL nếu không giới hạn',
    valid_from            datetime                               not null comment 'Thời điểm bắt đầu hiệu lực của chương trình giảm giá',
    valid_until           datetime                               not null comment 'Thời điểm kết thúc hiệu lực của chương trình giảm giá',
    current_uses          int unsigned default '0'               null comment 'Số lần đã sử dụng chương trình giảm giá này',
    max_uses              int unsigned                           null comment 'Số lần sử dụng tối đa cho chương trình giảm giá, NULL nếu không giới hạn',
    max_uses_per_customer smallint unsigned                      null comment 'Số lần tối đa mỗi khách hàng được sử dụng chương trình giảm giá này, NULL nếu không giới hạn',
    is_active             tinyint(1)   default 1                 null comment 'Trạng thái kích hoạt: 1 - đang hoạt động, 0 - không hoạt động',
    created_at            datetime     default CURRENT_TIMESTAMP null comment 'Thời điểm tạo chương trình giảm giá',
    updated_at            datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời điểm cập nhật gần nhất',
    constraint Discount_pk
        unique (coupon_id),
    constraint Discount_ibfk_1
        foreign key (discount_type_id) references milk_tea_shop_dev.DiscountType (discount_type_id),
    constraint Discount_ibfk_2
        foreign key (coupon_id) references milk_tea_shop_dev.Coupon (coupon_id)
);

