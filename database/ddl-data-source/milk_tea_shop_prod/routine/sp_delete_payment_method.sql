create procedure milk_tea_shop_prod.sp_delete_payment_method(IN p_payment_method_id tinyint unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (payment.payment_method_id ON DELETE RESTRICT)
    -- Deletion might fail if this payment method is used in payments.
    DELETE FROM payment_method WHERE payment_method_id = p_payment_method_id;
    SET p_row_count = ROW_COUNT();
END;

