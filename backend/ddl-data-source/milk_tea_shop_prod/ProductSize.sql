create table milk_tea_shop_prod.ProductSize
(
    size_id     smallint unsigned auto_increment comment 'Mã kích thước'
        primary key,
    unit_id     smallint unsigned                  null comment 'Mã đơn vị tính',
    name        varchar(5)                         not null comment 'Tên kích thước (ví dụ: S, M, L)',
    description varchar(1000)                      null,
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ProductSize_pk
        unique (unit_id, name),
    constraint ProductSize_ibfk_1
        foreign key (unit_id) references milk_tea_shop_prod.UnitOfMeasure (unit_id)
);

