create procedure milk_tea_shop_prod.sp_delete_membership_type(IN p_membership_type_id tinyint unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (customer.membership_type_id ON DELETE RESTRICT)
    -- Deletion might fail if a customer uses this membership type.
    -- Add logic to handle this (e.g., prevent deletion, set to default) if needed.
    DELETE FROM membership_type WHERE membership_type_id = p_membership_type_id;
    SET p_row_count = ROW_COUNT();
END;

