create procedure milk_tea_shop_prod.sp_update_membership_type(IN p_membership_type_id tinyint unsigned,
                                                              IN p_type varchar(50), IN p_discount_value decimal(10, 3),
                                                              IN p_discount_unit enum ('FIXED', 'PERCENTAGE'),
                                                              IN p_required_point int, IN p_description varchar(255),
                                                              IN p_valid_until datetime, IN p_is_active tinyint(1))
BEGIN
    UPDATE membership_type
    SET type = p_type,
        discount_value = p_discount_value,
        discount_unit = p_discount_unit,
        required_point = p_required_point,
        description = p_description,
        valid_until = p_valid_until,
        is_active = p_is_active,
        updated_at = CURRENT_TIMESTAMP
    WHERE membership_type_id = p_membership_type_id;
END;

