DELIMITER //

-- Kiểm tra trước khi thêm đơn vị tính
CREATE TRIGGER before_unit_of_measure_insert
BEFORE INSERT ON unit_of_measure
FOR EACH ROW
BEGIN
    -- Kiểm tra dữ liệu không rỗng nhưng định dạng không đúng
    IF LENGTH(TRIM(NEW.name)) < 1 OR LENGTH(TRIM(NEW.name)) > 30 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên đơn vị phải có độ dài từ 1 đến 30 ký tự';
    END IF;
    
    IF LENGTH(TRIM(NEW.symbol)) < 1 OR LENGTH(TRIM(NEW.symbol)) > 5 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ký hiệu đơn vị phải có độ dài từ 1 đến 5 ký tự';
    END IF;
END //

-- Kiểm tra trước khi cập nhật đơn vị tính
CREATE TRIGGER before_unit_of_measure_update
BEFORE UPDATE ON unit_of_measure
FOR EACH ROW
BEGIN
    -- Kiểm tra dữ liệu không rỗng nhưng định dạng không đúng
    IF LENGTH(TRIM(NEW.name)) < 1 OR LENGTH(TRIM(NEW.name)) > 30 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên đơn vị phải có độ dài từ 1 đến 30 ký tự';
    END IF;
    
    IF LENGTH(TRIM(NEW.symbol)) < 1 OR LENGTH(TRIM(NEW.symbol)) > 5 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ký hiệu đơn vị phải có độ dài từ 1 đến 5 ký tự';
    END IF;
END //

-- Kiểm tra trước khi xóa đơn vị tính
CREATE TRIGGER before_unit_of_measure_delete
BEFORE DELETE ON unit_of_measure
FOR EACH ROW
BEGIN
END //

DELIMITER ;