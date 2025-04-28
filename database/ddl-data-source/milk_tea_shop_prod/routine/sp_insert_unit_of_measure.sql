create procedure milk_tea_shop_prod.sp_insert_unit_of_measure(IN p_name varchar(30), IN p_symbol varchar(5),
                                                              IN p_description varchar(1000),
                                                              OUT p_unit_id smallint unsigned)
BEGIN
    INSERT INTO unit_of_measure (name, symbol, description)
    VALUES (p_name, p_symbol, p_description);
    SET p_unit_id = LAST_INSERT_ID();
END;

