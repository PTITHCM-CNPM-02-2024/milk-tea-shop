create table milk_tea_shop_prod.UnitOfMeasure
(
    unit_id     smallint unsigned auto_increment comment 'Mã đơn vị tính'
        primary key,
    name        varchar(30)                        not null comment 'Tên đơn vị tính (cái, cc, ml, v.v.)',
    symbol      varchar(5)                         not null comment 'Ký hiệu (cc, ml, v.v.)',
    description varchar(1000)                      null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint UnitOfMeasure_pk
        unique (name),
    constraint UnitOfMeasure_pk_2
        unique (symbol)
);

