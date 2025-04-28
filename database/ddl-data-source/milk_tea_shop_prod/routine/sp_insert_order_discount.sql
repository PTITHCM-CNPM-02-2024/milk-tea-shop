create procedure milk_tea_shop_prod.sp_insert_order_discount(IN p_order_id int unsigned, IN p_discount_id int unsigned,
                                                             IN p_discount_amount decimal(11, 3),
                                                             OUT p_order_discount_id int unsigned)
BEGIN

    -- 1. chèn vào order_discount
    INSERT INTO order_discount (order_id, discount_id, discount_amount)
    VALUES (p_order_id, p_discount_id, p_discount_amount);
    SET p_order_discount_id = LAST_INSERT_ID();

END;

