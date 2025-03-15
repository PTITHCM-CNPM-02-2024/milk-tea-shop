ALTER TABLE milk_tea_shop_prod.RewardPointLog
    ADD order_id INT UNSIGNED NOT NULL;

ALTER TABLE milk_tea_shop_prod.RewardPointLog
    ADD CONSTRAINT RewardPointLog_pk UNIQUE (order_id, customer_id);

ALTER TABLE milk_tea_shop_prod.RewardPointLog
    ADD CONSTRAINT RewardPointLog_ibfk_2 FOREIGN KEY (order_id) REFERENCES milk_tea_shop_prod.`Order` (order_id) ON DELETE NO ACTION;

ALTER TABLE milk_tea_shop_prod.CategoryDiscount
    MODIFY category_discount_id INT UNSIGNED AUTO_INCREMENT;

ALTER TABLE milk_tea_shop_prod.RewardPointLog
    MODIFY customer_id INT UNSIGNED NOT NULL;