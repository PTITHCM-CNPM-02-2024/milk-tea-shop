create procedure milk_tea_shop_prod.sp_update_manager(IN p_manager_id int unsigned, IN p_account_id int unsigned,
                                                      IN p_last_name varchar(70), IN p_first_name varchar(70),
                                                      IN p_gender enum ('MALE', 'FEMALE', 'OTHER'),
                                                      IN p_phone varchar(15), IN p_email varchar(100))
BEGIN
    UPDATE manager
    SET account_id = p_account_id,
        last_name = p_last_name,
        first_name = p_first_name,
        gender = p_gender,
        phone = p_phone,
        email = p_email,
        updated_at = CURRENT_TIMESTAMP
    WHERE manager_id = p_manager_id;
END;

