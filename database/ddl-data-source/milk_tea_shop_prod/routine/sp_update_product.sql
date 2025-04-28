create procedure milk_tea_shop_prod.sp_update_product(IN p_product_id mediumint unsigned,
                                                      IN p_category_id smallint unsigned, IN p_name varchar(100),
                                                      IN p_description varchar(1000), IN p_is_available tinyint(1),
                                                      IN p_is_signature tinyint(1), IN p_image_path varchar(1000))
BEGIN
    UPDATE product
    SET category_id = p_category_id,
        name = p_name,
        description = p_description,
        is_available = p_is_available,
        is_signature = p_is_signature,
        image_path = p_image_path,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_id = p_product_id;
END;

