create procedure milk_tea_shop_prod.sp_insert_order_table(IN p_order_id int unsigned, IN p_table_id smallint unsigned,
                                                          IN p_check_in datetime, IN p_check_out datetime,
                                                          OUT p_order_table_id int unsigned)
BEGIN
    INSERT INTO order_table (order_id, table_id, check_in, check_out)
    VALUES (p_order_id, p_table_id, p_check_in, p_check_out);
    SET p_order_table_id = LAST_INSERT_ID();
END;

