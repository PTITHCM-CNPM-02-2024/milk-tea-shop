DELIMITER //
-- Thêm tài khoản
CREATE PROCEDURE sp_insert_account(
    IN p_role_id TINYINT UNSIGNED,
    IN p_username VARCHAR(50),
    IN p_password_hash VARCHAR(255),
    IN p_is_active TINYINT(1)
)
BEGIN
    INSERT INTO Account(role_id, username, password_hash, is_active, is_locked, token_version)
    VALUES(p_role_id, p_username, p_password_hash, p_is_active, 0, 0);
    SELECT LAST_INSERT_ID() AS account_id;
END //

-- Cập nhật tài khoản
CREATE PROCEDURE sp_update_account(
    IN p_account_id INT UNSIGNED,
    IN p_role_id TINYINT UNSIGNED,
    IN p_is_active TINYINT(1),
    IN p_is_locked TINYINT(1)
)
BEGIN
    UPDATE Account
    SET role_id = p_role_id,
        is_active = p_is_active,
        is_locked = p_is_locked,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Đổi mật khẩu
CREATE PROCEDURE sp_change_password(
    IN p_account_id INT UNSIGNED,
    IN p_new_password_hash VARCHAR(255)
)
BEGIN
    UPDATE Account
    SET password_hash = p_new_password_hash,
        token_version = token_version + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa tài khoản
CREATE PROCEDURE sp_delete_account(
    IN p_account_id INT UNSIGNED
)
BEGIN
    DECLARE has_relations BOOLEAN;

    -- Kiểm tra tài khoản có được liên kết không
    SELECT EXISTS(
        SELECT 1 FROM Customer WHERE account_id = p_account_id
        UNION ALL
        SELECT 1 FROM Employee WHERE account_id = p_account_id
        UNION ALL
        SELECT 1 FROM Manager WHERE account_id = p_account_id
    ) INTO has_relations;

    IF has_relations THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa tài khoản đang được liên kết';
    ELSE
        DELETE FROM Account WHERE account_id = p_account_id;
        SELECT ROW_COUNT() > 0 AS success;
    END IF;
END //

-- Lấy tài khoản theo ID
CREATE PROCEDURE sp_get_account_by_id(
    IN p_account_id INT UNSIGNED
)
BEGIN
    SELECT a.*, r.name AS role_name
    FROM Account a
             JOIN Role r ON a.role_id = r.role_id
    WHERE a.account_id = p_account_id;
END //

-- Lấy tài khoản theo username
CREATE PROCEDURE sp_get_account_by_username(
    IN p_username VARCHAR(50)
)
BEGIN
    SELECT a.*, r.name AS role_name
    FROM Account a
             JOIN Role r ON a.role_id = r.role_id
    WHERE a.username = p_username;
END //

-- Lấy tất cả tài khoản
CREATE PROCEDURE sp_get_all_accounts()
BEGIN
    SELECT a.*, r.name AS role_name
    FROM Account a
             JOIN Role r ON a.role_id = r.role_id
    ORDER BY a.account_id;
END //

DELIMITER ;
