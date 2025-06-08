SET NAMES 'utf8mb4';
SET CHARACTER SET utf8mb4;

-- Test data setup cho testing
-- Thêm dữ liệu test cần thiết nếu chưa có

-- Test role và account cho employee
INSERT IGNORE INTO role (role_id, name, description) 
VALUES (1, 'employee', 'Nhân viên bán hàng'),
       (2, 'manager', 'Quản lý'),
       (3, 'admin', 'Quản trị viên');

INSERT IGNORE INTO account (account_id, role_id, username, password_hash, is_active, is_locked)
VALUES (1, 1, 'employee_test', '$2a$10$test_hash', 1, 0),
       (2, 2, 'manager_test', '$2a$10$test_hash', 1, 0);

INSERT IGNORE INTO employee (employee_id, account_id, position, last_name, first_name, gender, phone, email)
VALUES (1, 1, 'Nhân viên bán hàng', 'Nguyễn', 'Văn A', 'MALE', '0901234567', 'nva@test.com'),
       (2, 2, 'Quản lý ca', 'Trần', 'Thị B', 'FEMALE', '0901234568', 'ttb@test.com');

-- Test membership type
INSERT IGNORE INTO membership_type (membership_type_id, type, discount_value, discount_unit, required_point, description, is_active)
VALUES (1, 'Đồng', 0.000, 'PERCENTAGE', 0, 'Thành viên đồng', 1),
       (2, 'Bạc', 5.000, 'PERCENTAGE', 100, 'Thành viên bạc', 1),
       (3, 'Vàng', 10.000, 'PERCENTAGE', 500, 'Thành viên vàng', 1);

-- Test payment method
INSERT IGNORE INTO payment_method (payment_method_id, payment_name, payment_description)
VALUES (1, 'Tiền mặt', 'Thanh toán bằng tiền mặt'),
       (2, 'Chuyển khoản', 'Thanh toán qua chuyển khoản'),
       (3, 'Thẻ tín dụng', 'Thanh toán bằng thẻ tín dụng');

-- Test unit of measure và product size
INSERT IGNORE INTO unit_of_measure (unit_id, name, symbol, description)
VALUES (1, 'Cái', 'cái', 'Đơn vị đếm'),
       (2, 'Mililít', 'ml', 'Đơn vị thể tích');

INSERT IGNORE INTO product_size (size_id, unit_id, name, quantity, description)
VALUES (1, 1, 'S', 1, 'Size nhỏ'),
       (2, 2, 'M', 500, 'Size trung bình - 500ml'),
       (3, 2, 'L', 700, 'Size lớn - 700ml'),
       (4, 1, 'TG', 1, 'Topping');

-- Test categories
INSERT IGNORE INTO category (category_id, name, description)
VALUES (1, 'TOPPING', 'Các loại topping đi kèm'),
       (2, 'TOPPING ĐẶC BIỆT', 'Topping cao cấp, đặc biệt');

-- ===================================
-- STORED PROCEDURE TEST CASES
-- ===================================

DELIMITER //

-- Test Case 1: Đơn hàng đơn giản - chỉ có sản phẩm, không có discount và table
CREATE PROCEDURE test_sp_create_full_order_simple()
BEGIN
    DECLARE v_order_id INT UNSIGNED;
    DECLARE v_final_total DECIMAL(11, 3);
    DECLARE v_change DECIMAL(11, 3);
    DECLARE v_status_message VARCHAR(255);
    
    -- Chuẩn bị dữ liệu JSON
    SET @test_products = JSON_ARRAY(
        JSON_OBJECT('product_price_id', 1, 'quantity', 2, 'option', 'Ít đá, ít đường'),
        JSON_OBJECT('product_price_id', 3, 'quantity', 1, 'option', 'Nhiều đá')
    );
    
    SET @test_discounts = JSON_ARRAY();
    SET @test_tables = JSON_ARRAY();
    
    -- Gọi procedure
    CALL sp_create_full_order(
        4143047638,  -- customer_id (existing customer)
        1,           -- employee_id
        'Test đơn hàng đơn giản',  -- customize_note
        10,          -- point
        @test_products,
        @test_discounts,
        @test_tables,
        1,           -- payment_method_id (tiền mặt)
        100000.000,  -- amount_paid
        v_order_id,
        v_final_total,
        v_change,
        v_status_message
    );
    
    -- Hiển thị kết quả
    SELECT 'TEST CASE 1: Đơn hàng đơn giản' AS test_case;
    SELECT v_order_id AS order_id, v_final_total AS final_total, v_change AS change_amount, v_status_message AS status_message;
    
    -- Kiểm tra chi tiết đơn hàng
    SELECT 'Chi tiết đơn hàng:' AS info;
    SELECT o.order_id, o.customer_id, o.total_amount, o.final_amount, o.status, o.point
    FROM `order` o WHERE o.order_id = v_order_id;
    
    SELECT 'Sản phẩm trong đơn hàng:' AS info;
    SELECT op.order_product_id, op.product_price_id, op.quantity, op.option,
           pp.price, (pp.price * op.quantity) AS subtotal
    FROM order_product op 
    JOIN product_price pp ON op.product_price_id = pp.product_price_id
    WHERE op.order_id = v_order_id;
    
    SELECT 'Thanh toán:' AS info;
    SELECT p.payment_id, p.status, p.amount_paid, p.change_amount
    FROM payment p WHERE p.order_id = v_order_id;
END //

-- Test Case 2: Đơn hàng với discount
CREATE PROCEDURE test_sp_create_full_order_with_discount()
BEGIN
    DECLARE v_order_id INT UNSIGNED;
    DECLARE v_final_total DECIMAL(11, 3);
    DECLARE v_change DECIMAL(11, 3);
    DECLARE v_status_message VARCHAR(255);
    
    -- Chuẩn bị dữ liệu JSON
    SET @test_products = JSON_ARRAY(
        JSON_OBJECT('product_price_id', 2, 'quantity', 3, 'option', 'Bình thường'),
        JSON_OBJECT('product_price_id', 4, 'quantity', 2, 'option', 'Ít đường')
    );
    
    SET @test_discounts = JSON_ARRAY(
        JSON_OBJECT('discount_id', 1)
    );
    
    SET @test_tables = JSON_ARRAY();
    
    -- Gọi procedure
    CALL sp_create_full_order(
        4268434481,  -- customer_id (existing customer with higher points)
        2,           -- employee_id
        'Test đơn hàng có giảm giá',  -- customize_note
        15,          -- point
        @test_products,
        @test_discounts,
        @test_tables,
        1,           -- payment_method_id
        200000.000,  -- amount_paid
        v_order_id,
        v_final_total,
        v_change,
        v_status_message
    );
    
    -- Hiển thị kết quả
    SELECT 'TEST CASE 2: Đơn hàng có giảm giá' AS test_case;
    SELECT v_order_id AS order_id, v_final_total AS final_total, v_change AS change_amount, v_status_message AS status_message;
    
    -- Kiểm tra discount được áp dụng
    SELECT 'Giảm giá được áp dụng:' AS info;
    SELECT od.order_discount_id, od.discount_id, od.discount_amount,
           d.name AS discount_name, d.discount_type, d.discount_value
    FROM order_discount od 
    JOIN discount d ON od.discount_id = d.discount_id
    WHERE od.order_id = v_order_id;
    
    -- Kiểm tra customer points được cộng
    SELECT 'Điểm khách hàng sau khi đặt hàng:' AS info;
    SELECT customer_id, current_points FROM customer WHERE customer_id = 4268434481;
END //

-- Test Case 3: Đơn hàng hoàn chỉnh với table
CREATE PROCEDURE test_sp_create_full_order_complete()
BEGIN
    DECLARE v_order_id INT UNSIGNED;
    DECLARE v_final_total DECIMAL(11, 3);
    DECLARE v_change DECIMAL(11, 3);
    DECLARE v_status_message VARCHAR(255);
    
    -- Chuẩn bị dữ liệu JSON
    SET @test_products = JSON_ARRAY(
        JSON_OBJECT('product_price_id', 5, 'quantity', 1, 'option', 'Extra topping'),
        JSON_OBJECT('product_price_id', 75, 'quantity', 2, 'option', NULL),  -- topping
        JSON_OBJECT('product_price_id', 8, 'quantity', 1, 'option', 'Đá crush')
    );
    
    SET @test_discounts = JSON_ARRAY(
        JSON_OBJECT('discount_id', 2)
    );
    
    SET @test_tables = JSON_ARRAY(
        JSON_OBJECT('table_id', 1, 'check_in', '2025-01-15 10:00:00')
    );
    
    -- Gọi procedure
    CALL sp_create_full_order(
        4198039702,  -- customer_id
        1,           -- employee_id
        'Đơn hàng tại bàn với topping',  -- customize_note
        20,          -- point
        @test_products,
        @test_discounts,
        @test_tables,
        2,           -- payment_method_id (chuyển khoản)
        150000.000,  -- amount_paid
        v_order_id,
        v_final_total,
        v_change,
        v_status_message
    );
    
    -- Hiển thị kết quả
    SELECT 'TEST CASE 3: Đơn hàng hoàn chỉnh' AS test_case;
    SELECT v_order_id AS order_id, v_final_total AS final_total, v_change AS change_amount, v_status_message AS status_message;
    
    -- Kiểm tra table được gán
    SELECT 'Bàn được gán:' AS info;
    SELECT ot.order_table_id, ot.table_id, ot.check_in, ot.check_out,
           st.table_number, a.name AS area_name
    FROM order_table ot 
    JOIN service_table st ON ot.table_id = st.table_id
    LEFT JOIN area a ON st.area_id = a.area_id
    WHERE ot.order_id = v_order_id;
END //

-- Test Case 4: Test case lỗi - sản phẩm không tồn tại
CREATE PROCEDURE test_sp_create_full_order_error()
BEGIN
    DECLARE v_order_id INT UNSIGNED;
    DECLARE v_final_total DECIMAL(11, 3);
    DECLARE v_change DECIMAL(11, 3);
    DECLARE v_status_message VARCHAR(255);
    
    -- Chuẩn bị dữ liệu JSON với product_price_id không tồn tại
    SET @test_products = JSON_ARRAY(
        JSON_OBJECT('product_price_id', 99999, 'quantity', 1, 'option', 'Test')
    );
    
    SET @test_discounts = JSON_ARRAY();
    SET @test_tables = JSON_ARRAY();
    
    -- Gọi procedure (nên fail)
    CALL sp_create_full_order(
        4143047638,  -- customer_id
        1,           -- employee_id
        'Test error case',  -- customize_note
        5,           -- point
        @test_products,
        @test_discounts,
        @test_tables,
        1,           -- payment_method_id
        50000.000,   -- amount_paid
        v_order_id,
        v_final_total,
        v_change,
        v_status_message
    );
    
    -- Hiển thị kết quả
    SELECT 'TEST CASE 4: Test case lỗi' AS test_case;
    SELECT v_order_id AS order_id, v_final_total AS final_total, v_change AS change_amount, v_status_message AS status_message;
END //

-- Test Case 5: Thanh toán không đủ tiền
CREATE PROCEDURE test_sp_create_full_order_insufficient_payment()
BEGIN
    DECLARE v_order_id INT UNSIGNED;
    DECLARE v_final_total DECIMAL(11, 3);
    DECLARE v_change DECIMAL(11, 3);
    DECLARE v_status_message VARCHAR(255);
    
    -- Chuẩn bị dữ liệu JSON
    SET @test_products = JSON_ARRAY(
        JSON_OBJECT('product_price_id', 1, 'quantity', 1, 'option', 'Test insufficient payment')
    );
    
    SET @test_discounts = JSON_ARRAY();
    SET @test_tables = JSON_ARRAY();
    
    -- Gọi procedure với số tiền thanh toán không đủ
    CALL sp_create_full_order(
        4143047638,  -- customer_id
        1,           -- employee_id
        'Test thanh toán không đủ',  -- customize_note
        5,           -- point
        @test_products,
        @test_discounts,
        @test_tables,
        1,           -- payment_method_id
        30000.000,   -- amount_paid (ít hơn giá sản phẩm)
        v_order_id,
        v_final_total,
        v_change,
        v_status_message
    );
    
    -- Hiển thị kết quả
    SELECT 'TEST CASE 5: Thanh toán không đủ' AS test_case;
    SELECT v_order_id AS order_id, v_final_total AS final_total, v_change AS change_amount, v_status_message AS status_message;
    
    -- Kiểm tra trạng thái đơn hàng
    SELECT 'Trạng thái đơn hàng:' AS info;
    SELECT order_id, status, total_amount, final_amount FROM `order` WHERE order_id = v_order_id;
    
    SELECT 'Trạng thái thanh toán:' AS info;
    SELECT payment_id, status, amount_paid, change_amount FROM payment WHERE order_id = v_order_id;
END //

-- Procedure chạy tất cả test cases
CREATE PROCEDURE run_all_tests()
BEGIN
    SELECT '========================================' AS separator;
    SELECT 'BẮT ĐẦU CHẠY TẤT CẢ TEST CASES' AS title;
    SELECT '========================================' AS separator;
    
    CALL test_sp_create_full_order_simple();
    
    SELECT '\n' AS separator;
    CALL test_sp_create_full_order_with_discount();
    
    SELECT '\n' AS separator;
    CALL test_sp_create_full_order_complete();
    
    SELECT '\n' AS separator;
    CALL test_sp_create_full_order_insufficient_payment();
    
    SELECT '\n' AS separator;
    -- Test case lỗi cuối cùng vì có thể làm gián đoạn
    CALL test_sp_create_full_order_error();
    
    SELECT '========================================' AS separator;
    SELECT 'HOÀN THÀNH TẤT CẢ TEST CASES' AS title;
    SELECT '========================================' AS separator;
END //

DELIMITER ;

-- Hướng dẫn sử dụng:
-- Để chạy tất cả test cases: CALL run_all_tests();
-- Để chạy từng test case riêng lẻ:
-- CALL test_sp_create_full_order_simple();
-- CALL test_sp_create_full_order_with_discount();
-- CALL test_sp_create_full_order_complete();
-- CALL test_sp_create_full_order_insufficient_payment();
-- CALL test_sp_create_full_order_error(); 