create procedure milk_tea_shop_prod.sp_insert_discount(IN p_name varchar(500), IN p_description varchar(1000),
                                                       IN p_coupon_id int unsigned, IN p_discount_value decimal(11, 3),
                                                       IN p_discount_type enum ('FIXED', 'PERCENTAGE'),
                                                       IN p_min_required_order_value decimal(11, 3),
                                                       IN p_max_discount_amount decimal(11, 3),
                                                       IN p_min_required_product smallint unsigned,
                                                       IN p_valid_from datetime, IN p_valid_until datetime,
                                                       IN p_current_uses int unsigned, IN p_max_uses int unsigned,
                                                       IN p_max_uses_per_customer smallint unsigned,
                                                       IN p_is_active tinyint(1), OUT p_discount_id int unsigned)
BEGIN
    INSERT INTO discount (name, description, coupon_id, discount_value, discount_type, min_required_order_value, max_discount_amount, min_required_product, valid_from, valid_until, current_uses, max_uses, max_uses_per_customer, is_active)
    VALUES (p_name, p_description, p_coupon_id, p_discount_value, p_discount_type, p_min_required_order_value, p_max_discount_amount, p_min_required_product, p_valid_from, p_valid_until, p_current_uses, p_max_uses, p_max_uses_per_customer, p_is_active);
    SET p_discount_id = LAST_INSERT_ID();
END;

