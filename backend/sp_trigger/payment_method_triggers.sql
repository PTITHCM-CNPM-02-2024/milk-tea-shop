-- 16. PaymentMethod
DELIMITER //

-- Kiểm tra trước khi thêm phương thức thanh toán
CREATE TRIGGER before_payment_method_insert
BEFORE INSERT ON payment_method
FOR EACH ROW
BEGIN
    -- Kiểm tra tên phương thức thanh toán
    IF LENGTH(TRIM(NEW.payment_name)) < 3 OR LENGTH(TRIM(NEW.payment_name)) > 50 THEN               
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên phương thức thanh toán phải từ 3 đến 50 ký tự';
    END IF;
END //

-- Kiểm tra trước khi cập nhật phương thức thanh toán
CREATE TRIGGER before_payment_method_update
BEFORE UPDATE ON payment_method
FOR EACH ROW
BEGIN
    -- Kiểm tra tên phương thức thanh toán
    IF LENGTH(TRIM(NEW.payment_name)) < 3 OR LENGTH(TRIM(NEW.payment_name)) > 50 THEN               
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên phương thức thanh toán phải từ 3 đến 50 ký tự';
    END IF;
END //

-- Kiểm tra trước khi xóa phương thức thanh toán
CREATE TRIGGER before_payment_method_delete
BEFORE DELETE ON payment_method
FOR EACH ROW
BEGIN
    -- kiểm tra xem phương thức thanh toán có đang được sử dụng không
    DECLARE is_used BOOLEAN;
    SELECT EXISTS(
        SELECT 1 FROM `payment` AS p WHERE p.payment_method_id = OLD.payment_method_id
    ) INTO is_used;
    IF is_used THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa phương thức thanh toán đang được sử dụng';
    END IF;
END //

DELIMITER ;
