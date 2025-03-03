ALTER TABLE milk_tea_shop_prod.CategoryDiscount
    ADD created_at datetime DEFAULT CURRENT_TIMESTAMP NULL;

ALTER TABLE milk_tea_shop_prod.CategoryDiscount
    ADD updated_at datetime DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE milk_tea_shop_prod.ProductDiscount
    ADD created_at datetime DEFAULT CURRENT_TIMESTAMP NULL;

ALTER TABLE milk_tea_shop_prod.ProductDiscount
    ADD updated_at datetime DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE milk_tea_shop_prod.Account
    MODIFY created_at datetime DEFAULT CURRENT_TIMESTAMP NULL;

ALTER TABLE milk_tea_shop_prod.Account
    MODIFY updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;