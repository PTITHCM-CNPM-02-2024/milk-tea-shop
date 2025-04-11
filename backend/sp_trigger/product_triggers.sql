DELIMITER //

-- Kiểm tra trước khi thêm sản phẩm
CREATE TRIGGER before_product_insert
BEFORE INSERT ON Product
FOR EACH ROW
BEGIN
    -- Kiểm tra tên sản phẩm có trống không
    IF LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên sản phẩm không được để trống';
    END IF;
    
    -- Đặt giá trị mặc định cho is_available
    IF NEW.is_available IS NULL THEN
        SET NEW.is_available = 1;
    END IF;
    
    -- Đặt giá trị mặc định cho is_signature
    IF NEW.is_signature IS NULL THEN
        SET NEW.is_signature = 0;
    END IF;
END //

-- Kiểm tra trước khi cập nhật sản phẩm
CREATE TRIGGER before_product_update
BEFORE UPDATE ON Product
FOR EACH ROW
BEGIN
    -- Kiểm tra tên sản phẩm
    IF LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên sản phẩm không được để trống';
    END IF;
END //

DELIMITER ;