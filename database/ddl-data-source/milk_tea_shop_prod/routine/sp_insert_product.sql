create procedure milk_tea_shop_prod.sp_insert_product(IN p_category_id smallint unsigned, IN p_name varchar(100),
                                                      IN p_description varchar(1000), IN p_is_available tinyint(1),
                                                      IN p_is_signature tinyint(1), IN p_image_path varchar(1000),
                                                      OUT p_product_id mediumint unsigned)
BEGIN
    INSERT INTO product (category_id, name, description, is_available, is_signature, image_path)
    VALUES (p_category_id, p_name, p_description, p_is_available, p_is_signature, p_image_path);
    SET p_product_id = LAST_INSERT_ID();
END;

