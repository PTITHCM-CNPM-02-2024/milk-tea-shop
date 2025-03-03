ALTER TABLE milk_tea_shop_prod.`Order`
    ADD CONSTRAINT Order_ibfk_3 FOREIGN KEY (payment_id) REFERENCES milk_tea_shop_prod.Payment (payment_id) ON DELETE NO ACTION;

CREATE INDEX Order_ibfk_3 ON milk_tea_shop_prod.`Order` (payment_id);

ALTER TABLE milk_tea_shop_prod.Account
    MODIFY account_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Customer
    MODIFY account_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Employee
    MODIFY account_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Expense
    MODIFY account_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Income
    MODIFY account_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Manager
    MODIFY account_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Area
    MODIFY area_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ServiceTable
    MODIFY area_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Category
    MODIFY category_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.CategoryDiscount
    MODIFY category_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Product
    MODIFY category_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Coupon
    MODIFY coupon_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.CategoryDiscount
    MODIFY current_uses INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Customer
    MODIFY customer_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.`Order`
    MODIFY customer_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.RewardPointLog
    MODIFY customer_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.CategoryDiscount
    MODIFY discount_details_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.DiscountDetail
    MODIFY discount_details_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ProductPriceDiscount
    MODIFY discount_details_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.DiscountDetail
    MODIFY discount_type_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.DiscountType
    MODIFY discount_type_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.MembershipType
    MODIFY discount_value DECIMAL(10, 3);

ALTER TABLE milk_tea_shop_prod.Employee
    MODIFY employee_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.`Order`
    MODIFY employee_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Expense
    MODIFY expense_category_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ExpenseCategory
    MODIFY expense_category_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Expense
    MODIFY expense_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Income
    MODIFY income_category_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.IncomeCategory
    MODIFY income_category_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Income
    MODIFY income_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.InventoryTransaction
    MODIFY manager_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Manager
    MODIFY manager_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.InventoryTransaction
    MODIFY material_id MEDIUMINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Material
    MODIFY material_id MEDIUMINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.CategoryDiscount
    MODIFY max_uses INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.CategoryDiscount
    MODIFY max_uses_per_customer SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Customer
    MODIFY membership_type_id TINYINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.MembershipType
    MODIFY membership_type_id TINYINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.CategoryDiscount
    MODIFY min_prod_nums TINYINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ProductPriceDiscount
    MODIFY min_required_prod SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.`Order`
    MODIFY order_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.OrderProduct
    MODIFY order_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.OrderTable
    MODIFY order_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Payment
    MODIFY order_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.OrderProduct
    MODIFY order_product_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.OrderTopping
    MODIFY order_product_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.OrderTable
    MODIFY order_table_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.`Order`
    MODIFY payment_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Payment
    MODIFY payment_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Payment
    MODIFY payment_method_id TINYINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.PaymentMethod
    MODIFY payment_method_id TINYINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Topping
    MODIFY price DECIMAL(10, 3);

ALTER TABLE milk_tea_shop_prod.Product
    MODIFY product_id MEDIUMINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ProductPrice
    MODIFY product_id MEDIUMINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ProductPriceDiscount
    MODIFY product_price_discount_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.OrderProduct
    MODIFY product_price_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ProductPrice
    MODIFY product_price_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ProductPriceDiscount
    MODIFY product_price_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.OrderProduct
    MODIFY quantity SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.OrderTopping
    MODIFY quantity SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.RewardPointLog
    MODIFY reward_point_log_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Account
    MODIFY role_id TINYINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.`Role`
    MODIFY role_id TINYINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ProductPrice
    MODIFY size_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ProductSize
    MODIFY size_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.StoreHour
    MODIFY store_hour_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Store
    MODIFY store_id TINYINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.StoreHour
    MODIFY store_id TINYINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.InventoryTransaction
    MODIFY supplier_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Supplier
    MODIFY supplier_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.OrderTable
    MODIFY table_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.ServiceTable
    MODIFY table_id SMALLINT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.OrderTopping
    MODIFY topping_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.Topping
    MODIFY topping_id INT UNSIGNED;

ALTER TABLE milk_tea_shop_prod.InventoryTransaction
    MODIFY transaction_id INT UNSIGNED;