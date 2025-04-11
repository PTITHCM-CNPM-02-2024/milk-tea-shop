-- 17. Coupon
DELIMITER //

-- Kiểm tra trước khi thêm mã giảm giá
CREATE TRIGGER before_coupon_insert
BEFORE INSERT ON Coupon
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
BEFORE UPDATE ON Coupon
FOR EACH ROW
BEGIN
    -- Kiểm tra mã giảm giá
    IF NEW.coupon REGEXP '^[a-zA-Z0-9]{3,15}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá phải có độ dài từ 3 đến 15 ký tự và chỉ được chứa chữ cái và số';
    END IF;
END //

-- Kiểm tra trước khi xóa mã giảm giá
CREATE TRIGGER before_coupon_delete
BEFORE DELETE ON Coupon
FOR EACH ROW
BEGIN
END //

DELIMITER ;
