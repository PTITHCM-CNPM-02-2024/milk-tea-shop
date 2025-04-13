DELIMITER //

-- Thêm nhân viên mới
CREATE PROCEDURE sp_insert_employee(
    IN p_account_id INT UNSIGNED,
    IN p_position VARCHAR(50),
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100)
)
BEGIN
    INSERT INTO Employee(
        account_id, position, last_name, first_name,
        gender, phone, email
    )
    VALUES(
              p_account_id, p_position, p_last_name, p_first_name,
              p_gender, p_phone, p_email
          );
    SELECT LAST_INSERT_ID() AS employee_id;
END //

-- Cập nhật nhân viên
CREATE PROCEDURE sp_update_employee(
    IN p_employee_id INT UNSIGNED,
    IN p_position VARCHAR(50),
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100)
)
BEGIN
    UPDATE Employee
    SET position = p_position,
        last_name = p_last_name,
        first_name = p_first_name,
        gender = p_gender,
        phone = p_phone,
        email = p_email,
        updated_at = CURRENT_TIMESTAMP
    WHERE employee_id = p_employee_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa nhân viên
CREATE PROCEDURE sp_delete_employee(
    IN p_employee_id INT UNSIGNED
)
BEGIN
    DECLARE order_count INT;

    -- Kiểm tra nhân viên có đơn hàng không
    SELECT COUNT(*) INTO order_count FROM `Order` WHERE employee_id = p_employee_id;

    IF order_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa nhân viên đã xử lý đơn hàng';
    ELSE
        DELETE FROM Employee WHERE employee_id = p_employee_id;
        SELECT ROW_COUNT() > 0 AS success;
    END IF;
END //

-- Lấy nhân viên theo ID
CREATE PROCEDURE sp_get_employee_by_id(
    IN p_employee_id INT UNSIGNED
)
BEGIN
    SELECT e.*, CONCAT(e.first_name, ' ', e.last_name) AS full_name
    FROM Employee e
    WHERE e.employee_id = p_employee_id;
END //

-- Lấy nhân viên theo account_id
CREATE PROCEDURE sp_get_employee_by_account(
    IN p_account_id INT UNSIGNED
)
BEGIN
    SELECT e.*, CONCAT(e.first_name, ' ', e.last_name) AS full_name
    FROM Employee e
    WHERE e.account_id = p_account_id;
END //

-- Lấy tất cả nhân viên
CREATE PROCEDURE sp_get_all_employees()
BEGIN
    SELECT e.*, CONCAT(e.first_name, ' ', e.last_name) AS full_name
    FROM Employee e
    ORDER BY e.employee_id;
END //

DELIMITER ;