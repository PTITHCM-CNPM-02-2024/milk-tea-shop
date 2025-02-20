create table if not exists milk_tea_shop_dev.ProductPriceDiscount
(
    product_price_discount_id int auto_increment comment 'Mã giảm giá theo giá sản phẩm'
        primary key,
    product_price_id          int                                  null comment 'Mã giá sản phẩm',
    discount_details_id       int                                  null comment 'Mã chi tiết giảm giá',
    min_required_prod         int                                  not null comment 'Số lượng sản phẩm tối thiểu',
    valid_from                datetime                             not null comment 'Ngày bắt đầu hiệu lực',
    valid_until               datetime                             not null comment 'Ngày kết thúc hiệu lực',
    current_uses              int        default 0                 null comment 'Số lần đã sử dụng',
    max_uses                  int                                  null comment 'Số lần sử dụng tối đa',
    max_uses_per_customer     int                                  null comment 'Số lần sử dụng tối đa cho mỗi khách hàng',
    is_active                 tinyint(1) default 1                 null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at                datetime   default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at                datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ProductPriceDiscount_ibfk_1
        foreign key (product_price_id) references milk_tea_shop_dev.ProductPrice (product_price_id),
    constraint ProductPriceDiscount_ibfk_2
        foreign key (discount_details_id) references milk_tea_shop_dev.DiscountDetail (discount_details_id)
);

