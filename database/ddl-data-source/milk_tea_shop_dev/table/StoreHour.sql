create table if not exists milk_tea_shop_dev.StoreHour
(
    store_hour_id smallint unsigned auto_increment comment 'Mã giờ mở cửa hàng'
        primary key,
    store_id      tinyint unsigned                   null comment 'Mã cửa hàng',
    day_of_week   tinyint unsigned                   not null comment 'Thứ trong tuần (1-7, 1: Chủ nhật)',
    open_time     time                               not null comment 'Giờ mở cửa',
    close_time    time                               not null comment 'Giờ đóng cửa',
    created_at    datetime default CURRENT_TIMESTAMP null,
    updated_at    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint StoreHour_pk
        unique (store_id, day_of_week),
    constraint StoreHour_ibfk_1
        foreign key (store_id) references milk_tea_shop_dev.Store (store_id),
    constraint StoreHour_chk_1
        check (`day_of_week` between 1 and 7)
);

