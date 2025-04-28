create procedure milk_tea_shop_prod.sp_recalculate_customer_memberships()
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
END;

