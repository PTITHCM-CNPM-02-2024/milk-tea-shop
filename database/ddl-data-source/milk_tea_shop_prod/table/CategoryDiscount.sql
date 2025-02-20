create table if not exists milk_tea_shop_prod.CategoryDiscount
(
    category_discount_id  int auto_increment comment 'Mã giảm giá theo danh mục'
        primary key,
    category_id           int                                  null comment 'Mã danh mục',
    discount_details_id   int                                  null comment 'Mã chi tiết giảm giá',
    min_prod_nums         int                                  not null comment 'Số lượng sản phẩm tối thiểu',
    valid_from            datetime                             not null comment 'Ngày bắt đầu hiệu lực',
    valid_until           datetime                             not null comment 'Ngày kết thúc hiệu lực',
    current_uses          int        default 0                 null comment 'Số lần đã sử dụng',
    max_uses              int                                  null comment 'Số lần sử dụng tối đa',
    max_uses_per_customer int                                  null comment 'Số lần sử dụng tối đa cho mỗi khách hàng',
    is_active             tinyint(1) default 1                 null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at            datetime   default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at            datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint CategoryDiscount_ibfk_1
        foreign key (category_id) references milk_tea_shop_prod.Category (category_id),
    constraint CategoryDiscount_ibfk_2
        foreign key (discount_details_id) references milk_tea_shop_prod.DiscountDetail (discount_details_id)
);

