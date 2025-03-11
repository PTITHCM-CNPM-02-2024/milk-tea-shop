ALTER TABLE milk_tea_shop_prod.Payment
    MODIFY amount_paid DECIMAL(11, 3);

ALTER TABLE milk_tea_shop_prod.Payment
    MODIFY change_amount DECIMAL(11, 3);

ALTER TABLE milk_tea_shop_prod.OrderDiscount
    MODIFY discount_amount DECIMAL(11, 3);

ALTER TABLE milk_tea_shop_prod.`Order`
    MODIFY final_amount DECIMAL(11, 3);

ALTER TABLE milk_tea_shop_prod.ProductPrice
    MODIFY price DECIMAL(11, 3);

ALTER TABLE milk_tea_shop_prod.`Order`
    MODIFY total_amount DECIMAL(11, 3);