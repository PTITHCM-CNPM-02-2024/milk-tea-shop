create procedure milk_tea_shop_prod.sp_update_product_size(IN p_size_id smallint unsigned,
                                                           IN p_unit_id smallint unsigned, IN p_name varchar(5),
                                                           IN p_quantity smallint unsigned,
                                                           IN p_description varchar(1000))
BEGIN
    UPDATE product_size
    SET unit_id = p_unit_id,
        name = p_name,
        quantity = p_quantity,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE size_id = p_size_id;
END;

