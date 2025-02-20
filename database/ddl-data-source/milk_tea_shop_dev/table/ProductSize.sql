create table if not exists milk_tea_shop_dev.ProductSize
(
    size_id     int auto_increment comment 'Mã kích thước'
        primary key,
    name        varchar(10)                        not null comment 'Tên kích thước (ví dụ: S, M, L)',
    description text                               null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ProductSize_pk
        unique (name)
);

