create table if not exists milk_tea_shop_dev.MembershipType
(
    membership_type_id int auto_increment comment 'Mã loại thành viên'
        primary key,
    type               enum ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM') not null comment 'Loại thành viên',
    discount_value     int                                           not null comment 'Giá trị giảm giá',
    discount_unit      enum ('PERCENT', 'FIXED')                     not null comment 'Đơn vị giảm giá (PERCENT, FIXED)',
    required_point     int                                           not null comment 'Điểm yêu cầu',
    description        varchar(255)                                  null comment 'Mô tả',
    valid_until        datetime                                      null comment 'Ngày hết hạn',
    is_active          tinyint(1) default 1                          null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at         datetime   default CURRENT_TIMESTAMP          null,
    updated_at         datetime   default CURRENT_TIMESTAMP          null on update CURRENT_TIMESTAMP,
    constraint MembershipType_pk
        unique (type)
);

