create procedure milk_tea_shop_prod.sp_insert_order_product(IN p_order_id int unsigned,
                                                            IN p_product_price_id int unsigned,
                                                            IN p_quantity smallint unsigned, IN p_option varchar(500),
                                                            OUT p_order_product_id int unsigned)
BEGIN
    INSERT INTO order_product (order_id, product_price_id, quantity, `option`)
    VALUES (p_order_id, p_product_price_id, p_quantity, p_option);
    SET p_order_product_id = LAST_INSERT_ID();
END;

