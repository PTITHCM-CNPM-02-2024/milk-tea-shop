ALTER TABLE milk_tea_shop_prod.UnitOfMeasure
    ADD CONSTRAINT UnitOfMeasure_pk_2 UNIQUE (symbol);

ALTER TABLE milk_tea_shop_prod.UnitOfMeasure
    MODIFY symbol VARCHAR(5) NOT NULL;