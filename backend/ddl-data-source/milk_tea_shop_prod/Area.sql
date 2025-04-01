create table milk_tea_shop_prod.Area
(
    area_id     smallint unsigned auto_increment comment 'Mã khu vực'
        primary key,
    name        char(3)                              not null comment 'Tên khu vực',
    description varchar(255)                         null comment 'Mô tả',
    max_tables  int                                  null comment 'Số bàn tối đa',
    is_active   tinyint(1) default 1                 null comment 'Trạng thái hoạt động (1: Có, 0: Không)',
    created_at  datetime   default CURRENT_TIMESTAMP null,
    updated_at  datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Area_pk
        unique (name)
);

