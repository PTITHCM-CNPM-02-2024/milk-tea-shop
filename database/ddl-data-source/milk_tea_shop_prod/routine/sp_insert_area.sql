create procedure milk_tea_shop_prod.sp_insert_area(IN p_name varchar(3), IN p_max_tables int, IN p_is_active tinyint(1),
                                                   IN p_description varchar(255), OUT p_area_id smallint unsigned)
BEGIN
    INSERT INTO area (name, max_tables, is_active, description)
    VALUES (p_name, p_max_tables, p_is_active, p_description);
    SET p_area_id = LAST_INSERT_ID();
END;

