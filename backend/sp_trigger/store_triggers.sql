DELIMITER //

-- Before Insert Trigger
CREATE TRIGGER before_store_insert
BEFORE INSERT ON Store
FOR EACH ROW
BEGIN
    -- Kiểm tra giờ mở cửa phải trước giờ đóng cửa
    IF NEW.opening_time >= NEW.closing_time THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giờ mở cửa phải trước giờ đóng cửa';
    END IF;
    
    -- Kiểm tra định dạng email hợp lệ
    IF NEW.email NOT REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Định dạng email không hợp lệ';
    END IF;
    
    -- Kiểm tra số điện thoại hợp lệ
    IF NEW.phone NOT REGEXP '^[0-9]{10,15}$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;
END //

-- Before Update Trigger
CREATE TRIGGER before_store_update
BEFORE UPDATE ON Store
FOR EACH ROW
BEGIN
    -- Kiểm tra giờ mở cửa phải trước giờ đóng cửa
    IF NEW.opening_time >= NEW.closing_time THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giờ mở cửa phải trước giờ đóng cửa';
    END IF;
    
    -- Kiểm tra định dạng email hợp lệ
    IF NEW.email NOT REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Định dạng email không hợp lệ';
    END IF;
    
    -- Kiểm tra số điện thoại hợp lệ
    IF NEW.phone NOT REGEXP '^[0-9]{10,15}$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;
END //

DELIMITER ; 