create procedure milk_tea_shop_prod.sp_delete_category(IN p_category_id smallint unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (product.category_id ON DELETE SET NULL)
    DELETE FROM category WHERE category_id = p_category_id;
    SET p_row_count = ROW_COUNT();
END;

