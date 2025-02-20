create table if not exists milk_tea_shop_dev.Role
(
    role_id    int auto_increment comment 'Mã vai trò'
        primary key,
    name       varchar(50)                        not null comment 'Tên vai trò (ví dụ: admin, staff, customer)',
    created_at datetime default CURRENT_TIMESTAMP null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Role_pk
        unique (name)
);

