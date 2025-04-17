create procedure milk_tea_shop_prod.sp_insert_account(IN p_role_id tinyint unsigned, IN p_username varchar(50),
                                                      IN p_password_hash varchar(255), IN p_is_active tinyint(1),
                                                      IN p_is_locked tinyint(1), OUT p_account_id int unsigned)
BEGIN
    INSERT INTO account (role_id, username, password_hash, is_active, is_locked, token_version)
    VALUES (p_role_id, p_username, p_password_hash, p_is_active, p_is_locked, 0); -- Initial token version 0
    SET p_account_id = LAST_INSERT_ID();
END;

