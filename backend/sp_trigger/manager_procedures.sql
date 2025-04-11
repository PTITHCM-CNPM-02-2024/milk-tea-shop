DELIMITER //

-- Thêm quản lý mới
CREATE PROCEDURE sp_insert_manager(
    IN p_account_id INT UNSIGNED,
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100)
)
BEGIN
    INSERT INTO Manager(account_id, last_name, first_name, gender, phone, email)
    VALUES(p_account_id, p_last_name, p_first_name, p_gender, p_phone, p_email);

    SELECT LAST_INSERT_ID() AS manager_id;
END //

-- Cập nhật thông tin quản lý
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
    UPDATE Manager
    SET account_id = p_account_id,
        last_name = p_last_name,
        first_name = p_first_name,
        gender = p_gender,
        phone = p_phone,
        email = p_email,
        updated_at = CURRENT_TIMESTAMP
    WHERE manager_id = p_manager_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa quản lý
CREATE PROCEDURE sp_delete_manager(
    IN p_manager_id INT UNSIGNED
)
BEGIN
    DELETE FROM Manager WHERE manager_id = p_manager_id;
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Cập nhật tài khoản cho quản lý
CREATE PROCEDURE sp_update_manager_account(
    IN p_manager_id INT UNSIGNED,
    IN p_account_id INT UNSIGNED
)
BEGIN
    UPDATE Manager
    SET account_id = p_account_id,
        updated_at = CURRENT_TIMESTAMP
    WHERE manager_id = p_manager_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy quản lý theo ID
CREATE PROCEDURE sp_get_manager_by_id(
    IN p_manager_id INT UNSIGNED
)
BEGIN
    SELECT m.*, CONCAT(m.first_name, ' ', m.last_name) AS full_name
    FROM Manager m
    WHERE m.manager_id = p_manager_id;
END //

-- Lấy quản lý theo account_id
CREATE PROCEDURE sp_get_manager_by_account(
    IN p_account_id INT UNSIGNED
)
BEGIN
    SELECT m.*, CONCAT(m.first_name, ' ', m.last_name) AS full_name
    FROM Manager m
    WHERE m.account_id = p_account_id;
END //

-- Lấy tất cả quản lý
CREATE PROCEDURE sp_get_all_managers()
BEGIN
    SELECT m.*, CONCAT(m.first_name, ' ', m.last_name) AS full_name
    FROM Manager m
    ORDER BY m.last_name, m.first_name;
END //

-- Tìm kiếm quản lý
CREATE PROCEDURE sp_search_managers(
    IN p_keyword VARCHAR(100)
)
BEGIN
    SELECT m.*, CONCAT(m.first_name, ' ', m.last_name) AS full_name
    FROM Manager m
    WHERE m.first_name LIKE CONCAT('%', p_keyword, '%')
       OR m.last_name LIKE CONCAT('%', p_keyword, '%')
       OR m.phone LIKE CONCAT('%', p_keyword, '%')
       OR m.email LIKE CONCAT('%', p_keyword, '%')
    ORDER BY m.last_name, m.first_name;
END //

DELIMITER ;
