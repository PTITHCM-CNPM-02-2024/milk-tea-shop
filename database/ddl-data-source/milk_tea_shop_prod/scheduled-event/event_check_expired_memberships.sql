create event if not exists milk_tea_shop_prod.event_check_expired_memberships on schedule
    every '1' DAY
        starts '2025-04-21 00:00:00'
    enable
    do
    BEGIN
        CALL sp_reset_expired_memberships();
        -- Thêm thủ tục tái cấp lại thành viên dựa trên điểm hiện tại
        CALL sp_recalculate_customer_memberships();
    END;

