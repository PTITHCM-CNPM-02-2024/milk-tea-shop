-- 16. PaymentMethod
DELIMITER //

-- Kiểm tra trước khi thêm phương thức thanh toán
CREATE TRIGGER before_payment_method_insert
BEFORE INSERT ON PaymentMethod
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
BEFORE UPDATE ON PaymentMethod
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
BEFORE DELETE ON PaymentMethod
FOR EACH ROW
BEGIN
END //

DELIMITER ;
