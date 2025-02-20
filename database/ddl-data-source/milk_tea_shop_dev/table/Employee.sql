create table if not exists milk_tea_shop_dev.Employee
(
    employee_id int auto_increment comment 'Mã nhân viên'
        primary key,
    account_id  int                                null comment 'Mã tài khoản',
    position    varchar(50)                        not null comment 'Chức vụ',
    last_name   varchar(35)                        not null comment 'Họ',
    first_name  varchar(25)                        not null comment 'Tên',
    sex         enum ('MALE', 'FEMALE', 'OTHER')   null comment 'Giới tính',
    phone       varchar(15)                        not null comment 'Số điện thoại',
    email       varchar(100)                       not null comment 'Email',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint account_id
        unique (account_id),
    constraint Employee_ibfk_1
        foreign key (account_id) references milk_tea_shop_dev.Account (account_id)
);

