DELIMITER //

-- Kiểm tra trước khi thêm vai trò
CREATE TRIGGER before_role_insert
BEFORE INSERT ON Role
FOR EACH ROW
BEGIN
    IF LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên vai trò không được để trống';
    END IF;
END //

-- Kiểm tra trước khi cập nhật vai trò
CREATE TRIGGER before_role_update
BEFORE UPDATE ON Role
FOR EACH ROW
BEGIN
    IF LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên vai trò không được để trống';
    END IF;
END //

-- Kiểm tra trước khi xóa vai trò
CREATE TRIGGER before_role_delete
BEFORE DELETE ON Role
FOR EACH ROW
BEGIN
    DECLARE account_count INT;
    
    SELECT COUNT(*) INTO account_count
    FROM Account
    WHERE role_id = OLD.role_id;
    
    IF account_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể xóa vai trò đang được sử dụng bởi tài khoản';
    END IF;
END //

DELIMITER ;

