create table if not exists milk_tea_shop_prod.category
(
    category_id smallint unsigned auto_increment comment 'Mã danh mục'
        primary key,
    name        varchar(100)                       not null comment 'Tên danh mục',
    description varchar(1000)                      null comment 'Mô tả danh mục',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_category_name
        unique (name)
)
    auto_increment = 9;

