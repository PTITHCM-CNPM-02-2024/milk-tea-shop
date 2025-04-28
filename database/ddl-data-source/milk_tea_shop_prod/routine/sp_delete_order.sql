create procedure milk_tea_shop_prod.sp_delete_order(IN p_order_id int unsigned)
BEGIN
    -- Consider dependencies: ON DELETE CASCADE for order_discount, order_table, payment, order_product
    -- Deleting an order will automatically cascade deletes to related tables.
    DELETE FROM `order` WHERE order_id = p_order_id;
END;

