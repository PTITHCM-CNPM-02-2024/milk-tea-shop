create table milk_tea_shop_prod.Customer
(
    customer_id        int unsigned auto_increment comment 'Mã khách hàng'
        primary key,
    membership_type_id tinyint unsigned                    null comment 'Mã loại thành viên',
    account_id         int unsigned                        null comment 'Mã tài khoản',
    last_name          varchar(35)                         not null comment 'Họ',
    first_name         varchar(25)                         not null comment 'Tên',
    phone              varchar(15)                         not null comment 'Số điện thoại',
    email              varchar(100)                        not null comment 'Email',
    current_points     int       default 0                 null comment 'Điểm hiện tại',
    gender             enum ('MALE', 'FEMALE', 'OTHER')    null,
    created_at         timestamp default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at         timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Ngày cập nhật',
    constraint email
        unique (email),
    constraint phone
        unique (phone),
    constraint Customer_ibfk_1
        foreign key (membership_type_id) references milk_tea_shop_prod.MembershipType (membership_type_id),
    constraint Customer_ibfk_2
        foreign key (account_id) references milk_tea_shop_prod.Account (account_id)
);

create index account_id
    on milk_tea_shop_prod.Customer (account_id);

create index membership_type_id
    on milk_tea_shop_prod.Customer (membership_type_id);

