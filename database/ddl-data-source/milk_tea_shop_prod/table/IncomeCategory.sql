create table if not exists milk_tea_shop_prod.IncomeCategory
(
    income_category_id int auto_increment comment 'Mã loại thu nhập'
        primary key,
    name               varchar(50)                        not null comment 'Tên loại thu nhập (ví dụ: Bán hàng, Hoàn tiền)',
    description        text                               null comment 'Mô tả',
    created_at         datetime default CURRENT_TIMESTAMP null,
    updated_at         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint IncomeCategory_pk
        unique (name)
);

