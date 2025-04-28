create procedure milk_tea_shop_prod.sp_update_account_details(IN p_account_id int unsigned,
                                                              IN p_role_id tinyint unsigned, IN p_username varchar(50),
                                                              IN p_is_active tinyint(1), IN p_is_locked tinyint(1))
BEGIN
    UPDATE account
    SET role_id = p_role_id,
        username = p_username,
        is_active = p_is_active,
        is_locked = p_is_locked,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;
END;

