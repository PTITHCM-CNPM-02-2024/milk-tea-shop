-- 16. PaymentMethod
DELIMITER //

-- Kiểm tra trước khi thêm phương thức thanh toán
CREATE TRIGGER before_payment_method_insert
BEFORE INSERT ON PaymentMethod
FOR EACH ROW
BEGIN
    -- Kiểm tra tên phương thức thanh toán
    IF LENGTH(TRIM(NEW.payment_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên phương thức thanh toán không được để trống';
    END IF;

    -- Kiểm tra tên phương thức thanh toán đã tồn tại chưa
    DECLARE payment_method_exists BOOLEAN;
    SELECT EXISTS(SELECT 1 FROM PaymentMethod WHERE payment_name = NEW.payment_name)
    INTO payment_method_exists;

    IF payment_method_exists THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên phương thức thanh toán đã tồn tại';
    END IF;
END //

-- Kiểm tra trước khi cập nhật phương thức thanh toán
CREATE TRIGGER before_payment_method_update
BEFORE UPDATE ON PaymentMethod
FOR EACH ROW
BEGIN
    -- Kiểm tra tên phương thức thanh toán
    IF LENGTH(TRIM(NEW.payment_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên phương thức thanh toán không được để trống';
    END IF;

    -- Kiểm tra tên phương thức thanh toán đã tồn tại chưa (trừ chính nó)
    IF NEW.payment_name != OLD.payment_name THEN
        DECLARE payment_method_exists BOOLEAN;
        SELECT EXISTS(
            SELECT 1 FROM PaymentMethod
            WHERE payment_name = NEW.payment_name
              AND payment_method_id != NEW.payment_method_id
        ) INTO payment_method_exists;

        IF payment_method_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Tên phương thức thanh toán đã tồn tại';
        END IF;
    END IF;
END //

-- Kiểm tra trước khi xóa phương thức thanh toán
CREATE TRIGGER before_payment_method_delete
BEFORE DELETE ON PaymentMethod
FOR EACH ROW
BEGIN
    -- Kiểm tra phương thức thanh toán có liên kết với thanh toán không
    DECLARE payment_count INT;
    SELECT COUNT(*) INTO payment_count FROM Payment WHERE payment_method_id = OLD.payment_method_id;

    IF payment_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa phương thức thanh toán đang được sử dụng trong thanh toán';
    END IF;
END //

DELIMITER ;
