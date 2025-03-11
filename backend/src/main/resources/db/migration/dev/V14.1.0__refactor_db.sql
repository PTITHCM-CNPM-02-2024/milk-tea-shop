ALTER TABLE milk_tea_shop_prod.Expense
    DROP FOREIGN KEY Expense_ibfk_1;

ALTER TABLE milk_tea_shop_prod.Expense
    DROP FOREIGN KEY Expense_ibfk_2;

ALTER TABLE milk_tea_shop_prod.Income
    DROP FOREIGN KEY Income_ibfk_1;

ALTER TABLE milk_tea_shop_prod.Income
    DROP FOREIGN KEY Income_ibfk_2;

ALTER TABLE milk_tea_shop_prod.InventoryTransaction
    DROP FOREIGN KEY InventoryTransaction_ibfk_1;

ALTER TABLE milk_tea_shop_prod.InventoryTransaction
    DROP FOREIGN KEY InventoryTransaction_ibfk_2;

ALTER TABLE milk_tea_shop_prod.InventoryTransaction
    DROP FOREIGN KEY InventoryTransaction_ibfk_3;

ALTER TABLE milk_tea_shop_prod.Material
    DROP FOREIGN KEY Material__ibfk_1;

DROP TABLE milk_tea_shop_prod.Expense;

DROP TABLE milk_tea_shop_prod.ExpenseCategory;

DROP TABLE milk_tea_shop_prod.Income;

DROP TABLE milk_tea_shop_prod.IncomeCategory;

DROP TABLE milk_tea_shop_prod.InventoryTransaction;

DROP TABLE milk_tea_shop_prod.Material;

ALTER TABLE milk_tea_shop_prod.Payment
    MODIFY amount_paid DECIMAL(11, 3) NOT NULL;

ALTER TABLE milk_tea_shop_prod.Payment
    ALTER change_amount SET DEFAULT 0;

ALTER TABLE milk_tea_shop_prod.OrderDiscount
    MODIFY discount_amount DECIMAL(11, 3) NOT NULL;

ALTER TABLE milk_tea_shop_prod.`Order`
    MODIFY final_amount DECIMAL(11, 3) NOT NULL;

ALTER TABLE milk_tea_shop_prod.ProductPrice
    MODIFY price DECIMAL(11, 3) NOT NULL;

ALTER TABLE milk_tea_shop_prod.`Order`
    MODIFY total_amount DECIMAL(11, 3) NOT NULL;