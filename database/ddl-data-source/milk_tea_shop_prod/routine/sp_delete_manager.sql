create procedure milk_tea_shop_prod.sp_delete_manager(IN p_manager_id int unsigned)
BEGIN
    -- No direct dependencies listed in the schema, safe to delete.
    DELETE FROM manager WHERE manager_id = p_manager_id;
END;

