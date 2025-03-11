ALTER TABLE milk_tea_shop_prod.Product
    ADD image_path VARCHAR(1000) NULL COMMENT 'Đường dẫn mô tả hình ảnh';

ALTER TABLE milk_tea_shop_prod.Product
    DROP COLUMN image;