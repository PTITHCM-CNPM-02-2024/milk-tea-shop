-- 17. Coupon
DELIMITER //

-- Kiểm tra trước khi thêm mã giảm giá
CREATE TRIGGER before_coupon_insert
BEFORE INSERT ON coupon
FOR EACH ROW
BEGIN
    -- Kiểm tra mã giảm giá
    IF NEW.coupon REGEXP '^[a-zA-Z0-9]{3,15}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá phải có độ dài từ 3 đến 15 ký tự và chỉ được chứa chữ cái và số';
    END IF;
END //

-- Kiểm tra trước khi cập nhật mã giảm giá
CREATE TRIGGER before_coupon_update
BEFORE UPDATE ON coupon
FOR EACH ROW
BEGIN
    -- Kiểm tra mã giảm giá
    IF NEW.coupon REGEXP '^[a-zA-Z0-9]{3,15}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá phải có độ dài từ 3 đến 15 ký tự và chỉ được chứa chữ cái và số';
    END IF;
END //

-- Kiểm tra trước khi xóa mã giảm giá, cần kiểm tra xem có đang được sử dụng bởi discount không
CREATE TRIGGER before_coupon_delete
BEFORE DELETE ON coupon
FOR EACH ROW
BEGIN
    DECLARE discount_count BOOLEAN;
    
    -- Kiểm tra xem coupon có đang được sử dụng trong discount không
    SELECT EXISTS(SELECT 1 FROM discount WHERE coupon_id = OLD.coupon_id) INTO discount_count;
    
    IF discount_count THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa mã giảm giá đang được sử dụng trong chương trình giảm giá, vui lòng xóa discount trước';
    END IF;

END //

DELIMITER ;
