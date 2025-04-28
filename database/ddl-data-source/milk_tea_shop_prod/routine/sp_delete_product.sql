create procedure milk_tea_shop_prod.sp_delete_product(IN p_product_id mediumint unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (product_price.product_id ON DELETE CASCADE)
    -- Deleting a product will cascade delete related product_price entries.
    DELETE FROM product WHERE product_id = p_product_id;
    SET p_row_count = ROW_COUNT();
END;

