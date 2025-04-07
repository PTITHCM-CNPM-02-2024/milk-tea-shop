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

DELIMITER ;
