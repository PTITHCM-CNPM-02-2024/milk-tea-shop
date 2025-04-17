create procedure milk_tea_shop_prod.sp_delete_unit_of_measure(IN p_unit_id smallint unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (product_size.unit_id ON DELETE RESTRICT)
    -- Deletion might fail if product sizes use this unit.
    DELETE FROM unit_of_measure WHERE unit_id = p_unit_id;
    SET p_row_count = ROW_COUNT();
END;

