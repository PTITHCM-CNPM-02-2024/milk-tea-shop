-- 12. Service Table

DELIMITER //

-- Kiểm tra trước khi thêm bàn
CREATE TRIGGER before_service_table_insert
BEFORE INSERT ON ServiceTable
FOR EACH ROW
BEGIN
    -- Kiểm tra số bàn
    IF LENGTH(TRIM(NEW.table_number)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn không được để trống';
    END IF;

    -- Kiểm tra khu vực tồn tại nếu được chỉ định
    IF NEW.area_id IS NOT NULL THEN
        DECLARE area_exists BOOLEAN;
        DECLARE table_exists BOOLEAN;
        DECLARE max_tables INT;
        DECLARE current_tables INT;
        
        SELECT EXISTS(SELECT 1 FROM Area WHERE area_id = NEW.area_id) INTO area_exists;

        IF NOT area_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Khu vực không tồn tại';
        END IF;

        -- Kiểm tra số bàn không trùng lặp trong cùng khu vực
        SELECT EXISTS(
            SELECT 1 FROM ServiceTable
            WHERE area_id = NEW.area_id AND table_number = NEW.table_number
        ) INTO table_exists;

        IF table_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Số bàn đã tồn tại trong khu vực này';
        END IF;

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
    -- Kiểm tra số bàn
    IF LENGTH(TRIM(NEW.table_number)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn không được để trống';
    END IF;

    -- Kiểm tra khu vực tồn tại nếu được chỉ định
    IF NEW.area_id IS NOT NULL THEN
        DECLARE area_exists BOOLEAN;
        DECLARE max_tables INT;
        DECLARE current_tables INT;
        DECLARE has_active_order BOOLEAN;
        DECLARE table_exists BOOLEAN;
        
        SELECT EXISTS(SELECT 1 FROM Area WHERE area_id = NEW.area_id) INTO area_exists;

        IF NOT area_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Khu vực không tồn tại';
        END IF;

        -- Kiểm tra số bàn không trùng lặp trong cùng khu vực (trừ chính nó)
        IF NEW.area_id != OLD.area_id OR NEW.table_number != OLD.table_number THEN
            SELECT EXISTS(
                SELECT 1 FROM ServiceTable
                WHERE area_id = NEW.area_id
                  AND table_number = NEW.table_number
                  AND table_id != NEW.table_id
            ) INTO table_exists;

            IF table_exists THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Số bàn đã tồn tại trong khu vực này';
            END IF;
        END IF;

        -- Kiểm tra số lượng bàn không vượt quá giới hạn của khu vực
        IF NEW.area_id != OLD.area_id THEN
            SELECT max_tables INTO max_tables FROM Area WHERE area_id = NEW.area_id;
            SELECT COUNT(*) INTO current_tables FROM ServiceTable WHERE area_id = NEW.area_id;

            IF max_tables IS NOT NULL AND current_tables >= max_tables THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Số lượng bàn đã đạt giới hạn tối đa của khu vực';
            END IF;
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
              AND o.status != 'CANCELLED'
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
    -- Kiểm tra xem bàn có liên quan đến đơn hàng không
    DECLARE order_count INT;
    SELECT COUNT(*) INTO order_count FROM OrderTable WHERE table_id = OLD.table_id;

    IF order_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa bàn đã được sử dụng trong đơn hàng';
    END IF;
END //

DELIMITER ;
