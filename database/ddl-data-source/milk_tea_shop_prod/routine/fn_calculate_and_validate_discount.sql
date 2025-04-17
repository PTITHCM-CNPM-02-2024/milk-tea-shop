create function milk_tea_shop_prod.fn_calculate_and_validate_discount(p_discount_id int unsigned,
                                                                      p_customer_id int unsigned,
                                                                      p_order_total_amount decimal(11, 3),
                                                                      p_order_time timestamp,
                                                                      p_accumulated_discount decimal(11, 3)) returns decimal(11, 3)
    deterministic reads sql data
BEGIN
    DECLARE v_applied_discount DECIMAL(11, 3) DEFAULT 0.000;
    DECLARE v_discount_value DECIMAL(11, 3);
    DECLARE v_discount_type ENUM('FIXED', 'PERCENTAGE');
    DECLARE v_max_discount_amount DECIMAL(11, 3);
    DECLARE v_min_required_order_value DECIMAL(11, 3);
    DECLARE v_max_uses_per_customer SMALLINT UNSIGNED;
    DECLARE v_customer_usage_count INT DEFAULT 0;
    DECLARE v_is_discount_applicable BOOLEAN;

    -- 1. Check basic applicability (dates, active status)
    SET v_is_discount_applicable = fn_check_discount_applicable(p_discount_id, p_order_time);

    IF v_is_discount_applicable THEN
        -- 2. Fetch discount details (bao gồm max_uses_per_customer)
        SELECT
            discount_value, discount_type, max_discount_amount,
            min_required_order_value, max_uses_per_customer
        INTO
            v_discount_value, v_discount_type, v_max_discount_amount,
            v_min_required_order_value, v_max_uses_per_customer
        FROM discount
        WHERE discount_id = p_discount_id;

        -- 3. Check max_uses_per_customer limit
        IF p_customer_id IS NOT NULL AND v_max_uses_per_customer IS NOT NULL THEN
            SELECT COUNT(*)
            INTO v_customer_usage_count
            FROM order_discount od
                     JOIN `order` o ON od.order_id = o.order_id
            WHERE od.discount_id = p_discount_id
              AND o.customer_id = p_customer_id
              AND o.status = 'COMPLETED'; -- Chỉ đếm các đơn đã hoàn thành

            IF v_customer_usage_count >= v_max_uses_per_customer THEN
                RETURN 0.000; -- Khách hàng đã hết lượt sử dụng, không áp dụng
            END IF;
        END IF;
        -- Kết thúc kiểm tra max_uses_per_customer

        -- 4. Check minimum order value requirement
        IF p_order_total_amount >= IFNULL(v_min_required_order_value, 0) THEN
            -- 5. Calculate potential discount amount based on type
            IF v_discount_type = 'PERCENTAGE' THEN
                SET v_applied_discount = (p_order_total_amount * v_discount_value) / 100;
                IF v_max_discount_amount IS NOT NULL AND v_applied_discount > v_max_discount_amount THEN
                    SET v_applied_discount = v_max_discount_amount;
                END IF;
            ELSE -- FIXED amount
                SET v_applied_discount = IFNULL(v_discount_value, 0);
            END IF;

            -- 6. Ensure applied discount does not exceed the remaining order value
            IF (p_accumulated_discount + v_applied_discount) > p_order_total_amount THEN
                SET v_applied_discount = p_order_total_amount - p_accumulated_discount;
            END IF;

            IF v_applied_discount < 0 THEN
                SET v_applied_discount = 0.000;
            END IF;

        END IF; -- End check min order value
    END IF; -- End check applicability

    RETURN v_applied_discount;
END;

