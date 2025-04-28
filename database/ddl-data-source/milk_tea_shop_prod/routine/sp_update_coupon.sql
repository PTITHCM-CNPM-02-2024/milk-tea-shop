create procedure milk_tea_shop_prod.sp_update_coupon(IN p_coupon_id int unsigned, IN p_coupon varchar(15),
                                                     IN p_description varchar(1000), OUT p_row_count int)
BEGIN
    UPDATE coupon
    SET coupon = p_coupon,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE coupon_id = p_coupon_id;
    SET p_row_count = ROW_COUNT();
END;

