create procedure milk_tea_shop_prod.sp_update_service_table(IN p_table_id smallint unsigned,
                                                            IN p_area_id smallint unsigned,
                                                            IN p_table_number varchar(10), IN p_is_active tinyint(1))
BEGIN
    UPDATE service_table
    SET area_id = p_area_id,
        table_number = p_table_number,
        is_active = p_is_active,
        updated_at = CURRENT_TIMESTAMP
    WHERE table_id = p_table_id;
END;

