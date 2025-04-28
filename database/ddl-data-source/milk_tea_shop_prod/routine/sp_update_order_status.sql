create procedure milk_tea_shop_prod.sp_update_order_status(IN p_order_id int unsigned,
                                                           IN p_status enum ('PROCESSING', 'CANCELLED', 'COMPLETED'))
BEGIN
    UPDATE `order`
    SET status = p_status,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = p_order_id;
END;

