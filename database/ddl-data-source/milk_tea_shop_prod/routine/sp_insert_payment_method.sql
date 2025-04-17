create procedure milk_tea_shop_prod.sp_insert_payment_method(IN p_payment_name varchar(50),
                                                             IN p_payment_description varchar(255),
                                                             OUT p_payment_method_id tinyint unsigned)
BEGIN
    INSERT INTO payment_method (payment_name, payment_description)
    VALUES (p_payment_name, p_payment_description);
    SET p_payment_method_id = LAST_INSERT_ID();
END;

