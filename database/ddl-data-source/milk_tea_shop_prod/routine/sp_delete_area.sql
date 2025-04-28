create procedure milk_tea_shop_prod.sp_delete_area(IN p_area_id smallint unsigned, OUT p_row_count int)
BEGIN
    DELETE FROM area WHERE area_id = p_area_id;
    SET p_row_count = ROW_COUNT();
END;

