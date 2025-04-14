DELIMITER //

-- Kiểm tra trước khi thêm giá sản phẩm
CREATE TRIGGER before_product_price_insert
BEFORE INSERT ON product_price
FOR EACH ROW
BEGIN
    -- Kiểm tra giá sản phẩm
    IF NEW.price < 1000 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giá sản phẩm phải lớn hơn 1000';
    END IF;
END //

-- Kiểm tra trước khi cập nhật giá sản phẩm
CREATE TRIGGER before_product_price_update
BEFORE UPDATE ON product_price
FOR EACH ROW
BEGIN
    -- Kiểm tra giá sản phẩm
    IF NEW.price < 1000 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giá sản phẩm phải lớn hơn 1000';
    END IF;
END //

-- Kiểm tra trước khi xóa giá sản phẩm
CREATE TRIGGER before_product_price_delete
BEFORE DELETE ON product_price
FOR EACH ROW
BEGIN
    DECLARE order_product_exists BOOLEAN;
    
    SELECT EXISTS(
        SELECT 1 FROM order_product op INNER JOIN `order` o ON op.order_id = o.order_id
        WHERE product_price_id = OLD.product_price_id AND o.status = 'PROCESSING'
    ) INTO order_product_exists;
    
    -- Kiểm tra xem giá sản phẩm có được sử dụng trong đơn hàng không
    
    IF order_product_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể xóa giá sản phẩm đang được sử dụng trong đơn hàng';
    END IF;
END //

DELIMITER ;