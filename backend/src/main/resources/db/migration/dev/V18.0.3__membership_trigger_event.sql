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

-- Tạo event scheduler chạy hàng ngày để kiểm tra membership hết hạn
CREATE EVENT IF NOT EXISTS event_check_expired_memberships
    ON SCHEDULE EVERY 1 DAY
        STARTS CURRENT_DATE + INTERVAL 1 DAY
    DO
    BEGIN
        CALL sp_reset_expired_memberships();
        -- Thêm thủ tục tái cấp lại thành viên dựa trên điểm hiện tại
        CALL sp_recalculate_customer_memberships();
    END //
    
DELIMITER ;


-- Kiểm tra trạng thái hiện tại của Event Scheduler
SHOW VARIABLES LIKE 'event_scheduler';

-- Bật Event Scheduler nếu chưa được bật
SET GLOBAL event_scheduler = ON;
