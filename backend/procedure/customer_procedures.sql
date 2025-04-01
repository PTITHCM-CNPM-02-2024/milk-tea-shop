-- 3. Customer


DELIMITER //

-- Thêm khách hàng mới
CREATE PROCEDURE sp_insert_customer(
    IN p_membership_type_id TINYINT UNSIGNED,
    IN p_account_id INT UNSIGNED,
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER')
)
BEGIN
    INSERT INTO Customer(
        membership_type_id, account_id, last_name, first_name,
        phone, email, current_points, gender
    )
    VALUES(
              p_membership_type_id, p_account_id, p_last_name, p_first_name,
              p_phone, p_email, 0, p_gender
          );
    SELECT LAST_INSERT_ID() AS customer_id;
END //

-- Cập nhật khách hàng
CREATE PROCEDURE sp_update_customer(
    IN p_customer_id INT UNSIGNED,
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER')
)
BEGIN
    UPDATE Customer
    SET last_name = p_last_name,
        first_name = p_first_name,
        phone = p_phone,
        email = p_email,
        gender = p_gender,
        updated_at = CURRENT_TIMESTAMP
    WHERE customer_id = p_customer_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa khách hàng
CREATE PROCEDURE sp_delete_customer(
    IN p_customer_id INT UNSIGNED
)
BEGIN
    DECLARE order_count INT;

    -- Kiểm tra khách hàng có đơn hàng không
    SELECT COUNT(*) INTO order_count FROM `Order` WHERE customer_id = p_customer_id;

    IF order_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa khách hàng có đơn hàng';
    ELSE
        DELETE FROM Customer WHERE customer_id = p_customer_id;
        SELECT ROW_COUNT() > 0 AS success;
    END IF;
END //

-- Thêm điểm cho khách hàng
CREATE PROCEDURE sp_add_customer_points(
    IN p_customer_id INT UNSIGNED,
    IN p_points INT
)
BEGIN
    UPDATE Customer
    SET current_points = current_points + p_points,
        updated_at = CURRENT_TIMESTAMP
    WHERE customer_id = p_customer_id;

    SELECT c.customer_id, CONCAT(c.first_name, ' ', c.last_name) AS full_name,
           c.current_points, m.type AS membership_type
    FROM Customer c
             JOIN MembershipType m ON c.membership_type_id = m.membership_type_id
    WHERE c.customer_id = p_customer_id;
END //

-- Tìm khách hàng theo số điện thoại
CREATE PROCEDURE sp_get_customer_by_phone(
    IN p_phone VARCHAR(15)
)
BEGIN
    SELECT c.*, CONCAT(c.first_name, ' ', c.last_name) AS full_name,
           m.type AS membership_type
    FROM Customer c
             JOIN MembershipType m ON c.membership_type_id = m.membership_type_id
    WHERE c.phone = p_phone;
END //

-- Lấy khách hàng theo ID
CREATE PROCEDURE sp_get_customer_by_id(
    IN p_customer_id INT UNSIGNED
)
BEGIN
    SELECT c.*, CONCAT(c.first_name, ' ', c.last_name) AS full_name,
           m.type AS membership_type
    FROM Customer c
             JOIN MembershipType m ON c.membership_type_id = m.membership_type_id
    WHERE c.customer_id = p_customer_id;
END //

-- Lấy tất cả khách hàng
CREATE PROCEDURE sp_get_all_customers()
BEGIN
    SELECT c.*, CONCAT(c.first_name, ' ', c.last_name) AS full_name,
           m.type AS membership_type
    FROM Customer c
             JOIN MembershipType m ON c.membership_type_id = m.membership_type_id
    ORDER BY c.customer_id;
END //

DELIMITER ;