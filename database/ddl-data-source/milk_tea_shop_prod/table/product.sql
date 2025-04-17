create table if not exists milk_tea_shop_prod.product
(
    product_id   mediumint unsigned auto_increment comment 'Mã sản phẩm'
        primary key,
    category_id  smallint unsigned                    null comment 'Mã danh mục',
    name         varchar(100)                         not null comment 'Tên sản phẩm',
    description  varchar(1000)                        null comment 'Mô tả sản phẩm',
    is_available tinyint(1) default 1                 null comment 'Sản phẩm có sẵn (1: Có, 0: Không)',
    is_signature tinyint(1) default 0                 null comment 'Sản phẩm đặc trưng (1: Có, 0: Không)',
    image_path   varchar(1000)                        null comment 'Đường dẫn mô tả hình ảnh',
    created_at   datetime   default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint product_ibfk_1
        foreign key (category_id) references milk_tea_shop_prod.category (category_id)
            on update cascade on delete set null
)
    auto_increment = 6008;

