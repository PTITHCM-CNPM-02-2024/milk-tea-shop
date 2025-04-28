create function milk_tea_shop_prod.fn_check_discount_applicable(p_discount_id int unsigned, p_order_time datetime) returns tinyint(1)
    deterministic reads sql data
BEGIN
    DECLARE is_applicable BOOLEAN DEFAULT FALSE;
    DECLARE v_is_active BOOLEAN;
    DECLARE v_valid_from DATETIME;
    DECLARE v_valid_until DATETIME;
    -- Add other variables for more complex checks if needed

    SELECT is_active, valid_from, valid_until
    INTO v_is_active, v_valid_from, v_valid_until
    FROM discount
    WHERE discount_id = p_discount_id;

    IF v_is_active IS NOT NULL AND v_is_active = TRUE THEN
        IF (v_valid_from IS NULL OR p_order_time >= v_valid_from) AND (p_order_time <= v_valid_until) THEN
            -- Add more checks here (min order value, usage limits etc.)
            -- For now, only checks active status and validity period
            SET is_applicable = TRUE;
        END IF;
    END IF;

    RETURN is_applicable;
END;

