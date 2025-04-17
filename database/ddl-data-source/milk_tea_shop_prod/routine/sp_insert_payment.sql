create procedure milk_tea_shop_prod.sp_insert_payment(IN p_order_id int unsigned,
                                                      IN p_payment_method_id tinyint unsigned,
                                                      IN p_status enum ('PROCESSING', 'CANCELLED', 'PAID'),
                                                      IN p_amount_paid decimal(11, 3),
                                                      IN p_change_amount decimal(11, 3), IN p_payment_time timestamp,
                                                      OUT p_payment_id int unsigned)
BEGIN
    INSERT INTO payment (order_id, payment_method_id, status, amount_paid, change_amount, payment_time)
    VALUES (p_order_id, p_payment_method_id, p_status, p_amount_paid, p_change_amount, p_payment_time);
    SET p_payment_id = LAST_INSERT_ID();
END;

