
-- 2. Account
DELIMITER //

-- Kiểm tra trước khi thêm tài khoản
CREATE TRIGGER before_account_insert
    BEFORE INSERT ON Account
    FOR EACH ROW
BEGIN
    -- Kiểm tra username
    IF NEW.username IS NULL OR LENGTH(TRIM(NEW.username)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đăng nhập không được để trống';
    END IF;

    -- Kiểm tra username có chứa ký tự đặc biệt
    IF NEW.username REGEXP '^[a-zA-Z0-9_-]{3,20}$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đăng nhập chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài từ 3 đến 20 ký tự';
    END IF;

    -- Kiểm tra mật khẩu đã được mã hóa
    IF NEW.password_hash IS NULL OR LENGTH(NEW.password_hash) < 20 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mật khẩu phải được mã hóa trước khi lưu vào cơ sở dữ liệu';
    END IF;

    -- Đặt token_version mặc định
    IF NEW.token_version IS NULL THEN
        SET NEW.token_version = 0;
    END IF;
END //

-- Kiểm tra trước khi cập nhật tài khoản, KHÔNG SỬ DỤNG. 
CREATE TRIGGER before_account_update
    BEFORE UPDATE ON Account
    FOR EACH ROW
BEGIN
    -- Cập nhật token_version khi đổi mật khẩu
    IF OLD.password_hash != NEW.password_hash THEN
        SET NEW.token_version = OLD.token_version + 1;
    END IF;

    -- Cập nhật last_login
    IF OLD.last_login != NEW.last_login AND NEW.last_login IS NOT NULL THEN
        SET NEW.updated_at = CURRENT_TIMESTAMP;
    END IF;
END //

DELIMITER ;
