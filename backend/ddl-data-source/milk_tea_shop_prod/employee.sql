create table milk_tea_shop_prod.employee
(
    employee_id int unsigned auto_increment comment 'Mã nhân viên'
        primary key,
    account_id  int unsigned                       null comment 'Mã tài khoản',
    position    varchar(50)                        not null comment 'Chức vụ',
    last_name   varchar(70)                        not null comment 'Họ',
    first_name  varchar(70)                        not null comment 'Tên',
    gender      enum ('MALE', 'FEMALE', 'OTHER')   null comment 'Giới tính',
    phone       varchar(15)                        not null comment 'Số điện thoại',
    email       varchar(100)                       not null comment 'Email',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint employee_account_uk
        unique (account_id),
    constraint employee_ibfk_1
        foreign key (account_id) references milk_tea_shop_prod.account (account_id)
            on update cascade
);

