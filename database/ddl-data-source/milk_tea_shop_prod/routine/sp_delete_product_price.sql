create procedure milk_tea_shop_prod.sp_delete_product_price(IN p_product_price_id int unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (order_product.product_price_id ON DELETE CASCADE)
    -- Deleting a price will cascade delete related order_product entries.
    DELETE FROM product_price WHERE product_price_id = p_product_price_id;
    SET p_row_count = ROW_COUNT();
END;

