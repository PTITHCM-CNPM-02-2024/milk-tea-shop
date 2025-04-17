create table if not exists milk_tea_shop_prod.service_table
(
    table_id     smallint unsigned auto_increment comment 'Mã bàn'
        primary key,
    area_id      smallint unsigned                    null comment 'Mã khu vực',
    table_number varchar(10)                          not null comment 'Số bàn',
    is_active    tinyint(1) default 1                 null comment 'Bàn có sẵn (1: Có, 0: Không)',
    created_at   datetime   default CURRENT_TIMESTAMP null,
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint service_table_pk
        unique (area_id, table_number),
    constraint service_table_ibfk_1
        foreign key (area_id) references milk_tea_shop_prod.area (area_id)
            on update cascade on delete set null
)
    auto_increment = 12;

