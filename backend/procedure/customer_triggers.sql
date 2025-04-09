DELIMITER //

-- Kiểm tra trước khi thêm khách hàng
CREATE TRIGGER before_customer_insert
BEFORE INSERT ON Customer
FOR EACH ROW
BEGIN
    DECLARE account_used BOOLEAN;
    -- Kiểm tra số điện thoại hợp lệ
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra email hợp lệ nếu có
    IF NEW.email REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;

    -- Đặt điểm mặc định
    IF NEW.current_points IS NULL THEN
        SET NEW.current_points = 0;
    END IF;

    -- Kiểm tra account_id không thuộc manager hoặc employee

    IF NEW.account_id IS NOT NULL THEN
        SELECT EXISTS(SELECT 1 FROM Manager WHERE account_id = NEW.account_id) INTO account_used;
        SELECT EXISTS(SELECT 1 FROM Employee WHERE account_id = NEW.account_id) INTO account_used;
        
        IF account_used THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Tài khoản đã được sử dụng';
        END IF;
    END IF;


END //

CREATE TRIGGER before_customer_update
BEFORE UPDATE ON Customer
FOR EACH ROW
BEGIN
    DECLARE account_used BOOLEAN;
    -- Kiểm tra account_id không thuộc manager hoặc employee
    IF NEW.account_id IS NOT NULL AND NEW.account_id <> OLD.account_id THEN
        SELECT EXISTS(SELECT 1 FROM Manager WHERE account_id = NEW.account_id) INTO account_used;
        SELECT EXISTS(SELECT 1 FROM Employee WHERE account_id = NEW.account_id) INTO account_used;
        
        
    END IF;

    -- Kiểm tra số điện thoại hợp lệ
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;
    
    

    -- Kiểm tra email hợp lệ nếu có
    IF NEW.email REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;
    
    -- Đặt điểm mặc định
    IF NEW.current_points IS NULL THEN
        SET NEW.current_points = 0;
    END IF;

    -- Kiểm tra account_id không thuộc manager hoặc employee
    IF NEW.account_id IS NOT NULL AND NEW.account_id <> OLD.account_id THEN
        SELECT EXISTS(SELECT 1 FROM Manager WHERE account_id = NEW.account_id) INTO account_used;
        SELECT EXISTS(SELECT 1 FROM Employee WHERE account_id = NEW.account_id) INTO account_used;
        
        
    END IF;


END //

-- Tự động cập nhật loại thành viên sau khi cập nhật điểm
CREATE TRIGGER after_customer_update
AFTER UPDATE ON Customer
FOR EACH ROW
BEGIN
    DECLARE new_membership_type_id TINYINT UNSIGNED;
    -- Chỉ thực hiện khi điểm thay đổi
    IF NEW.current_points != OLD.current_points THEN
        -- Tìm loại thành viên phù hợp với số điểm mới
        SELECT membership_type_id INTO new_membership_type_id
        FROM MembershipType
        WHERE required_point <= NEW.current_points
        ORDER BY required_point DESC
        LIMIT 1;

        -- Cập nhật loại thành viên nếu khác
        IF new_membership_type_id IS NOT NULL AND new_membership_type_id != NEW.membership_type_id THEN
            UPDATE Customer
            SET membership_type_id = new_membership_type_id
            WHERE customer_id = NEW.customer_id;
        END IF;
    END IF;


END //



-- Trigger sau khi xóa khách hàng, xóa tài khoản của khách hàng
CREATE TRIGGER after_customer_delete
AFTER DELETE ON Customer
FOR EACH ROW
BEGIN
    DELETE FROM Account WHERE account_id = OLD.account_id;
END //

DELIMITER ;