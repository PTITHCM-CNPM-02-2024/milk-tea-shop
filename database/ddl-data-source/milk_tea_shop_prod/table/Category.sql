create table if not exists milk_tea_shop_prod.Category
(
    category_id int auto_increment comment 'Mã danh mục'
        primary key,
    name        varchar(100)                       not null comment 'Tên danh mục',
    description text                               null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Category_pk
        unique (name)
);

