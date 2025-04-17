create procedure milk_tea_shop_prod.sp_update_store(IN p_store_id tinyint unsigned, IN p_name varchar(100),
                                                    IN p_address varchar(255), IN p_phone varchar(15),
                                                    IN p_opening_time time, IN p_closing_time time,
                                                    IN p_email varchar(100), IN p_opening_date date,
                                                    IN p_tax_code varchar(20))
BEGIN
    UPDATE store
    SET name = p_name,
        address = p_address,
        phone = p_phone,
        opening_time = p_opening_time,
        closing_time = p_closing_time,
        email = p_email,
        opening_date = p_opening_date,
        tax_code = p_tax_code,
        updated_at = CURRENT_TIMESTAMP
    WHERE store_id = p_store_id;
END;

