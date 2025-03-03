create table if not exists milk_tea_shop_dev.Category
(
    category_id        smallint unsigned auto_increment comment 'Mã danh mục'
        primary key,
    name               varchar(100)                       not null comment 'Tên danh mục',
    description        varchar(1000)                      null comment 'Mô tả danh mục',
    parent_category_id smallint unsigned                  null comment 'Mã danh mục sản phẩm cha',
    created_at         datetime default CURRENT_TIMESTAMP null,
    updated_at         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Category_pk
        unique (name),
    constraint Category_ibfk_1
        foreign key (parent_category_id) references milk_tea_shop_dev.Category (category_id)
);

