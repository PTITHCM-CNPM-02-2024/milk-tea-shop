create procedure milk_tea_shop_prod.sp_update_payment_method(IN p_payment_method_id tinyint unsigned,
                                                             IN p_payment_name varchar(50),
                                                             IN p_payment_description varchar(255))
BEGIN
    UPDATE payment_method
    SET payment_name = p_payment_name,
        payment_description = p_payment_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE payment_method_id = p_payment_method_id;
END;

