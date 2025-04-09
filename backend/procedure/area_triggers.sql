-- 11. Area

DELIMITER //

-- Kiểm tra trước khi thêm khu vực
CREATE TRIGGER before_area_insert
BEFORE INSERT ON Area
FOR EACH ROW
BEGIN
    -- Kiểm tra tên khu vực
    IF NEW.name REGEXP '^[a-zA-Z0-9_-]{3}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài 3 ký tự';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn 0 nếu được chỉ định
    IF NEW.max_tables IS NOT NULL AND NEW.max_tables <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn 0';
    END IF;
END //

-- Kiểm tra trước khi cập nhật khu vực
CREATE TRIGGER before_area_update
BEFORE UPDATE ON Area
FOR EACH ROW
BEGIN
    DECLARE current_tables INT;
    DECLARE order_count INT;
    -- Kiểm tra tên khu vực
    IF LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực không được để trống';
    END IF;

    IF  NEW.name REGEXP '^[a-zA-Z0-9_-]{3}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài 3 ký tự';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn 0, nhỏ hơn 100
    IF NEW.max_tables IS NOT NULL AND (NEW.max_tables <= 0 OR NEW.max_tables >= 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn 0 và nhỏ hơn 100';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn hoặc bằng số bàn hiện có
    IF NEW.max_tables IS NOT NULL THEN
        SELECT COUNT(*) INTO current_tables FROM ServiceTable WHERE area_id = NEW.area_id;

        IF NEW.max_tables < current_tables THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn hoặc bằng số bàn hiện có';
        END IF;
    END IF;

    IF OLD.is_active <> NEW.is_active AND NEW.is_active = 0 THEN
        -- Nếu là 0 cần kiểm tra order status = processing
        SELECT COUNT(*) INTO order_count FROM `Order` WHERE area_id = NEW.area_id AND status = 'PROCESSING';
            
            IF order_count > 0 THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Không thể deactive khu vực có đơn hàng đang xử lý';
            END IF;
    END IF;
END //

-- Kiểm tra trước khi xóa khu vực
CREATE TRIGGER before_area_delete
BEFORE DELETE ON Area
FOR EACH ROW
BEGIN
END //

DELIMITER ;
