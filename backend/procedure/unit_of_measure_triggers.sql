DELIMITER //

-- Kiểm tra trước khi thêm đơn vị tính
CREATE TRIGGER before_unit_of_measure_insert
BEFORE INSERT ON UnitOfMeasure
FOR EACH ROW
BEGIN
    -- Kiểm tra tên đơn vị
    IF NEW.name IS NULL OR LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên đơn vị không được để trống';
    END IF;
    
    -- Kiểm tra ký hiệu đơn vị
    IF NEW.symbol IS NULL OR LENGTH(TRIM(NEW.symbol)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ký hiệu đơn vị không được để trống';
    END IF;
END //

-- Kiểm tra trước khi cập nhật đơn vị tính
CREATE TRIGGER before_unit_of_measure_update
BEFORE UPDATE ON UnitOfMeasure
FOR EACH ROW
BEGIN
    -- Kiểm tra tên đơn vị
    IF NEW.name IS NULL OR LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên đơn vị không được để trống';
    END IF;
    
    -- Kiểm tra ký hiệu đơn vị
    IF NEW.symbol IS NULL OR LENGTH(TRIM(NEW.symbol)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ký hiệu đơn vị không được để trống';
    END IF;
END //

DELIMITER ; 