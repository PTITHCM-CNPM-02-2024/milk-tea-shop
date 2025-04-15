DELIMITER //

-- Kiểm tra trước khi thêm sản phẩm vào đơn hàng
CREATE TRIGGER before_order_product_insert
BEFORE INSERT ON order_product
FOR EACH ROW
BEGIN

    -- đơn hàng phải ở trạng thái processing
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể thêm sản phẩm vào đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra số lượng phải lớn hơn 0
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số lượng sản phẩm phải lớn hơn 0';
    END IF;
END //

-- Kiểm tra trước khi cập nhật sản phẩm trong đơn hàng
CREATE TRIGGER before_order_product_update
BEFORE UPDATE ON order_product
FOR EACH ROW
BEGIN

    -- Kiểm tra xem order ở trạng thái khác PROCESSING thì không được cập nhật
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;

    IF NEW.order_id IS NOT NULL AND NEW.order_id <> OLD.order_id THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể cập nhật số lượng sản phẩm cho đơn hàng khác';
    END IF;

    -- Kiểm tra xem order ở trạng thái khác PROCESSING thì không được cập nhật
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không được cập nhật số lượng sản phẩm cho đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra số lượng phải lớn hơn 0
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số lượng sản phẩm phải lớn hơn 0';
    END IF;
END //

-- Cập nhật tổng tiền sau khi thêm sản phẩm
CREATE TRIGGER after_order_product_insert
AFTER INSERT ON order_product
FOR EACH ROW
BEGIN

END //

-- Cập nhật tổng tiền sau khi cập nhật sản phẩm
CREATE TRIGGER after_order_product_update
AFTER UPDATE ON order_product
FOR EACH ROW
BEGIN

END //

-- Cập nhật tổng tiền sau khi xóa sản phẩm
CREATE TRIGGER after_order_product_delete
AFTER DELETE ON order_product
FOR EACH ROW
BEGIN
    -- Cập nhật tổng tiền đơn hàng
END //

DELIMITER ;