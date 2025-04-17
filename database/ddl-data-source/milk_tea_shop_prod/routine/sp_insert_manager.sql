create procedure milk_tea_shop_prod.sp_insert_manager(IN p_account_id int unsigned, IN p_last_name varchar(70),
                                                      IN p_first_name varchar(70),
                                                      IN p_gender enum ('MALE', 'FEMALE', 'OTHER'),
                                                      IN p_phone varchar(15), IN p_email varchar(100),
                                                      OUT p_manager_id int unsigned)
BEGIN
    INSERT INTO manager (account_id, last_name, first_name, gender, phone, email)
    VALUES (p_account_id, p_last_name, p_first_name, p_gender, p_phone, p_email);
    SET p_manager_id = LAST_INSERT_ID();
END;

