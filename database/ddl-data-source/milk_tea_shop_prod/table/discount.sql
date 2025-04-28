create table if not exists milk_tea_shop_prod.discount
(
    discount_id              int unsigned auto_increment comment 'Mã định danh duy nhất cho chương trình giảm giá'
        primary key,
    name                     varchar(500)                         not null,
    description              varchar(1000)                        null,
    coupon_id                int unsigned                         not null comment 'Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã giảm giá',
    discount_value           decimal(11, 3)                       not null comment 'Giá trị giảm giá (phần trăm hoặc số tiền cố định)',
    discount_type            enum ('FIXED', 'PERCENTAGE')         not null comment 'Loại giảm giá enum ("PERCENTAGE", "FIXED")',
    min_required_order_value decimal(11, 3)                       not null comment 'Gái trị đơn hàng tối thiểu có thể áp dụng',
    max_discount_amount      decimal(11, 3)                       not null comment 'Giới hạn số tiền giảm giá tối đa, NULL nếu không giới hạn',
    min_required_product     smallint unsigned                    null comment 'Số lượng sản phẩm tối thiểu cần mua để khuyến mãi',
    valid_from               datetime                             null comment 'Thời điểm bắt đầu hiệu lực của chương trình giảm giá',
    valid_until              datetime                             not null comment 'Thời điểm kết thúc hiệu lực của chương trình giảm giá',
    current_uses             int unsigned                         null comment 'Số lần đã sử dụng chương trình giảm giá này',
    max_uses                 int unsigned                         null comment 'Số lần sử dụng tối đa cho chương trình giảm giá, NULL nếu không giới hạn',
    max_uses_per_customer    smallint unsigned                    null comment 'Số lần tối đa mỗi khách hàng được sử dụng chương trình giảm giá này, NULL nếu không giới hạn',
    is_active                tinyint(1) default 1                 not null comment 'Trạng thái kích hoạt: 1 - đang hoạt động, 0 - không hoạt động',
    created_at               datetime   default CURRENT_TIMESTAMP null comment 'Thời điểm tạo chương trình giảm giá',
    updated_at               datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời điểm cập nhật gần nhất',
    constraint uk_discount_coupon_id
        unique (coupon_id),
    constraint uk_discount_name
        unique (name),
    constraint fk_discount_coupon
        foreign key (coupon_id) references milk_tea_shop_prod.coupon (coupon_id)
            on update cascade
)
    auto_increment = 1396183247;

