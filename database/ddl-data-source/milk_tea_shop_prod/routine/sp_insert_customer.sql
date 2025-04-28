create procedure milk_tea_shop_prod.sp_insert_customer(IN p_membership_type_id tinyint unsigned,
                                                       IN p_account_id int unsigned, IN p_last_name varchar(70),
                                                       IN p_first_name varchar(70), IN p_phone varchar(15),
                                                       IN p_email varchar(100), IN p_current_points int,
                                                       IN p_gender enum ('MALE', 'FEMALE', 'OTHER'),
                                                       OUT p_customer_id int unsigned)
BEGIN
    INSERT INTO customer (membership_type_id, account_id, last_name, first_name, phone, email, current_points, gender)
    VALUES (p_membership_type_id, p_account_id, p_last_name, p_first_name, p_phone, p_email, p_current_points, p_gender);
    SET p_customer_id = LAST_INSERT_ID();
END;

