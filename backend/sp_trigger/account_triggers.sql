-- 2. Account
DELIMITER //

-- Kiểm tra trước khi thêm tài khoản
CREATE TRIGGER before_account_insert
BEFORE INSERT ON account
FOR EACH ROW
BEGIN
    -- Kiểm tra username có chứa ký tự đặc biệt
    IF LENGTH(TRIM(NEW.username)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đăng nhập không được để trống';
    END IF;

    IF NEW.username REGEXP '^[a-zA-Z0-9_-]{3,20}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đăng nhập chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài từ 3 đến 20 ký tự';
    END IF;

    -- Kiểm tra mật khẩu đã được mã hóa
    IF LENGTH(NEW.password_hash) < 20 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mật khẩu phải được mã hóa trước khi lưu vào cơ sở dữ liệu';
    END IF;

    -- Đặt token_version mặc định
    IF NEW.token_version IS NULL THEN
        SET NEW.token_version = 0;
    END IF;
END //

-- Kiểm tra trước khi cập nhật tài khoản
CREATE TRIGGER before_account_update
BEFORE UPDATE ON account
FOR EACH ROW
BEGIN
    IF LENGTH(TRIM(NEW.username)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đăng nhập không được để trống';
    END IF;

    IF NEW.username REGEXP '^[a-zA-Z0-9_-]{3,20}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đăng nhập chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài từ 3 đến 20 ký tự';
    END IF;

    IF LENGTH(NEW.password_hash) < 20 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mật khẩu phải được mã hóa trước khi lưu vào cơ sở dữ liệu';
    END IF;

    
END //

-- Kiểm tra trước khi xóa tài khoản
CREATE TRIGGER before_account_delete
    BEFORE DELETE ON account
FOR EACH ROW
BEGIN
    DECLARE has_relations BOOLEAN;
    
    SELECT EXISTS(
        SELECT 1 FROM customer WHERE account_id = OLD.account_id
        UNION ALL
        SELECT 1 FROM employee WHERE account_id = OLD.account_id
        UNION ALL
        SELECT 1 FROM manager WHERE account_id = OLD.account_id
    ) INTO has_relations;
    
    IF has_relations THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể xóa tài khoản đã được liên kết, vui lòng xóa người dùng của tài khoản này trước';
    END IF;
END //

DELIMITER ;
