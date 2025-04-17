create procedure milk_tea_shop_prod.sp_delete_discount(IN p_discount_id int unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (order_discount.discount_id ON DELETE CASCADE)
    -- Deleting a discount will also delete related order_discount entries.
    DELETE FROM discount WHERE discount_id = p_discount_id;
    SET p_row_count = ROW_COUNT();
END;

