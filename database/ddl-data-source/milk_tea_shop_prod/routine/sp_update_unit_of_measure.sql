create procedure milk_tea_shop_prod.sp_update_unit_of_measure(IN p_unit_id smallint unsigned, IN p_name varchar(30),
                                                              IN p_symbol varchar(5), IN p_description varchar(1000))
BEGIN
    UPDATE unit_of_measure
    SET name = p_name,
        symbol = p_symbol,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE unit_id = p_unit_id;
END;

