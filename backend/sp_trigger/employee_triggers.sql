DELIMITER //

-- Kiểm tra trước khi thêm nhân viên
CREATE TRIGGER before_employee_insert
BEFORE INSERT ON employee
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
    IF NEW.email IS NOT NULL AND NEW.email REGEXP '[a-zA-Z0-9_!#$%&\'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;
END //

-- Kiểm tra trước khi cập nhật nhân viên
CREATE TRIGGER before_employee_update
BEFORE UPDATE ON employee
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
    IF NEW.email IS NOT NULL AND NEW.email REGEXP '[a-zA-Z0-9_!#$%&\'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;
END //


-- Kiểm tra trước khi xóa nhân viên
CREATE TRIGGER before_employee_delete
BEFORE DELETE ON employee
FOR EACH ROW
BEGIN
    -- Kiểm tra xem nhân viên có đang có đơn hàng đang chờ xử lý không
    DECLARE has_relations BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM `order` WHERE employee_id = OLD.employee_id AND status = 'PROCESSING'
    ) INTO has_relations;

    IF has_relations THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa nhân viên đang có đơn hàng đang chờ xử lý';
    END IF;
END //


-- sau khi xóa nhân viên xóa tài khoản liên kết
CREATE TRIGGER after_employee_delete
AFTER DELETE ON employee
FOR EACH ROW
BEGIN
    -- kiểm tra tài khoản có đang liên kết với nhân viên không
    DECLARE has_relations BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM account WHERE account_id = OLD.account_id
    ) INTO has_relations;
    
    IF has_relations THEN
        DELETE FROM account WHERE account_id = OLD.account_id;
    END IF;
END //

DELIMITER ;