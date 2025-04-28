create procedure milk_tea_shop_prod.sp_insert_membership_type(IN p_type varchar(50), IN p_discount_value decimal(10, 3),
                                                              IN p_discount_unit enum ('FIXED', 'PERCENTAGE'),
                                                              IN p_required_point int, IN p_description varchar(255),
                                                              IN p_valid_until datetime, IN p_is_active tinyint(1),
                                                              OUT p_membership_type_id tinyint unsigned)
BEGIN
    INSERT INTO membership_type (type, discount_value, discount_unit, required_point, description, valid_until, is_active)
    VALUES (p_type, p_discount_value, p_discount_unit, p_required_point, p_description, p_valid_until, p_is_active);
    SET p_membership_type_id = LAST_INSERT_ID();
END;

