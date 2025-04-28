create procedure milk_tea_shop_prod.sp_insert_product_price(IN p_product_id mediumint unsigned,
                                                            IN p_size_id smallint unsigned, IN p_price decimal(11, 3),
                                                            OUT p_product_price_id int unsigned)
BEGIN
    INSERT INTO product_price (product_id, size_id, price)
    VALUES (p_product_id, p_size_id, p_price);
    SET p_product_price_id = LAST_INSERT_ID();
END;

