DELIMITER //

-- Kiểm tra trước khi thêm giá sản phẩm
CREATE TRIGGER before_product_price_insert
BEFORE INSERT ON ProductPrice
FOR EACH ROW
BEGIN
    -- Kiểm tra giá sản phẩm
    IF NEW.price < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giá sản phẩm không được âm';
    END IF;
END //

-- Kiểm tra trước khi cập nhật giá sản phẩm
CREATE TRIGGER before_product_price_update
BEFORE UPDATE ON ProductPrice
FOR EACH ROW
BEGIN
    -- Kiểm tra giá sản phẩm
    IF NEW.price < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giá sản phẩm không được âm';
    END IF;
END //

DELIMITER ; 