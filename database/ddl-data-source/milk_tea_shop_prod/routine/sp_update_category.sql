create procedure milk_tea_shop_prod.sp_update_category(IN p_category_id smallint unsigned, IN p_name varchar(100),
                                                       IN p_description varchar(1000), OUT p_row_count int)
BEGIN
    UPDATE category
    SET name = p_name,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE category_id = p_category_id;
    SET p_row_count = ROW_COUNT();
END;

