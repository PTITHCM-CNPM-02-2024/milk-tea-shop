create procedure milk_tea_shop_prod.sp_delete_coupon(IN p_coupon_id int unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (discount.coupon_id ON DELETE RESTRICT)
    -- Need to handle or prevent deletion if used in discount table
    -- For now, simple delete. Add logic later if needed.
    DELETE FROM coupon WHERE coupon_id = p_coupon_id;
    SET p_row_count = ROW_COUNT();
END;

