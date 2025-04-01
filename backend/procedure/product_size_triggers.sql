DELIMITER //

-- Kiểm tra trước khi thêm kích thước sản phẩm
CREATE TRIGGER before_product_size_insert
BEFORE INSERT ON ProductSize
FOR EACH ROW
BEGIN
    -- Kiểm tra tên kích thước
    IF NEW.name IS NULL OR LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên kích thước không được để trống';
    END IF;
    
    -- Kiểm tra số lượng
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số lượng phải lớn hơn 0';
    END IF;
END //

-- Kiểm tra trước khi cập nhật kích thước sản phẩm
CREATE TRIGGER before_product_size_update
BEFORE UPDATE ON ProductSize
FOR EACH ROW
BEGIN
    -- Kiểm tra tên kích thước
    IF NEW.name IS NULL OR LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên kích thước không được để trống';
    END IF;
    
    -- Kiểm tra số lượng
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số lượng phải lớn hơn 0';
    END IF;
END //

DELIMITER ; 