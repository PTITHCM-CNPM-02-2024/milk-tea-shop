create table milk_tea_shop_prod.Employee
(
    employee_id int unsigned auto_increment comment 'Mã nhân viên'
        primary key,
    account_id  int unsigned                       null comment 'Mã tài khoản',
    position    varchar(50)                        not null comment 'Chức vụ',
    last_name   varchar(70)                        not null,
    first_name  varchar(70)                        not null,
    gender      enum ('MALE', 'FEMALE', 'OTHER')   null comment 'Giới tính',
    phone       varchar(15)                        not null comment 'Số điện thoại',
    email       varchar(100)                       not null comment 'Email',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint account_id
        unique (account_id),
    constraint Employee_ibfk_1
        foreign key (account_id) references milk_tea_shop_prod.Account (account_id)
);

