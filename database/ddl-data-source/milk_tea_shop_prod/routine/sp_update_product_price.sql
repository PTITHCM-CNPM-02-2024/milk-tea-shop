create procedure milk_tea_shop_prod.sp_update_product_price(IN p_product_price_id int unsigned,
                                                            IN p_product_id mediumint unsigned,
                                                            IN p_size_id smallint unsigned, IN p_price decimal(11, 3))
BEGIN
    UPDATE product_price
    SET product_id = p_product_id,
        size_id = p_size_id,
        price = p_price,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_price_id = p_product_price_id;
END;

