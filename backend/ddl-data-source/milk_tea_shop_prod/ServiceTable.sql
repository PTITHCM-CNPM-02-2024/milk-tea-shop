create table milk_tea_shop_prod.ServiceTable
(
    table_id     smallint unsigned auto_increment comment 'Mã bàn'
        primary key,
    area_id      smallint unsigned                    null comment 'Mã khu vực',
    table_number varchar(10)                          not null comment 'Số bàn',
    is_active    tinyint(1) default 1                 null comment 'Bàn có sẵn (1: Có, 0: Không)',
    created_at   datetime   default CURRENT_TIMESTAMP null,
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ServiceTable_pk
        unique (area_id, table_number),
    constraint ServiceTable_ibfk_1
        foreign key (area_id) references milk_tea_shop_prod.Area (area_id)
);

create index area_id
    on milk_tea_shop_prod.ServiceTable (area_id);

