ALTER TABLE milk_tea_shop_prod.StoreHour
    MODIFY day_of_week TINYINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.StoreHour
    ADD CONSTRAINT StoreHour_pk UNIQUE (store_id, day_of_week);