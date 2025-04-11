-- 12. Service Table

DELIMITER //

-- Kiểm tra trước khi thêm bàn
CREATE TRIGGER before_service_table_insert
BEFORE INSERT ON ServiceTable
FOR EACH ROW
BEGIN
    DECLARE max_tables INT;
    DECLARE current_tables INT;
    -- Kiểm tra số bàn
    IF LENGTH(TRIM(NEW.table_number)) < 3 OR LENGTH(TRIM(NEW.table_number)) > 50 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn phải từ 3 đến 50 ký tự';
    END IF;

    -- Kiểm tra khu vực tồn tại nếu được chỉ định
    IF NEW.area_id IS NOT NULL THEN
        -- Kiểm tra số lượng bàn không vượt quá giới hạn của khu vực
        SELECT max_tables INTO max_tables FROM Area WHERE area_id = NEW.area_id;
        SELECT COUNT(*) INTO current_tables FROM ServiceTable WHERE area_id = NEW.area_id;
        
        IF max_tables IS NOT NULL AND current_tables >= max_tables THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Số lượng bàn đã đạt giới hạn tối đa của khu vực';
        END IF;
        
    END IF;

END //

-- Kiểm tra trước khi cập nhật bàn
CREATE TRIGGER before_service_table_update
BEFORE UPDATE ON ServiceTable
FOR EACH ROW
BEGIN
    DECLARE max_tables INT;
    DECLARE current_tables INT;
    DECLARE has_active_order BOOLEAN;
    -- Kiểm tra số bàn
    IF LENGTH(TRIM(NEW.table_number)) < 3 OR LENGTH(TRIM(NEW.table_number)) > 50 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn phải từ 3 đến 50 ký tự';
    END IF;

    -- Kiểm tra khu vực tồn tại nếu được chỉ định
    IF NEW.area_id IS NOT NULL AND NEW.area_id <> OLD.area_id THEN
        SELECT max_tables INTO max_tables FROM Area WHERE area_id = NEW.area_id;
        SELECT COUNT(*) INTO current_tables FROM ServiceTable WHERE area_id = NEW.area_id;

        IF max_tables IS NOT NULL AND current_tables >= max_tables THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Số lượng bàn đã đạt giới hạn tối đa của khu vực';
        END IF;
    END IF;

    -- Không cho phép vô hiệu hóa bàn đang được sử dụng
    IF OLD.is_active = 1 AND NEW.is_active = 0 THEN
        SELECT EXISTS(
            SELECT 1
            FROM OrderTable ot
                     JOIN `Order` o ON ot.order_id = o.order_id
            WHERE ot.table_id = NEW.table_id
              AND ot.check_out IS NULL
              AND o.status <> 'CANCELLED'
        ) INTO has_active_order;

        IF has_active_order THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể vô hiệu hóa bàn đang được sử dụng';
        END IF;
    END IF;
END //

-- Kiểm tra trước khi xóa bàn
CREATE TRIGGER before_service_table_delete
BEFORE DELETE ON ServiceTable
FOR EACH ROW
BEGIN
    DECLARE has_active_order BOOLEAN;

    SELECT EXISTS(
        SELECT 1
        FROM OrderTable ot
                 JOIN `Order` o ON ot.order_id = o.order_id
        WHERE ot.table_id = OLD.table_id
          AND ot.check_out IS NULL
          AND o.status <> 'CANCELLED'
    ) INTO has_active_order;

    IF has_active_order THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa bàn đang được sử dụng';
    END IF;
END //

DELIMITER ;