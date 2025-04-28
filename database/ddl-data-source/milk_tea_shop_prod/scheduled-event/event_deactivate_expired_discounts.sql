create event if not exists milk_tea_shop_prod.event_deactivate_expired_discounts on schedule
    every '1' DAY
        starts '2025-04-20 01:00:00'
    enable
    do
    BEGIN
    -- Gọi stored procedure
    CALL sp_deactivate_expired_discounts();
END;

