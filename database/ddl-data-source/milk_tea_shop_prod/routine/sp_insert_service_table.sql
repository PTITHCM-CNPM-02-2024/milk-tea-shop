create procedure milk_tea_shop_prod.sp_insert_service_table(IN p_area_id smallint unsigned,
                                                            IN p_table_number varchar(10), IN p_is_active tinyint(1),
                                                            OUT p_table_id smallint unsigned)
BEGIN
    INSERT INTO service_table (area_id, table_number, is_active)
    VALUES (p_area_id, p_table_number, p_is_active);
    SET p_table_id = LAST_INSERT_ID();
END;

