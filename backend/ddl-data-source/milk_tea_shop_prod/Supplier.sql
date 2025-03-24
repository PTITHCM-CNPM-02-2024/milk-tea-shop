create table milk_tea_shop_prod.Supplier
(
    supplier_id    int unsigned auto_increment comment 'Mã nhà cung cấp'
        primary key,
    name           varchar(100)                       not null comment 'Tên nhà cung cấp',
    contact_person varchar(100)                       null comment 'Người liên hệ',
    phone          varchar(15)                        not null comment 'Số điện thoại',
    email          varchar(100)                       null comment 'Email',
    address        text                               null comment 'Địa chỉ',
    created_at     datetime default CURRENT_TIMESTAMP null,
    updated_at     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

