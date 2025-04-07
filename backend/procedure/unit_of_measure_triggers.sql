DELIMITER //

-- Kiểm tra trước khi thêm đơn vị tính
CREATE TRIGGER before_unit_of_measure_insert
BEFORE INSERT ON UnitOfMeasure
FOR EACH ROW
BEGIN
    -- Kiểm tra dữ liệu không rỗng nhưng định dạng không đúng
    IF LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên đơn vị không được để trống';
    END IF;
    
    IF LENGTH(TRIM(NEW.symbol)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ký hiệu đơn vị không được để trống';
    END IF;
END //

-- Kiểm tra trước khi cập nhật đơn vị tính
CREATE TRIGGER before_unit_of_measure_update
BEFORE UPDATE ON UnitOfMeasure
FOR EACH ROW
BEGIN
    -- Kiểm tra dữ liệu không rỗng nhưng định dạng không đúng
    IF LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên đơn vị không được để trống';
    END IF;
    
    IF LENGTH(TRIM(NEW.symbol)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ký hiệu đơn vị không được để trống';
    END IF;
END //

-- Kiểm tra trước khi xóa đơn vị tính
CREATE TRIGGER before_unit_of_measure_delete
BEFORE DELETE ON UnitOfMeasure
FOR EACH ROW
BEGIN
    DECLARE product_size_count INT;
    SELECT COUNT(*) INTO product_size_count 
    FROM ProductSize 
    WHERE unit_id = OLD.unit_id;
    
    IF product_size_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể xóa đơn vị tính đang được sử dụng bởi kích thước sản phẩm';
    END IF;
END //

DELIMITER ;