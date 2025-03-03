create table if not exists milk_tea_shop_dev.Customer
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
    sex                enum ('MALE', 'FEMALE', 'OTHER')    null comment 'Giới tính',
    created_at         timestamp default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at         timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Ngày cập nhật',
    constraint email
        unique (email),
    constraint phone
        unique (phone),
    constraint Customer_ibfk_1
        foreign key (membership_type_id) references milk_tea_shop_dev.MembershipType (membership_type_id),
    constraint Customer_ibfk_2
        foreign key (account_id) references milk_tea_shop_dev.Account (account_id)
);

