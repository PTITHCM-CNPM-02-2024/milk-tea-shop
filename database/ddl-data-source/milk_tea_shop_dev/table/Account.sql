create table if not exists milk_tea_shop_dev.Account
(
    account_id    int auto_increment comment 'Mã tài khoản'
        primary key,
    role_id       int                                  null comment 'Mã vai trò',
    username      varchar(50)                          not null comment 'Tên đăng nhập',
    password_hash varchar(255)                         not null comment 'Mật khẩu đã mã hóa',
    is_active     tinyint(1) default 1                 null comment 'Tài khoản hoạt động (1: Có, 0: Không)',
    last_login    timestamp                            null comment 'Lần đăng nhập cuối',
    created_at    timestamp  default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at    timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint username
        unique (username),
    constraint Account_ibfk_1
        foreign key (role_id) references milk_tea_shop_dev.Role (role_id)
);

