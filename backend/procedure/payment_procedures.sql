
DELIMITER //

-- Tạo thanh toán mới
CREATE PROCEDURE sp_insert_payment(
    IN p_order_id INT UNSIGNED,
    IN p_payment_method_id TINYINT UNSIGNED,
    IN p_status ENUM('PROCESSING', 'CANCELLED', 'PAID'),
    IN p_amount_paid DECIMAL(11, 3)
)
BEGIN
    DECLARE final_amount_val DECIMAL(11, 3);
    DECLARE change_amount DECIMAL(11, 3);

    -- Lấy tổng tiền đơn hàng
    SELECT final_amount INTO final_amount_val
    FROM `Order`
    WHERE order_id = p_order_id;

    -- Tính tiền thừa
    SET change_amount = p_amount_paid - final_amount_val;
    IF change_amount < 0 THEN
        SET change_amount = 0;
    END IF;

    -- Kiểm tra nếu thanh toán đủ, cập nhật trạng thái
    IF p_status IS NULL THEN
        IF p_amount_paid >= final_amount_val THEN
            SET p_status = 'PAID';
        ELSE
            SET p_status = 'PROCESSING';
        END IF;
    END IF;

    -- Tạo thanh toán
    INSERT INTO Payment(
        order_id, payment_method_id, status,
        amount_paid, change_amount, payment_time
    )
    VALUES(
              p_order_id, p_payment_method_id, p_status,
              p_amount_paid, change_amount, CURRENT_TIMESTAMP
          );

    -- Nếu thanh toán đủ, cập nhật trạng thái đơn hàng
    IF p_status = 'PAID' THEN
        UPDATE `Order`
        SET status = 'COMPLETED'
        WHERE order_id = p_order_id;

        -- Cộng điểm cho khách hàng
        CALL sp_add_points_for_completed_order(p_order_id);
    END IF;

    SELECT LAST_INSERT_ID() AS payment_id;
END //

-- Cập nhật thanh toán
CREATE PROCEDURE sp_update_payment(
    IN p_payment_id INT UNSIGNED,
    IN p_status ENUM('PROCESSING', 'CANCELLED', 'PAID'),
    IN p_amount_paid DECIMAL(11, 3)
)
BEGIN
    DECLARE order_id_val INT UNSIGNED;
    DECLARE final_amount_val DECIMAL(11, 3);
    DECLARE change_amount DECIMAL(11, 3);
    DECLARE current_amount DECIMAL(11, 3);

    -- Lấy thông tin thanh toán và đơn hàng
    SELECT p.order_id, p.amount_paid, o.final_amount
    INTO order_id_val, current_amount, final_amount_val
    FROM Payment p
             JOIN `Order` o ON p.order_id = o.order_id
    WHERE p.payment_id = p_payment_id;

    -- Tính tiền thừa nếu cập nhật số tiền thanh toán
    IF p_amount_paid IS NOT NULL AND p_amount_paid != current_amount THEN
        SET change_amount = p_amount_paid - final_amount_val;
        IF change_amount < 0 THEN
            SET change_amount = 0;
        END IF;
    ELSE
        -- Giữ nguyên số tiền hiện tại
        SET p_amount_paid = current_amount;
        SET change_amount = NULL;  -- Giữ nguyên giá trị hiện tại
    END IF;

    -- Cập nhật thanh toán
    UPDATE Payment
    SET status = COALESCE(p_status, status),
        amount_paid = COALESCE(p_amount_paid, amount_paid),
        change_amount = COALESCE(change_amount, change_amount),
        payment_time = CURRENT_TIMESTAMP,
        updated_at = CURRENT_TIMESTAMP
    WHERE payment_id = p_payment_id;

    -- Nếu đã thanh toán đủ, cập nhật trạng thái đơn hàng
    IF (p_status = 'PAID' OR (p_status IS NULL AND p_amount_paid >= final_amount_val)) THEN
        UPDATE `Order`
        SET status = 'COMPLETED'
        WHERE order_id = order_id_val;

        -- Cộng điểm cho khách hàng
        CALL sp_add_points_for_completed_order(order_id_val);
    END IF;

    SELECT TRUE AS success;
END //

-- Hủy thanh toán
CREATE PROCEDURE sp_cancel_payment(
    IN p_payment_id INT UNSIGNED
)
BEGIN
    UPDATE Payment
    SET status = 'CANCELLED',
        updated_at = CURRENT_TIMESTAMP
    WHERE payment_id = p_payment_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy thanh toán theo đơn hàng
CREATE PROCEDURE sp_get_payments_by_order(
    IN p_order_id INT UNSIGNED
)
BEGIN
    SELECT p.*, pm.payment_name AS payment_method_name
    FROM Payment p
             JOIN PaymentMethod pm ON p.payment_method_id = pm.payment_method_id
    WHERE p.order_id = p_order_id
    ORDER BY p.payment_time DESC;
END //

-- Lấy thanh toán theo ID
CREATE PROCEDURE sp_get_payment_by_id(
    IN p_payment_id INT UNSIGNED
)
BEGIN
    SELECT p.*, pm.payment_name AS payment_method_name
    FROM Payment p
             JOIN PaymentMethod pm ON p.payment_method_id = pm.payment_method_id
    WHERE p.payment_id = p_payment_id;
END //

DELIMITER ;
