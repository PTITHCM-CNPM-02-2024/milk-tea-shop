create procedure milk_tea_shop_prod.sp_update_order_amounts(IN p_order_id int unsigned,
                                                            IN p_total_amount decimal(11, 3),
                                                            IN p_final_amount decimal(11, 3))
BEGIN
    UPDATE `order`
    SET total_amount = p_total_amount,
        final_amount = p_final_amount,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = p_order_id;
END;

