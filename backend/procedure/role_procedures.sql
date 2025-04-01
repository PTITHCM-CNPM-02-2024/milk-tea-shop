DELIMITER //

-- Thêm vai trò mới
CREATE PROCEDURE sp_insert_role(
    IN p_name VARCHAR(50),
    IN p_description VARCHAR(1000)
)
BEGIN
    INSERT INTO Role(name, description)
    VALUES(p_name, p_description);
    SELECT LAST_INSERT_ID() AS role_id;
END //

-- Cập nhật vai trò
CREATE PROCEDURE sp_update_role(
    IN p_role_id TINYINT UNSIGNED,
    IN p_name VARCHAR(50),
    IN p_description VARCHAR(1000)
)
BEGIN
    UPDATE Role
    SET name = p_name,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE role_id = p_role_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa vai trò
CREATE PROCEDURE sp_delete_role(
    IN p_role_id TINYINT UNSIGNED
)
BEGIN
    DECLARE account_count INT;

    -- Kiểm tra vai trò có được sử dụng không
    SELECT COUNT(*) INTO account_count FROM Account WHERE role_id = p_role_id;

    IF account_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa vai trò đang được sử dụng';
    ELSE
        DELETE FROM Role WHERE role_id = p_role_id;
        SELECT ROW_COUNT() > 0 AS success;
    END IF;
END //

-- Lấy tất cả vai trò
CREATE PROCEDURE sp_get_all_roles()
BEGIN
    SELECT * FROM Role ORDER BY role_id;
END //

-- Lấy vai trò theo ID
CREATE PROCEDURE sp_get_role_by_id(
    IN p_role_id TINYINT UNSIGNED
)
BEGIN
    SELECT * FROM Role WHERE role_id = p_role_id;
END //

DELIMITER ;
