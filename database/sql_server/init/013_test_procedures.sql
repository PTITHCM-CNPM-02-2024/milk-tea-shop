-- SQL Server 2022 Test Scripts for Milk Tea Shop Stored Procedures
-- Test scripts riêng lẻ cho từng stored procedure

USE MilkTeaShop;
GO

SET NOCOUNT ON;
GO

PRINT N'=== SCRIPTS KIỂM TRA CÁC STORED PROCEDURES ===';
PRINT N'Sử dụng dữ liệu test từ file 005_init_optional_data.sql';
PRINT N'';
GO

-- =====================================================
-- 1. TEST AREA PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_area ===';
DECLARE @area_id SMALLINT = SCOPE_IDENTITY();
EXEC sp_insert_area 
    @p_name = 'TST',
    @p_max_tables = 5,
    @p_is_active = 1,
    @p_description = N'Test Area',
    @p_area_id = @area_id OUTPUT;
PRINT N'Inserted Area ID: ' + CAST(@area_id AS NVARCHAR(10));
SELECT * FROM area WHERE area_id = @area_id;
GO

PRINT N'=== TEST sp_update_area ===';
DECLARE @area_id SMALLINT = (SELECT TOP 1 area_id FROM area WHERE name = 'TST');
DECLARE @row_count INT;
EXEC sp_update_area
    @p_area_id = @area_id,
    @p_name = 'UPD',
    @p_max_tables = 10,
    @p_is_active = 1,
    @p_description = N'Updated Test Area',
    @p_row_count = @row_count OUTPUT;
PRINT N'Update affected rows: ' + CAST(@row_count AS NVARCHAR(10));
SELECT * FROM area WHERE area_id = @area_id;
GO

PRINT N'=== TEST sp_delete_area ===';
DECLARE @area_id SMALLINT = (SELECT TOP 1 area_id FROM area WHERE name = 'UPD');
DECLARE @row_count INT;
EXEC sp_delete_area
    @p_area_id = @area_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- 2. TEST CATEGORY PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_category ===';
DECLARE @category_id SMALLINT;
EXEC sp_insert_category
    @p_name = N'Test Category',
    @p_description = N'This is a test category',
    @p_category_id = @category_id OUTPUT;
PRINT N'Inserted Category ID: ' + CAST(@category_id AS NVARCHAR(10));
SELECT * FROM category WHERE category_id = @category_id;
GO

PRINT N'=== TEST sp_update_category ===';
DECLARE @category_id SMALLINT = (SELECT TOP 1 category_id FROM category WHERE name = N'Test Category');
DECLARE @row_count INT;
EXEC sp_update_category
    @p_category_id = @category_id,
    @p_name = N'Updated Test Category',
    @p_description = N'Updated description',
    @p_row_count = @row_count OUTPUT;
PRINT N'Update affected rows: ' + CAST(@row_count AS NVARCHAR(10));
SELECT * FROM category WHERE category_id = @category_id;
GO

PRINT N'=== TEST sp_delete_category ===';
DECLARE @category_id SMALLINT = (SELECT TOP 1 category_id FROM category WHERE name = N'Updated Test Category');
DECLARE @row_count INT;
EXEC sp_delete_category
    @p_category_id = @category_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- 3. TEST ROLE PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_role ===';
DECLARE @role_id TINYINT;
EXEC sp_insert_role
    @p_name = N'test_role',
    @p_description = N'Test role for testing',
    @p_role_id = @role_id OUTPUT;
PRINT N'Inserted Role ID: ' + CAST(@role_id AS NVARCHAR(10));
SELECT * FROM role WHERE role_id = @role_id;
GO

PRINT N'=== TEST sp_update_role ===';
DECLARE @role_id TINYINT = (SELECT TOP 1 role_id FROM role WHERE name = N'test_role');
EXEC sp_update_role
    @p_role_id = @role_id,
    @p_name = N'updated_test_role',
    @p_description = N'Updated test role';
SELECT * FROM role WHERE role_id = @role_id;
GO

PRINT N'=== TEST sp_delete_role ===';
DECLARE @role_id TINYINT = (SELECT TOP 1 role_id FROM role WHERE name = N'updated_test_role');
DECLARE @row_count INT;
EXEC sp_delete_role
    @p_role_id = @role_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- 4. TEST MEMBERSHIP TYPE PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_membership_type ===';
DECLARE @membership_type_id TINYINT;
EXEC sp_insert_membership_type
    @p_type = N'Test Bronze',
    @p_discount_value = 5.000,
    @p_discount_unit = 'PERCENTAGE',
    @p_required_point = 500,
    @p_description = N'Test membership type',
    @p_valid_until = '2026-12-31',
    @p_is_active = 1,
    @p_membership_type_id = @membership_type_id OUTPUT;
PRINT N'Inserted Membership Type ID: ' + CAST(@membership_type_id AS NVARCHAR(10));
SELECT * FROM membership_type WHERE membership_type_id = @membership_type_id;
GO

PRINT N'=== TEST sp_update_membership_type ===';
DECLARE @membership_type_id TINYINT = (SELECT TOP 1 membership_type_id FROM membership_type WHERE type = N'Test Bronze');
EXEC sp_update_membership_type
    @p_membership_type_id = @membership_type_id,
    @p_type = N'Test Silver',
    @p_discount_value = 10.000,
    @p_discount_unit = 'PERCENTAGE',
    @p_required_point = 550,
    @p_description = N'Updated test membership type',
    @p_valid_until = '2026-12-31',
    @p_is_active = 1;
SELECT * FROM membership_type WHERE membership_type_id = @membership_type_id;
GO

PRINT N'=== TEST sp_delete_membership_type ===';
DECLARE @membership_type_id TINYINT = (SELECT TOP 1 membership_type_id FROM membership_type WHERE type = N'Test Silver');
DECLARE @row_count INT;
EXEC sp_delete_membership_type
    @p_membership_type_id = @membership_type_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- 5. TEST PAYMENT METHOD PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_payment_method ===';
DECLARE @payment_method_id TINYINT;
EXEC sp_insert_payment_method
    @p_payment_name = N'Test QR Code',
    @p_payment_description = N'Test QR code payment',
    @p_payment_method_id = @payment_method_id OUTPUT;
PRINT N'Inserted Payment Method ID: ' + CAST(@payment_method_id AS NVARCHAR(10));
SELECT * FROM payment_method WHERE payment_method_id = @payment_method_id;
GO

PRINT N'=== TEST sp_update_payment_method ===';
DECLARE @payment_method_id TINYINT = (SELECT TOP 1 payment_method_id FROM payment_method WHERE payment_name = N'Test QR Code');
EXEC sp_update_payment_method
    @p_payment_method_id = @payment_method_id,
    @p_payment_name = N'Test Mobile Pay',
    @p_payment_description = N'Test mobile payment method';
SELECT * FROM payment_method WHERE payment_method_id = @payment_method_id;
GO

PRINT N'=== TEST sp_delete_payment_method ===';
DECLARE @payment_method_id TINYINT = (SELECT TOP 1 payment_method_id FROM payment_method WHERE payment_name = N'Test Mobile Pay');
DECLARE @row_count INT;
EXEC sp_delete_payment_method
    @p_payment_method_id = @payment_method_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- 6. TEST PRODUCT PROCEDURES (sử dụng data có sẵn)
-- =====================================================

PRINT N'=== TEST sp_insert_product ===';
DECLARE @category_id SMALLINT = (SELECT TOP 1 category_id FROM category WHERE name LIKE N'%TRÀ SỮA%');
DECLARE @product_id INT;
EXEC sp_insert_product
    @p_category_id = @category_id,
    @p_name = N'Test Product New',
    @p_description = N'This is a test product',
    @p_is_available = 1,
    @p_is_signature = 0,
    @p_image_path = N'/images/test_product.jpg',
    @p_product_id = @product_id OUTPUT;
PRINT N'Inserted Product ID: ' + CAST(@product_id AS NVARCHAR(10));
SELECT * FROM product WHERE product_id = @product_id;
GO

PRINT N'=== TEST sp_update_product ===';
DECLARE @product_id INT = (SELECT TOP 1 product_id FROM product WHERE name = N'Test Product New');
DECLARE @category_id SMALLINT = (SELECT TOP 1 category_id FROM category WHERE name LIKE N'%TRÀ SỮA%');
EXEC sp_update_product
    @p_product_id = @product_id,
    @p_category_id = @category_id,
    @p_name = N'Updated Test Product',
    @p_description = N'Updated test product description',
    @p_is_available = 1,
    @p_is_signature = 1,
    @p_image_path = N'/images/updated_test_product.jpg';
SELECT * FROM product WHERE product_id = @product_id;
GO

PRINT N'=== TEST sp_delete_product ===';
DECLARE @product_id INT = (SELECT TOP 1 product_id FROM product WHERE name = N'Updated Test Product');
DECLARE @row_count INT;
EXEC sp_delete_product
    @p_product_id = @product_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- 7. TEST ACCOUNT PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_account ===';
DECLARE @role_id TINYINT = (SELECT TOP 1 role_id FROM role WHERE name = 'staff'); -- sử dụng role có sẵn
DECLARE @account_id INT;
EXEC sp_insert_account
    @p_role_id = @role_id,
    @p_username = N'test_user_new',
    @p_password_hash = N'$2b$10$hashedpassword123',
    @p_is_active = 1,
    @p_is_locked = 0,
    @p_account_id = @account_id OUTPUT;
PRINT N'Inserted Account ID: ' + CAST(@account_id AS NVARCHAR(10));
SELECT * FROM account WHERE account_id = @account_id;
GO

PRINT N'=== TEST sp_update_account_details ===';
DECLARE @account_id INT = (SELECT TOP 1 account_id FROM account WHERE username = N'test_user_new');
DECLARE @role_id TINYINT = (SELECT TOP 1 role_id FROM role WHERE name = 'staff');
EXEC sp_update_account_details
    @p_account_id = @account_id,
    @p_role_id = @role_id,
    @p_username = N'updated_test_user',
    @p_is_active = 1,
    @p_is_locked = 0;
SELECT * FROM account WHERE account_id = @account_id;
GO

PRINT N'=== TEST sp_update_account_password ===';
DECLARE @account_id INT = (SELECT TOP 1 account_id FROM account WHERE username = N'updated_test_user');
EXEC sp_update_account_password
    @p_account_id = @account_id,
    @p_new_password_hash = N'$2b$10$newhashedpassword456';
SELECT account_id, username, password_hash, token_version FROM account WHERE account_id = @account_id;
GO

PRINT N'=== TEST sp_update_account_last_login ===';
DECLARE @account_id INT = (SELECT TOP 1 account_id FROM account WHERE username = N'updated_test_user');
EXEC sp_update_account_last_login
    @p_account_id = @account_id;
SELECT account_id, username, last_login FROM account WHERE account_id = @account_id;
GO

PRINT N'=== TEST sp_delete_account ===';
DECLARE @account_id INT = (SELECT TOP 1 account_id FROM account WHERE username = N'updated_test_user');
DECLARE @row_count INT;
EXEC sp_delete_account
    @p_account_id = @account_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- 8. TEST CUSTOMER PROCEDURES (sử dụng data có sẵn)
-- =====================================================

PRINT N'=== TEST sp_insert_customer ===';
DECLARE @membership_type_id TINYINT = (SELECT TOP 1 membership_type_id FROM membership_type WHERE is_active = 1);
DECLARE @customer_id INT;
EXEC sp_insert_customer
    @p_membership_type_id = @membership_type_id,
    @p_account_id = NULL,
    @p_last_name = N'Nguyễn',
    @p_first_name = N'Test Customer',
    @p_phone = '0999888777',
    @p_email = 'test_customer@example.com',
    @p_current_points = 0,
    @p_gender = 'MALE',
    @p_customer_id = @customer_id OUTPUT;
PRINT N'Inserted Customer ID: ' + CAST(@customer_id AS NVARCHAR(10));
SELECT * FROM customer WHERE customer_id = @customer_id;
GO

PRINT N'=== TEST sp_update_customer ===';
DECLARE @customer_id INT = (SELECT TOP 1 customer_id FROM customer WHERE phone = '0999888777');
DECLARE @membership_type_id TINYINT = (SELECT TOP 1 membership_type_id FROM membership_type WHERE is_active = 1);
EXEC sp_update_customer
    @p_customer_id = @customer_id,
    @p_membership_type_id = @membership_type_id,
    @p_account_id = NULL,
    @p_last_name = N'Trần',
    @p_first_name = N'Updated Test Customer',
    @p_phone = '0999888778',
    @p_email = 'updated_test_customer@example.com',
    @p_current_points = 50,
    @p_gender = 'FEMALE';
SELECT * FROM customer WHERE customer_id = @customer_id;
GO

PRINT N'=== TEST sp_update_customer_points ===';
DECLARE @customer_id INT = (SELECT TOP 1 customer_id FROM customer WHERE phone = '0999888778');
EXEC sp_update_customer_points
    @p_customer_id = @customer_id,
    @p_points_to_add = 25;
SELECT customer_id, first_name, last_name, current_points FROM customer WHERE customer_id = @customer_id;
GO

PRINT N'=== TEST sp_delete_customer ===';
DECLARE @customer_id INT = (SELECT TOP 1 customer_id FROM customer WHERE phone = '0999888778');
DECLARE @row_count INT;
EXEC sp_delete_customer
    @p_customer_id = @customer_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- 9. TEST ORDER PROCEDURES (sử dụng data có sẵn)
-- =====================================================

PRINT N'=== TEST sp_insert_order ===';
DECLARE @customer_id INT = (SELECT TOP 1 customer_id FROM customer WHERE phone = '0387488914'); -- data có sẵn
DECLARE @employee_id INT = (SELECT TOP 1 employee_id FROM employee); -- data có sẵn
DECLARE @order_id INT;
DECLARE @order_time DATETIME2 = GETDATE();
EXEC sp_insert_order
    @p_customer_id = @customer_id,
    @p_employee_id = @employee_id,
    @p_order_time = @order_time,
    @p_status = 'PROCESSING',
    @p_customize_note = N'Test order từ script test',
    @p_point = 1,
    @p_order_id = @order_id OUTPUT;
PRINT N'Inserted Order ID: ' + CAST(@order_id AS NVARCHAR(10));
SELECT * FROM [order] WHERE order_id = @order_id;
GO

PRINT N'=== TEST sp_update_order_status ===';
DECLARE @order_id INT = (SELECT TOP 1 order_id FROM [order] WHERE customize_note LIKE N'%Test order từ script test%');
EXEC sp_update_order_status
    @p_order_id = @order_id,
    @p_status = 'PROCESSING';
SELECT order_id, status, updated_at FROM [order] WHERE order_id = @order_id;
GO

PRINT N'=== TEST sp_update_order_amounts ===';
DECLARE @order_id INT = (SELECT TOP 1 order_id FROM [order] WHERE customize_note LIKE N'%Test order từ script test%');
EXEC sp_update_order_amounts
    @p_order_id = @order_id,
    @p_total_amount = 75000.000,
    @p_final_amount = 65000.000;
SELECT order_id, total_amount, final_amount, updated_at FROM [order] WHERE order_id = @order_id;
GO

PRINT N'=== TEST sp_complete_order ===';
DECLARE @order_id INT = (SELECT TOP 1 order_id FROM [order] WHERE customize_note LIKE N'%Test order từ script test%');
DECLARE @payment_method_id TINYINT = (SELECT TOP 1 payment_method_id FROM payment_method); -- data có sẵn
DECLARE @success BIT;
DECLARE @message NVARCHAR(255);
DECLARE @change_amount DECIMAL(11, 3);
EXEC sp_complete_order
    @p_order_id = @order_id,
    @p_payment_method_id = @payment_method_id,
    @p_amount_paid = 110000.000,
    @p_success = @success OUTPUT,
    @p_message = @message OUTPUT,
    @p_change_amount = @change_amount OUTPUT;
PRINT N'Complete Order Success: ' + CAST(@success AS NVARCHAR(10));
PRINT N'Message: ' + @message;
PRINT N'Change Amount: ' + CAST(@change_amount AS NVARCHAR(20));
SELECT * FROM [order] WHERE order_id = @order_id;
SELECT * FROM payment WHERE order_id = @order_id;
GO

PRINT N'=== TEST sp_cancel_order ===';
-- Tạo order mới để test cancel
DECLARE @customer_id INT = (SELECT TOP 1 customer_id FROM customer WHERE phone = '0387488914');
DECLARE @employee_id INT = (SELECT TOP 1 employee_id FROM employee);
DECLARE @order_id INT;
DECLARE @order_time DATETIME2 = GETDATE();
EXEC sp_insert_order
    @p_customer_id = @customer_id,
    @p_employee_id = @employee_id,
    @p_order_time = @order_time,
    @p_status = 'PROCESSING',
    @p_customize_note = N'Test order để cancel',
    @p_point = 1,
    @p_order_id = @order_id OUTPUT;

-- Cancel order
DECLARE @success BIT;
DECLARE @message NVARCHAR(255);
EXEC sp_cancel_order
    @p_order_id = @order_id,
    @p_reason = N'Test cancellation từ script',
    @p_success = @success OUTPUT,
    @p_message = @message OUTPUT;
PRINT N'Cancel Order Success: ' + CAST(@success AS NVARCHAR(10));
PRINT N'Cancel Message: ' + @message;
SELECT * FROM [order] WHERE order_id = @order_id;
GO

-- =====================================================
-- 10. TEST ORDER WORKFLOW PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_add_product_to_order ===';
-- Lấy order đang PROCESSING và product có sẵn
DECLARE @order_id INT = (SELECT TOP 1 order_id FROM [order] WHERE status = 'PROCESSING');
DECLARE @product_price_id INT = (SELECT TOP 1 product_price_id FROM product_price 
    INNER JOIN product ON product_price.product_id = product.product_id
    WHERE product.name LIKE N'%Trà sữa%' AND product.is_available = 1);
DECLARE @success BIT;
DECLARE @message NVARCHAR(255);
DECLARE @final_quantity SMALLINT;

EXEC sp_add_product_to_order
    @p_order_id = @order_id,
    @p_product_price_id = @product_price_id,
    @p_quantity = 2,
    @p_option = N'Ít đá, ít đường',
    @p_success = @success OUTPUT,
    @p_message = @message OUTPUT,
    @p_final_quantity = @final_quantity OUTPUT;
PRINT N'Add Product Success: ' + CAST(@success AS NVARCHAR(10));
PRINT N'Message: ' + @message;
PRINT N'Final Quantity: ' + CAST(@final_quantity AS NVARCHAR(10));
SELECT * FROM order_product WHERE order_id = @order_id;
SELECT order_id, total_amount, final_amount FROM [order] WHERE order_id = @order_id;
GO

PRINT N'=== TEST sp_update_order_product_quantity ===';
DECLARE @order_product_id INT = (SELECT TOP 1 order_product_id FROM order_product 
    WHERE [option] = N'Ít đá, ít đường');
DECLARE @success BIT;
DECLARE @message NVARCHAR(255);

EXEC sp_update_order_product_quantity
    @p_order_product_id = @order_product_id,
    @p_new_quantity = 3,
    @p_success = @success OUTPUT,
    @p_message = @message OUTPUT;
PRINT N'Update Quantity Success: ' + CAST(@success AS NVARCHAR(10));
PRINT N'Message: ' + @message;
SELECT * FROM order_product WHERE order_product_id = @order_product_id;
GO

PRINT N'=== TEST sp_calculate_order_total ===';
DECLARE @order_id INT = (SELECT TOP 1 order_id FROM [order] WHERE status = 'PROCESSING');
DECLARE @total_amount DECIMAL(11, 3);
DECLARE @order_discount_amount DECIMAL(11, 3);
DECLARE @customer_discount_amount DECIMAL(11, 3);
DECLARE @final_amount DECIMAL(11, 3);

EXEC sp_calculate_order_total
    @p_order_id = @order_id,
    @p_total_amount = @total_amount OUTPUT,
    @p_order_discount_amount = @order_discount_amount OUTPUT,
    @p_customer_discount_amount = @customer_discount_amount OUTPUT,
    @p_final_amount = @final_amount OUTPUT;
PRINT N'Total Amount: ' + CAST(@total_amount AS NVARCHAR(20));
PRINT N'Order Discount: ' + CAST(@order_discount_amount AS NVARCHAR(20));
PRINT N'Customer Discount: ' + CAST(@customer_discount_amount AS NVARCHAR(20));
PRINT N'Final Amount: ' + CAST(@final_amount AS NVARCHAR(20));
GO

PRINT N'=== TEST sp_apply_discount_to_order ===';
DECLARE @order_id INT = (SELECT TOP 1 order_id FROM [order] WHERE status = 'PROCESSING');
DECLARE @discount_id INT = (SELECT TOP 1 discount_id FROM discount WHERE is_active = 1);
DECLARE @success BIT;
DECLARE @message NVARCHAR(255);
DECLARE @discount_amount DECIMAL(11, 3);

EXEC sp_apply_discount_to_order
    @p_order_id = @order_id,
    @p_discount_id = @discount_id,
    @p_success = @success OUTPUT,
    @p_message = @message OUTPUT,
    @p_discount_amount = @discount_amount OUTPUT;
PRINT N'Apply Discount Success: ' + CAST(@success AS NVARCHAR(10));
PRINT N'Message: ' + @message;
PRINT N'Discount Amount: ' + CAST(@discount_amount AS NVARCHAR(20));
SELECT * FROM order_discount WHERE order_id = @order_id;
GO

PRINT N'=== TEST sp_remove_product_from_order ===';
DECLARE @order_product_id INT = (SELECT TOP 1 order_product_id FROM order_product 
    WHERE [option] = N'Ít đá, ít đường');
DECLARE @success BIT;
DECLARE @message NVARCHAR(255);

EXEC sp_remove_product_from_order
    @p_order_product_id = @order_product_id,
    @p_success = @success OUTPUT,
    @p_message = @message OUTPUT;
PRINT N'Remove Product Success: ' + CAST(@success AS NVARCHAR(10));
PRINT N'Message: ' + @message;
GO

-- =====================================================
-- 11. TEST SALES REPORT PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_get_sales_report ===';
DECLARE @start_date DATETIME2 = DATEADD(DAY, -7, GETDATE());
DECLARE @end_date DATETIME2 = GETDATE();
EXEC sp_get_sales_report @start_date, @end_date;
GO

PRINT N'=== TEST sp_get_product_sales_report ===';
DECLARE @start_date DATETIME2 = DATEADD(DAY, -7, GETDATE());
DECLARE @end_date DATETIME2 = GETDATE();
EXEC sp_get_product_sales_report @start_date, @end_date;
GO

-- =====================================================
-- 12. TEST COUPON & DISCOUNT PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_coupon ===';
DECLARE @coupon_id INT;
EXEC sp_insert_coupon
    @p_coupon = N'TESTCODE123',
    @p_description = N'Test coupon từ script',
    @p_coupon_id = @coupon_id OUTPUT;
PRINT N'Inserted Coupon ID: ' + CAST(@coupon_id AS NVARCHAR(10));
SELECT * FROM coupon WHERE coupon_id = @coupon_id;
GO

PRINT N'=== TEST sp_insert_discount ===';
DECLARE @coupon_id INT = (SELECT TOP 1 coupon_id FROM coupon WHERE coupon = N'TESTCODE123');
DECLARE @discount_id INT;
DECLARE @valid_from DATETIME2 = GETDATE();
DECLARE @valid_until DATETIME2 = DATEADD(MONTH, 1, GETDATE());
EXEC sp_insert_discount
    @p_name = N'Test Discount 15%',
    @p_description = N'Test discount từ script',
    @p_coupon_id = @coupon_id,
    @p_discount_value = 15.000,
    @p_discount_type = 'PERCENTAGE',
    @p_min_required_order_value = 50000.000,
    @p_max_discount_amount = 20000.000,
    @p_min_required_product = 1,
    @p_valid_from = @valid_from,
    @p_valid_until = @valid_until,
    @p_current_uses = 0,
    @p_max_uses = 100,
    @p_max_uses_per_customer = 1,
    @p_is_active = 1,
    @p_discount_id = @discount_id OUTPUT;
PRINT N'Inserted Discount ID: ' + CAST(@discount_id AS NVARCHAR(10));
SELECT * FROM discount WHERE discount_id = @discount_id;
GO

PRINT N'=== TEST sp_check_coupon_validity ===';
DECLARE @is_valid BIT;
DECLARE @discount_id INT;
DECLARE @order_time DATETIME2 = GETDATE();
EXEC sp_check_coupon_validity
    @p_coupon_code = N'TESTCODE123',
    @p_order_time = @order_time,
    @p_is_valid = @is_valid OUTPUT,
    @p_discount_id = @discount_id OUTPUT;
PRINT N'Coupon Valid: ' + CAST(@is_valid AS NVARCHAR(10));
PRINT N'Discount ID: ' + CAST(ISNULL(@discount_id, 0) AS NVARCHAR(10));
GO

-- =====================================================
-- 13. CLEANUP TEST DATA
-- =====================================================

PRINT N'=== THÊM TEST CHO CÁC PROCEDURES CHƯA ĐƯỢC TEST ===';

-- =====================================================
-- TEST COUPON & DISCOUNT UPDATE/DELETE PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_update_coupon ===';
DECLARE @coupon_id INT = (SELECT TOP 1 coupon_id FROM coupon WHERE coupon = N'TESTCODE123');
DECLARE @row_count INT;
EXEC sp_update_coupon
    @p_coupon_id = @coupon_id,
    @p_coupon = N'UPDATEDCODE123',
    @p_description = N'Updated test coupon từ script'
    , @p_row_count = @row_count OUTPUT;
SELECT * FROM coupon WHERE coupon_id = @coupon_id;
GO

PRINT N'=== TEST sp_delete_coupon ===';
DECLARE @coupon_id INT = (SELECT TOP 1 coupon_id FROM coupon WHERE coupon = N'UPDATEDCODE123');
DECLARE @row_count INT;
-- Phải xóa discount trước vì có foreign key
DELETE FROM discount WHERE coupon_id = @coupon_id;
EXEC sp_delete_coupon
    @p_coupon_id = @coupon_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete Coupon affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

PRINT N'=== TEST sp_update_discount ===';
-- Tạo coupon và discount mới để test
DECLARE @coupon_id INT;
EXEC sp_insert_coupon
    @p_coupon = N'UPDATETEST123',
    @p_description = N'Test coupon for update discount',
    @p_coupon_id = @coupon_id OUTPUT;

DECLARE @discount_id INT;
DECLARE @valid_from DATETIME2 = GETDATE();
DECLARE @valid_until DATETIME2 = DATEADD(MONTH, 1, GETDATE());
EXEC sp_insert_discount
    @p_name = N'Test Discount for Update',
    @p_description = N'Test discount for update từ script',
    @p_coupon_id = @coupon_id,
    @p_discount_value = 10.000,
    @p_discount_type = 'PERCENTAGE',
    @p_min_required_order_value = 30000.000,
    @p_max_discount_amount = 15000.000,
    @p_min_required_product = 1,
    @p_valid_from = @valid_from,
    @p_valid_until = @valid_until,
    @p_current_uses = 0,
    @p_max_uses = 50,
    @p_max_uses_per_customer = 1,
    @p_is_active = 1,
    @p_discount_id = @discount_id OUTPUT;

-- Update discount
DECLARE @new_valid_until DATETIME2 = DATEADD(MONTH, 2, GETDATE());
EXEC sp_update_discount
    @p_discount_id = @discount_id,
    @p_name = N'Updated Test Discount 20%',
    @p_description = N'Updated test discount từ script',
    @p_coupon_id = @coupon_id,
    @p_discount_value = 20.000,
    @p_discount_type = 'PERCENTAGE',
    @p_min_required_order_value = 40000.000,
    @p_max_discount_amount = 25000.000,
    @p_min_required_product = 2,
    @p_valid_from = @valid_from,
    @p_valid_until = @new_valid_until,
    @p_current_uses = 5,
    @p_max_uses = 100,
    @p_max_uses_per_customer = 2,
    @p_is_active = 1;
SELECT * FROM discount WHERE discount_id = @discount_id;
GO

PRINT N'=== TEST sp_delete_discount ===';
DECLARE @discount_id INT = (SELECT TOP 1 discount_id FROM discount WHERE name = N'Updated Test Discount 20%');
DECLARE @row_count INT;
EXEC sp_delete_discount
    @p_discount_id = 1003,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete Discount affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- TEST EMPLOYEE PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_employee ===';
-- Tạo account cho employee
DECLARE @role_id TINYINT = (SELECT TOP 1 role_id FROM role WHERE name = 'staff');
DECLARE @account_id INT;
EXEC sp_insert_account
    @p_role_id = @role_id,
    @p_username = N'test_employee_user',
    @p_password_hash = N'$2b$10$hashedpassword123',
    @p_is_active = 1,
    @p_is_locked = 0,
    @p_account_id = @account_id OUTPUT;

DECLARE @employee_id INT;
EXEC sp_insert_employee
    @p_account_id = @account_id,
    @p_position = N'Nhân viên pha chế',
    @p_last_name = N'Nguyễn',
    @p_first_name = N'Test Employee',
    @p_gender = 'MALE',
    @p_phone = '0999123456',
    @p_email = 'test_employee@example.com',
    @p_employee_id = @employee_id OUTPUT;
PRINT N'Inserted Employee ID: ' + CAST(@employee_id AS NVARCHAR(10));
SELECT * FROM employee WHERE employee_id = @employee_id;
GO

PRINT N'=== TEST sp_update_employee ===';
DECLARE @employee_id INT = (SELECT TOP 1 employee_id FROM employee WHERE email = 'test_employee@example.com');
DECLARE @account_id INT = (SELECT account_id FROM employee WHERE employee_id = @employee_id);
EXEC sp_update_employee
    @p_employee_id = @employee_id,
    @p_account_id = @account_id,
    @p_position = N'Nhân viên phục vụ',
    @p_last_name = N'Trần',
    @p_first_name = N'Updated Test Employee',
    @p_gender = 'FEMALE',
    @p_phone = '0999123457',
    @p_email = 'updated_test_employee@example.com';
SELECT * FROM employee WHERE employee_id = @employee_id;
GO

PRINT N'=== TEST sp_delete_employee ===';
DECLARE @employee_id INT = (SELECT TOP 1 employee_id FROM employee WHERE email = 'updated_test_employee@example.com');
EXEC sp_delete_employee @p_employee_id = @employee_id;
-- Cleanup account
DECLARE @account_id INT = (SELECT account_id FROM account WHERE username = N'test_employee_user');
DELETE FROM account WHERE account_id = @account_id;
PRINT N'Employee deleted successfully';
GO

-- =====================================================
-- TEST UNIT OF MEASURE & PRODUCT SIZE PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_unit_of_measure ===';
DECLARE @unit_id SMALLINT;
EXEC sp_insert_unit_of_measure
    @p_name = N'Test Milliliter',
    @p_symbol = N'tml',
    @p_description = N'Test unit for milliliter',
    @p_unit_id = @unit_id OUTPUT;
PRINT N'Inserted Unit ID: ' + CAST(@unit_id AS NVARCHAR(10));
SELECT * FROM unit_of_measure WHERE unit_id = @unit_id;
GO

PRINT N'=== TEST sp_update_unit_of_measure ===';
DECLARE @unit_id SMALLINT = (SELECT TOP 1 unit_id FROM unit_of_measure WHERE symbol = N'tml');
EXEC sp_update_unit_of_measure
    @p_unit_id = @unit_id,
    @p_name = N'Updated Test Liter',
    @p_symbol = N'utl',
    @p_description = N'Updated test unit for liter';
SELECT * FROM unit_of_measure WHERE unit_id = @unit_id;
GO

PRINT N'=== TEST sp_insert_product_size ===';
DECLARE @unit_id SMALLINT = (SELECT TOP 1 unit_id FROM unit_of_measure WHERE symbol = N'utl');
DECLARE @size_id SMALLINT;
EXEC sp_insert_product_size
    @p_unit_id = @unit_id,
    @p_name = N'XL',
    @p_quantity = 800,
    @p_description = N'Extra Large test size',
    @p_size_id = @size_id OUTPUT;
PRINT N'Inserted Size ID: ' + CAST(@size_id AS NVARCHAR(10));
SELECT * FROM product_size WHERE size_id = @size_id;
GO

PRINT N'=== TEST sp_update_product_size ===';
DECLARE @size_id SMALLINT = (SELECT TOP 1 size_id FROM product_size WHERE name = N'XL');
DECLARE @unit_id SMALLINT = (SELECT unit_id FROM product_size WHERE size_id = @size_id);
EXEC sp_update_product_size
    @p_size_id = @size_id,
    @p_unit_id = @unit_id,
    @p_name = N'XXL',
    @p_quantity = 1000,
    @p_description = N'Extra Extra Large test size';
SELECT * FROM product_size WHERE size_id = @size_id;
GO

PRINT N'=== TEST sp_delete_product_size ===';
DECLARE @size_id SMALLINT = (SELECT TOP 1 size_id FROM product_size WHERE name = N'XXL');
DECLARE @row_count INT;
EXEC sp_delete_product_size
    @p_size_id = @size_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete Product Size affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

PRINT N'=== TEST sp_delete_unit_of_measure ===';
DECLARE @unit_id SMALLINT = (SELECT TOP 1 unit_id FROM unit_of_measure WHERE symbol = N'utl');
DECLARE @row_count INT;
EXEC sp_delete_unit_of_measure
    @p_unit_id = @unit_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete Unit of Measure affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- TEST PRODUCT PRICE PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_product_price ===';
DECLARE @product_id INT = (SELECT TOP 1 product_id FROM product WHERE name LIKE N'%Trà sữa đác%');
DECLARE @size_id SMALLINT = (SELECT TOP 1 size_id FROM product_size WHERE name = 'M');
DECLARE @product_price_id INT;
EXEC sp_insert_product_price
    @p_product_id = @product_id,
    @p_size_id = @size_id,
    @p_price = 45000.000,
    @p_product_price_id = @product_price_id OUTPUT;
PRINT N'Inserted Product Price ID: ' + CAST(@product_price_id AS NVARCHAR(10));
SELECT * FROM product_price WHERE product_price_id = @product_price_id;
GO

PRINT N'=== TEST sp_update_product_price ===';
DECLARE @product_price_id INT = (SELECT TOP 1 product_price_id FROM product_price WHERE price = 45000.000);
DECLARE @product_id INT = (SELECT product_id FROM product_price WHERE product_price_id = @product_price_id);
DECLARE @size_id SMALLINT = (SELECT size_id FROM product_price WHERE product_price_id = @product_price_id);
EXEC sp_update_product_price
    @p_product_price_id = @product_price_id,
    @p_product_id = @product_id,
    @p_size_id = @size_id,
    @p_unit_id = @size_id, -- placeholder, không sử dụng trong procedure thực tế
    @p_price = 48000.000,
    @p_is_active = 1; -- placeholder, không sử dụng trong procedure thực tế
SELECT * FROM product_price WHERE product_price_id = @product_price_id;
GO

PRINT N'=== TEST sp_delete_product_price ===';
DECLARE @product_price_id INT = (SELECT TOP 1 product_price_id FROM product_price WHERE price = 48000.000);
DECLARE @row_count INT;
EXEC sp_delete_product_price
    @p_product_price_id = @product_price_id,
    @p_row_count = @row_count OUTPUT;
PRINT N'Delete Product Price affected rows: ' + CAST(@row_count AS NVARCHAR(10));
GO

-- =====================================================
-- TEST SERVICE TABLE PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_service_table ===';
DECLARE @area_id SMALLINT = (SELECT TOP 1 area_id FROM area WHERE is_active = 1);
DECLARE @table_id SMALLINT;
EXEC sp_insert_service_table
    @p_area_id = @area_id,
    @p_table_number = N'T99',
    @p_is_active = 1,
    @p_table_id = @table_id OUTPUT;
PRINT N'Inserted Service Table ID: ' + CAST(@table_id AS NVARCHAR(10));
SELECT * FROM service_table WHERE table_id = @table_id;
GO

PRINT N'=== TEST sp_update_service_table ===';
DECLARE @table_id SMALLINT = (SELECT TOP 1 table_id FROM service_table WHERE table_number = N'T99');
DECLARE @area_id SMALLINT = (SELECT area_id FROM service_table WHERE table_id = @table_id);
EXEC sp_update_service_table
    @p_table_id = @table_id,
    @p_area_id = @area_id,
    @p_table_number = N'T100',
    @p_is_active = 1;
SELECT * FROM service_table WHERE table_id = @table_id;
GO

PRINT N'=== TEST sp_delete_service_table ===';
DECLARE @table_id SMALLINT = (SELECT TOP 1 table_id FROM service_table WHERE table_number = N'T100');
EXEC sp_delete_service_table @p_table_id = @table_id;
PRINT N'Service Table deleted successfully';
GO

-- =====================================================
-- TEST STORE PROCEDURES
-- =====================================================

PRINT N'=== TEST sp_insert_store ===';
DECLARE @store_id TINYINT;
EXEC sp_insert_store
    @p_name = N'Test Milk Tea Store',
    @p_address = N'123 Test Street, Test City',
    @p_phone = '0299999999',
    @p_opening_time = '07:00:00',
    @p_closing_time = '22:00:00',
    @p_email = 'test_store@example.com',
    @p_opening_date = '2024-01-01',
    @p_tax_code = 'TEST123456789',
    @p_store_id = @store_id OUTPUT;
PRINT N'Inserted Store ID: ' + CAST(@store_id AS NVARCHAR(10));
SELECT * FROM store WHERE store_id = @store_id;
GO

PRINT N'=== TEST sp_update_store ===';
DECLARE @store_id TINYINT = (SELECT TOP 1 store_id FROM store WHERE tax_code = 'TEST123456789');
EXEC sp_update_store
    @p_store_id = @store_id,
    @p_name = N'Updated Test Milk Tea Store',
    @p_address = N'456 Updated Test Street, Updated Test City',
    @p_phone = '0288888888',
    @p_opening_time = '06:30:00',
    @p_closing_time = '23:00:00',
    @p_email = 'updated_test_store@example.com',
    @p_opening_date = '2024-01-15',
    @p_tax_code = 'UPDATED123456789';
SELECT * FROM store WHERE store_id = @store_id;
GO

PRINT N'=== CLEANUP TEST DATA ===';
-- Xóa dữ liệu test vừa tạo
DELETE FROM order_discount WHERE order_id IN (
    SELECT order_id FROM [order] WHERE customize_note LIKE N'%Test%' OR customize_note LIKE N'%script%'
);
DELETE FROM order_product WHERE order_id IN (
    SELECT order_id FROM [order] WHERE customize_note LIKE N'%Test%' OR customize_note LIKE N'%script%'
);
DELETE FROM payment WHERE order_id IN (
    SELECT order_id FROM [order] WHERE customize_note LIKE N'%Test%' OR customize_note LIKE N'%script%'
);
DELETE FROM [order] WHERE customize_note LIKE N'%Test%' OR customize_note LIKE N'%script%';
DELETE FROM discount WHERE name LIKE N'%Test%';
DELETE FROM coupon WHERE coupon LIKE N'%TEST%';
DELETE FROM store WHERE tax_code LIKE N'%TEST%';
PRINT N'Test data cleaned up!';
GO

PRINT N'=== HOÀN THÀNH TẤT CẢ TEST SCRIPTS ===';
PRINT N'';
PRINT N'📋 DANH SÁCH CÁC STORED PROCEDURES ĐÃ ĐƯỢC TEST:';
PRINT N'✅ BASIC CRUD PROCEDURES:';
PRINT N'   - Area: sp_insert/update/delete_area';
PRINT N'   - Category: sp_insert/update/delete_category';
PRINT N'   - Role: sp_insert/update/delete_role';
PRINT N'   - Membership Type: sp_insert/update/delete_membership_type';
PRINT N'   - Payment Method: sp_insert/update/delete_payment_method';
PRINT N'   - Product: sp_insert/update/delete_product';
PRINT N'   - Account: sp_insert/update/delete_account + password & login updates';
PRINT N'   - Customer: sp_insert/update/delete_customer + points update';
PRINT N'';
PRINT N'✅ COUPON & DISCOUNT PROCEDURES:';
PRINT N'   - Coupon: sp_insert/update/delete_coupon';
PRINT N'   - Discount: sp_insert/update/delete_discount';
PRINT N'   - sp_check_coupon_validity';
PRINT N'';
PRINT N'✅ EMPLOYEE & MANAGEMENT PROCEDURES:';
PRINT N'   - Employee: sp_insert/update/delete_employee';
PRINT N'';
PRINT N'✅ PRODUCT MANAGEMENT PROCEDURES:';
PRINT N'   - Unit of Measure: sp_insert/update/delete_unit_of_measure';
PRINT N'   - Product Size: sp_insert/update/delete_product_size';
PRINT N'   - Product Price: sp_insert/update/delete_product_price';
PRINT N'';
PRINT N'✅ SERVICE & STORE PROCEDURES:';
PRINT N'   - Service Table: sp_insert/update/delete_service_table';
PRINT N'   - Store: sp_insert/update_store';
PRINT N'';
PRINT N'✅ ORDER WORKFLOW PROCEDURES:';
PRINT N'   - Order: sp_insert_order, sp_update_order_status/amounts';
PRINT N'   - sp_complete_order, sp_cancel_order';
PRINT N'   - sp_add_product_to_order, sp_update_order_product_quantity';
PRINT N'   - sp_remove_product_from_order';
PRINT N'   - sp_apply_discount_to_order';
PRINT N'   - sp_calculate_order_total';
PRINT N'';
PRINT N'✅ REPORTING PROCEDURES:';
PRINT N'   - sp_get_sales_report';
PRINT N'   - sp_get_product_sales_report';
PRINT N'';
PRINT N'🔧 CÁCH SỬ DỤNG:';
PRINT N'- Copy từng section để test riêng lẻ các procedure';
PRINT N'- Hoặc chạy toàn bộ file để test tất cả';
PRINT N'- Dữ liệu test sử dụng từ 005_init_optional_data.sql';
PRINT N'- Script tự động cleanup dữ liệu test ở cuối';
PRINT N'';
PRINT N'📊 TỔNG KẾT: Đã test 60+ stored procedures cơ bản và workflow';
GO