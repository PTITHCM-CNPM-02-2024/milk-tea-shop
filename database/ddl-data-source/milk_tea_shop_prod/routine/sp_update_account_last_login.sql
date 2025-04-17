create procedure milk_tea_shop_prod.sp_update_account_last_login(IN p_account_id int unsigned)
BEGIN
    UPDATE account
    SET last_login = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;
END;

