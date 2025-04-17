-- 15. Payment

DELIMITER //

-- Kiểm tra trước khi thêm thanh toán
CREATE TRIGGER before_payment_insert
BEFORE INSERT ON payment
FOR EACH ROW
BEGIN
    -- Kiểm tra trạng thái
    IF NEW.status IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái thanh toán không được để trống';
    END IF;

    -- Kiểm tra số tiền đã trả
    IF NEW.amount_paid IS NOT NULL AND NEW.amount_paid <> 0 AND NEW.amount_paid < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền đã trả phải là 0 hoặc lớn hơn 1000';
    END IF;

    -- Kiểm tra tiền thừa
    IF NEW.change_amount IS NOT NULL AND NEW.change_amount <> 0 AND NEW.change_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tiền thừa phải là 0 hoặc lớn hơn 1000';
    END IF;
END //

DELIMITER ;

DELIMITER //

-- Kiểm tra trước khi cập nhật thanh toán
CREATE TRIGGER before_payment_update
BEFORE UPDATE ON payment
FOR EACH ROW
BEGIN
    -- không cho phép cập nhật trạng thái từ PAID về PROCESSING
    IF NEW.status is not null and OLD.status = 'PAID' and NEW.status <> 'PAID' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi trạng thái thanh toán của thanh toán đã hoàn tất';
    END IF;

    -- không cho phép cập nhật trạng thái từ CANCELLED về PROCESSING
    IF NEW.status is not null and OLD.status = 'CANCELLED' and NEW.status <> 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi trạng thái thanh toán của thanh toán đã hủy';
    END IF;

    -- Kiểm tra trạng thái
    IF NEW.status IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái thanh toán không được để trống';
    END IF;

    -- Kiểm tra số tiền đã trả
    IF NEW.amount_paid IS NOT NULL AND NEW.amount_paid <> 0 AND NEW.amount_paid < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền đã trả phải là 0 hoặc lớn hơn 1000';
    END IF;

    -- Kiểm tra tiền thừa
    IF NEW.change_amount IS NOT NULL AND NEW.change_amount <> 0 AND NEW.change_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tiền thừa phải là 0 hoặc lớn hơn 1000';
    END IF;
END //

DELIMITER ;
