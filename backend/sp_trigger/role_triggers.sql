DELIMITER //

-- Kiểm tra trước khi thêm vai trò
CREATE TRIGGER before_role_insert
BEFORE INSERT ON role
FOR EACH ROW
BEGIN
    IF NEW.name REGEXP '^[a-zA-Z0-9_]{3,20}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên vai trò phải từ 3 đến 20 ký tự và không được chứa ký tự đặc biệt';
    END IF;
END //

-- Kiểm tra trước khi cập nhật vai trò
CREATE TRIGGER before_role_update
BEFORE UPDATE ON role
FOR EACH ROW
BEGIN
    IF NEW.name REGEXP '^[a-zA-Z0-9_]{3,20}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên vai trò phải từ 3 đến 20 ký tự và không được chứa ký tự đặc biệt';
    END IF;
END //

-- Kiểm tra trước khi xóa vai trò
CREATE TRIGGER before_role_delete
BEFORE DELETE ON role
FOR EACH ROW
BEGIN
END //

DELIMITER ;

