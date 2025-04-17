create procedure milk_tea_shop_prod.sp_delete_product_size(IN p_size_id smallint unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (product_price.size_id ON DELETE CASCADE)
    -- Deleting a size will cascade delete related product_price entries.
    DELETE FROM product_size WHERE size_id = p_size_id;
    SET p_row_count = ROW_COUNT();
END;

