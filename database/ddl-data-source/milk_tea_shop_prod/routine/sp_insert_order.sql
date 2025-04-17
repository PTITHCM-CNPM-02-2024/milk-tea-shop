create procedure milk_tea_shop_prod.sp_insert_order(IN p_customer_id int unsigned, IN p_employee_id int unsigned,
                                                    IN p_order_time timestamp, IN p_total_amount decimal(11, 3),
                                                    IN p_final_amount decimal(11, 3),
                                                    IN p_status enum ('PROCESSING', 'CANCELLED', 'COMPLETED'),
                                                    IN p_customize_note varchar(1000), IN p_point int unsigned,
                                                    OUT p_order_id int unsigned)
BEGIN
    INSERT INTO `order` (customer_id, employee_id, order_time, total_amount, final_amount, status, customize_note, point)
    VALUES (p_customer_id, p_employee_id, p_order_time, p_total_amount, p_final_amount, p_status, p_customize_note, p_point);
    SET p_order_id = LAST_INSERT_ID();
END;

