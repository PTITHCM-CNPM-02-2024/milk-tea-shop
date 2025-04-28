create table if not exists milk_tea_shop_prod.product_size
(
    size_id     smallint unsigned auto_increment comment 'Mã kích thước'
        primary key,
    unit_id     smallint unsigned                  not null comment 'Mã đơn vị tính',
    name        varchar(5)                         not null comment 'Tên kích thước (ví dụ: S, M, L)',
    quantity    smallint unsigned                  not null,
    description varchar(1000)                      null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_product_size_unit_name
        unique (unit_id, name),
    constraint fk_product_size_unit_of_measure
        foreign key (unit_id) references milk_tea_shop_prod.unit_of_measure (unit_id)
            on update cascade
)
    auto_increment = 5;

