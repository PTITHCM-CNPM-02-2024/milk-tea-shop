DELIMITER //

-- Kiểm tra trước khi thêm khách hàng
CREATE TRIGGER before_customer_insert
    BEFORE INSERT ON Customer
    FOR EACH ROW
BEGIN
    -- Kiểm tra số điện thoại hợp lệ
    IF NEW.phone IS NULL OR LENGTH(TRIM(NEW.phone)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không được để trống';
    ELSEIF NEW.phone NOT REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\d]+|$)' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại chỉ được chứa các chữ số';
    END IF;

    -- Kiểm tra email hợp lệ nếu có
    IF NEW.email IS NOT NULL AND NEW.email != '' AND NEW.email NOT REGEXP '^[a-zA-Z0-9_!#$%&''*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Định dạng email không hợp lệ';
    END IF;

    -- Đặt điểm mặc định
    IF NEW.current_points IS NULL THEN
        SET NEW.current_points = 0;
    END IF;
END //

-- Tự động cập nhật loại thành viên sau khi cập nhật điểm
CREATE TRIGGER after_customer_points_update
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

DELIMITER ;