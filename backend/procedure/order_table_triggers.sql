DELIMITER //

-- Kiểm tra trước khi đặt bàn
CREATE TRIGGER before_order_table_insert
BEFORE INSERT ON OrderTable
FOR EACH ROW
BEGIN
    -- Kiểm tra bàn có trống không
    DECLARE table_occupied BOOLEAN;
    
    SELECT EXISTS (
        SELECT 1
        FROM OrderTable ot
        JOIN `Order` o ON ot.order_id = o.order_id
        WHERE ot.table_id = NEW.table_id
          AND ot.check_out IS NULL
          AND o.status = 'PROCESSING'
    ) INTO table_occupied;
    
    IF table_occupied THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Bàn đã được sử dụng';
    END IF;
    
    -- Đặt thời gian vào mặc định
    IF NEW.check_in IS NULL THEN
        SET NEW.check_in = NOW();
    END IF;
END //

-- Kiểm tra trước khi cập nhật đặt bàn
CREATE TRIGGER before_order_table_update
BEFORE UPDATE ON OrderTable
FOR EACH ROW
BEGIN
    -- Ngăn cập nhật check_in
    IF NEW.check_in != OLD.check_in THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không được phép thay đổi thời gian check-in';
    END IF;
    
    -- Ngăn cập nhật lại check_out sau khi đã checkout
    IF OLD.check_out IS NOT NULL AND NEW.check_out != OLD.check_out THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không được phép thay đổi thời gian check-out sau khi đã checkout';
    END IF;
END //

DELIMITER ; 