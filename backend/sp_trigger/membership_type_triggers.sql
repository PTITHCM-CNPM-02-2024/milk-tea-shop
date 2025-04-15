DELIMITER //

-- Kiểm tra trước khi thêm membership_type
CREATE TRIGGER before_membership_type_insert
BEFORE INSERT ON membership_type
FOR EACH ROW
BEGIN
    -- Kiểm tra tên loại thành viên
    IF LENGTH(TRIM(NEW.type)) = 0 OR LENGTH(TRIM(NEW.type)) > 50 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên loại thành viên phải có độ dài từ 1 đến 50 ký tự';
    END IF;

    -- Kiểm tra giá trị giảm giá
    IF NEW.discount_unit = 'PERCENTAGE' AND (NEW.discount_value < 0 OR NEW.discount_value > 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá phần trăm phải nằm trong khoảng từ 0 đến 100%';
    ELSEIF NEW.discount_unit = 'FIXED' AND NEW.discount_value <> 0 AND NEW.discount_value < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá cố định phải là 0 hoặc lớn hơn 1000';
    END IF;

    -- Kiểm tra điểm yêu cầu
    IF NEW.required_point < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm yêu cầu không được âm';
    END IF;
END //

-- Kiểm tra trước khi cập nhật membership_type
CREATE TRIGGER before_membership_type_update
BEFORE UPDATE ON membership_type
FOR EACH ROW
BEGIN
    -- Kiểm tra tên loại thành viên
    IF LENGTH(TRIM(NEW.type)) = 0 OR LENGTH(TRIM(NEW.type)) > 50 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên loại thành viên phải có độ dài từ 1 đến 50 ký tự';
    END IF;

    -- Kiểm tra giá trị giảm giá
    IF NEW.discount_unit = 'PERCENTAGE' AND (NEW.discount_value < 0 OR NEW.discount_value > 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá phần trăm phải nằm trong khoảng từ 0 đến 100%';
    ELSEIF NEW.discount_unit = 'FIXED' AND NEW.discount_value <> 0 AND NEW.discount_value < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá cố định phải là 0 hoặc lớn hơn 1000';
    END IF;

    -- Kiểm tra điểm yêu cầu
    IF NEW.required_point < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm yêu cầu không được âm';
    END IF;
END //

DELIMITER ;