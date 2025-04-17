create procedure milk_tea_shop_prod.sp_insert_store(IN p_name varchar(100), IN p_address varchar(255),
                                                    IN p_phone varchar(15), IN p_opening_time time,
                                                    IN p_closing_time time, IN p_email varchar(100),
                                                    IN p_opening_date date, IN p_tax_code varchar(20),
                                                    OUT p_store_id tinyint unsigned)
BEGIN
    INSERT INTO store (name, address, phone, opening_time, closing_time, email, opening_date, tax_code)
    VALUES (p_name, p_address, p_phone, p_opening_time, p_closing_time, p_email, p_opening_date, p_tax_code);
    SET p_store_id = LAST_INSERT_ID();
END;

