create table if not exists milk_tea_shop_prod.Material
(
    material_id  int auto_increment comment 'Mã nguyên vật liệu'
        primary key,
    name         varchar(100)                       not null comment 'Tên nguyên vật liệu',
    description  text                               null comment 'Mô tả',
    unit         varchar(20)                        not null comment 'Đơn vị tính (e.g., kg, lít, cái)',
    min_quantity decimal(10, 2)                     not null comment 'Số lượng tối thiểu',
    max_quantity decimal(10, 2)                     not null comment 'Số lượng tối đa',
    created_at   datetime default CURRENT_TIMESTAMP null,
    updated_at   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Material_pk
        unique (name)
);

