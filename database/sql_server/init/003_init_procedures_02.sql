-- SQL Server 2022 Stored Procedures for Milk Tea Shop - Part 2
-- Converted from MySQL V18.0.3__init_procedures.sql

USE MilkTeaShop;
GO

-- =====================================================
-- 11. EMPLOYEE PROCEDURES
-- =====================================================

-- Insert Employee
CREATE PROCEDURE sp_insert_employee @p_account_id INT,
                                    @p_position NVARCHAR(50),
                                    @p_last_name NVARCHAR(70),
                                    @p_first_name NVARCHAR(70),
                                    @p_gender NVARCHAR(10),
                                    @p_phone NVARCHAR(15),
                                    @p_email NVARCHAR(100),
                                    @p_employee_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO employee (account_id, position, last_name, first_name, gender, phone, email)
    VALUES (@p_account_id, @p_position, @p_last_name, @p_first_name, @p_gender, @p_phone, @p_email);

    -- Lấy employee_id của record vừa insert với INSTEAD OF trigger
    SELECT @p_employee_id = employee_id
    FROM employee 
    WHERE account_id = @p_account_id;
END
GO

-- Update Employee
CREATE PROCEDURE sp_update_employee @p_employee_id INT,
                                    @p_account_id INT,
                                    @p_position NVARCHAR(50),
                                    @p_last_name NVARCHAR(70),
                                    @p_first_name NVARCHAR(70),
                                    @p_gender NVARCHAR(10),
                                    @p_phone NVARCHAR(15),
                                    @p_email NVARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE employee
    SET account_id = @p_account_id,
        position   = @p_position,
        last_name  = @p_last_name,
        first_name = @p_first_name,
        gender     = @p_gender,
        phone      = @p_phone,
        email      = @p_email,
        updated_at = GETDATE()
    WHERE employee_id = @p_employee_id;
END
GO

-- Delete Employee
CREATE PROCEDURE sp_delete_employee @p_employee_id INT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM employee WHERE employee_id = @p_employee_id;
END
GO

-- =====================================================
-- 12. MANAGER PROCEDURES
-- =====================================================

-- Insert Manager
CREATE PROCEDURE sp_insert_manager @p_account_id INT,
                                   @p_last_name NVARCHAR(70),
                                   @p_first_name NVARCHAR(70),
                                   @p_gender NVARCHAR(10),
                                   @p_phone NVARCHAR(15),
                                   @p_email NVARCHAR(100),
                                   @p_manager_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO manager (account_id, last_name, first_name, gender, phone, email)
    VALUES (@p_account_id, @p_last_name, @p_first_name, @p_gender, @p_phone, @p_email);

    SET @p_manager_id = SCOPE_IDENTITY();
END
GO

-- Update Manager
CREATE PROCEDURE sp_update_manager @p_manager_id INT,
                                   @p_account_id INT,
                                   @p_last_name NVARCHAR(70),
                                   @p_first_name NVARCHAR(70),
                                   @p_gender NVARCHAR(10),
                                   @p_phone NVARCHAR(15),
                                   @p_email NVARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE manager
    SET account_id = @p_account_id,
        last_name  = @p_last_name,
        first_name = @p_first_name,
        gender     = @p_gender,
        phone      = @p_phone,
        email      = @p_email,
        updated_at = GETDATE()
    WHERE manager_id = @p_manager_id;
END
GO

-- Delete Manager
CREATE PROCEDURE sp_delete_manager @p_manager_id INT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM manager WHERE manager_id = @p_manager_id;
END
GO

-- =====================================================
-- 13. SERVICE TABLE PROCEDURES
-- =====================================================

-- Insert Service Table
CREATE PROCEDURE sp_insert_service_table @p_area_id SMALLINT,
                                         @p_table_number NVARCHAR(10),
                                         @p_is_active BIT,
                                         @p_table_id SMALLINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO service_table (area_id, table_number, is_active)
    VALUES (@p_area_id, @p_table_number, @p_is_active);

    SET @p_table_id = SCOPE_IDENTITY();
END
GO

-- Update Service Table
CREATE PROCEDURE sp_update_service_table @p_table_id SMALLINT,
                                         @p_area_id SMALLINT,
                                         @p_table_number NVARCHAR(10),
                                         @p_is_active BIT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE service_table
    SET area_id      = @p_area_id,
        table_number = @p_table_number,
        updated_at   = GETDATE()
    WHERE table_id = @p_table_id;
END
GO

-- Delete Service Table
CREATE PROCEDURE sp_delete_service_table @p_table_id SMALLINT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM service_table WHERE table_id = @p_table_id;
END
GO

-- =====================================================
-- 14. STORE PROCEDURES
-- =====================================================

-- Insert Store
CREATE PROCEDURE sp_insert_store @p_name NVARCHAR(100),
                                 @p_address NVARCHAR(255),
                                 @p_phone NVARCHAR(15),
                                 @p_opening_time TIME,
                                 @p_closing_time TIME,
                                 @p_email NVARCHAR(100),
                                 @p_opening_date DATE,
                                 @p_tax_code NVARCHAR(20),
                                 @p_store_id TINYINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO store (name, address, phone, opening_time, closing_time, email, opening_date, tax_code)
    VALUES (@p_name, @p_address, @p_phone, @p_opening_time, @p_closing_time, @p_email, @p_opening_date, @p_tax_code);

    SET @p_store_id = SCOPE_IDENTITY();
END
GO

-- Update Store
CREATE PROCEDURE sp_update_store @p_store_id TINYINT,
                                 @p_name NVARCHAR(100),
                                 @p_address NVARCHAR(255),
                                 @p_phone NVARCHAR(15),
                                 @p_opening_time TIME,
                                 @p_closing_time TIME,
                                 @p_email NVARCHAR(100),
                                 @p_opening_date DATE,
                                 @p_tax_code NVARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE store
    SET name         = @p_name,
        address      = @p_address,
        phone        = @p_phone,
        opening_time = @p_opening_time,
        closing_time = @p_closing_time,
        email        = @p_email,
        opening_date = @p_opening_date,
        tax_code     = @p_tax_code,
        updated_at   = GETDATE()
    WHERE store_id = @p_store_id;
END
GO

-- =====================================================
-- 15. UNIT OF MEASURE PROCEDURES
-- =====================================================

-- Insert Unit of Measure
CREATE PROCEDURE sp_insert_unit_of_measure @p_name NVARCHAR(30),
                                           @p_symbol NVARCHAR(5),
                                           @p_description NVARCHAR(1000),
                                           @p_unit_id SMALLINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO unit_of_measure (name, symbol, description)
    VALUES (@p_name, @p_symbol, @p_description);

    SET @p_unit_id = SCOPE_IDENTITY();
END
GO

-- Update Unit of Measure
CREATE PROCEDURE sp_update_unit_of_measure @p_unit_id SMALLINT,
                                           @p_name NVARCHAR(30),
                                           @p_symbol NVARCHAR(5),
                                           @p_description NVARCHAR(1000)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE unit_of_measure
    SET name        = @p_name,
        symbol      = @p_symbol,
        description = @p_description,
        updated_at  = GETDATE()
    WHERE unit_id = @p_unit_id;
END
GO

-- Delete Unit of Measure
CREATE PROCEDURE sp_delete_unit_of_measure @p_unit_id SMALLINT,
                                           @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM unit_of_measure WHERE unit_id = @p_unit_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 16. PRODUCT SIZE PROCEDURES
-- =====================================================

-- Insert Product Size
CREATE PROCEDURE sp_insert_product_size @p_unit_id SMALLINT,
                                        @p_name NVARCHAR(5),
                                        @p_quantity SMALLINT,
                                        @p_description NVARCHAR(1000),
                                        @p_size_id SMALLINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO product_size (unit_id, name, quantity, description)
    VALUES (@p_unit_id, @p_name, @p_quantity, @p_description);

    SET @p_size_id = SCOPE_IDENTITY();
END
GO

-- Update Product Size
CREATE PROCEDURE sp_update_product_size @p_size_id SMALLINT,
                                        @p_unit_id SMALLINT,
                                        @p_name NVARCHAR(5),
                                        @p_quantity SMALLINT,
                                        @p_description NVARCHAR(1000)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE product_size
    SET name        = @p_name,
        quantity    = @p_quantity,
        description = @p_description,
        updated_at  = GETDATE()
    WHERE size_id = @p_size_id;
END
GO

-- Delete Product Size
CREATE PROCEDURE sp_delete_product_size @p_size_id SMALLINT,
                                        @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM product_size WHERE size_id = @p_size_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 17. PRODUCT PRICE PROCEDURES
-- =====================================================

-- Insert Product Price
CREATE PROCEDURE sp_insert_product_price @p_product_id INT,
                                         @p_size_id SMALLINT,
                                         @p_price DECIMAL(11, 3),
                                         @p_product_price_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO product_price (product_id, size_id, price)
    VALUES (@p_product_id, @p_size_id, @p_price);

    SET @p_product_price_id = SCOPE_IDENTITY();
END
GO

-- Update Product Price
CREATE PROCEDURE sp_update_product_price @p_product_price_id INT,
                                         @p_product_id INT,
                                         @p_size_id SMALLINT,
                                         @p_unit_id SMALLINT,
                                         @p_price DECIMAL(11, 3),
                                         @p_is_active BIT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE product_price
    SET product_id = @p_product_id,
        size_id    = @p_size_id,
        price      = @p_price,
        updated_at = GETDATE()
    WHERE product_price_id = @p_product_price_id;
END
GO

-- Delete Product Price
CREATE PROCEDURE sp_delete_product_price @p_product_price_id INT,
                                         @p_row_count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM product_price WHERE product_price_id = @p_product_price_id;
    SET @p_row_count = @@ROWCOUNT;
END
GO

-- =====================================================
-- 18. ORDER PROCEDURES (Basic)
-- =====================================================

-- Insert Order
CREATE PROCEDURE sp_insert_order @p_customer_id INT,
                                 @p_employee_id INT,
                                 @p_order_time DATETIME2,
                                 @p_status NVARCHAR(20),
                                 @p_customize_note NVARCHAR(1000),
                                 @p_point INT = 1, -- Mặc định 1 điểm
                                 @p_order_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO [order] (customer_id, employee_id, order_time, total_amount, final_amount,
                         status, customize_note, point)
    VALUES (@p_customer_id, @p_employee_id, @p_order_time, NULL, NULL,
            @p_status, @p_customize_note, ISNULL(@p_point, 1)); -- Đảm bảo mặc định 1

    SET @p_order_id = SCOPE_IDENTITY();
END
GO


-- Update Order Status
CREATE PROCEDURE sp_update_order_status @p_order_id INT,
                                        @p_status NVARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE [order]
    SET status = @p_status,
        updated_at = GETDATE()
    WHERE order_id = @p_order_id;
END
GO

-- Update Order Amounts (called by triggers)
CREATE PROCEDURE sp_update_order_amounts @p_order_id INT,
                                         @p_total_amount DECIMAL(11, 3),
                                         @p_final_amount DECIMAL(11, 3)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE [order]
    SET total_amount = @p_total_amount,
        final_amount = @p_final_amount,
        updated_at = GETDATE()
    WHERE order_id = @p_order_id;
END
GO

-- Complete Order (với logic nghiệp vụ hoàn chỉnh)
CREATE PROCEDURE sp_complete_order @p_order_id INT,
                                   @p_payment_method_id TINYINT,
                                   @p_amount_paid DECIMAL(11, 3),
                                   @p_success BIT OUTPUT,
                                   @p_message NVARCHAR(255) OUTPUT,
                                   @p_change_amount DECIMAL(11, 3) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @current_status NVARCHAR(20), @final_amount DECIMAL(11, 3);
    DECLARE @existing_payment_id INT;
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Lấy thông tin đơn hàng
        SELECT @current_status = status, @final_amount = final_amount
        FROM [order] WHERE order_id = @p_order_id;
        
        -- Kiểm tra trạng thái
        IF @current_status != 'PROCESSING'
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Đơn hàng không ở trạng thái xử lý';
            ROLLBACK TRANSACTION;
            RETURN;
        END
        
        -- Kiểm tra final_amount
        IF @final_amount IS NULL OR @final_amount < 1000
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Đơn hàng chưa có thành tiền hợp lệ (tối thiểu 1000)';
            ROLLBACK TRANSACTION;
            RETURN;
        END
        
        -- Kiểm tra số tiền thanh toán
        IF @p_amount_paid < @final_amount
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Số tiền thanh toán không đủ. Cần: ' + CAST(@final_amount AS NVARCHAR(20)) + ', có: ' + CAST(@p_amount_paid AS NVARCHAR(20));
            ROLLBACK TRANSACTION;
            RETURN;
        END
        
        -- Tính tiền thừa
        SET @p_change_amount = @p_amount_paid - @final_amount;
        
        -- Kiểm tra xem đã có payment record chưa
        SELECT @existing_payment_id = payment_id
        FROM payment 
        WHERE order_id = @p_order_id AND status IN ('PROCESSING', 'PENDING');
        
        IF @existing_payment_id IS NOT NULL
        BEGIN
            -- Cập nhật payment record hiện có thành PAID
            UPDATE payment
            SET payment_method_id = @p_payment_method_id,
                status = 'PAID',
                amount_paid = @p_amount_paid,
                change_amount = @p_change_amount,
                payment_time = GETDATE(),
                updated_at = GETDATE()
            WHERE payment_id = @existing_payment_id;
        END
        ELSE
        BEGIN
            -- Tạo payment record mới
            INSERT INTO payment (order_id, payment_method_id, status, amount_paid, change_amount, payment_time)
            VALUES (@p_order_id, @p_payment_method_id, 'PAID', @p_amount_paid, @p_change_amount, GETDATE());
        END
        
        -- Trigger after_payment_complete_order sẽ tự động cập nhật order status thành COMPLETED
        
        COMMIT TRANSACTION;
        
        SET @p_success = 1;
        SET @p_message = N'Hoàn thành đơn hàng thành công. Tiền thừa: ' + CAST(@p_change_amount AS NVARCHAR(20)) + N' VND';
        
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
            
        SET @p_success = 0;
        SET @p_message = N'Lỗi: ' + ERROR_MESSAGE();
        SET @p_change_amount = 0;
    END CATCH
END
GO

-- Cancel Order
CREATE PROCEDURE sp_cancel_order @p_order_id INT,
                                 @p_reason NVARCHAR(255),
                                 @p_success BIT OUTPUT,
                                 @p_message NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @current_status NVARCHAR(20);
    
    SELECT @current_status = status
    FROM [order] WHERE order_id = @p_order_id;
    
    IF @current_status != 'PROCESSING'
    BEGIN
        SET @p_success = 0;
        SET @p_message = N'Chỉ có thể hủy đơn hàng đang xử lý';
        RETURN;
    END
    
    UPDATE [order]
    SET status = 'CANCELLED',
        customize_note = ISNULL(customize_note + '. ', '') + 'Hủy: ' + @p_reason,
        updated_at = GETDATE()
    WHERE order_id = @p_order_id;
    
    SET @p_success = 1;
    SET @p_message = N'Đã hủy đơn hàng thành công';
END
GO

-- Delete Order


-- =====================================================
-- 19. ORDER DISCOUNT PROCEDURES
-- =====================================================

-- Insert Order Discount
CREATE PROCEDURE sp_insert_order_discount @p_order_id INT,
                                          @p_discount_id INT,
                                          @p_discount_amount DECIMAL(11, 3),
                                          @p_order_discount_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO order_discount (order_id, discount_id, discount_amount)
    VALUES (@p_order_id, @p_discount_id, @p_discount_amount);

    SET @p_order_discount_id = SCOPE_IDENTITY();
END
GO

-- =====================================================
-- 20. PAYMENT PROCEDURES
-- =====================================================

-- Insert Payment (khởi tạo tracking thanh toán)
CREATE PROCEDURE sp_insert_payment @p_order_id INT,
                                   @p_payment_method_id TINYINT,
                                   @p_status NVARCHAR(20) = 'PROCESSING',
                                   @p_amount_paid DECIMAL(11, 3) = NULL,
                                   @p_change_amount DECIMAL(11, 3) = NULL,
                                   @p_payment_time DATETIME2 = NULL,
                                   @p_payment_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO payment (order_id, payment_method_id, status, amount_paid, change_amount, payment_time)
    VALUES (@p_order_id, @p_payment_method_id, @p_status, @p_amount_paid, @p_change_amount, @p_payment_time);

    SET @p_payment_id = SCOPE_IDENTITY();
END
GO

-- Update Payment Status (cập nhật trạng thái thanh toán)
CREATE PROCEDURE sp_update_payment_status @p_payment_id INT,
                                          @p_status NVARCHAR(20),
                                          @p_amount_paid DECIMAL(11, 3) = NULL,
                                          @p_success BIT OUTPUT,
                                          @p_message NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        -- Kiểm tra payment có tồn tại không
        IF NOT EXISTS (SELECT 1 FROM payment WHERE payment_id = @p_payment_id)
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Không tìm thấy giao dịch thanh toán';
            RETURN;
        END
        
        -- Cập nhật trạng thái (trigger sẽ xử lý validation và logic)
        UPDATE payment
        SET status = @p_status,
            amount_paid = CASE WHEN @p_amount_paid IS NOT NULL THEN @p_amount_paid ELSE amount_paid END,
            updated_at = GETDATE()
        WHERE payment_id = @p_payment_id;
        
        SET @p_success = 1;
        SET @p_message = N'Đã cập nhật trạng thái thanh toán thành ' + @p_status;
        
    END TRY
    BEGIN CATCH
        SET @p_success = 0;
        SET @p_message = N'Lỗi: ' + ERROR_MESSAGE();
    END CATCH
END
GO

-- Process Payment (xử lý thanh toán)
CREATE PROCEDURE sp_process_payment @p_order_id INT,
                                    @p_payment_method_id TINYINT,
                                    @p_amount_paid DECIMAL(11, 3),
                                    @p_success BIT OUTPUT,
                                    @p_message NVARCHAR(255) OUTPUT,
                                    @p_payment_id INT OUTPUT,
                                    @p_change_amount DECIMAL(11, 3) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @existing_payment_id INT;
    
    BEGIN TRY
        -- Kiểm tra có payment PROCESSING hoặc PENDING không
        SELECT @existing_payment_id = payment_id
        FROM payment 
        WHERE order_id = @p_order_id AND status IN ('PROCESSING', 'PENDING');
        
        IF @existing_payment_id IS NOT NULL
        BEGIN
            -- Cập nhật payment hiện có thành PAID
            UPDATE payment
            SET payment_method_id = @p_payment_method_id,
                status = 'PAID',
                amount_paid = @p_amount_paid,
                updated_at = GETDATE()
            WHERE payment_id = @existing_payment_id;
            
            SET @p_payment_id = @existing_payment_id;
        END
        ELSE
        BEGIN
            -- Tạo payment mới với trạng thái PAID
            INSERT INTO payment (order_id, payment_method_id, status, amount_paid)
            VALUES (@p_order_id, @p_payment_method_id, 'PAID', @p_amount_paid);
            
            SET @p_payment_id = SCOPE_IDENTITY();
        END
        
        -- Lấy change_amount sau khi trigger xử lý
        SELECT @p_change_amount = ISNULL(change_amount, 0)
        FROM payment WHERE payment_id = @p_payment_id;
        
        SET @p_success = 1;
        SET @p_message = N'Thanh toán thành công. Tiền thừa: ' + CAST(@p_change_amount AS NVARCHAR(20)) + N' VND';
        
    END TRY
    BEGIN CATCH
        SET @p_success = 0;
        SET @p_message = N'Lỗi: ' + ERROR_MESSAGE();
        SET @p_payment_id = 0;
        SET @p_change_amount = 0;
    END CATCH
END
GO

-- =====================================================
-- 21. ORDER TABLE PROCEDURES
-- =====================================================

-- Insert Order Table
CREATE PROCEDURE sp_insert_order_table @p_order_id INT,
                                       @p_table_id SMALLINT,
                                       @p_check_in DATETIME2,
                                       @p_check_out DATETIME2,
                                       @p_order_table_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO order_table (order_id, table_id, check_in, check_out)
    VALUES (@p_order_id, @p_table_id, @p_check_in, @p_check_out);

    SET @p_order_table_id = SCOPE_IDENTITY();
END
GO

-- Update Order Table Checkout
CREATE PROCEDURE sp_update_order_table_checkout @p_order_table_id INT,
                                                @p_check_out DATETIME2
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE order_table
    SET check_out  = @p_check_out,
        updated_at = GETDATE()
    WHERE order_table_id = @p_order_table_id;
END
GO

-- =====================================================
-- 22. ORDER PRODUCT PROCEDURES
-- =====================================================

-- Insert Order Product
CREATE PROCEDURE sp_insert_order_product @p_order_id INT,
                                         @p_product_price_id INT,
                                         @p_quantity SMALLINT,
                                         @p_option NVARCHAR(500),
                                         @p_order_product_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO order_product (order_id, product_price_id, quantity, [option])
    VALUES (@p_order_id, @p_product_price_id, @p_quantity, @p_option);

    SET @p_order_product_id = SCOPE_IDENTITY();
END
GO


-- Check Coupon Validity
CREATE PROCEDURE sp_check_coupon_validity @p_coupon_code NVARCHAR(15),
                                          @p_order_time DATETIME2,
                                          @p_is_valid BIT OUTPUT,
                                          @p_discount_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @v_valid_from DATETIME2, @v_valid_until DATETIME2, @v_is_active BIT;

    SET @p_is_valid = 0;
    SET @p_discount_id = NULL;

    SELECT @p_discount_id = d.discount_id,
           @v_valid_from = d.valid_from,
           @v_valid_until = d.valid_until,
           @v_is_active = d.is_active
    FROM discount d
             INNER JOIN coupon c ON d.coupon_id = c.coupon_id
    WHERE c.coupon = @p_coupon_code;

    IF @p_discount_id IS NOT NULL
        AND @v_is_active = 1
        AND (@v_valid_from IS NULL OR @p_order_time >= @v_valid_from)
        AND @p_order_time <= @v_valid_until
        BEGIN
            SET @p_is_valid = 1;
        END
END
GO

-- =================================================================
-- FUNCTION: fn_calculate_customer_discount
-- =================================================================
CREATE FUNCTION fn_calculate_customer_discount (
    @p_customer_id INT,
    @p_total_amount DECIMAL(11, 3)
)
RETURNS DECIMAL(11, 3)
AS
BEGIN
    DECLARE @membership_discount DECIMAL(11, 3) = 0;
    
    -- Nếu không có khách hàng thì không có giảm giá
    IF @p_customer_id IS NULL
        RETURN 0;
    
    -- Tính giảm giá từ membership
    DECLARE @discount_value DECIMAL(11, 3), @discount_unit NVARCHAR(10);
    
    SELECT @discount_value = mt.discount_value, @discount_unit = mt.discount_unit
    FROM customer c
    INNER JOIN membership_type mt ON c.membership_type_id = mt.membership_type_id
    WHERE c.customer_id = @p_customer_id
    AND mt.is_active = 1
    AND (mt.valid_until IS NULL OR mt.valid_until >= CAST(GETDATE() AS DATE));
    
    IF @discount_value IS NOT NULL AND @discount_value > 0
    BEGIN
        IF @discount_unit = 'PERCENTAGE'
        BEGIN
            SET @membership_discount = @p_total_amount * (@discount_value / 100);
        END
        ELSE IF @discount_unit = 'FIXED'
        BEGIN
            SET @membership_discount = @discount_value;
        END
    END
    
    -- Giảm giá không được vượt quá tổng tiền
    IF @membership_discount > @p_total_amount
        SET @membership_discount = @p_total_amount;
    
    RETURN @membership_discount;
END
GO

-- =================================================================
-- FUNCTION: fn_calculate_final_amount
-- =================================================================
CREATE FUNCTION fn_calculate_final_amount (
    @p_order_id INT
)
RETURNS DECIMAL(11, 3)
AS
BEGIN
    DECLARE @total_amount DECIMAL(11, 3) = 0;
    DECLARE @order_discount_amount DECIMAL(11, 3) = 0;
    DECLARE @customer_discount_amount DECIMAL(11, 3) = 0;
    DECLARE @final_amount DECIMAL(11, 3) = 0;
    DECLARE @customer_id INT;
    
    -- Lấy thông tin đơn hàng
    SELECT @total_amount = ISNULL(total_amount, 0),
           @customer_id = customer_id
    FROM [order] 
    WHERE order_id = @p_order_id;
    
    -- Tính tổng giảm giá từ order_discount
    SELECT @order_discount_amount = ISNULL(SUM(discount_amount), 0)
    FROM order_discount
    WHERE order_id = @p_order_id;
    
    -- Tính giảm giá từ khách hàng (chỉ membership)
    SET @customer_discount_amount = dbo.fn_calculate_customer_discount(@customer_id, @total_amount);
    
    -- Tính final_amount
    SET @final_amount = @total_amount - @order_discount_amount - @customer_discount_amount;
    
    -- Đảm bảo không âm
    IF @final_amount < 0
        SET @final_amount = 0;
    
    RETURN @final_amount;
END
GO

-- =================================================================
-- FUNCTION: fn_calculate_discount_amount
-- =================================================================
CREATE FUNCTION fn_calculate_discount_amount (
    @p_discount_id INT,
    @p_customer_id INT,
    @p_order_id INT
)
RETURNS DECIMAL(11, 3)
AS
BEGIN
    DECLARE @applied_discount DECIMAL(11, 3) = 0;
    DECLARE @error_message NVARCHAR(255);

    -- Lấy thông tin cần thiết
    DECLARE @total_amount DECIMAL(11, 3);
    DECLARE @product_count INT;
    SELECT @total_amount = total_amount FROM [order] WHERE order_id = @p_order_id;
    SELECT @product_count = COUNT(*) FROM order_product WHERE order_id = @p_order_id;

    -- 1. Kiểm tra đơn hàng có sản phẩm không
    IF @product_count = 0
    BEGIN
        RETURN 0; -- Không áp dụng nếu không có sản phẩm
    END

    -- Lấy thông tin discount
    DECLARE @d_is_active BIT;
    DECLARE @d_valid_until DATETIME2;
    DECLARE @d_min_value DECIMAL(11, 3);
    DECLARE @d_max_uses INT, @d_current_uses INT;
    DECLARE @d_max_uses_per_customer INT;
    DECLARE @d_type NVARCHAR(20), @d_value DECIMAL(11, 3), @d_max_amount DECIMAL(11, 3);

    SELECT 
        @d_is_active = is_active, @d_valid_until = valid_until, @d_min_value = min_required_order_value,
        @d_max_uses = max_uses, @d_current_uses = ISNULL(current_uses, 0),
        @d_max_uses_per_customer = max_uses_per_customer,
        @d_type = discount_type, @d_value = discount_value, @d_max_amount = max_discount_amount
    FROM discount WHERE discount_id = @p_discount_id;

    -- 2. Kiểm tra các điều kiện của discount
    IF @d_is_active = 0 OR @d_valid_until < GETDATE() OR (@d_max_uses IS NOT NULL AND @d_current_uses >= @d_max_uses) OR @total_amount < @d_min_value
    BEGIN
        RETURN 0;
    END

    -- 3. Kiểm tra lượt sử dụng của khách hàng
    IF @p_customer_id IS NOT NULL AND @d_max_uses_per_customer IS NOT NULL
    BEGIN
        DECLARE @customer_uses INT;
        SELECT @customer_uses = COUNT(*) FROM order_discount od JOIN [order] o ON od.order_id = o.order_id
        WHERE od.discount_id = @p_discount_id AND o.customer_id = @p_customer_id;
        
        IF @customer_uses >= @d_max_uses_per_customer
        BEGIN
            RETURN 0;
        END
    END

    -- 4. Tính toán số tiền giảm giá
    IF @d_type = 'PERCENTAGE'
    BEGIN
        SET @applied_discount = @total_amount * (@d_value / 100);
        IF @applied_discount > @d_max_amount
        BEGIN
            SET @applied_discount = @d_max_amount;
        END
    END
    ELSE IF @d_type = 'FIXED'
    BEGIN
        SET @applied_discount = @d_value;
    END

    RETURN @applied_discount;
END
GO

-- =====================================================
-- 23. SALES WORKFLOW PROCEDURES
-- =====================================================

-- Thêm sản phẩm vào đơn hàng
CREATE PROCEDURE sp_add_product_to_order @p_order_id INT,
                                         @p_product_price_id INT,
                                         @p_quantity SMALLINT,
                                         @p_option NVARCHAR(500) = NULL,
                                         @p_success BIT OUTPUT,
                                         @p_message NVARCHAR(255) OUTPUT,
                                         @p_final_quantity SMALLINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @existing_quantity SMALLINT = 0;
    
    BEGIN TRY
        -- Kiểm tra đơn hàng có tồn tại và đang processing không
        IF NOT EXISTS (SELECT 1 FROM [order] WHERE order_id = @p_order_id AND status = 'PROCESSING')
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Đơn hàng không tồn tại hoặc không ở trạng thái xử lý';
            RETURN;
        END
        
        -- Kiểm tra số lượng hiện tại của sản phẩm trong đơn hàng
        SELECT @existing_quantity = ISNULL(quantity, 0)
        FROM order_product 
        WHERE order_id = @p_order_id 
        AND product_price_id = @p_product_price_id
        AND (([option] IS NULL AND @p_option IS NULL) OR [option] = @p_option);
        
        -- Thêm sản phẩm (trigger sẽ xử lý logic merge)
        INSERT INTO order_product (order_id, product_price_id, quantity, [option])
        VALUES (@p_order_id, @p_product_price_id, @p_quantity, @p_option);
        
        -- Lấy số lượng cuối cùng sau khi thêm
        SELECT @p_final_quantity = quantity
        FROM order_product 
        WHERE order_id = @p_order_id 
        AND product_price_id = @p_product_price_id
        AND (([option] IS NULL AND @p_option IS NULL) OR [option] = @p_option);
        
        SET @p_success = 1;
        IF @existing_quantity > 0
        BEGIN
            SET @p_message = N'Đã cập nhật số lượng sản phẩm từ ' + CAST(@existing_quantity AS NVARCHAR(10)) + 
                           N' lên ' + CAST(@p_final_quantity AS NVARCHAR(10));
        END
        ELSE
        BEGIN
            SET @p_message = N'Đã thêm sản phẩm mới vào đơn hàng với số lượng ' + CAST(@p_final_quantity AS NVARCHAR(10));
        END
        
    END TRY
    BEGIN CATCH
        SET @p_success = 0;
        SET @p_message = N'Lỗi: ' + ERROR_MESSAGE();
        SET @p_final_quantity = 0;
    END CATCH
END
GO

-- Cập nhật số lượng sản phẩm trong đơn hàng
CREATE PROCEDURE sp_update_order_product_quantity @p_order_product_id INT,
                                                  @p_new_quantity SMALLINT,
                                                  @p_success BIT OUTPUT,
                                                  @p_message NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        -- Kiểm tra order_product có tồn tại không
        IF NOT EXISTS (
            SELECT 1 FROM order_product op
            INNER JOIN [order] o ON op.order_id = o.order_id
            WHERE op.order_product_id = @p_order_product_id
            AND o.status = 'PROCESSING'
        )
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Sản phẩm không tồn tại trong đơn hàng hoặc đơn hàng không ở trạng thái xử lý';
            RETURN;
        END
        
        -- Kiểm tra số lượng mới
        IF @p_new_quantity <= 0
        BEGIN
            -- Nếu số lượng <= 0, xóa sản phẩm khỏi đơn hàng
            DELETE FROM order_product WHERE order_product_id = @p_order_product_id;
            SET @p_success = 1;
            SET @p_message = N'Đã xóa sản phẩm khỏi đơn hàng';
        END
        ELSE IF @p_new_quantity > 999
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Số lượng không được vượt quá 999';
        END
        ELSE
        BEGIN
            -- Cập nhật số lượng
            UPDATE order_product 
            SET quantity = @p_new_quantity,
                updated_at = GETDATE()
            WHERE order_product_id = @p_order_product_id;
            
            SET @p_success = 1;
            SET @p_message = N'Đã cập nhật số lượng sản phẩm thành ' + CAST(@p_new_quantity AS NVARCHAR(10));
        END
        
    END TRY
    BEGIN CATCH
        SET @p_success = 0;
        SET @p_message = N'Lỗi: ' + ERROR_MESSAGE();
    END CATCH
END
GO

-- Xóa sản phẩm khỏi đơn hàng
CREATE PROCEDURE sp_remove_product_from_order @p_order_product_id INT,
                                              @p_success BIT OUTPUT,
                                              @p_message NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        -- Kiểm tra order_product có tồn tại không
        IF NOT EXISTS (
            SELECT 1 FROM order_product op
            INNER JOIN [order] o ON op.order_id = o.order_id
            WHERE op.order_product_id = @p_order_product_id
            AND o.status = 'PROCESSING'
        )
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Sản phẩm không tồn tại trong đơn hàng hoặc đơn hàng không ở trạng thái xử lý';
            RETURN;
        END
        
        -- Xóa sản phẩm
        DELETE FROM order_product WHERE order_product_id = @p_order_product_id;
        
        SET @p_success = 1;
        SET @p_message = N'Đã xóa sản phẩm khỏi đơn hàng thành công';
        
    END TRY
    BEGIN CATCH
        SET @p_success = 0;
        SET @p_message = N'Lỗi: ' + ERROR_MESSAGE();
    END CATCH
END
GO


-- Áp dụng mã giảm giá
CREATE PROCEDURE sp_apply_discount_to_order @p_order_id INT,
                                            @p_discount_id INT,
                                            @p_success BIT OUTPUT,
                                            @p_message NVARCHAR(255) OUTPUT,
                                            @p_discount_amount DECIMAL(11, 3) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        -- Kiểm tra đơn hàng có tồn tại và đang processing không
        IF NOT EXISTS (SELECT 1 FROM [order] WHERE order_id = @p_order_id AND status = 'PROCESSING')
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Đơn hàng không tồn tại hoặc không ở trạng thái xử lý';
            RETURN;
        END
        
        -- Áp dụng giảm giá (trigger sẽ xử lý tính toán và validation)
        INSERT INTO order_discount (order_id, discount_id, discount_amount)
        VALUES (@p_order_id, @p_discount_id, 0); -- trigger sẽ tính lại
        
        -- Lấy discount_amount vừa được tính
        SELECT @p_discount_amount = discount_amount
        FROM order_discount
        WHERE order_id = @p_order_id AND discount_id = @p_discount_id;
        
        SET @p_success = 1;
        SET @p_message = N'Đã áp dụng mã giảm giá thành công. Giảm: ' + CAST(@p_discount_amount AS NVARCHAR(20));
        
    END TRY
    BEGIN CATCH
        SET @p_success = 0;
        SET @p_message = N'Lỗi: ' + ERROR_MESSAGE();
        SET @p_discount_amount = 0;
    END CATCH
END
GO

-- Đặt bàn cho đơn hàng
CREATE PROCEDURE sp_assign_table_to_order @p_order_id INT,
                                          @p_table_id SMALLINT,
                                          @p_success BIT OUTPUT,
                                          @p_message NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        -- Kiểm tra đơn hàng có tồn tại và đang processing không
        IF NOT EXISTS (SELECT 1 FROM [order] WHERE order_id = @p_order_id AND status = 'PROCESSING')
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Đơn hàng không tồn tại hoặc không ở trạng thái xử lý';
            RETURN;
        END
        
        -- Đặt bàn (trigger sẽ xử lý validation)
        INSERT INTO order_table (order_id, table_id, check_in)
        VALUES (@p_order_id, @p_table_id, GETDATE());
        
        SET @p_success = 1;
        SET @p_message = N'Đã đặt bàn thành công';
        
    END TRY
    BEGIN CATCH
        SET @p_success = 0;
        SET @p_message = N'Lỗi: ' + ERROR_MESSAGE();
    END CATCH
END
GO

-- Checkout bàn
CREATE PROCEDURE sp_checkout_table @p_order_table_id INT,
                                   @p_success BIT OUTPUT,
                                   @p_message NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        -- Cập nhật checkout time
        UPDATE order_table
        SET check_out = GETDATE(),
            updated_at = GETDATE()
        WHERE order_table_id = @p_order_table_id
        AND check_out IS NULL;
        
        IF @@ROWCOUNT = 0
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Không tìm thấy bàn để checkout hoặc đã checkout rồi';
            RETURN;
        END
        
        SET @p_success = 1;
        SET @p_message = N'Đã checkout bàn thành công';
        
    END TRY
    BEGIN CATCH
        SET @p_success = 0;
        SET @p_message = N'Lỗi: ' + ERROR_MESSAGE();
    END CATCH
END
GO

-- Tính tổng tiền đơn hàng (dùng để hiển thị)
CREATE PROCEDURE sp_calculate_order_total @p_order_id INT,
                                          @p_total_amount DECIMAL(11, 3) OUTPUT,
                                          @p_order_discount_amount DECIMAL(11, 3) OUTPUT,
                                          @p_customer_discount_amount DECIMAL(11, 3) OUTPUT,
                                          @p_final_amount DECIMAL(11, 3) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @customer_id INT;
    
    -- Lấy thông tin đơn hàng
    SELECT @customer_id = customer_id
    FROM [order] WHERE order_id = @p_order_id;
    
    -- Tính tổng tiền từ sản phẩm
    SELECT @p_total_amount = ISNULL(SUM(op.quantity * pp.price), 0)
    FROM order_product op
    INNER JOIN product_price pp ON op.product_price_id = pp.product_price_id
    WHERE op.order_id = @p_order_id;
    
    -- Tính tổng giảm giá từ order_discount
    SELECT @p_order_discount_amount = ISNULL(SUM(discount_amount), 0)
    FROM order_discount
    WHERE order_id = @p_order_id;
    
    -- Tính giảm giá từ khách hàng (chỉ membership)
    SET @p_customer_discount_amount = dbo.fn_calculate_customer_discount(@customer_id, @p_total_amount);
    
    -- Tính thành tiền cuối cùng
    SET @p_final_amount = @p_total_amount - @p_order_discount_amount - @p_customer_discount_amount;
    
    -- Đảm bảo không âm
    IF @p_final_amount < 0
        SET @p_final_amount = 0;
END
GO

PRINT N'Đã tạo thành công tất cả stored procedures cho SQL Server - Part 2';
GO 