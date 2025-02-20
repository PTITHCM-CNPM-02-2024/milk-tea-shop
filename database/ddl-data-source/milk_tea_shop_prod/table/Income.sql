create table if not exists milk_tea_shop_prod.Income
(
    income_id          int auto_increment comment 'Mã thu nhập'
        primary key,
    account_id         int                                null comment 'Mã tài khoản',
    income_category_id int                                null comment 'Mã loại thu nhập',
    amount             decimal(10, 2)                     not null comment 'Số tiền',
    transaction_date   datetime default CURRENT_TIMESTAMP null comment 'Ngày giao dịch',
    description        text                               null comment 'Mô tả',
    created_at         datetime default CURRENT_TIMESTAMP null,
    updated_at         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Income_ibfk_1
        foreign key (account_id) references milk_tea_shop_prod.Account (account_id),
    constraint Income_ibfk_2
        foreign key (income_category_id) references milk_tea_shop_prod.IncomeCategory (income_category_id)
);

