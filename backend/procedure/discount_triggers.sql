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

    -- Kiểm tra giá trị discount
    IF NEW.discount_type = 'PERCENTAGE' AND (NEW.discount_value <= 0 OR NEW.discount_value > 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá phần trăm phải nằm trong khoảng 0-100%';
    ELSEIF NEW.discount_type = 'FIXED' AND NEW.discount_value <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá cố định phải lớn hơn 0';
    END IF;

    -- Đặt giá trị mặc định cho current_uses
    IF NEW.current_uses IS NULL THEN
        SET NEW.current_uses = 0;
    END IF;
END//

DELIMITER ;


DELIMITER //