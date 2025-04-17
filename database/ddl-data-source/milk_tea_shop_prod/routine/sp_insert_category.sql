create procedure milk_tea_shop_prod.sp_insert_category(IN p_name varchar(100), IN p_description varchar(1000),
                                                       OUT p_category_id smallint unsigned)
BEGIN
    INSERT INTO category (name, description)
    VALUES (p_name, p_description);
    SET p_category_id = LAST_INSERT_ID();
END;

