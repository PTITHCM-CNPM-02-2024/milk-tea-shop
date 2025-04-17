create procedure milk_tea_shop_prod.sp_update_area(IN p_area_id smallint unsigned, IN p_name varchar(3),
                                                   IN p_max_tables int, IN p_is_active tinyint(1),
                                                   IN p_description varchar(255), OUT p_row_count int)
BEGIN
    UPDATE area
    SET name = p_name,
        max_tables = p_max_tables,
        is_active = p_is_active,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE area_id = p_area_id;
    SET p_row_count = ROW_COUNT();
END;

