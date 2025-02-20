create table if not exists milk_tea_shop_dev.ServiceTable
(
    table_id     int auto_increment comment 'Mã bàn'
        primary key,
    area_id      int                                  null comment 'Mã khu vực',
    table_number varchar(10)                          not null comment 'Số bàn',
    is_available tinyint(1) default 1                 null comment 'Bàn có sẵn (1: Có, 0: Không)',
    created_at   datetime   default CURRENT_TIMESTAMP null,
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ServiceTable_pk
        unique (area_id, table_number),
    constraint ServiceTable_ibfk_1
        foreign key (area_id) references milk_tea_shop_dev.Area (area_id)
);

