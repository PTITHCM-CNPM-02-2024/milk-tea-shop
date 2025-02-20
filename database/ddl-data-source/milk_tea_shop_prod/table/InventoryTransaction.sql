create table if not exists milk_tea_shop_prod.InventoryTransaction
(
    transaction_id   int auto_increment comment 'Mã giao dịch kho'
        primary key,
    material_id      int                                null comment 'Mã nguyên vật liệu',
    supplier_id      int                                null comment 'Mã nhà cung cấp',
    manager_id       int                                null comment 'Mã quản lý',
    transaction_type enum ('INBOUND', 'OUTBOUND')       not null comment 'Loại giao dịch (INBOUND, OUTBOUND)',
    quantity         decimal(10, 2)                     not null comment 'Số lượng',
    transaction_date datetime default CURRENT_TIMESTAMP null comment 'Ngày giao dịch',
    unit_cost        decimal(10, 2)                     null comment 'Đơn giá',
    batch_number     varchar(50)                        null comment 'Số lô',
    notes            text                               null comment 'Ghi chú',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint InventoryTransaction_ibfk_1
        foreign key (material_id) references milk_tea_shop_prod.Material (material_id),
    constraint InventoryTransaction_ibfk_2
        foreign key (supplier_id) references milk_tea_shop_prod.Supplier (supplier_id),
    constraint InventoryTransaction_ibfk_3
        foreign key (manager_id) references milk_tea_shop_prod.Manager (manager_id)
);

