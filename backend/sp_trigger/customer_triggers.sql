DELIMITER //

-- Kiểm tra trước khi thêm khách hàng
CREATE TRIGGER before_customer_insert
BEFORE INSERT ON customer
FOR EACH ROW
BEGIN
    -- Kiểm tra họ
    IF NEW.last_name IS NOT NULL AND LENGTH(NEW.last_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra tên
    IF NEW.first_name IS NOT NULL AND LENGTH(NEW.first_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra email
    IF NEW.email IS NOT NULL AND NEW.email REGEXP '[a-zA-Z0-9_!#$%&\'*+/=?\`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;

    -- Kiểm tra điểm hiện tại
    IF NEW.current_points < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm hiện tại không được âm';
    END IF;
END //

-- Kiểm tra trước khi cập nhật khách hàng
CREATE TRIGGER before_customer_update
BEFORE UPDATE ON customer
FOR EACH ROW
BEGIN
    -- Kiểm tra họ
    IF NEW.last_name IS NOT NULL AND LENGTH(NEW.last_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra tên
    IF NEW.first_name IS NOT NULL AND LENGTH(NEW.first_name) > 70 THEN
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

    -- Kiểm tra điểm hiện tại
    IF NEW.current_points < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm hiện tại không được âm';
    END IF;
END //

DELIMITER ;

DELIMITER //

-- Kiểm tra trước khi xóa khách hàng
CREATE TRIGGER before_customer_delete
BEFORE DELETE ON customer
FOR EACH ROW
BEGIN
    -- Kiểm tra xem khách hàng có đang có đơn hàng đang chờ xử lý không
    DECLARE has_relations BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM `order` WHERE customer_id = OLD.customer_id AND status = 'PROCESSING'
    ) INTO has_relations;
    
    IF has_relations THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa khách hàng đã có đơn hàng đang chờ xử lý';
    END IF;
END //

DELIMITER ;

DELIMITER //
-- sau khi xóa khách hàng xóa tài khoản liên kết
CREATE TRIGGER after_customer_delete
AFTER DELETE ON customer
FOR EACH ROW
BEGIN
    -- kiểm tra tài khoản có đang liên kết với khách hàng không
    DECLARE has_relations BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM account WHERE account_id = OLD.account_id
    ) INTO has_relations;

    IF has_relations THEN
        DELETE FROM account WHERE account_id = OLD.account_id;
    END IF;
    
END //

DELIMITER ;
