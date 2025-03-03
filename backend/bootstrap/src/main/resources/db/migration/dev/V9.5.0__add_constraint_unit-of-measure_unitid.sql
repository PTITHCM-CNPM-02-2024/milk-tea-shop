ALTER TABLE milk_tea_shop_prod.ProductSize
    ADD unit_id SMALLINT UNSIGNED NULL COMMENT 'Mã đơn vị tính' AFTER name;

ALTER TABLE milk_tea_shop_prod.ProductSize
    ADD CONSTRAINT ProductSize_ibfk_1 FOREIGN KEY (unit_id) REFERENCES milk_tea_shop_prod.UnitOfMeasure (unit_id) ON DELETE NO ACTION;

ALTER TABLE milk_tea_shop_prod.UnitOfMeasure
    MODIFY `description` TEXT;

ALTER TABLE milk_tea_shop_prod.ProductSize
    MODIFY name VARCHAR(5);

ALTER TABLE milk_tea_shop_prod.ProductSize
    DROP KEY ProductSize_pk;

ALTER TABLE milk_tea_shop_prod.ProductSize
    ADD CONSTRAINT ProductSize_pk UNIQUE (unit_id, name);