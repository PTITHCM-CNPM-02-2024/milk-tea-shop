ALTER TABLE milk_tea_shop_prod.Customer
    MODIFY first_name VARCHAR(70);

ALTER TABLE milk_tea_shop_prod.Employee
    MODIFY first_name VARCHAR(70) NOT NULL;

ALTER TABLE milk_tea_shop_prod.Manager
    MODIFY first_name VARCHAR(70) NOT NULL;

ALTER TABLE milk_tea_shop_prod.Customer
    MODIFY last_name VARCHAR(70);

ALTER TABLE milk_tea_shop_prod.Employee
    MODIFY last_name VARCHAR(70) NOT NULL;

ALTER TABLE milk_tea_shop_prod.Manager
    MODIFY last_name VARCHAR(70) NOT NULL;