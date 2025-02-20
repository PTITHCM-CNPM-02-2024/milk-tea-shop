create table if not exists milk_tea_shop_dev.ExpenseCategory
(
    expense_category_id int auto_increment comment 'Mã loại chi phí'
        primary key,
    name                varchar(50)                        not null comment 'Tên loại chi phí (ví dụ: Nguyên liệu, Tiền điện, Lương nhân viên)',
    description         text                               null comment 'Mô tả',
    created_at          datetime default CURRENT_TIMESTAMP null,
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ExpenseCategory_pk
        unique (name)
);

