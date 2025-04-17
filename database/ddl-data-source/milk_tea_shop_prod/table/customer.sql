create table if not exists milk_tea_shop_prod.customer
(
    customer_id        int unsigned auto_increment comment 'Mã khách hàng'
        primary key,
    membership_type_id tinyint unsigned                    not null comment 'Mã loại thành viên',
    account_id         int unsigned                        null comment 'Mã tài khoản',
    last_name          varchar(70)                         null comment 'Họ',
    first_name         varchar(70)                         null comment 'Tên',
    phone              varchar(15)                         not null comment 'Số điện thoại',
    email              varchar(100)                        null comment 'Email',
    current_points     int       default 0                 null comment 'Điểm hiện tại',
    gender             enum ('MALE', 'FEMALE', 'OTHER')    null comment 'Giới tính',
    created_at         timestamp default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at         timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Ngày cập nhật',
    constraint customer_email_uk
        unique (email),
    constraint customer_phone_uk
        unique (phone),
    constraint customer_ibfk_1
        foreign key (membership_type_id) references milk_tea_shop_prod.membership_type (membership_type_id)
            on update cascade,
    constraint customer_ibfk_2
        foreign key (account_id) references milk_tea_shop_prod.account (account_id)
            on update cascade on delete set null
)
    auto_increment = 4268434482;

