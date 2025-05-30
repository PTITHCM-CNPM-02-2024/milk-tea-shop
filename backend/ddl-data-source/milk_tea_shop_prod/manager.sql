create table milk_tea_shop_prod.manager
(
    manager_id int unsigned auto_increment comment 'Mã quản lý'
        primary key,
    account_id int unsigned                       null comment 'Mã tài khoản',
    last_name  varchar(70)                        not null comment 'Họ',
    first_name varchar(70)                        not null comment 'Tên',
    gender     enum ('MALE', 'FEMALE', 'OTHER')   null comment 'Giới tính',
    phone      varchar(15)                        not null comment 'Số điện thoại',
    email      varchar(100)                       not null comment 'Email',
    created_at datetime default CURRENT_TIMESTAMP null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint manager_account_uk
        unique (account_id),
    constraint manager_ibfk_1
        foreign key (account_id) references milk_tea_shop_prod.account (account_id)
            on update cascade
);

