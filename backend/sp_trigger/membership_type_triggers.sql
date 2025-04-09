DELIMITER //

-- Kiểm tra trước khi xóa loại thành viên
CREATE TRIGGER before_membership_type_delete
BEFORE DELETE ON MembershipType
FOR EACH ROW
BEGIN

END //

-- Before Insert Trigger
CREATE TRIGGER before_membership_type_insert
BEFORE INSERT ON MembershipType
FOR EACH ROW
BEGIN
    
    -- Kiểm tra giới hạn phần trăm giảm giá
    IF NEW.discount_unit = 'PERCENTAGE' AND (NEW.discount_value < 0 OR NEW.discount_value > 100) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Phần trăm giảm giá phải lớn hơn 0 và nhỏ hơn 100%';
    END IF;

    
    -- Kiểm tra giới hạn tiền cố định giảm giá
    IF NEW.discount_unit = 'FIXED' AND NEW.discount_value <= 1000 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tiền cố định giảm giá phải lớn hơn 1000 VNĐ';
    END IF;
    
    -- Kiểm tra ngày hết hạn phải sau ngày hiện tại nếu được cung cấp
    IF NEW.valid_until IS NOT NULL AND NEW.valid_until <= CURRENT_TIMESTAMP THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày hết hạn phải sau thời điểm hiện tại';
    END IF;

    -- Kiểm tra điểm yêu cầu không âm
    IF NEW.required_point < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Điểm yêu cầu không được âm';
    END IF;

    -- Kiểm tra tên loại thành viên không được để trống
    IF LENGTH(TRIM(NEW.type)) = 0 OR LENGTH(TRIM(NEW.type)) > 50 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên loại thành viên không được để trống và có độ dài tối đa 50 ký tự';
    END IF;

END //

-- Before Update Trigger
CREATE TRIGGER before_membership_type_update
BEFORE UPDATE ON MembershipType
FOR EACH ROW
BEGIN

    -- Kiểm tra giới hạn phần trăm giảm giá
    IF NEW.discount_unit = 'PERCENTAGE' AND (NEW.discount_value < 0 OR NEW.discount_value > 100) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Phần trăm giảm giá phải lớn hơn 0 và nhỏ hơn 100%';
    END IF;

    -- Kiểm tra giới hạn tiền cố định giảm giá
    IF NEW.discount_unit = 'FIXED' AND NEW.discount_value <= 1000 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tiền cố định giảm giá phải lớn hơn 1000 VNĐ';
    END IF;

    -- Kiểm tra ngày hết hạn phải sau ngày hiện tại nếu được cung cấp
    IF NEW.valid_until IS NOT NULL AND NEW.valid_until <= CURRENT_TIMESTAMP THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày hết hạn phải sau thời điểm hiện tại';
    END IF;

    -- Kiểm tra điểm yêu cầu không âm
    IF NEW.required_point < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Điểm yêu cầu không được âm';
    END IF;
    
    -- Kiểm tra tên loại thành viên không được để trống
    IF LENGTH(TRIM(NEW.type)) = 0 OR LENGTH(TRIM(NEW.type)) > 50 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên loại thành viên không được để trống và có độ dài tối đa 50 ký tự';
    END IF;
    
END //

-- Trigger sau khi cập nhật loại thành viên
CREATE TRIGGER after_membership_type_update
AFTER UPDATE ON MembershipType
FOR EACH ROW
BEGIN
    -- Không nên tự động cập nhật điểm của khách hàng khi thay đổi điểm yêu cầu
    -- Thay vào đó nên để khách hàng tích điểm tự nhiên
    -- Khi khách hàng đạt đủ điểm mới, trigger after_customer_update sẽ tự động cập nhật loại thành viên
END //

    DELIMITER ;