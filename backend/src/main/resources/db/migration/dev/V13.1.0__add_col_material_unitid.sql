ALTER TABLE milk_tea_shop_prod.Material
    ADD unit_id SMALLINT UNSIGNED NOT NULL;

ALTER TABLE milk_tea_shop_prod.Material
    ADD CONSTRAINT Material__ibfk_1 FOREIGN KEY (unit_id) REFERENCES milk_tea_shop_prod.UnitOfMeasure (unit_id) ON DELETE NO ACTION;

CREATE INDEX Material__ibfk_1 ON milk_tea_shop_prod.Material (unit_id);

ALTER TABLE milk_tea_shop_prod.Material
    DROP COLUMN unit;