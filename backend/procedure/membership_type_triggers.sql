DELIMITER //

-- Kiểm tra trước khi xóa loại thành viên
CREATE TRIGGER before_membership_type_delete
BEFORE DELETE ON MembershipType
FOR EACH ROW
BEGIN
    DECLARE customer_count INT;
    
    SELECT COUNT(*) INTO customer_count
    FROM Customer
    WHERE membership_type_id = OLD.membership_type_id;
    
    IF customer_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể xóa loại thành viên đang được sử dụng bởi khách hàng';
    END IF;
END //

-- Before Insert Trigger
CREATE TRIGGER before_membership_type_insert
BEFORE INSERT ON MembershipType
FOR EACH ROW
BEGIN
    -- Kiểm tra giá trị giảm giá hợp lệ
    IF NEW.discount_value < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giá trị giảm giá không được âm';
    END IF;
    
    -- Kiểm tra giới hạn phần trăm giảm giá
    IF NEW.discount_unit = 'PERCENTAGE' AND NEW.discount_value > 100 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Phần trăm giảm giá không được vượt quá 100%';
    END IF;
    
    -- Kiểm tra điểm yêu cầu không âm
    IF NEW.required_point < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Điểm yêu cầu không được âm';
    END IF;
    
    -- Kiểm tra ngày hết hạn phải sau ngày hiện tại nếu được cung cấp
    IF NEW.valid_until IS NOT NULL AND NEW.valid_until <= CURRENT_TIMESTAMP THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày hết hạn phải sau thời điểm hiện tại';
    END IF;
END //

-- Before Update Trigger
CREATE TRIGGER before_membership_type_update
BEFORE UPDATE ON MembershipType
FOR EACH ROW
BEGIN
    -- Kiểm tra giá trị giảm giá hợp lệ
    IF NEW.discount_value < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giá trị giảm giá không được âm';
    END IF;
    
    -- Kiểm tra giới hạn phần trăm giảm giá
    IF NEW.discount_unit = 'PERCENTAGE' AND NEW.discount_value > 100 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Phần trăm giảm giá không được vượt quá 100%';
    END IF;
    
    -- Kiểm tra điểm yêu cầu không âm
    IF NEW.required_point < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Điểm yêu cầu không được âm';
    END IF;
    
    -- Kiểm tra ngày hết hạn phải sau ngày hiện tại nếu được cung cấp và đã thay đổi
    IF NEW.valid_until IS NOT NULL AND NEW.valid_until <= CURRENT_TIMESTAMP 
       AND (OLD.valid_until IS NULL OR NEW.valid_until != OLD.valid_until) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày hết hạn phải sau thời điểm hiện tại';
    END IF;
END //

DELIMITER ;
