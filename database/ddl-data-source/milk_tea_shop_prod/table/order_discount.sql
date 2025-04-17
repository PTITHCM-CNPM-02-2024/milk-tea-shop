create table if not exists milk_tea_shop_prod.order_discount
(
    order_discount_id int unsigned auto_increment comment 'Mã giảm giá đơn hàng'
        primary key,
    order_id          int unsigned                       not null comment 'Mã đơn  hàng áp dụng giảm giá',
    discount_id       int unsigned                       not null comment 'Mã chương trình giảm giá được áp dụng',
    discount_amount   decimal(11, 3)                     not null comment 'Số tiền giảm giá được áp dụng',
    created_at        datetime default CURRENT_TIMESTAMP null,
    updated_at        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint order_discount_pk
        unique (order_id, discount_id),
    constraint order_discount_ibfk_1
        foreign key (order_id) references milk_tea_shop_prod.`order` (order_id)
            on update cascade on delete cascade,
    constraint order_discount_ibfk_2
        foreign key (discount_id) references milk_tea_shop_prod.discount (discount_id)
            on update cascade on delete cascade
);

