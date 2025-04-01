DELIMITER //

-- Kiểm tra trước khi thêm vai trò
CREATE TRIGGER before_role_insert
    BEFORE INSERT ON Role
    FOR EACH ROW
BEGIN
    IF NEW.name IS NULL OR LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên vai trò không được để trống';
    END IF;
END //

-- Kiểm tra trước khi cập nhật vai trò
CREATE TRIGGER before_role_update
    BEFORE UPDATE ON Role
    FOR EACH ROW
BEGIN
    IF NEW.name IS NULL OR LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên vai trò không được để trống';
    END IF;
END //

DELIMITER ;

