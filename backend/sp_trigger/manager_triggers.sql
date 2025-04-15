DELIMITER //

-- Kiểm tra trước khi thêm quản lý
CREATE TRIGGER before_manager_insert
BEFORE INSERT ON manager
FOR EACH ROW
BEGIN
    -- Kiểm tra họ
    IF LENGTH(NEW.last_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra tên
    IF LENGTH(NEW.first_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra email
    IF NEW.email REGEXP '^[a-zA-Z0-9_!#$%&\'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;
END //

-- Kiểm tra trước khi cập nhật quản lý
CREATE TRIGGER before_manager_update
BEFORE UPDATE ON manager
FOR EACH ROW
BEGIN
    -- Kiểm tra họ
    IF LENGTH(NEW.last_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra tên
    IF LENGTH(NEW.first_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra email
    IF NEW.email REGEXP '^[a-zA-Z0-9_!#$%&\'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;
END //


-- Kiểm tra trước khi xóa quản lý
CREATE TRIGGER after_manager_delete
AFTER DELETE ON manager
FOR EACH ROW
BEGIN
    -- kiểm tra tài khoản có đang liên kết với quản lý không
    DECLARE has_relations BOOLEAN;
    
    SELECT EXISTS(
        SELECT 1 FROM account WHERE account_id = OLD.account_id
    ) INTO has_relations;

    IF has_relations THEN
        DELETE FROM account WHERE account_id = OLD.account_id;
    END IF;
END //

DELIMITER ;
