create table if not exists milk_tea_shop_prod.manager
(
    manager_id int unsigned auto_increment comment 'Mã quản lý'
        primary key,
    account_id int unsigned                       not null comment 'Mã tài khoản',
    last_name  varchar(70)                        not null comment 'Họ',
    first_name varchar(70)                        not null comment 'Tên',
    gender     enum ('MALE', 'FEMALE', 'OTHER')   null comment 'Giới tính',
    phone      varchar(15)                        not null comment 'Số điện thoại',
    email      varchar(100)                       not null comment 'Email',
    created_at datetime default CURRENT_TIMESTAMP null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_manager_account_id
        unique (account_id),
    constraint uk_manager_email
        unique (email),
    constraint uk_manager_phone
        unique (phone),
    constraint fk_manager_account
        foreign key (account_id) references milk_tea_shop_prod.account (account_id)
            on update cascade
)
    auto_increment = 2;

