create procedure milk_tea_shop_prod.sp_delete_customer(IN p_customer_id int unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (`order`.customer_id ON DELETE SET NULL)
    -- Orders associated with this customer will have customer_id set to NULL.
    DELETE FROM customer WHERE customer_id = p_customer_id;
    SET p_row_count = ROW_COUNT();
END;

