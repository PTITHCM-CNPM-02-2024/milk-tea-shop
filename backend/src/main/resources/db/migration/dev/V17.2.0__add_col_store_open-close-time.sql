ALTER TABLE milk_tea_shop_prod.Store
    ADD closing_time time NOT NULL;

ALTER TABLE milk_tea_shop_prod.Store
    ADD opening_time time NOT NULL COMMENT 'Thời gian mở cửa';

ALTER TABLE milk_tea_shop_prod.Store
    MODIFY email VARCHAR(100) NOT NULL;

ALTER TABLE milk_tea_shop_prod.Store
    MODIFY tax_code VARCHAR(20) NOT NULL;