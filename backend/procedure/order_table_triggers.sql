DELIMITER //

-- Kiểm tra trước khi đặt bàn
CREATE TRIGGER before_order_table_insert
BEFORE INSERT ON OrderTable
FOR EACH ROW
BEGIN
    -- đơn hàng phải ở trạng thái processing
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    DECLARE is_active BOOLEAN;
    SELECT status INTO order_status FROM `Order` WHERE order_id = NEW.order_id;
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể đặt bàn cho đơn hàng không ở trạng thái processing';
    END IF;
    -- không được đặt bàn cho bàn không hoạt động
    SELECT is_active INTO is_active FROM `ServiceTable` WHERE table_id = NEW.table_id;
    IF is_active = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể đặt bàn cho bàn không hoạt động';
    END IF;

    IF NEW.check_out IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể đặt bàn cho đơn hàng đã checkout';
    END IF;
END //

-- Kiểm tra trước khi cập nhật đặt bàn
CREATE TRIGGER before_order_table_update
BEFORE UPDATE ON OrderTable
FOR EACH ROW
BEGIN

    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    DECLARE is_active BOOLEAN;
    -- không được cập nhật order_id
    IF NEW.order_id IS NOT NULL AND NEW.order_id <> OLD.order_id THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể cập nhật order_id';
    END IF;
    -- nếu đơn hàng đang ở trạng thái processing thì được cập nhật, ngược lại không được cập nhật
    SELECT status INTO order_status FROM `Order` WHERE order_id = NEW.order_id;
    IF order_status = 'PROCESSING' THEN
        -- không được cập nhật bàn không hoạt động
        SELECT is_active INTO is_active FROM `ServiceTable` WHERE table_id = NEW.table_id;
        IF is_active = FALSE THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể cập nhật đặt bàn cho bàn không hoạt động';
        END IF;
    ELSE
        IF NEW.check_in IS NOT NULL AND NEW.check_in <> OLD.check_in THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể cập nhật check_in';
        END IF;
        IF NEW.check_out IS NOT NULL AND NEW.check_out <> OLD.check_out THEN
            IF OLD.check_out IS NOT NULL THEN
                SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể cập nhật check_out cho đơn hàng đã checkout';
            END IF;
            IF NEW.check_out < NEW.check_in THEN
                SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể cập nhật check_out trước check_in';
            END IF;
        END IF;
    END IF;

END //

DELIMITER ;