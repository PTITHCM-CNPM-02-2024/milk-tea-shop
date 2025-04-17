create procedure milk_tea_shop_prod.sp_insert_role(IN p_name varchar(50), IN p_description varchar(1000),
                                                   OUT p_role_id tinyint unsigned)
BEGIN
    INSERT INTO role (name, description)
    VALUES (p_name, p_description);
    SET p_role_id = LAST_INSERT_ID();
END;

