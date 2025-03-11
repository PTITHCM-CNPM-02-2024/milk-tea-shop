ALTER TABLE milk_tea_shop_prod.DiscountDetail
    ADD coupon_id INT UNSIGNED NULL;

ALTER TABLE milk_tea_shop_prod.DiscountDetail
    ADD CONSTRAINT DiscountDetail_ibfk_2 FOREIGN KEY (coupon_id) REFERENCES milk_tea_shop_prod.Coupon (coupon_id) ON DELETE NO ACTION;

CREATE INDEX DiscountDetail_ibfk_2 ON milk_tea_shop_prod.DiscountDetail (coupon_id);