DELIMITER //

-- Kiểm tra trước khi xóa đơn hàng
CREATE TRIGGER before_order_delete
BEFORE DELETE ON `Order`
FOR EACH ROW
BEGIN
    -- Kiểm tra đơn hàng đã hoàn thành chưa
    IF OLD.status = 'COMPLETED' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể xóa đơn hàng đã hoàn thành';
    END IF;
    
    -- Các ràng buộc khác nếu cần
END //

-- Before Insert Trigger cho Order
CREATE TRIGGER before_order_insert
BEFORE INSERT ON `Order`
FOR EACH ROW
BEGIN
    -- Kiểm tra status phải được cung cấp
    IF NEW.status IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Trạng thái đơn hàng không được để trống';
    END IF;
    
    -- Kiểm tra tổng tiền và thành tiền không âm
    IF NEW.total_amount < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tổng tiền không được âm';
    END IF;
    
    IF NEW.final_amount < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Thành tiền không được âm';
    END IF;
    
    -- Kiểm tra thành tiền không lớn hơn tổng tiền
    IF NEW.final_amount > NEW.total_amount THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Thành tiền không được lớn hơn tổng tiền';
    END IF;
    
    -- Kiểm tra điểm không âm
    IF NEW.point < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Điểm không được âm';
    END IF;
END //

-- Before Update Trigger cho Order
CREATE TRIGGER before_order_update
BEFORE UPDATE ON `Order`
FOR EACH ROW
BEGIN

    IF OLD.status = 'COMPLETED' OR OLD.status = 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể thay đổi trạng thái đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;
    -- Kiểm tra tổng tiền và thành tiền không âm
    IF NEW.total_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tổng tiền không được nhỏ hơn 1000 VNĐ';
    END IF;
    
    IF NEW.final_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Thành tiền không được nhỏ hơn 1000 VNĐ';
    END IF;
    
    -- Kiểm tra thành tiền không lớn hơn tổng tiền
    IF NEW.final_amount > NEW.total_amount THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Thành tiền không được lớn hơn tổng tiền';
    END IF;
    
    -- Kiểm tra điểm không âm
    IF NEW.point < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Điểm không được âm';
    END IF;
    

END //

DELIMITER ;
