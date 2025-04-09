-- 5. Manager
DELIMITER //

-- Kiểm tra trước khi thêm quản lý
CREATE TRIGGER before_manager_insert
BEFORE INSERT ON Manager
FOR EACH ROW
BEGIN
    -- Kiểm tra tên và họ
    IF LENGTH(TRIM(NEW.first_name)) = 0 OR LENGTH(TRIM(NEW.first_name)) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được để trống và có độ dài tối đa 70 ký tự';
    END IF;

    IF LENGTH(TRIM(NEW.last_name)) = 0 OR LENGTH(TRIM(NEW.last_name)) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được để trống và có độ dài tối đa 70 ký tự ';
    END IF;

    -- Kiểm tra email
    IF NEW.email REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

END //

-- Kiểm tra trước khi cập nhật quản lý
CREATE TRIGGER before_manager_update
BEFORE UPDATE ON Manager
FOR EACH ROW
BEGIN
    -- Kiểm tra tên và họ
    IF LENGTH(TRIM(NEW.first_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được để trống';
    END IF;

    IF LENGTH(TRIM(NEW.last_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được để trống';
    END IF;

    -- Kiểm tra email
    IF LENGTH(TRIM(NEW.email)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không được để trống';
    ELSEIF NEW.email NOT REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;

    -- Kiểm tra số điện thoại
    IF LENGTH(TRIM(NEW.phone)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không được để trống';
    ELSEIF NEW.phone NOT REGEXP '^[0-9]{10,15}$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra account_id duy nhất
    IF NEW.account_id IS NOT NULL AND (OLD.account_id IS NULL OR NEW.account_id != OLD.account_id) THEN
        DECLARE account_used BOOLEAN;
        SELECT EXISTS(SELECT 1 FROM Manager WHERE account_id = NEW.account_id AND manager_id != NEW.manager_id) INTO account_used;

        IF account_used THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Tài khoản đã được liên kết với quản lý khác';
        END IF;
    END IF;
END //

-- Kiểm tra trước khi xóa quản lý
CREATE TRIGGER before_manager_delete
BEFORE DELETE ON Manager
FOR EACH ROW
BEGIN
    -- Thực hiện kiểm tra trước khi xóa quản lý nếu cần
    -- Hiện tại quản lý chưa liên kết với bảng khác nên không cần kiểm tra
END //

DELIMITER ;