create procedure milk_tea_shop_prod.sp_update_customer(IN p_customer_id int unsigned,
                                                       IN p_membership_type_id tinyint unsigned,
                                                       IN p_account_id int unsigned, IN p_last_name varchar(70),
                                                       IN p_first_name varchar(70), IN p_phone varchar(15),
                                                       IN p_email varchar(100), IN p_current_points int,
                                                       IN p_gender enum ('MALE', 'FEMALE', 'OTHER'))
BEGIN
    UPDATE customer
    SET membership_type_id = p_membership_type_id,
        account_id = p_account_id,
        last_name = p_last_name,
        first_name = p_first_name,
        phone = p_phone,
        email = p_email,
        current_points = p_current_points,
        gender = p_gender,
        updated_at = CURRENT_TIMESTAMP
    WHERE customer_id = p_customer_id;
END;

