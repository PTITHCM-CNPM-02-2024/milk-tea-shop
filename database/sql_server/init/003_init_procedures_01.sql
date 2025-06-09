-- SQL Server 2022 Stored Procedures for Milk Tea Shop
-- Converted from MySQL V18.0.3__init_procedures.sql

USE MilkTeaShop;
GO

-- =====================================================
-- 1. AREA PROCEDURES
-- =====================================================

-- Insert Area
CREATE PROCEDURE sp_insert_area @p_name CHAR(3),
                                @p_max_tables INT,
                                @p_is_active BIT,
                                @p_description NVARCHAR(255),
                                @p_area_id SMALLINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO area (name, max_tables, is_active, description)
    VALUES (@p_name, @p_max_tables, @p_is_active, @p_description);

    SET @p_area_id = SCOPE_IDENTITY();
END
GO

-- Update Area
CREATE PROCEDURE sp_update_area @p_area_id SMALLINT,
                                @p_name CHAR(3),
                                @p_max_tables INT,
                                @p_is_active BIT,
                                @p_description NVARCHAR(255),
                                @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE area
    SET name        = @p_name,
        max_tables  = @p_max_tables,
        is_active   = @p_is_active,
        description = @p_description,
        updated_at  = GETDATE()
    WHERE area_id = @p_area_id;

    SET @p_row_count = @@ROWCOUNT;
END
GO

-- Delete Area
CREATE PROCEDURE sp_delete_area @p_area_id SMALLINT,
                                @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM area WHERE area_id = @p_area_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 2. CATEGORY PROCEDURES
-- =====================================================

-- Insert Category
CREATE PROCEDURE sp_insert_category @p_name NVARCHAR(100),
                                    @p_description NVARCHAR(1000),
                                    @p_category_id SMALLINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO category (name, description)
    VALUES (@p_name, @p_description);

    SET @p_category_id = SCOPE_IDENTITY();
END
GO

-- Update Category
CREATE PROCEDURE sp_update_category @p_category_id SMALLINT,
                                    @p_name NVARCHAR(100),
                                    @p_description NVARCHAR(1000),
                                    @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE category
    SET name        = @p_name,
        description = @p_description,
        updated_at  = GETDATE()
    WHERE category_id = @p_category_id;

    SET @p_row_count = @@ROWCOUNT;
END
GO

-- Delete Category
CREATE PROCEDURE sp_delete_category @p_category_id SMALLINT,
                                    @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM category WHERE category_id = @p_category_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 3. COUPON PROCEDURES
-- =====================================================

-- Insert Coupon
CREATE PROCEDURE sp_insert_coupon @p_coupon NVARCHAR(15),
                                  @p_description NVARCHAR(1000),
                                  @p_coupon_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO coupon (coupon, description)
    VALUES (@p_coupon, @p_description);

    SET @p_coupon_id = SCOPE_IDENTITY();
END
GO

-- Update Coupon
CREATE PROCEDURE sp_update_coupon @p_coupon_id INT,
                                  @p_coupon NVARCHAR(15),
                                  @p_description NVARCHAR(1000),
                                  @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE coupon
    SET coupon      = @p_coupon,
        description = @p_description,
        updated_at  = GETDATE()
    WHERE coupon_id = @p_coupon_id;

    SET @p_row_count = @@ROWCOUNT;
END
GO

-- Delete Coupon
CREATE PROCEDURE sp_delete_coupon @p_coupon_id INT,
                                  @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM coupon WHERE coupon_id = @p_coupon_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 4. DISCOUNT PROCEDURES
-- =====================================================

-- Insert Discount
CREATE PROCEDURE sp_insert_discount @p_name NVARCHAR(500),
                                    @p_description NVARCHAR(1000),
                                    @p_coupon_id INT,
                                    @p_discount_value DECIMAL(11, 3),
                                    @p_discount_type NVARCHAR(20),
                                    @p_min_required_order_value DECIMAL(11, 3),
                                    @p_max_discount_amount DECIMAL(11, 3),
                                    @p_min_required_product SMALLINT,
                                    @p_valid_from DATETIME2,
                                    @p_valid_until DATETIME2,
                                    @p_current_uses INT,
                                    @p_max_uses INT,
                                    @p_max_uses_per_customer SMALLINT,
                                    @p_is_active BIT,
                                    @p_discount_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO discount (name, description, coupon_id, discount_value, discount_type,
                          min_required_order_value, max_discount_amount, min_required_product,
                          valid_from, valid_until, current_uses, max_uses, max_uses_per_customer, is_active)
    VALUES (@p_name, @p_description, @p_coupon_id, @p_discount_value, @p_discount_type,
            @p_min_required_order_value, @p_max_discount_amount, @p_min_required_product,
            @p_valid_from, @p_valid_until, @p_current_uses, @p_max_uses, @p_max_uses_per_customer, @p_is_active);

    SET @p_discount_id = SCOPE_IDENTITY();
END
GO

-- Update Discount
CREATE PROCEDURE sp_update_discount @p_discount_id INT,
                                    @p_name NVARCHAR(500),
                                    @p_description NVARCHAR(1000),
                                    @p_coupon_id INT,
                                    @p_discount_value DECIMAL(11, 3),
                                    @p_discount_type NVARCHAR(20),
                                    @p_min_required_order_value DECIMAL(11, 3),
                                    @p_max_discount_amount DECIMAL(11, 3),
                                    @p_min_required_product SMALLINT,
                                    @p_valid_from DATETIME2,
                                    @p_valid_until DATETIME2,
                                    @p_current_uses INT,
                                    @p_max_uses INT,
                                    @p_max_uses_per_customer SMALLINT,
                                    @p_is_active BIT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE discount
    SET name                     = @p_name,
        description              = @p_description,
        coupon_id                = @p_coupon_id,
        discount_value           = @p_discount_value,
        discount_type            = @p_discount_type,
        min_required_order_value = @p_min_required_order_value,
        max_discount_amount      = @p_max_discount_amount,
        min_required_product     = @p_min_required_product,
        valid_from               = @p_valid_from,
        valid_until              = @p_valid_until,
        current_uses             = @p_current_uses,
        max_uses                 = @p_max_uses,
        max_uses_per_customer    = @p_max_uses_per_customer,
        is_active                = @p_is_active,
        updated_at               = GETDATE()
    WHERE discount_id = @p_discount_id;
END
GO

-- Delete Discount
CREATE PROCEDURE sp_delete_discount @p_discount_id INT,
                                    @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM discount WHERE discount_id = @p_discount_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 5. MEMBERSHIP TYPE PROCEDURES
-- =====================================================

-- Insert Membership Type
CREATE PROCEDURE sp_insert_membership_type @p_type NVARCHAR(50),
                                           @p_discount_value DECIMAL(10, 3),
                                           @p_discount_unit NVARCHAR(20),
                                           @p_required_point INT,
                                           @p_description NVARCHAR(255),
                                           @p_valid_until DATETIME2,
                                           @p_is_active BIT,
                                           @p_membership_type_id TINYINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO membership_type (type, discount_value, discount_unit, required_point,
                                 description, valid_until, is_active)
    VALUES (@p_type, @p_discount_value, @p_discount_unit, @p_required_point,
            @p_description, @p_valid_until, @p_is_active);

    SET @p_membership_type_id = SCOPE_IDENTITY();
END
GO

-- Update Membership Type
CREATE PROCEDURE sp_update_membership_type @p_membership_type_id TINYINT,
                                           @p_type NVARCHAR(50),
                                           @p_discount_value DECIMAL(10, 3),
                                           @p_discount_unit NVARCHAR(20),
                                           @p_required_point INT,
                                           @p_description NVARCHAR(255),
                                           @p_valid_until DATETIME2,
                                           @p_is_active BIT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE membership_type
    SET type           = @p_type,
        discount_value = @p_discount_value,
        discount_unit  = @p_discount_unit,
        required_point = @p_required_point,
        description    = @p_description,
        valid_until    = @p_valid_until,
        is_active      = @p_is_active,
        updated_at     = GETDATE()
    WHERE membership_type_id = @p_membership_type_id;
END
GO

-- Delete Membership Type
CREATE PROCEDURE sp_delete_membership_type @p_membership_type_id TINYINT,
                                           @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM membership_type WHERE membership_type_id = @p_membership_type_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 6. PAYMENT METHOD PROCEDURES
-- =====================================================

-- Insert Payment Method
CREATE PROCEDURE sp_insert_payment_method @p_payment_name NVARCHAR(50),
                                          @p_payment_description NVARCHAR(255),
                                          @p_payment_method_id TINYINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO payment_method (payment_name, payment_description)
    VALUES (@p_payment_name, @p_payment_description);

    SET @p_payment_method_id = SCOPE_IDENTITY();
END
GO

-- Update Payment Method
CREATE PROCEDURE sp_update_payment_method @p_payment_method_id TINYINT,
                                          @p_payment_name NVARCHAR(50),
                                          @p_payment_description NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE payment_method
    SET payment_name        = @p_payment_name,
        payment_description = @p_payment_description,
        updated_at          = GETDATE()
    WHERE payment_method_id = @p_payment_method_id;
END
GO

-- Delete Payment Method
CREATE PROCEDURE sp_delete_payment_method @p_payment_method_id TINYINT,
                                          @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM payment_method WHERE payment_method_id = @p_payment_method_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 7. PRODUCT PROCEDURES
-- =====================================================

-- Insert Product
CREATE PROCEDURE sp_insert_product @p_category_id SMALLINT,
                                   @p_name NVARCHAR(100),
                                   @p_description NVARCHAR(1000),
                                   @p_is_available BIT,
                                   @p_is_signature BIT,
                                   @p_image_path NVARCHAR(1000),
                                   @p_product_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO product (category_id, name, description, is_available, is_signature, image_path)
    VALUES (@p_category_id, @p_name, @p_description, @p_is_available, @p_is_signature, @p_image_path);

    SET @p_product_id = SCOPE_IDENTITY();
END
GO

-- Update Product
CREATE PROCEDURE sp_update_product @p_product_id INT,
                                   @p_category_id SMALLINT,
                                   @p_name NVARCHAR(100),
                                   @p_description NVARCHAR(1000),
                                   @p_is_available BIT,
                                   @p_is_signature BIT,
                                   @p_image_path NVARCHAR(1000)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE product
    SET category_id  = @p_category_id,
        name         = @p_name,
        description  = @p_description,
        is_available = @p_is_available,
        is_signature = @p_is_signature,
        image_path   = @p_image_path,
        updated_at   = GETDATE()
    WHERE product_id = @p_product_id;
END
GO

-- Delete Product
CREATE PROCEDURE sp_delete_product @p_product_id INT,
                                   @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM product WHERE product_id = @p_product_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 8. ROLE PROCEDURES
-- =====================================================

-- Insert Role
CREATE PROCEDURE sp_insert_role @p_name NVARCHAR(50),
                                @p_description NVARCHAR(1000),
                                @p_role_id TINYINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO role (name, description)
    VALUES (@p_name, @p_description);

    SET @p_role_id = SCOPE_IDENTITY();
END
GO

-- Update Role
CREATE PROCEDURE sp_update_role @p_role_id TINYINT,
                                @p_name NVARCHAR(50),
                                @p_description NVARCHAR(1000)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE role
    SET name        = @p_name,
        description = @p_description,
        updated_at  = GETDATE()
    WHERE role_id = @p_role_id;
END
GO

-- Delete Role
CREATE PROCEDURE sp_delete_role @p_role_id TINYINT,
                                @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @v_has_account BIT;

    SELECT @v_has_account = CASE WHEN EXISTS(SELECT 1 FROM account WHERE role_id = @p_role_id) THEN 1 ELSE 0 END;

    IF @v_has_account = 1
        BEGIN
            THROW 50001, N'Không thể xóa vai trò đang được sử dụng bởi tài khoản', 1;
            RETURN;
        END

    DELETE FROM role WHERE role_id = @p_role_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 9. ACCOUNT PROCEDURES
-- =====================================================

-- Insert Account
CREATE PROCEDURE sp_insert_account @p_role_id TINYINT,
                                   @p_username NVARCHAR(50),
                                   @p_password_hash NVARCHAR(255),
                                   @p_is_active BIT,
                                   @p_is_locked BIT,
                                   @p_account_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO account (role_id, username, password_hash, is_active, is_locked, token_version)
    VALUES (@p_role_id, @p_username, @p_password_hash, @p_is_active, @p_is_locked, 0);

    -- Lấy account_id vừa tạo vì SCOPE_IDENTITY() không hoạt động với INSTEAD OF triggers
    SELECT @p_account_id = account_id 
    FROM account 
    WHERE username = @p_username AND role_id = @p_role_id;
END
GO

-- Update Account Details
CREATE PROCEDURE sp_update_account_details @p_account_id INT,
                                           @p_role_id TINYINT,
                                           @p_username NVARCHAR(50),
                                           @p_is_active BIT,
                                           @p_is_locked BIT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE account
    SET role_id    = @p_role_id,
        username   = @p_username,
        is_active  = @p_is_active,
        is_locked  = @p_is_locked,
        updated_at = GETDATE()
    WHERE account_id = @p_account_id;
END
GO

-- Update Account Password
CREATE PROCEDURE sp_update_account_password @p_account_id INT,
                                            @p_new_password_hash NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE account
    SET password_hash = @p_new_password_hash,
        token_version = token_version + 1,
        updated_at    = GETDATE()
    WHERE account_id = @p_account_id;
END
GO

-- Update Account Last Login
CREATE PROCEDURE sp_update_account_last_login @p_account_id INT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE account
    SET last_login = GETDATE()
    WHERE account_id = @p_account_id;
END
GO

-- Delete Account
CREATE PROCEDURE sp_delete_account @p_account_id INT,
                                   @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @v_has_employee BIT, @v_has_manager BIT, @v_has_customer BIT;

    SELECT
        @v_has_employee = CASE WHEN EXISTS(SELECT 1 FROM employee WHERE account_id = @p_account_id) THEN 1 ELSE 0 END;
    SELECT @v_has_manager = CASE WHEN EXISTS(SELECT 1 FROM manager WHERE account_id = @p_account_id) THEN 1 ELSE 0 END;
    SELECT
        @v_has_customer = CASE WHEN EXISTS(SELECT 1 FROM customer WHERE account_id = @p_account_id) THEN 1 ELSE 0 END;

    IF @v_has_employee = 1
        BEGIN
            THROW 50002, N'Không thể xóa tài khoản đang được sử dụng bởi nhân viên', 1;
            RETURN;
        END

    IF @v_has_manager = 1
        BEGIN
            THROW 50003, N'Không thể xóa tài khoản đang được sử dụng bởi quản lý', 1;
            RETURN;
        END

    IF @v_has_customer = 1
        BEGIN
            THROW 50004, N'Không thể xóa tài khoản đang được sử dụng bởi khách hàng', 1;
            RETURN;
        END

    DELETE FROM account WHERE account_id = @p_account_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 10. CUSTOMER PROCEDURES
-- =====================================================

-- Insert Customer
CREATE PROCEDURE sp_insert_customer @p_membership_type_id TINYINT,
                                    @p_account_id INT,
                                    @p_last_name NVARCHAR(70),
                                    @p_first_name NVARCHAR(70),
                                    @p_phone NVARCHAR(15),
                                    @p_email NVARCHAR(100),
                                    @p_current_points INT,
                                    @p_gender NVARCHAR(10),
                                    @p_customer_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO customer (membership_type_id, account_id, last_name, first_name,
                          phone, email, current_points, gender)
    VALUES (@p_membership_type_id, @p_account_id, @p_last_name, @p_first_name,
            @p_phone, @p_email, @p_current_points, @p_gender);

    SET @p_customer_id = SCOPE_IDENTITY();
END
GO

-- Update Customer
CREATE PROCEDURE sp_update_customer @p_customer_id INT,
                                    @p_membership_type_id TINYINT,
                                    @p_account_id INT,
                                    @p_last_name NVARCHAR(70),
                                    @p_first_name NVARCHAR(70),
                                    @p_phone NVARCHAR(15),
                                    @p_email NVARCHAR(100),
                                    @p_current_points INT,
                                    @p_gender NVARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE customer
    SET membership_type_id = @p_membership_type_id,
        account_id         = @p_account_id,
        last_name          = @p_last_name,
        first_name         = @p_first_name,
        phone              = @p_phone,
        email              = @p_email,
        current_points     = @p_current_points,
        gender             = @p_gender,
        updated_at         = GETDATE()
    WHERE customer_id = @p_customer_id;
END
GO

-- Delete Customer
CREATE PROCEDURE sp_delete_customer @p_customer_id INT,
                                    @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM customer WHERE customer_id = @p_customer_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- UTILITY PROCEDURES
-- =====================================================

-- Update Customer Points
CREATE PROCEDURE sp_update_customer_points @p_customer_id INT,
                                           @p_points_to_add INT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE customer
    SET current_points = current_points + @p_points_to_add,
        updated_at     = GETDATE()
    WHERE customer_id = @p_customer_id;
END
GO

-- Get Sales Report
CREATE PROCEDURE sp_get_sales_report @p_start_date DATETIME2,
                                     @p_end_date DATETIME2
AS
BEGIN
    SET NOCOUNT ON;

    SELECT CAST(o.order_time AS DATE) as order_date,
           COUNT(o.order_id)          as total_orders,
           SUM(o.total_amount)        as total_sales,
           SUM(o.final_amount)        as final_sales,
           AVG(o.final_amount)        as avg_order_value
    FROM [order] o
    WHERE o.order_time BETWEEN @p_start_date AND @p_end_date
      AND o.status = 'COMPLETED'
    GROUP BY CAST(o.order_time AS DATE)
    ORDER BY order_date;
END
GO

-- Get Product Sales Report
CREATE PROCEDURE sp_get_product_sales_report @p_start_date DATETIME2,
                                             @p_end_date DATETIME2
AS
BEGIN
    SET NOCOUNT ON;

    SELECT p.product_id,
           p.name                      as product_name,
           c.name                      as category_name,
           SUM(op.quantity)            as total_quantity_sold,
           SUM(op.quantity * pp.price) as total_revenue
    FROM order_product op
             INNER JOIN product_price pp ON op.product_price_id = pp.product_price_id
             INNER JOIN product p ON pp.product_id = p.product_id
             INNER JOIN category c ON p.category_id = c.category_id
             INNER JOIN [order] o ON op.order_id = o.order_id
    WHERE o.order_time BETWEEN @p_start_date AND @p_end_date
      AND o.status = 'COMPLETED'
    GROUP BY p.product_id, p.name, c.name
    ORDER BY total_revenue DESC;
END
GO

PRINT N'Đã tạo thành công tất cả stored procedures cơ bản cho SQL Server';
PRINT N'Ghi chú: Các procedures phức tạp với JSON đã được đơn giản hóa do SQL Server có cách xử lý JSON khác với MySQL';
GO 