create procedure milk_tea_shop_prod.sp_update_employee(IN p_employee_id int unsigned, IN p_account_id int unsigned,
                                                       IN p_position varchar(50), IN p_last_name varchar(70),
                                                       IN p_first_name varchar(70),
                                                       IN p_gender enum ('MALE', 'FEMALE', 'OTHER'),
                                                       IN p_phone varchar(15), IN p_email varchar(100))
BEGIN
    UPDATE employee
    SET account_id = p_account_id,
        position = p_position,
        last_name = p_last_name,
        first_name = p_first_name,
        gender = p_gender,
        phone = p_phone,
        email = p_email,
        updated_at = CURRENT_TIMESTAMP
    WHERE employee_id = p_employee_id;
END;

