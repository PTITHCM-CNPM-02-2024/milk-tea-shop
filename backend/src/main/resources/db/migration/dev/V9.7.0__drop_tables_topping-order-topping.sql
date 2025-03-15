ALTER TABLE milk_tea_shop_prod.OrderTopping
    DROP FOREIGN KEY OrderTopping_ibfk_1;

ALTER TABLE milk_tea_shop_prod.OrderTopping
    DROP FOREIGN KEY OrderTopping_ibfk_2;

DROP TABLE milk_tea_shop_prod.OrderTopping;

DROP TABLE milk_tea_shop_prod.Topping;