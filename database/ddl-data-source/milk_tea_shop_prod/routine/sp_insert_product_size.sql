create procedure milk_tea_shop_prod.sp_insert_product_size(IN p_unit_id smallint unsigned, IN p_name varchar(5),
                                                           IN p_quantity smallint unsigned,
                                                           IN p_description varchar(1000),
                                                           OUT p_size_id smallint unsigned)
BEGIN
    INSERT INTO product_size (unit_id, name, quantity, description)
    VALUES (p_unit_id, p_name, p_quantity, p_description);
    SET p_size_id = LAST_INSERT_ID();
END;

