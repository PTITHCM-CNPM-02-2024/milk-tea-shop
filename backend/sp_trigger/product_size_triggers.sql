DELIMITER //

-- Kiểm tra trước khi thêm kích thước sản phẩm
CREATE TRIGGER before_product_size_insert
BEFORE INSERT ON product_size
FOR EACH ROW
BEGIN
    -- Kiểm tra tên kích thước
    IF LENGTH(TRIM(NEW.name)) = 0 OR LENGTH(TRIM(NEW.name)) > 5 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên kích thước phải có độ dài từ 1 đến 5 ký tự';
    END IF;
    
    -- Kiểm tra số lượng
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số lượng phải lớn hơn 0';
    END IF;
END //

-- Kiểm tra trước khi cập nhật kích thước sản phẩm
CREATE TRIGGER before_product_size_update
BEFORE UPDATE ON product_size
FOR EACH ROW
BEGIN
    -- Kiểm tra tên kích thước
    IF LENGTH(TRIM(NEW.name)) = 0 OR LENGTH(TRIM(NEW.name)) > 5 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên kích thước phải có độ dài từ 1 đến 5 ký tự';
    END IF;
    
    -- Kiểm tra số lượng
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số lượng phải lớn hơn 0';
    END IF;
END //

DELIMITER ;