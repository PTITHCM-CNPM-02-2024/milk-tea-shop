-- 15. Payment

DELIMITER //

-- Kiểm tra trước khi thêm thanh toán
CREATE TRIGGER before_payment_insert
BEFORE INSERT ON Payment
FOR EACH ROW
BEGIN
    -- Kiểm tra số tiền thanh toán
    IF NEW.amount_paid < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền thanh toán không được âm';
    END IF;

    -- Đặt thời gian thanh toán mặc định
    IF NEW.payment_time IS NULL THEN
        SET NEW.payment_time = CURRENT_TIMESTAMP;
    END IF;
END //

-- Kiểm tra trước khi cập nhật thanh toán
CREATE TRIGGER before_payment_update
BEFORE UPDATE ON Payment
FOR EACH ROW
BEGIN
    -- Kiểm tra số tiền thanh toán
    IF NEW.amount_paid < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền thanh toán không được âm';
    END IF;

    -- Không cho phép thay đổi trạng thái từ CANCELLED
    IF OLD.status = 'CANCELLED' AND NEW.status != 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi trạng thái từ CANCELLED';
    END IF;

    -- Không cho phép thay đổi trạng thái từ PAID về PROCESSING
    IF OLD.status = 'PAID' AND NEW.status = 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi trạng thái từ PAID về PROCESSING';
    END IF;
END //

DELIMITER ;
