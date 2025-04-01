DELIMITER //
-- Thủ tục tạo coupon và discount mới
CREATE PROCEDURE sp_create_discount(
    IN p_name VARCHAR(500),
    IN p_description VARCHAR(1000),
    IN p_coupon VARCHAR(15),
    IN p_discount_value DECIMAL(11, 3),
    IN p_discount_type ENUM('FIXED', 'PERCENTAGE'),
    IN p_min_required_order_value DECIMAL(11, 3),
    IN p_max_discount_amount DECIMAL(11, 3),
    IN p_valid_from DATETIME,
    IN p_valid_until DATETIME,
    IN p_max_uses INT UNSIGNED,
    IN p_max_uses_per_customer SMALLINT UNSIGNED
)
BEGIN
    DECLARE new_coupon_id INT UNSIGNED;

    -- Tạo coupon mới
    INSERT INTO Coupon (coupon, description)
    VALUES (p_coupon, p_description);

    SET new_coupon_id = LAST_INSERT_ID();

    -- Tạo discount liên kết với coupon
    INSERT INTO Discount (
        name, description, coupon_id, discount_value, discount_type,
        min_required_order_value, max_discount_amount, valid_from,
        valid_until, current_uses, max_uses, max_uses_per_customer, is_active
    )
    VALUES (
               p_name, p_description, new_coupon_id, p_discount_value, p_discount_type,
               p_min_required_order_value, p_max_discount_amount, p_valid_from,
               p_valid_until, 0, p_max_uses, p_max_uses_per_customer, 1
           );

    SELECT d.*, c.coupon
    FROM Discount d
             JOIN Coupon c ON d.coupon_id = c.coupon_id
    WHERE d.discount_id = LAST_INSERT_ID();
END//

-- Thủ tục kiểm tra mã giảm giá có hợp lệ không
CREATE PROCEDURE sp_validate_coupon(
    IN p_coupon VARCHAR(15),
    IN p_order_amount DECIMAL(11, 3),
    IN p_customer_id INT UNSIGNED
)
BEGIN
    DECLARE discount_id_val INT UNSIGNED;
    DECLARE coupon_id_val INT UNSIGNED;
    DECLARE customer_uses INT;

    -- Tìm coupon
    SELECT coupon_id INTO coupon_id_val
    FROM Coupon
    WHERE coupon = p_coupon;

    IF coupon_id_val IS NULL THEN
        SELECT FALSE AS valid, 'Mã giảm giá không tồn tại' AS message;
    ELSE
        -- Kiểm tra discount liên kết với coupon
        SELECT d.discount_id INTO discount_id_val
        FROM Discount d
        WHERE d.coupon_id = coupon_id_val
          AND d.is_active = 1
          AND d.valid_from <= NOW()
          AND d.valid_until >= NOW()
          AND (d.max_uses IS NULL OR d.current_uses < d.max_uses)
          AND d.min_required_order_value <= p_order_amount;

        IF discount_id_val IS NULL THEN
            SELECT FALSE AS valid, 'Mã giảm giá không hợp lệ hoặc đã hết hạn' AS message;
        ELSE
            -- Kiểm tra số lần khách hàng đã sử dụng
            IF p_customer_id IS NOT NULL THEN
                SELECT COUNT(*) INTO customer_uses
                FROM `Order` o
                         JOIN OrderDiscount od ON o.order_id = od.order_id
                WHERE o.customer_id = p_customer_id
                  AND od.discount_id = discount_id_val AND o.status = 'COMPLETED';

                -- Kiểm tra giới hạn sử dụng của khách hàng
                SELECT d.*,
                       (d.max_uses_per_customer IS NULL OR customer_uses < d.max_uses_per_customer) AS valid,
                       CASE
                           WHEN d.max_uses_per_customer IS NOT NULL AND customer_uses >= d.max_uses_per_customer
                               THEN 'Bạn đã vượt quá số lần sử dụng mã giảm giá này'
                           ELSE 'Mã giảm giá hợp lệ'
                           END AS message
                FROM Discount d
                WHERE d.discount_id = discount_id_val;
            ELSE
                SELECT d.*, TRUE AS valid, 'Mã giảm giá hợp lệ' AS message
                FROM Discount d
                WHERE d.discount_id = discount_id_val;
            END IF;
        END IF;
    END IF;
END//

DELIMITER ;