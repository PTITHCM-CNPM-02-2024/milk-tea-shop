ALTER TABLE milk_tea_shop_prod.Payment
    MODIFY order_id INT UNSIGNED NOT NULL;

ALTER TABLE milk_tea_shop_prod.Payment
    MODIFY payment_method_id TINYINT UNSIGNED NOT NULL;