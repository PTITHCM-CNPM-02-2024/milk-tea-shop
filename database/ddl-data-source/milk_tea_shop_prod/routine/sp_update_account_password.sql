create procedure milk_tea_shop_prod.sp_update_account_password(IN p_account_id int unsigned, IN p_new_password_hash varchar(255))
BEGIN
    UPDATE account
    SET password_hash = p_new_password_hash,
        token_version = token_version + 1, -- Increment token version on password change
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;
END;

