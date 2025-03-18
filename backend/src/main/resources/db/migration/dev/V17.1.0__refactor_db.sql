ALTER TABLE milk_tea_shop_prod.StoreHour
    DROP FOREIGN KEY StoreHour_ibfk_1;

DROP TABLE milk_tea_shop_prod.StoreHour;

DROP TABLE milk_tea_shop_prod.Supplier;

ALTER TABLE milk_tea_shop_prod.MembershipType
    MODIFY type VARCHAR(50) NOT NULL;