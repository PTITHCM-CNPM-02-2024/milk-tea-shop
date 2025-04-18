create table milk_tea_shop_prod.membership_type
(
    membership_type_id tinyint unsigned auto_increment comment 'Mã loại thành viên'
        primary key,
    type               varchar(50)                          not null comment 'Loại thành viên',
    discount_value     decimal(10, 3)                       not null comment 'Giá trị giảm giá',
    discount_unit      enum ('FIXED', 'PERCENTAGE')         not null comment 'Đơn vị giảm giá (PERCENT, FIXED)',
    required_point     int                                  not null comment 'Điểm yêu cầu',
    description        varchar(255)                         null comment 'Mô tả',
    valid_until        datetime                             null comment 'Ngày hết hạn',
    is_active          tinyint(1) default 1                 null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at         datetime   default CURRENT_TIMESTAMP null,
    updated_at         datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint membership_type_pk
        unique (type),
    constraint membership_type_pk_2
        unique (required_point)
);

