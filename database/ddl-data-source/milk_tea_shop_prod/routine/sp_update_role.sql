create procedure milk_tea_shop_prod.sp_update_role(IN p_role_id tinyint unsigned, IN p_name varchar(50),
                                                   IN p_description varchar(1000))
BEGIN
    UPDATE role
    SET name = p_name,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE role_id = p_role_id;
END;

