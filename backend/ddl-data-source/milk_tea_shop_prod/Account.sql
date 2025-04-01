create table milk_tea_shop_prod.Account
(
    account_id    int unsigned auto_increment comment 'Mã tài khoản'
        primary key,
    role_id       tinyint unsigned                       not null comment 'Mã vai trò',
    username      varchar(50)                            not null comment 'Tên đăng nhập',
    password_hash varchar(255)                           not null comment 'Mật khẩu đã mã hóa',
    is_active     tinyint(1)   default 0                 null comment 'Tài khoản hoạt động (1: Có, 0: Không)',
    is_locked     tinyint(1)   default 0                 not null comment 'Tài khoản có bị khóa hay không (Có: 1, Không:0)',
    last_login    timestamp                              null comment 'Lần đăng nhập cuối',
    token_version int unsigned default '0'               not null comment 'Kiểm tra tính hợp lệ của token',
    created_at    datetime     default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at    datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint username
        unique (username),
    constraint Account_ibfk_1
        foreign key (role_id) references milk_tea_shop_prod.Role (role_id)
);

create index role_id
    on milk_tea_shop_prod.Account (role_id);

