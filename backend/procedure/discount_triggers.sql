-- 13. Discount

DELIMITER //

-- Trigger trước khi thêm mới discount
CREATE TRIGGER before_discount_insert
    BEFORE INSERT ON Discount
    FOR EACH ROW
BEGIN
    -- Kiểm tra ngày hợp lệ
    IF NEW.valid_until <= NOW() THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời gian kết thúc khuyến mãi phải sau thời điểm hiện tại';
    END IF;


    IF NEW.valid_from is not null and NEW.valid_until is not null and NEW.valid_from >= NEW.valid_until THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời gian bắt đầu khuyến mãi phải trước thời gian kết thúc';
    END IF; 

    -- Kiểm tra giá trị discount
    IF NEW.discount_type = 'PERCENTAGE' AND (NEW.discount_value <= 0 OR NEW.discount_value > 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá phần trăm phải nằm trong khoảng 0-100%';
    ELSEIF NEW.discount_type = 'FIXED' AND NEW.discount_value <= 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá cố định phải lớn hơn 1000 VNĐ';
    END IF;

    -- Đặt giá trị mặc định cho current_uses
    IF NEW.current_uses IS NULL THEN
        SET NEW.current_uses = 0;
    END IF;
END//

-- Trigger trước khi cập nhật discount
CREATE TRIGGER before_discount_update
    BEFORE UPDATE ON Discount
    FOR EACH ROW
BEGIN
    -- Kiểm tra ngày hợp lệ
    IF NEW.valid_until <= NOW() AND NEW.valid_until != OLD.valid_until THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời gian kết thúc khuyến mãi phải sau thời điểm hiện tại';
    END IF;

    IF NEW.valid_from is not null and NEW.valid_until is not null and NEW.valid_from >= NEW.valid_until THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời gian bắt đầu khuyến mãi phải trước thời gian kết thúc';
    END IF;

    -- Kiểm tra giá trị discount
    IF NEW.discount_type = 'PERCENTAGE' AND (NEW.discount_value <= 0 OR NEW.discount_value > 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá phần trăm phải nằm trong khoảng 0-100%';
    ELSEIF NEW.discount_type = 'FIXED' AND NEW.discount_value <= 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá cố định phải lớn hơn 1000 VNĐ';
    END IF;
END//

-- Trigger trước khi xóa discount
CREATE TRIGGER before_discount_delete
BEFORE DELETE ON Discount
FOR EACH ROW
BEGIN
    DECLARE order_discount_count INT;
    
    SELECT COUNT(*) INTO order_discount_count
    FROM OrderDiscount od INNER JOIN `Order` o ON od.order_id = o.order_id
    WHERE od.discount_id = OLD.discount_id AND o.status = 'PROCESSING';
    WHERE discount_id = OLD.discount_id;
    
    IF order_discount_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể xóa chương trình giảm giá đang được sử dụng trong đơn hàng';
    END IF;
END //

DELIMITER ;
