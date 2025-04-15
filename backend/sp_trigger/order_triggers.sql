DELIMITER //

-- Kiểm tra trước khi thêm đơn hàng
CREATE TRIGGER before_order_insert
BEFORE INSERT ON `order`
FOR EACH ROW
BEGIN
    -- Kiểm tra trạng thái
    IF NEW.status IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái đơn hàng không được để trống';
    END IF;

    -- Kiểm tra tổng tiền
    IF NEW.total_amount IS NOT NULL AND NEW.total_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tổng tiền phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra thành tiền
    IF NEW.final_amount IS NOT NULL AND NEW.final_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thành tiền phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra mối quan hệ giữa total_amount và final_amount
    IF NEW.total_amount IS NOT NULL AND NEW.final_amount IS NOT NULL AND NEW.final_amount > NEW.total_amount THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thành tiền không được lớn hơn tổng tiền';
    END IF;

    -- Kiểm tra điểm
    IF NEW.point IS NOT NULL AND NEW.point < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm không được âm';
    END IF;
END //

-- Kiểm tra trước khi cập nhật đơn hàng
CREATE TRIGGER before_order_update
BEFORE UPDATE ON `order`
FOR EACH ROW
BEGIN
    -- Kiểm tra trạng thái
    IF NEW.status IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái đơn hàng không được để trống';
    ELSEIF NEW.status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái đơn hàng không được chỉnh sửa';
    END IF;

    -- Kiểm tra tổng tiền
    IF NEW.total_amount IS NOT NULL AND NEW.total_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tổng tiền phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra thành tiền
    IF NEW.final_amount IS NOT NULL AND NEW.final_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thành tiền phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra mối quan hệ giữa total_amount và final_amount
    IF NEW.total_amount IS NOT NULL AND NEW.final_amount IS NOT NULL AND NEW.final_amount > NEW.total_amount THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thành tiền không được lớn hơn tổng tiền';
    END IF;

    -- Kiểm tra điểm
    IF NEW.point IS NOT NULL AND NEW.point < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm không được âm';
    END IF;
END //


-- không cho phép xóa đơn hàng, cần xóa từ nhân viên    
CREATE TRIGGER before_order_delete
BEFORE DELETE ON `order`
FOR EACH ROW
BEGIN
END //

DELIMITER ;


