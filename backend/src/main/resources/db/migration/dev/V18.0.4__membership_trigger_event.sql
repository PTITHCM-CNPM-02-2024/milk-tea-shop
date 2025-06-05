SET NAMES 'utf8mb4';
SET CHARACTER SET utf8mb4;
-- Trigger kiểm tra thời hạn membership
DELIMITER //

-- Trigger kiểm tra thời hạn khi cập nhật membership_type
CREATE TRIGGER before_membership_update_check_expiration
    BEFORE UPDATE
    ON membership_type
    FOR EACH ROW
BEGIN
    -- Nếu cập nhật valid_until, đảm bảo phải là ngày trong tương lai
    IF NEW.valid_until IS NOT NULL AND NEW.valid_until <= CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời hạn membership phải là ngày trong tương lai';
    END IF;
END //

-- Trigger kiểm tra thời hạn khi thêm mới membership_type
CREATE TRIGGER before_membership_insert_check_expiration
    BEFORE INSERT
    ON membership_type
    FOR EACH ROW
BEGIN
    -- Nếu cập nhật valid_until, đảm bảo phải là ngày trong tương lai
    IF NEW.valid_until IS NOT NULL AND NEW.valid_until <= CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời hạn membership phải là ngày trong tương lai';
    END IF;
END //

DELIMITER ;

DELIMITER //

-- Thủ tục reset membership về NEWMEM khi hết hạn
CREATE PROCEDURE sp_reset_expired_memberships()
BEGIN
    DECLARE newmem_id TINYINT UNSIGNED;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            SELECT 'Error occurred - transaction rolled back' AS result;
        END;

    START TRANSACTION;


    -- Lấy ID của loại thành viên NEWMEM
    SELECT membership_type_id
    INTO newmem_id
    FROM membership_type
    WHERE type = 'NEWMEM';

    -- Tìm và cập nhật các khách hàng có loại thành viên đã hết hạn
    UPDATE customer c
        JOIN membership_type mt ON c.membership_type_id = mt.membership_type_id
    SET c.membership_type_id = newmem_id,
        c.current_points     = 0,
        c.updated_at         = CURRENT_TIMESTAMP
    WHERE mt.valid_until IS NOT NULL
      AND mt.valid_until < CURRENT_DATE
      AND mt.type != 'NEWMEM';
    -- Log kết quả
    SELECT CONCAT('Đã reset ', ROW_COUNT(), ' khách hàng về loại thành viên NEWMEM do hết hạn') AS result;
    
    -- Tự động cập nhật valid_until về sau 1 năm cho các membership đã hết hạn
    UPDATE membership_type mt
    SET mt.valid_until = DATE_ADD(CURRENT_DATE, INTERVAL 1 YEAR)
    WHERE mt.valid_until IS NOT NULL
      AND mt.valid_until < CURRENT_DATE
      AND mt.type != 'NEWMEM';

    -- Log kết quả cập nhật thời hạn
    SELECT CONCAT('Đã cập nhật thời hạn cho ', ROW_COUNT(), ' loại thành viên thêm 1 năm') AS update_result;

    COMMIT;
END //

DELIMITER ;

DELIMITER //

-- Thủ tục tái cấp lại thành viên dựa trên điểm hiện tại
CREATE PROCEDURE sp_recalculate_customer_memberships()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            SELECT 'Error occurred - transaction rolled back' AS result;
        END;

    START TRANSACTION;

    -- Cập nhật loại thành viên dựa trên điểm hiện tại
    UPDATE customer c
    SET c.membership_type_id = (SELECT mt.membership_type_id
                                FROM membership_type mt
                                WHERE c.current_points >= mt.required_point
                                  AND (mt.valid_until IS NULL OR mt.valid_until > CURRENT_DATE)
                                ORDER BY mt.required_point DESC
                                LIMIT 1),
        c.updated_at         = CURRENT_TIMESTAMP
    WHERE c.current_points > 0;

    -- Log kết quả
    SELECT CONCAT('Đã tái cấp loại thành viên cho ', ROW_COUNT(), ' khách hàng dựa trên điểm hiện tại') AS result;

    COMMIT;
END //

DELIMITER ;

DELIMITER //

-- Thủ tục đồng bộ điểm từ các đơn hàng COMPLETED từ đầu năm đến hiện tại
CREATE PROCEDURE sp_sync_year_to_date_customer_points()
BEGIN
    DECLARE current_year INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            SELECT 'Đã xảy ra lỗi - transaction đã được rollback' AS result;
        END;

    START TRANSACTION;
    
    -- Lấy năm hiện tại
    SET current_year = YEAR(CURRENT_DATE);
    
    -- Tạo bảng tạm để lưu kết quả tính toán
    DROP TEMPORARY TABLE IF EXISTS temp_customer_points;
    CREATE TEMPORARY TABLE temp_customer_points (
        customer_id INT UNSIGNED,
        calculated_points INT UNSIGNED
    );
    
    -- Tính tổng điểm từ các đơn hàng hoàn thành của khách hàng từ đầu năm đến hiện tại
    INSERT INTO temp_customer_points (customer_id, calculated_points)
    SELECT 
        o.customer_id,
        SUM(o.point) AS calculated_points
    FROM 
        `order` o
    WHERE 
        o.customer_id IS NOT NULL
        AND o.status = 'COMPLETED'
        AND o.point IS NOT NULL
        AND YEAR(o.order_time) = current_year
        AND o.order_time <= CURRENT_TIMESTAMP
    GROUP BY 
        o.customer_id;
    
    -- Cập nhật điểm cho khách hàng nếu có sự khác biệt
    UPDATE 
        customer c
        JOIN temp_customer_points tp ON c.customer_id = tp.customer_id
    SET 
        c.current_points = tp.calculated_points,
        c.updated_at = CURRENT_TIMESTAMP
    WHERE 
        c.current_points <> tp.calculated_points;
    
    -- Log kết quả
    SELECT CONCAT('Đã đồng bộ điểm cho ', ROW_COUNT(), ' khách hàng từ đơn hàng năm ', current_year) AS result;
    
    -- Xóa bảng tạm
    DROP TEMPORARY TABLE IF EXISTS temp_customer_points;
    
    COMMIT;
END //

DELIMITER ;

DELIMITER //

-- Tạo event scheduler chạy hàng ngày để kiểm tra membership hết hạn
CREATE EVENT IF NOT EXISTS event_check_expired_memberships
    ON SCHEDULE EVERY 1 DAY
        STARTS CURRENT_DATE + INTERVAL 1 DAY
    DO
    BEGIN
        -- Đồng bộ điểm khách hàng
        CALL sp_sync_year_to_date_customer_points();
        -- Reset thành viên hết hạn
        CALL sp_reset_expired_memberships();
        -- Tái cấp loại thành viên dựa trên điểm hiện tại
        CALL sp_recalculate_customer_memberships();
    END //
    
DELIMITER ;


-- Kiểm tra trạng thái hiện tại của Event Scheduler
-- SHOW VARIABLES LIKE 'event_scheduler';
-- Bật Event Scheduler nếu chưa được bật
-- SET GLOBAL event_scheduler = ON;
-- Migration completed successfully
