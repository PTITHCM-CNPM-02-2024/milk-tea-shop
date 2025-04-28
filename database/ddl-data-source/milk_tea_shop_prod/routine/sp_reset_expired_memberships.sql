create procedure milk_tea_shop_prod.sp_reset_expired_memberships()
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
END;

