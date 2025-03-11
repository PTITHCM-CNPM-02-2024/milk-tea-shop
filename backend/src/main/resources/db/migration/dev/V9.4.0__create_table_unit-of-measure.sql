CREATE TABLE milk_tea_shop_prod.UnitOfMeasure
(
    unit_id       SMALLINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT 'Mã đơn vị tính',
    name          VARCHAR(30)                      NOT NULL COMMENT 'Tên đơn vị tính (cái, cc, ml, v.v.)',
    symbol        VARCHAR(10)                      NULL COMMENT 'Ký hiệu (cc, ml, v.v.)',
    `description` LONGTEXT                         NULL COMMENT 'Mô tả',
    created_at    datetime DEFAULT NOW()           NULL,
    updated_at    datetime DEFAULT NOW()           NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (unit_id)
);

ALTER TABLE milk_tea_shop_prod.UnitOfMeasure
    ADD CONSTRAINT UnitOfMeasure_pk UNIQUE (name);