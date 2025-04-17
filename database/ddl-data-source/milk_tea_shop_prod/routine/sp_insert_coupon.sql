create procedure milk_tea_shop_prod.sp_insert_coupon(IN p_coupon varchar(15), IN p_description varchar(1000),
                                                     OUT p_coupon_id int unsigned)
BEGIN
    INSERT INTO coupon (coupon, description)
    VALUES (p_coupon, p_description);
    SET p_coupon_id = LAST_INSERT_ID();
END;

