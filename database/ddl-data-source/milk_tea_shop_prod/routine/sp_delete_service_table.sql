create procedure milk_tea_shop_prod.sp_delete_service_table(IN p_table_id smallint unsigned)
BEGIN
    -- Consider dependencies (order_table.table_id ON DELETE CASCADE)
    -- Deleting a table will cascade delete its entries in order_table.
    DELETE FROM service_table WHERE table_id = p_table_id;
END;

