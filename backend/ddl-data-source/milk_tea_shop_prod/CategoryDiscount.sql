create table milk_tea_shop_prod.CategoryDiscount
(
    category_discount_id int unsigned auto_increment comment 'Mã định danh duy nhất cho mối quan hệ giữa danh mục và chương trình giảm giá'
        primary key,
    discount_id          int unsigned                       not null comment 'Liên kết với bảng Discount, xác định chương trình giảm giá áp dụng',
    category_id          smallint unsigned                  not null comment 'Liên kết với bảng Category, xác định danh mục được giảm giá',
    min_prod_quantity    tinyint unsigned                   not null comment 'Số lượng sản phẩm tối thiểu từ danh mục này để áp dụng giảm giá',
    created_at           datetime default CURRENT_TIMESTAMP null,
    updated_at           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint CategoryDiscount_pk
        unique (discount_id, category_id),
    constraint CategoryDiscount_ibfk_1
        foreign key (discount_id) references milk_tea_shop_prod.Discount (discount_id),
    constraint CategoryDiscount_ibfk_2
        foreign key (category_id) references milk_tea_shop_prod.Category (category_id)
);

create index category_id
    on milk_tea_shop_prod.CategoryDiscount (category_id);

create index discount_id
    on milk_tea_shop_prod.CategoryDiscount (discount_id);

