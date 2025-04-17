create procedure milk_tea_shop_prod.sp_update_order_table_checkout(IN p_order_table_id int unsigned, IN p_check_out datetime)
BEGIN
    UPDATE order_table
    SET check_out = p_check_out,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_table_id = p_order_table_id;
END;

