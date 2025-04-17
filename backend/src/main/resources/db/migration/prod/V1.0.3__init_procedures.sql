-- 1. Area
DELIMITER //
CREATE PROCEDURE sp_insert_area(
    IN p_name VARCHAR(3),
    IN p_max_tables INT,
    IN p_is_active BOOLEAN,
    IN p_description VARCHAR(255), -- Adjusted length based on table definition
    OUT p_area_id SMALLINT UNSIGNED
)
BEGIN
    INSERT INTO area (name, max_tables, is_active, description)
    VALUES (p_name, p_max_tables, p_is_active, p_description);
    SET p_area_id = LAST_INSERT_ID();
END //
DELIMITER ;

-- Add UPDATE and DELETE procedures for area
DELIMITER //
CREATE PROCEDURE sp_update_area(
    IN p_area_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(3),
    IN p_max_tables INT,
    IN p_is_active BOOLEAN,
    IN p_description VARCHAR(255),
    OUT p_row_count INT
)
BEGIN
    UPDATE area
    SET name = p_name,
        max_tables = p_max_tables,
        is_active = p_is_active,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE area_id = p_area_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_area(
    IN p_area_id SMALLINT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    DELETE FROM area WHERE area_id = p_area_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;


-- 2. Category Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_category(
    IN p_name VARCHAR(100),
    IN p_description VARCHAR(1000),
    OUT p_category_id SMALLINT UNSIGNED
)
BEGIN
    INSERT INTO category (name, description)
    VALUES (p_name, p_description);
    SET p_category_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_category(
    IN p_category_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(100),
    IN p_description VARCHAR(1000),
    OUT p_row_count INT
)
BEGIN
    UPDATE category
    SET name = p_name,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE category_id = p_category_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_category(
    IN p_category_id SMALLINT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (product.category_id ON DELETE SET NULL)
    DELETE FROM category WHERE category_id = p_category_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;


-- 3. Coupon Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_coupon(
    IN p_coupon VARCHAR(15),
    IN p_description VARCHAR(1000),
    OUT p_coupon_id INT UNSIGNED
)
BEGIN
    INSERT INTO coupon (coupon, description)
    VALUES (p_coupon, p_description);
    SET p_coupon_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_coupon(
    IN p_coupon_id INT UNSIGNED,
    IN p_coupon VARCHAR(15),
    IN p_description VARCHAR(1000),
    OUT p_row_count INT
)
BEGIN
    UPDATE coupon
    SET coupon = p_coupon,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE coupon_id = p_coupon_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_coupon(
    IN p_coupon_id INT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (discount.coupon_id ON DELETE RESTRICT)
    -- Need to handle or prevent deletion if used in discount table
    -- For now, simple delete. Add logic later if needed.
    DELETE FROM coupon WHERE coupon_id = p_coupon_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;


-- 4. Discount Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_discount(
    IN p_name VARCHAR(500),
    IN p_description VARCHAR(1000),
    IN p_coupon_id INT UNSIGNED,
    IN p_discount_value DECIMAL(11, 3),
    IN p_discount_type ENUM('FIXED', 'PERCENTAGE'),
    IN p_min_required_order_value DECIMAL(11, 3),
    IN p_max_discount_amount DECIMAL(11, 3),
    IN p_min_required_product SMALLINT UNSIGNED,
    IN p_valid_from DATETIME,
    IN p_valid_until DATETIME,
    IN p_current_uses INT UNSIGNED,
    IN p_max_uses INT UNSIGNED,
    IN p_max_uses_per_customer SMALLINT UNSIGNED,
    IN p_is_active BOOLEAN,
    OUT p_discount_id INT UNSIGNED
)
BEGIN
    INSERT INTO discount (name, description, coupon_id, discount_value, discount_type, min_required_order_value, max_discount_amount, min_required_product, valid_from, valid_until, current_uses, max_uses, max_uses_per_customer, is_active)
    VALUES (p_name, p_description, p_coupon_id, p_discount_value, p_discount_type, p_min_required_order_value, p_max_discount_amount, p_min_required_product, p_valid_from, p_valid_until, p_current_uses, p_max_uses, p_max_uses_per_customer, p_is_active);
    SET p_discount_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_discount(
    IN p_discount_id INT UNSIGNED,
    IN p_name VARCHAR(500),
    IN p_description VARCHAR(1000),
    IN p_coupon_id INT UNSIGNED,
    IN p_discount_value DECIMAL(11, 3),
    IN p_discount_type ENUM('FIXED', 'PERCENTAGE'),
    IN p_min_required_order_value DECIMAL(11, 3),
    IN p_max_discount_amount DECIMAL(11, 3),
    IN p_min_required_product SMALLINT UNSIGNED,
    IN p_valid_from DATETIME,
    IN p_valid_until DATETIME,
    IN p_current_uses INT UNSIGNED,
    IN p_max_uses INT UNSIGNED,
    IN p_max_uses_per_customer SMALLINT UNSIGNED,
    IN p_is_active BOOLEAN
)
BEGIN
    UPDATE discount
    SET name = p_name,
        description = p_description,
        coupon_id = p_coupon_id,
        discount_value = p_discount_value,
        discount_type = p_discount_type,
        min_required_order_value = p_min_required_order_value,
        max_discount_amount = p_max_discount_amount,
        min_required_product = p_min_required_product,
        valid_from = p_valid_from,
        valid_until = p_valid_until,
        current_uses = p_current_uses,
        max_uses = p_max_uses,
        max_uses_per_customer = p_max_uses_per_customer,
        is_active = p_is_active,
        updated_at = CURRENT_TIMESTAMP
    WHERE discount_id = p_discount_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_discount(
    IN p_discount_id INT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (order_discount.discount_id ON DELETE CASCADE)
    -- Deleting a discount will also delete related order_discount entries.
    DELETE FROM discount WHERE discount_id = p_discount_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;


-- 5. Membership Type Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_membership_type(
    IN p_type VARCHAR(50),
    IN p_discount_value DECIMAL(10, 3),
    IN p_discount_unit ENUM('FIXED', 'PERCENTAGE'),
    IN p_required_point INT,
    IN p_description VARCHAR(255),
    IN p_valid_until DATETIME,
    IN p_is_active BOOLEAN,
    OUT p_membership_type_id TINYINT UNSIGNED
)
BEGIN
    INSERT INTO membership_type (type, discount_value, discount_unit, required_point, description, valid_until, is_active)
    VALUES (p_type, p_discount_value, p_discount_unit, p_required_point, p_description, p_valid_until, p_is_active);
    SET p_membership_type_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_membership_type(
    IN p_membership_type_id TINYINT UNSIGNED,
    IN p_type VARCHAR(50),
    IN p_discount_value DECIMAL(10, 3),
    IN p_discount_unit ENUM('FIXED', 'PERCENTAGE'),
    IN p_required_point INT,
    IN p_description VARCHAR(255),
    IN p_valid_until DATETIME,
    IN p_is_active BOOLEAN
)
BEGIN
    UPDATE membership_type
    SET type = p_type,
        discount_value = p_discount_value,
        discount_unit = p_discount_unit,
        required_point = p_required_point,
        description = p_description,
        valid_until = p_valid_until,
        is_active = p_is_active,
        updated_at = CURRENT_TIMESTAMP
    WHERE membership_type_id = p_membership_type_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_membership_type(
    IN p_membership_type_id TINYINT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (customer.membership_type_id ON DELETE RESTRICT)
    -- Deletion might fail if a customer uses this membership type.
    -- Add logic to handle this (e.g., prevent deletion, set to default) if needed.
    DELETE FROM membership_type WHERE membership_type_id = p_membership_type_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;


-- 6. Payment Method Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_payment_method(
    IN p_payment_name VARCHAR(50),
    IN p_payment_description VARCHAR(255),
    OUT p_payment_method_id TINYINT UNSIGNED
)
BEGIN
    INSERT INTO payment_method (payment_name, payment_description)
    VALUES (p_payment_name, p_payment_description);
    SET p_payment_method_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_payment_method(
    IN p_payment_method_id TINYINT UNSIGNED,
    IN p_payment_name VARCHAR(50),
    IN p_payment_description VARCHAR(255)
)
BEGIN
    UPDATE payment_method
    SET payment_name = p_payment_name,
        payment_description = p_payment_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE payment_method_id = p_payment_method_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_payment_method(
    IN p_payment_method_id TINYINT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (payment.payment_method_id ON DELETE RESTRICT)
    -- Deletion might fail if this payment method is used in payments.
    DELETE FROM payment_method WHERE payment_method_id = p_payment_method_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;


-- 7. Product Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_product(
    IN p_category_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(100),
    IN p_description VARCHAR(1000),
    IN p_is_available BOOLEAN,
    IN p_is_signature BOOLEAN,
    IN p_image_path VARCHAR(1000),
    OUT p_product_id MEDIUMINT UNSIGNED
)
BEGIN
    INSERT INTO product (category_id, name, description, is_available, is_signature, image_path)
    VALUES (p_category_id, p_name, p_description, p_is_available, p_is_signature, p_image_path);
    SET p_product_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_product(
    IN p_product_id MEDIUMINT UNSIGNED,
    IN p_category_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(100),
    IN p_description VARCHAR(1000),
    IN p_is_available BOOLEAN,
    IN p_is_signature BOOLEAN,
    IN p_image_path VARCHAR(1000)
)
BEGIN
    UPDATE product
    SET category_id = p_category_id,
        name = p_name,
        description = p_description,
        is_available = p_is_available,
        is_signature = p_is_signature,
        image_path = p_image_path,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_id = p_product_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_product(
    IN p_product_id MEDIUMINT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (product_price.product_id ON DELETE CASCADE)
    -- Deleting a product will cascade delete related product_price entries.
    DELETE FROM product WHERE product_id = p_product_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;


-- 8. Role Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_role(
    IN p_name VARCHAR(50),
    IN p_description VARCHAR(1000),
    OUT p_role_id TINYINT UNSIGNED
)
BEGIN
    INSERT INTO role (name, description)
    VALUES (p_name, p_description);
    SET p_role_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_role(
    IN p_role_id TINYINT UNSIGNED,
    IN p_name VARCHAR(50),
    IN p_description VARCHAR(1000)
)
BEGIN
    UPDATE role
    SET name = p_name,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE role_id = p_role_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_role(
    IN p_role_id TINYINT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (account.role_id ON DELETE RESTRICT)
    -- Deletion might fail if accounts use this role.
    SET @v_account_id = (
        SELECT account_id FROM account WHERE role_id = p_role_id
        LIMIT 1
    );

    IF @v_account_id IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = CONCAT('Không thể xóa vai trò đang được sử dụng bởi tài khoản: ', @v_account_id);
    ELSE
        DELETE FROM role WHERE role_id = p_role_id;
        SET p_row_count = ROW_COUNT();
    END IF;
END //
DELIMITER ;

-- Procedures for other tables will follow...

-- 9. Account Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_account(
    IN p_role_id TINYINT UNSIGNED,
    IN p_username VARCHAR(50),
    IN p_password_hash VARCHAR(255), -- Note: Password hashing should happen in the application layer
    IN p_is_active BOOLEAN,
    IN p_is_locked BOOLEAN,
    OUT p_account_id INT UNSIGNED
)
BEGIN
    INSERT INTO account (role_id, username, password_hash, is_active, is_locked, token_version)
    VALUES (p_role_id, p_username, p_password_hash, p_is_active, p_is_locked, 0); -- Initial token version 0
    SET p_account_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_account_details( -- Separate procedure for non-sensitive updates
    IN p_account_id INT UNSIGNED,
    IN p_role_id TINYINT UNSIGNED,
    IN p_username VARCHAR(50),
    IN p_is_active BOOLEAN,
    IN p_is_locked BOOLEAN
)
BEGIN
    UPDATE account
    SET role_id = p_role_id,
        username = p_username,
        is_active = p_is_active,
        is_locked = p_is_locked,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_account_password(
    IN p_account_id INT UNSIGNED,
    IN p_new_password_hash VARCHAR(255) -- Hashed password from application
)
BEGIN
    UPDATE account
    SET password_hash = p_new_password_hash,
        token_version = token_version + 1, -- Increment token version on password change
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_account_last_login(
    IN p_account_id INT UNSIGNED
)
BEGIN
    UPDATE account
    SET last_login = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;
END //
DELIMITER ;


DELIMITER //
CREATE PROCEDURE sp_delete_account(
    IN p_account_id INT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies:
    -- customer.account_id ON DELETE SET NULL
    -- employee.account_id ON DELETE RESTRICT
    -- manager.account_id ON DELETE RESTRICT
    -- Deletion might fail if used by employee or manager.
    -- Setting customer.account_id to NULL is handled automatically.

    SET @v_employee_id = (
        SELECT employee_id FROM employee WHERE account_id = p_account_id
        LIMIT 1
    );

    SET @v_manager_id = (
        SELECT manager_id FROM manager WHERE account_id = p_account_id
        LIMIT 1
    );

    SET @v_customer_id = (
        SELECT customer_id FROM customer WHERE account_id = p_account_id
        LIMIT 1
    );

    IF @v_employee_id IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = CONCAT('Không thể xóa tài khoản đang được sử dụng bởi nhân viên: ', @v_employee_id);
    ELSEIF @v_manager_id IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = CONCAT('Không thể xóa tài khoản đang được sử dụng bởi quản lý: ', @v_manager_id);
    ELSEIF @v_customer_id IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = CONCAT('Không thể xóa tài khoản đang được sử dụng bởi khách hàng: ', @v_customer_id);
    ELSE
        DELETE FROM account WHERE account_id = p_account_id;
        SET p_row_count = ROW_COUNT();
    END IF;
END //
DELIMITER ;


-- 10. Customer Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_customer(
    IN p_membership_type_id TINYINT UNSIGNED,
    IN p_account_id INT UNSIGNED, -- Can be NULL
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100), -- Can be NULL
    IN p_current_points INT,
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER'), -- Can be NULL
    OUT p_customer_id INT UNSIGNED
)
BEGIN
    INSERT INTO customer (membership_type_id, account_id, last_name, first_name, phone, email, current_points, gender)
    VALUES (p_membership_type_id, p_account_id, p_last_name, p_first_name, p_phone, p_email, p_current_points, p_gender);
    SET p_customer_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_customer(
    IN p_customer_id INT UNSIGNED,
    IN p_membership_type_id TINYINT UNSIGNED,
    IN p_account_id INT UNSIGNED,
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100),
    IN p_current_points INT,
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER')
)
BEGIN
    UPDATE customer
    SET membership_type_id = p_membership_type_id,
        account_id = p_account_id,
        last_name = p_last_name,
        first_name = p_first_name,
        phone = p_phone,
        email = p_email,
        current_points = p_current_points,
        gender = p_gender,
        updated_at = CURRENT_TIMESTAMP
    WHERE customer_id = p_customer_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_customer(
    IN p_customer_id INT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (`order`.customer_id ON DELETE SET NULL)
    -- Orders associated with this customer will have customer_id set to NULL.
    DELETE FROM customer WHERE customer_id = p_customer_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;


-- 11. Employee Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_employee(
    IN p_account_id INT UNSIGNED, -- Should exist and have appropriate role
    IN p_position VARCHAR(50),
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100),
    OUT p_employee_id INT UNSIGNED
)
BEGIN
    INSERT INTO employee (account_id, position, last_name, first_name, gender, phone, email)
    VALUES (p_account_id, p_position, p_last_name, p_first_name, p_gender, p_phone, p_email);
    SET p_employee_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_employee(
    IN p_employee_id INT UNSIGNED,
    IN p_account_id INT UNSIGNED,
    IN p_position VARCHAR(50),
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100)
)
BEGIN
    UPDATE employee
    SET account_id = p_account_id,
        position = p_position,
        last_name = p_last_name,
        first_name = p_first_name,
        gender = p_gender,
        phone = p_phone,
        email = p_email,
        updated_at = CURRENT_TIMESTAMP
    WHERE employee_id = p_employee_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_employee(
    IN p_employee_id INT UNSIGNED
)
BEGIN
    -- Consider dependencies (`order`.employee_id ON DELETE CASCADE)
    -- Deleting an employee will cascade delete their associated orders.
    -- This might be undesirable; consider changing the foreign key constraint or deactivating the employee instead.
    DELETE FROM employee WHERE employee_id = p_employee_id;
END //
DELIMITER ;


-- 12. Manager Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_manager(
    IN p_account_id INT UNSIGNED, -- Should exist and have appropriate role
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100),
    OUT p_manager_id INT UNSIGNED
)
BEGIN
    INSERT INTO manager (account_id, last_name, first_name, gender, phone, email)
    VALUES (p_account_id, p_last_name, p_first_name, p_gender, p_phone, p_email);
    SET p_manager_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_manager(
    IN p_manager_id INT UNSIGNED,
    IN p_account_id INT UNSIGNED,
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100)
)
BEGIN
    UPDATE manager
    SET account_id = p_account_id,
        last_name = p_last_name,
        first_name = p_first_name,
        gender = p_gender,
        phone = p_phone,
        email = p_email,
        updated_at = CURRENT_TIMESTAMP
    WHERE manager_id = p_manager_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_manager(
    IN p_manager_id INT UNSIGNED
)
BEGIN
    -- No direct dependencies listed in the schema, safe to delete.
    DELETE FROM manager WHERE manager_id = p_manager_id;
END //
DELIMITER ;

-- Procedures for other tables will follow...

-- 13. Service Table Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_service_table(
    IN p_area_id SMALLINT UNSIGNED,
    IN p_table_number VARCHAR(10),
    IN p_is_active BOOLEAN,
    OUT p_table_id SMALLINT UNSIGNED
)
BEGIN
    INSERT INTO service_table (area_id, table_number, is_active)
    VALUES (p_area_id, p_table_number, p_is_active);
    SET p_table_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_service_table(
    IN p_table_id SMALLINT UNSIGNED,
    IN p_area_id SMALLINT UNSIGNED,
    IN p_table_number VARCHAR(10),
    IN p_is_active BOOLEAN
)
BEGIN
    UPDATE service_table
    SET area_id = p_area_id,
        table_number = p_table_number,
        is_active = p_is_active,
        updated_at = CURRENT_TIMESTAMP
    WHERE table_id = p_table_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_service_table(
    IN p_table_id SMALLINT UNSIGNED
)
BEGIN
    -- Consider dependencies (order_table.table_id ON DELETE CASCADE)
    -- Deleting a table will cascade delete its entries in order_table.
    DELETE FROM service_table WHERE table_id = p_table_id;
END //
DELIMITER ;


-- 14. Store Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_store(
    IN p_name VARCHAR(100),
    IN p_address VARCHAR(255),
    IN p_phone VARCHAR(15),
    IN p_opening_time TIME,
    IN p_closing_time TIME,
    IN p_email VARCHAR(100),
    IN p_opening_date DATE,
    IN p_tax_code VARCHAR(20),
    OUT p_store_id TINYINT UNSIGNED
)
BEGIN
    INSERT INTO store (name, address, phone, opening_time, closing_time, email, opening_date, tax_code)
    VALUES (p_name, p_address, p_phone, p_opening_time, p_closing_time, p_email, p_opening_date, p_tax_code);
    SET p_store_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_store(
    IN p_store_id TINYINT UNSIGNED,
    IN p_name VARCHAR(100),
    IN p_address VARCHAR(255),
    IN p_phone VARCHAR(15),
    IN p_opening_time TIME,
    IN p_closing_time TIME,
    IN p_email VARCHAR(100),
    IN p_opening_date DATE,
    IN p_tax_code VARCHAR(20)
)
BEGIN
    UPDATE store
    SET name = p_name,
        address = p_address,
        phone = p_phone,
        opening_time = p_opening_time,
        closing_time = p_closing_time,
        email = p_email,
        opening_date = p_opening_date,
        tax_code = p_tax_code,
        updated_at = CURRENT_TIMESTAMP
    WHERE store_id = p_store_id;
END //
DELIMITER ;

-- 15. Unit Of Measure Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_unit_of_measure(
    IN p_name VARCHAR(30),
    IN p_symbol VARCHAR(5),
    IN p_description VARCHAR(1000),
    OUT p_unit_id SMALLINT UNSIGNED
)
BEGIN
    INSERT INTO unit_of_measure (name, symbol, description)
    VALUES (p_name, p_symbol, p_description);
    SET p_unit_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_unit_of_measure(
    IN p_unit_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(30),
    IN p_symbol VARCHAR(5),
    IN p_description VARCHAR(1000)
)
BEGIN
    UPDATE unit_of_measure
    SET name = p_name,
        symbol = p_symbol,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE unit_id = p_unit_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_unit_of_measure(
    IN p_unit_id SMALLINT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (product_size.unit_id ON DELETE RESTRICT)
    -- Deletion might fail if product sizes use this unit.
    DELETE FROM unit_of_measure WHERE unit_id = p_unit_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;


-- 16. Product Size Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_product_size(
    IN p_unit_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(5),
    IN p_quantity SMALLINT UNSIGNED,
    IN p_description VARCHAR(1000),
    OUT p_size_id SMALLINT UNSIGNED
)
BEGIN
    INSERT INTO product_size (unit_id, name, quantity, description)
    VALUES (p_unit_id, p_name, p_quantity, p_description);
    SET p_size_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_product_size(
    IN p_size_id SMALLINT UNSIGNED,
    IN p_unit_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(5),
    IN p_quantity SMALLINT UNSIGNED,
    IN p_description VARCHAR(1000)
)
BEGIN
    UPDATE product_size
    SET unit_id = p_unit_id,
        name = p_name,
        quantity = p_quantity,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE size_id = p_size_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_product_size(
    IN p_size_id SMALLINT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (product_price.size_id ON DELETE CASCADE)
    -- Deleting a size will cascade delete related product_price entries.
    DELETE FROM product_size WHERE size_id = p_size_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;


-- 17. Product Price Procedures
DELIMITER //
CREATE PROCEDURE sp_insert_product_price(
    IN p_product_id MEDIUMINT UNSIGNED,
    IN p_size_id SMALLINT UNSIGNED,
    IN p_price DECIMAL(11, 3),
    OUT p_product_price_id INT UNSIGNED
)
BEGIN
    INSERT INTO product_price (product_id, size_id, price)
    VALUES (p_product_id, p_size_id, p_price);
    SET p_product_price_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_product_price(
    IN p_product_price_id INT UNSIGNED,
    IN p_product_id MEDIUMINT UNSIGNED,
    IN p_size_id SMALLINT UNSIGNED,
    IN p_price DECIMAL(11, 3)
)
BEGIN
    UPDATE product_price
    SET product_id = p_product_id,
        size_id = p_size_id,
        price = p_price,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_price_id = p_product_price_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_delete_product_price(
    IN p_product_price_id INT UNSIGNED,
    OUT p_row_count INT
)
BEGIN
    -- Consider dependencies (order_product.product_price_id ON DELETE CASCADE)
    -- Deleting a price will cascade delete related order_product entries.
    DELETE FROM product_price WHERE product_price_id = p_product_price_id;
    SET p_row_count = ROW_COUNT();
END //
DELIMITER ;

-- CRUD procedures for Order related tables (simple versions)
-- These might be used internally by the transaction procedure later

-- 18. Order Procedures (Basic)
DELIMITER //
CREATE PROCEDURE sp_insert_order(
    IN p_customer_id INT UNSIGNED,
    IN p_employee_id INT UNSIGNED,
    IN p_order_time TIMESTAMP,
    IN p_total_amount DECIMAL(11, 3), -- Initially might be 0 or calculated later
    IN p_final_amount DECIMAL(11, 3), -- Initially might be 0 or calculated later
    IN p_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED'),
    IN p_customize_note VARCHAR(1000),
    IN p_point INT UNSIGNED,
    OUT p_order_id INT UNSIGNED
)
BEGIN
    INSERT INTO `order` (customer_id, employee_id, order_time, total_amount, final_amount, status, customize_note, point)
    VALUES (p_customer_id, p_employee_id, p_order_time, p_total_amount, p_final_amount, p_status, p_customize_note, p_point);
    SET p_order_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_order_status(
    IN p_order_id INT UNSIGNED,
    IN p_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED')
)
BEGIN
    UPDATE `order`
    SET status = p_status,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = p_order_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_order_amounts(
    IN p_order_id INT UNSIGNED,
    IN p_total_amount DECIMAL(11, 3),
    IN p_final_amount DECIMAL(11, 3)
)
BEGIN
    UPDATE `order`
    SET total_amount = p_total_amount,
        final_amount = p_final_amount,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = p_order_id;
END //
DELIMITER ;


DELIMITER //
CREATE PROCEDURE sp_delete_order(
    IN p_order_id INT UNSIGNED
)
BEGIN
    -- Consider dependencies: ON DELETE CASCADE for order_discount, order_table, payment, order_product
    -- Deleting an order will automatically cascade deletes to related tables.
    DELETE FROM `order` WHERE order_id = p_order_id;
END //
DELIMITER ;


-- 19. Order Discount Procedures (Basic)
DELIMITER //
CREATE PROCEDURE sp_insert_order_discount(
    IN p_order_id INT UNSIGNED,
    IN p_discount_id INT UNSIGNED,
    IN p_discount_amount DECIMAL(11, 3),
    OUT p_order_discount_id INT UNSIGNED
)
BEGIN
    
    -- 1. chèn vào order_discount
    INSERT INTO order_discount (order_id, discount_id, discount_amount)
    VALUES (p_order_id, p_discount_id, p_discount_amount);
    SET p_order_discount_id = LAST_INSERT_ID();

END //
DELIMITER ;
-- UPDATE/DELETE for order_discount might not be common, often managed via order update

-- 20. Payment Procedures (Basic)
DELIMITER //
CREATE PROCEDURE sp_insert_payment(
    IN p_order_id INT UNSIGNED,
    IN p_payment_method_id TINYINT UNSIGNED,
    IN p_status ENUM('PROCESSING', 'CANCELLED', 'PAID'),
    IN p_amount_paid DECIMAL(11, 3),
    IN p_change_amount DECIMAL(11, 3),
    IN p_payment_time TIMESTAMP,
    OUT p_payment_id INT UNSIGNED
)
BEGIN
    INSERT INTO payment (order_id, payment_method_id, status, amount_paid, change_amount, payment_time)
    VALUES (p_order_id, p_payment_method_id, p_status, p_amount_paid, p_change_amount, p_payment_time);
    SET p_payment_id = LAST_INSERT_ID();
END //
DELIMITER ;
-- UPDATE/DELETE for payment might not be common, often managed via order update

-- 21. Order Table Procedures (Basic)
DELIMITER //
CREATE PROCEDURE sp_insert_order_table(
    IN p_order_id INT UNSIGNED,
    IN p_table_id SMALLINT UNSIGNED,
    IN p_check_in DATETIME,
    IN p_check_out DATETIME, -- Can be NULL initially
    OUT p_order_table_id INT UNSIGNED
)
BEGIN
    INSERT INTO order_table (order_id, table_id, check_in, check_out)
    VALUES (p_order_id, p_table_id, p_check_in, p_check_out);
    SET p_order_table_id = LAST_INSERT_ID();
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_update_order_table_checkout(
    IN p_order_table_id INT UNSIGNED,
    IN p_check_out DATETIME
)
BEGIN
    UPDATE order_table
    SET check_out = p_check_out,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_table_id = p_order_table_id;
END //
DELIMITER ;
-- DELETE for order_table might not be common, often managed via order update

-- 22. Order Product Procedures (Basic)
DELIMITER //
CREATE PROCEDURE sp_insert_order_product(
    IN p_order_id INT UNSIGNED,
    IN p_product_price_id INT UNSIGNED,
    IN p_quantity SMALLINT UNSIGNED,
    IN p_option VARCHAR(500), -- Can be NULL
    OUT p_order_product_id INT UNSIGNED
)
BEGIN
    INSERT INTO order_product (order_id, product_price_id, quantity, `option`)
    VALUES (p_order_id, p_product_price_id, p_quantity, p_option);
    SET p_order_product_id = LAST_INSERT_ID();
END //
DELIMITER ;
-- UPDATE/DELETE for order_product might not be common, often managed via order update

-- 23. Create Full Order Transaction Procedure
DELIMITER //

-- Drop existing procedure if it exists to redefine it
DROP PROCEDURE IF EXISTS sp_create_full_order;
CREATE PROCEDURE sp_create_full_order(
    -- Order details
    IN p_customer_id INT UNSIGNED,
    IN p_employee_id INT UNSIGNED,
    IN p_customize_note VARCHAR(1000),
    IN p_point INT UNSIGNED, -- Điểm được cộng cho đơn hàng này (nếu hoàn thành)
    -- Related items as JSON strings
    IN p_order_products JSON,
    IN p_order_discounts JSON,
    IN p_order_tables JSON,
    -- Payment details
    IN p_payment_method_id TINYINT UNSIGNED,
    IN p_amount_paid DECIMAL(11, 3),
    -- Output
    OUT p_new_order_id INT UNSIGNED,
    OUT p_final_total DECIMAL(11, 3),
    OUT p_change DECIMAL(11, 3),
    OUT p_status_message VARCHAR(255)
)
order_proc: BEGIN -- Thêm nhãn 'order_proc' ở đây
    -- Variables
    DECLARE v_order_id INT UNSIGNED;
    DECLARE v_total_amount DECIMAL(11, 3) DEFAULT 0.000;
    DECLARE v_total_discount_amount DECIMAL(11, 3) DEFAULT 0.000;
    DECLARE v_final_amount DECIMAL(11, 3) DEFAULT 0.000;
    DECLARE v_payment_status ENUM('PROCESSING', 'CANCELLED', 'PAID');
    DECLARE v_change_amount DECIMAL(11, 3) DEFAULT 0.000;
    DECLARE v_order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

    -- Variables for loops
    DECLARE i INT DEFAULT 0;
    DECLARE product_count INT DEFAULT 0;
    DECLARE discount_count INT DEFAULT 0;
    DECLARE table_count INT DEFAULT 0;

    DECLARE v_product_price_id INT UNSIGNED;
    DECLARE v_quantity SMALLINT UNSIGNED;
    DECLARE v_option VARCHAR(500);
    DECLARE v_price DECIMAL(11, 3);

    DECLARE v_discount_id INT UNSIGNED;
    DECLARE v_applied_discount DECIMAL(11, 3);

    DECLARE v_table_id SMALLINT UNSIGNED;
    DECLARE v_check_in DATETIME;

    -- Error handling
    DECLARE exit handler for sqlexception
    BEGIN
        ROLLBACK;
        SET p_new_order_id = NULL;
        SET p_final_total = NULL;
        SET p_change = NULL;
        SET p_status_message = 'Lỗi xảy ra, giao dịch đã được hủy bỏ';
         -- Consider logging the specific SQL error here for debugging
    END;

    -- Input validation (basic)
    IF p_employee_id IS NULL OR p_payment_method_id IS NULL THEN
        SET p_status_message = 'Employee ID và Payment Method ID là bắt buộc.';
        LEAVE order_proc; -- Sử dụng nhãn 'order_proc' thay vì 'BEGIN'
    END IF;
    IF JSON_VALID(p_order_products) = 0 OR JSON_LENGTH(p_order_products) = 0 THEN
         SET p_status_message = 'Đơn hàng phải chứa ít nhất một sản phẩm hợp lệ.';
         LEAVE order_proc; -- Sử dụng nhãn 'order_proc' thay vì 'BEGIN'
    END IF;
    -- Add more JSON validation if needed for discounts/tables


    -- Start transaction
    START TRANSACTION;

    -- 1. Insert into `order` table (initial amounts are 0) -- chèn vào bảng order với các giá trị khởi tạo là 0
    CALL sp_insert_order(p_customer_id, p_employee_id, v_order_time, 0.000, 0.000, 'PROCESSING', p_customize_note, p_point, v_order_id);
    SET p_new_order_id = v_order_id; -- Set output order ID

    -- 2. Insert into `order_product` and calculate total_amount -- chèn vào bảng order_product và tính toán total_amount
    SET product_count = JSON_LENGTH(p_order_products);
    WHILE i < product_count DO
        -- Extract product details from JSON array element -- trích xuất thông tin sản phẩm từ phần tử mảng JSON
        SET v_product_price_id = JSON_UNQUOTE(JSON_EXTRACT(p_order_products, CONCAT('$[', i, '].product_price_id')));
        SET v_quantity = JSON_UNQUOTE(JSON_EXTRACT(p_order_products, CONCAT('$[', i, '].quantity')));
        SET v_option = JSON_UNQUOTE(JSON_EXTRACT(p_order_products, CONCAT('$[', i, '].option'))); -- Handle potential NULL

        -- Basic validation -- kiểm tra dữ liệu sản phẩm
        IF v_product_price_id IS NULL OR v_quantity IS NULL OR v_quantity <= 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Dữ liệu sản phẩm không hợp lệ tìm thấy trong đơn hàng (ID hoặc số lượng).';
        END IF;

        -- Get price for calculation -- lấy giá sản phẩm cho tính toán
        SELECT price INTO v_price FROM product_price WHERE product_price_id = v_product_price_id;
        IF v_price IS NULL THEN
             -- Product price not found, trigger rollback
             SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không tìm thấy giá sản phẩm trong đơn hàng.';
        END IF;

        -- Insert into order_product -- chèn vào bảng order_product
        CALL sp_insert_order_product(v_order_id, v_product_price_id, v_quantity, v_option, @op_id); -- Using session variable temporarily

        -- Accumulate total amount -- tích lũy tổng số tiền
        SET v_total_amount = v_total_amount + (v_price * v_quantity);

        SET i = i + 1;
    END WHILE;

    -- Update order with calculated total_amount (final_amount is still 0) -- cập nhật đơn hàng với tổng số tiền đã tính toán (final_amount vẫn là 0)
    CALL sp_update_order_amounts(v_order_id, v_total_amount, 0.000);

    -- 3. Apply discounts, calculate total_discount_amount, AND INCREMENT USAGE
    SET i = 0;
    IF JSON_VALID(p_order_discounts) = 1 THEN
        SET discount_count = JSON_LENGTH(p_order_discounts);
        WHILE i < discount_count DO
            SET v_discount_id = JSON_UNQUOTE(JSON_EXTRACT(p_order_discounts, CONCAT('$[', i, '].discount_id')));

            IF v_discount_id IS NOT NULL THEN
                SET v_applied_discount = fn_calculate_and_validate_discount(
                    v_discount_id,
                    p_customer_id,
                    v_total_amount,
                    v_order_time,
                    v_total_discount_amount
                );

                IF v_applied_discount > 0 THEN
                    IF NOT EXISTS (SELECT 1 FROM order_discount WHERE order_id = v_order_id AND discount_id = v_discount_id) THEN
                        CALL sp_insert_order_discount(v_order_id, v_discount_id, v_applied_discount, @od_id);
                        SET v_total_discount_amount = v_total_discount_amount + v_applied_discount;

                        -- **Tăng số lần sử dụng discount**
                        UPDATE discount
                        SET current_uses = IFNULL(current_uses, 0) + 1,
                            updated_at = CURRENT_TIMESTAMP
                        WHERE discount_id = v_discount_id;
                        -- Lưu ý: Chưa kiểm tra current_uses > max_uses ở đây,
                        -- việc này nên được xử lý bởi trigger hoặc logic khác nếu cần chặn vượt quá.
                    END IF;
                END IF;
            END IF;

            SET i = i + 1;
        END WHILE;
    END IF;

    -- 4. Calculate final_amount and update order
    SET v_final_amount = v_total_amount - v_total_discount_amount;
    -- Ensure final amount is not negative
    IF v_final_amount < 0 THEN
       SET v_final_amount = 0;
    END IF;

    CALL sp_update_order_amounts(v_order_id, v_total_amount, v_final_amount);
    SET p_final_total = v_final_amount; -- Set output final total

    -- 5. Insert into `order_table` -- chèn vào bảng order_table
    SET i = 0; -- Reset loop counter
    IF JSON_VALID(p_order_tables) = 1 THEN
        SET table_count = JSON_LENGTH(p_order_tables);
        WHILE i < table_count DO
            SET v_table_id = JSON_UNQUOTE(JSON_EXTRACT(p_order_tables, CONCAT('$[', i, '].table_id')));
            SET v_check_in = JSON_UNQUOTE(JSON_EXTRACT(p_order_tables, CONCAT('$[', i, '].check_in')));

            IF v_table_id IS NOT NULL AND v_check_in IS NOT NULL THEN
                -- Basic validation for check_in time format could be added here if needed -- kiểm tra định dạng thời gian check_in có thể được thêm vào đây nếu cần
                 -- Check if this table combination already exists for the order -- kiểm tra xem bàn này đã tồn tại trong đơn hàng chưa
                 IF NOT EXISTS (SELECT 1 FROM order_table WHERE order_id = v_order_id AND table_id = v_table_id) THEN
                    CALL sp_insert_order_table(v_order_id, v_table_id, v_check_in, NULL, @ot_id); -- check_out is initially NULL
                 END IF;
            END IF;
            SET i = i + 1;
        END WHILE;
    END IF;

    -- 6. Insert into `payment` -- chèn vào bảng payment
    IF p_amount_paid IS NULL THEN -- Handle case where payment might be deferred or invalid input -- xử lý trường hợp thanh toán có thể bị trì hoãn hoặc dữ liệu nhập không hợp lệ
       SET v_payment_status = 'PROCESSING';
       SET v_change_amount = 0.000;
       SET p_amount_paid = 0.000; -- Ensure amount paid is not NULL for insert
    ELSEIF p_amount_paid >= v_final_amount THEN
        SET v_payment_status = 'PAID';
        SET v_change_amount = p_amount_paid - v_final_amount;
    ELSE -- Partial payment or less than total -- thanh toán một phần hoặc nhỏ hơn tổng số tiền
        SET v_payment_status = 'PROCESSING'; -- Or potentially another status like 'PARTIAL' if added to ENUM
        SET v_change_amount = 0.000; -- No change if not fully paid
        -- Consider adding logic here if partial payments need specific handling
    END IF;
    SET p_change = v_change_amount; -- Set output change -- thiết lập số tiền thay đổi  

    CALL sp_insert_payment(v_order_id, p_payment_method_id, v_payment_status, p_amount_paid, v_change_amount, CURRENT_TIMESTAMP, @pay_id);

    -- 7. Update final order status AND customer points
    IF v_payment_status = 'PAID' THEN
        CALL sp_update_order_status(v_order_id, 'COMPLETED');

        -- **Cộng điểm cho khách hàng nếu có**
        IF p_customer_id IS NOT NULL AND p_point > 0 THEN
            UPDATE customer
            SET current_points = current_points + p_point,
                updated_at = CURRENT_TIMESTAMP
            WHERE customer_id = p_customer_id;
        END IF;

    ELSE
         CALL sp_update_order_status(v_order_id, 'PROCESSING');
    END IF;

    -- Commit transaction -- xác nhận giao dịch
    COMMIT;
    SET p_status_message = 'Đơn hàng đã được tạo thành công.';

END //

DELIMITER ;

-- Function to check basic discount applicability (Simplified: checks dates and active status) -- hàm kiểm tra tính áp dụng chiết khấu cơ bản (Đơn giản: kiểm tra ngày và trạng thái hoạt động)
DELIMITER //
CREATE FUNCTION fn_check_discount_applicable (
    p_discount_id INT UNSIGNED,
    p_order_time DATETIME -- Assuming check is done based on order time
)
RETURNS BOOLEAN
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE is_applicable BOOLEAN DEFAULT FALSE;
    DECLARE v_is_active BOOLEAN;
    DECLARE v_valid_from DATETIME;
    DECLARE v_valid_until DATETIME;
    -- Add other variables for more complex checks if needed

    SELECT is_active, valid_from, valid_until
    INTO v_is_active, v_valid_from, v_valid_until
    FROM discount
    WHERE discount_id = p_discount_id;

    IF v_is_active IS NOT NULL AND v_is_active = TRUE THEN
        IF (v_valid_from IS NULL OR p_order_time >= v_valid_from) AND (p_order_time <= v_valid_until) THEN
             -- Add more checks here (min order value, usage limits etc.)
             -- For now, only checks active status and validity period
            SET is_applicable = TRUE;
        END IF;
    END IF;

    RETURN is_applicable;
END //
DELIMITER ;

-- Function to calculate and validate the applicable amount for a single discount
DELIMITER //
DROP FUNCTION IF EXISTS fn_calculate_and_validate_discount;
CREATE FUNCTION fn_calculate_and_validate_discount (
    p_discount_id INT UNSIGNED,
    p_customer_id INT UNSIGNED,
    p_order_total_amount DECIMAL(11, 3),
    p_order_time TIMESTAMP,
    p_accumulated_discount DECIMAL(11, 3)
)
RETURNS DECIMAL(11, 3)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_applied_discount DECIMAL(11, 3) DEFAULT 0.000;
    DECLARE v_discount_value DECIMAL(11, 3);
    DECLARE v_discount_type ENUM('FIXED', 'PERCENTAGE');
    DECLARE v_max_discount_amount DECIMAL(11, 3);
    DECLARE v_min_required_order_value DECIMAL(11, 3);
    DECLARE v_max_uses_per_customer SMALLINT UNSIGNED;
    DECLARE v_customer_usage_count INT DEFAULT 0;
    DECLARE v_is_discount_applicable BOOLEAN;

    -- 1. Check basic applicability (dates, active status)
    SET v_is_discount_applicable = fn_check_discount_applicable(p_discount_id, p_order_time);

    IF v_is_discount_applicable THEN
        -- 2. Fetch discount details (bao gồm max_uses_per_customer)
        SELECT
            discount_value, discount_type, max_discount_amount,
            min_required_order_value, max_uses_per_customer
        INTO
            v_discount_value, v_discount_type, v_max_discount_amount,
            v_min_required_order_value, v_max_uses_per_customer
        FROM discount
        WHERE discount_id = p_discount_id;

        -- 3. Check max_uses_per_customer limit
        IF p_customer_id IS NOT NULL AND v_max_uses_per_customer IS NOT NULL THEN
            SELECT COUNT(*)
            INTO v_customer_usage_count
            FROM order_discount od
            JOIN `order` o ON od.order_id = o.order_id
            WHERE od.discount_id = p_discount_id
              AND o.customer_id = p_customer_id
              AND o.status = 'COMPLETED'; -- Chỉ đếm các đơn đã hoàn thành

            IF v_customer_usage_count >= v_max_uses_per_customer THEN
                RETURN 0.000; -- Khách hàng đã hết lượt sử dụng, không áp dụng
            END IF;
        END IF;
        -- Kết thúc kiểm tra max_uses_per_customer

        -- 4. Check minimum order value requirement
        IF p_order_total_amount >= IFNULL(v_min_required_order_value, 0) THEN
            -- 5. Calculate potential discount amount based on type
            IF v_discount_type = 'PERCENTAGE' THEN
                SET v_applied_discount = (p_order_total_amount * v_discount_value) / 100;
                IF v_max_discount_amount IS NOT NULL AND v_applied_discount > v_max_discount_amount THEN
                    SET v_applied_discount = v_max_discount_amount;
                END IF;
            ELSE -- FIXED amount
                SET v_applied_discount = IFNULL(v_discount_value, 0);
            END IF;

            -- 6. Ensure applied discount does not exceed the remaining order value
            IF (p_accumulated_discount + v_applied_discount) > p_order_total_amount THEN
                SET v_applied_discount = p_order_total_amount - p_accumulated_discount;
            END IF;

            IF v_applied_discount < 0 THEN
                 SET v_applied_discount = 0.000;
             END IF;

        END IF; -- End check min order value
    END IF; -- End check applicability

    RETURN v_applied_discount;
END //
DELIMITER ;