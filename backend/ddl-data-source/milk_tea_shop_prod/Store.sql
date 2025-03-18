create table milk_tea_shop_prod.Store
(
    store_id     tinyint unsigned auto_increment comment 'Mã cửa hàng'
        primary key,
    name         varchar(100)                       not null comment 'Tên cửa hàng',
    address      varchar(255)                       not null comment 'Địa chỉ',
    phone        varchar(15)                        not null comment 'Số điện thoại',
    email        varchar(100)                       not null,
    opening_date date                               not null comment 'Ngày khai trương',
    tax_code     varchar(20)                        not null,
    closing_time time                               not null,
    opening_time time                               not null comment 'Thời gian mở cửa',
    created_at   datetime default CURRENT_TIMESTAMP null,
    updated_at   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

