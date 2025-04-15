DELIMITER //

-- Kiểm tra trước khi đặt bàn
CREATE TRIGGER before_order_table_insert
BEFORE INSERT ON order_table
FOR EACH ROW
BEGIN
    -- đơn hàng phải ở trạng thái processing
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    DECLARE is_active BOOLEAN;
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể đặt bàn cho đơn hàng không ở trạng thái processing';
    END IF;
    -- không được đặt bàn cho bàn không hoạt động
    SELECT is_active INTO is_active FROM `service_table` WHERE table_id = NEW.table_id;
    IF is_active = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể đặt bàn cho bàn không hoạt động';
    END IF;

    IF NEW.check_out IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể đặt bàn cho đơn hàng đã checkout';
    END IF;
    
    -- Kiểm tra thời gian vào/ra bàn
    IF NEW.check_out IS NOT NULL AND NEW.check_out < NEW.check_in THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời gian rời bàn phải sau thời gian vào bàn';
    END IF;
END //

-- Kiểm tra trước khi cập nhật đặt bàn
CREATE TRIGGER before_order_table_update
BEFORE UPDATE ON order_table
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
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;
    IF order_status = 'PROCESSING' THEN
        -- không được cập nhật bàn không hoạt động
        SELECT is_active INTO is_active FROM `service_table` WHERE table_id = NEW.table_id;
        IF is_active is not null and is_active = FALSE THEN
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

-- Kiểm tra trước khi xóa đặt bàn
CREATE TRIGGER before_order_table_delete
BEFORE DELETE ON order_table
FOR EACH ROW
BEGIN
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    
    -- Kiểm tra xem đơn hàng có đang ở trạng thái PROCESSING không
    SELECT status INTO order_status FROM `order` WHERE order_id = OLD.order_id;
    
    IF order_status = 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể xóa đặt bàn cho đơn hàng đang xử lý';
    END IF;
END //

-- Sau khi thêm đặt bàn
CREATE TRIGGER after_order_table_insert
AFTER INSERT ON order_table
FOR EACH ROW
BEGIN
    -- Cập nhật trạng thái bàn hoặc thực hiện các hành động khác sau khi đặt bàn
    -- (Có thể để trống nếu không cần xử lý gì)
END //

-- Sau khi cập nhật đặt bàn
CREATE TRIGGER after_order_table_update
AFTER UPDATE ON order_table
FOR EACH ROW
BEGIN
END //

-- Sau khi xóa đặt bàn
CREATE TRIGGER after_order_table_delete
AFTER DELETE ON order_table
FOR EACH ROW
BEGIN
    -- Thực hiện các hành động sau khi xóa đặt bàn
    -- (Có thể để trống nếu không cần xử lý gì)
END //

DELIMITER ;