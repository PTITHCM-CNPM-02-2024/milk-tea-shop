-- 14. Order
DELIMITER //

-- Kiểm tra trước khi thêm đơn hàng
CREATE TRIGGER before_order_insert
    BEFORE INSERT ON `Order`
    FOR EACH ROW
BEGIN
    -- Đặt thời gian mặc định nếu chưa có
    IF NEW.order_time IS NULL THEN
        SET NEW.order_time = CURRENT_TIMESTAMP;
    END IF;

    -- Đặt trạng thái mặc định nếu chưa có
    IF NEW.status IS NULL THEN
        SET NEW.status = 'PROCESSING';
    END IF;

    -- Đặt điểm mặc định
    IF NEW.point IS NULL THEN
        SET NEW.point = 0;
    END IF;

    -- Đặt tổng tiền mặc định
    IF NEW.total_amount IS NULL THEN
        SET NEW.total_amount = 0;
    END IF;

    -- Đặt thành tiền mặc định
    IF NEW.final_amount IS NULL THEN
        SET NEW.final_amount = 0;
    END IF;
END //

DELIMITER ;

DELIMITER //

-- Kiểm tra trước khi đặt bàn
CREATE TRIGGER before_order_table_insert
    BEFORE INSERT ON OrderTable
    FOR EACH ROW
BEGIN
    -- Kiểm tra bàn có trống không
    DECLARE table_occupied BOOLEAN;

    SELECT EXISTS (
        SELECT 1
        FROM OrderTable ot
                 JOIN `Order` o ON ot.order_id = o.order_id
        WHERE ot.table_id = NEW.table_id
          AND ot.check_out IS NULL
          AND o.status = 'PROCESSING'
    ) INTO table_occupied;

    IF table_occupied THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Bàn đã được sử dụng';
    END IF;

    -- Đặt thời gian vào mặc định
    IF NEW.check_in IS NULL THEN
        SET NEW.check_in = NOW();
    END IF;
END //

-- Kiểm tra trước khi cập nhật đặt bàn
CREATE TRIGGER before_order_table_update
    BEFORE UPDATE ON OrderTable
    FOR EACH ROW
BEGIN
    -- Ngăn cập nhật check_in
    IF NEW.check_in != OLD.check_in THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không được phép thay đổi thời gian check-in';
    END IF;

    -- Ngăn cập nhật lại check_out sau khi đã checkout
    IF OLD.check_out IS NOT NULL AND NEW.check_out != OLD.check_out THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không được phép thay đổi thời gian check-out sau khi đã checkout';
    END IF;
END //

DELIMITER ;

DELIMITER //

-- Kiểm tra trước khi thêm sản phẩm vào đơn hàng
CREATE TRIGGER before_order_product_insert
    BEFORE INSERT ON OrderProduct
    FOR EACH ROW
BEGIN
    -- Kiểm tra số lượng phải lớn hơn 0
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lượng sản phẩm phải lớn hơn 0';
    END IF;
END //

-- Kiểm tra trước khi cập nhật sản phẩm trong đơn hàng
CREATE TRIGGER before_order_product_update
    BEFORE UPDATE ON OrderProduct
    FOR EACH ROW
BEGIN
    -- Kiểm tra số lượng phải lớn hơn 0
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lượng sản phẩm phải lớn hơn 0';
    END IF;
END //

-- Cập nhật tổng tiền sau khi thêm sản phẩm
CREATE TRIGGER after_order_product_insert
    AFTER INSERT ON OrderProduct
    FOR EACH ROW
BEGIN
    -- Cập nhật tổng tiền đơn hàng
    CALL sp_update_order_total(NEW.order_id);
END //

-- Cập nhật tổng tiền sau khi cập nhật sản phẩm
CREATE TRIGGER after_order_product_update
    AFTER UPDATE ON OrderProduct
    FOR EACH ROW
BEGIN
    -- Cập nhật tổng tiền đơn hàng
    CALL sp_update_order_total(NEW.order_id);
END //

-- Cập nhật tổng tiền sau khi xóa sản phẩm
CREATE TRIGGER after_order_product_delete
    AFTER DELETE ON OrderProduct
    FOR EACH ROW
BEGIN
    -- Cập nhật tổng tiền đơn hàng
    CALL sp_update_order_total(OLD.order_id);
END //

DELIMITER //

-- Thủ tục cập nhật tổng tiền đơn hàng
CREATE PROCEDURE sp_update_order_total(
    IN p_order_id INT UNSIGNED
)
BEGIN
    DECLARE total_amount DECIMAL(11, 3);

    -- Tính tổng tiền từ các sản phẩm
    SELECT COALESCE(SUM(op.quantity * pp.price), 0) INTO total_amount
    FROM OrderProduct op
             JOIN ProductPrice pp ON op.product_price_id = pp.product_price_id
    WHERE op.order_id = p_order_id;

    -- Áp dụng khuyến mãi nếu có
    SELECT total_amount - COALESCE(SUM(
                                           CASE
                                               WHEN d.discount_type = 'PERCENTAGE' THEN total_amount * d.discount_value / 100
                                               ELSE d.discount_value
                                               END
                                   ), 0) INTO total_amount
    FROM `Order` o
             LEFT JOIN OrderDiscount od ON o.order_id = od.order_id
             LEFT JOIN Discount d ON od.discount_id = d.discount_id
    WHERE o.order_id = p_order_id;

    -- Cập nhật tổng tiền
    UPDATE `Order`
    SET total_amount = total_amount,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = p_order_id;
END//

-- Thủ tục tạo đơn hàng mới
CREATE PROCEDURE sp_create_order(
    IN p_order_id INT UNSIGNED,
    IN p_customer_id INT UNSIGNED,
    IN p_employee_id INT UNSIGNED,
    IN p_status ENUM('PROCESSING', 'COMPLETED', 'CANCELLED'),
    IN p_total_amount DECIMAL(11, 3),
    IN p_final_amount DECIMAL(11, 3),
    IN p_point INT UNSIGNED,
    IN p_order_time DATETIME
)
BEGIN
    INSERT INTO `Order` (customer_id, employee_id, status, total_amount, final_amount, point, order_time  )
    VALUES (p_customer_id, p_employee_id, p_status, p_total_amount, p_final_amount, p_point, p_order_time);

    SET p_order_id = LAST_INSERT_ID();

    SELECT p_order_id AS order_id;
END//

-- Thủ tục thêm sản phẩm vào đơn hàng
CREATE PROCEDURE sp_add_product_to_order(
    IN p_order_id INT UNSIGNED,
    IN p_product_price_id INT UNSIGNED,
    IN p_quantity SMALLINT UNSIGNED,
    IN p_option VARCHAR(500)
)
BEGIN
    -- Kiểm tra xem sản phẩm đã có trong đơn hàng chưa
    DECLARE order_product_id_exists INT UNSIGNED;

    SELECT order_product_id_exists INTO order_product_id_exists
    FROM OrderProduct
    WHERE order_id = p_order_id AND product_price_id = p_product_price_id;

    -- Nếu đã có thì cập nhật số lượng
    IF order_product_id_exists IS NOT NULL THEN
        UPDATE OrderProduct
        SET quantity = quantity + p_quantity,
            `option` = p_option
        WHERE order_product_id = order_product_id_exists;
    ELSE
        -- Nếu chưa có thì thêm mới
        INSERT INTO OrderProduct (order_id, product_price_id, quantity, `option`)
        VALUES (p_order_id, p_product_price_id, p_quantity, p_option);
    END IF;

    -- Cập nhật tổng tiền đơn hàng
    CALL sp_update_order_total(p_order_id);

    SELECT TRUE AS success;
END//

-- Thủ tục cập nhật trạng thái đơn hàng
CREATE PROCEDURE sp_update_order_status(
    IN p_order_id INT UNSIGNED,
    IN p_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'CANCELLED')
)
BEGIN
    DECLARE curr_status VARCHAR(20);

    -- Kiểm tra trạng thái hiện tại
    SELECT status INTO curr_status
    FROM `Order`
    WHERE order_id = p_order_id;

    -- Kiểm tra logic chuyển trạng thái
    IF (curr_status = 'CANCELLED') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi trạng thái đơn hàng đã hủy';
    ELSEIF (curr_status = 'COMPLETED' AND p_status != 'COMPLETED') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi trạng thái đơn hàng đã hoàn thành';
    END IF;

    -- Cập nhật trạng thái
    UPDATE `Order`
    SET status = p_status,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = p_order_id;

    -- Nếu đơn hàng hoàn thành, cộng điểm cho khách hàng
    IF p_status = 'COMPLETED' THEN
        CALL sp_add_points_for_completed_order(p_order_id);
    END IF;

    SELECT TRUE AS success;
END//

-- Thủ tục cộng điểm cho khách hàng khi hoàn thành đơn hàng
CREATE PROCEDURE sp_add_points_for_completed_order(
    IN p_order_id INT UNSIGNED
)
BEGIN
    DECLARE customer_id_val INT UNSIGNED;
    DECLARE total_amount_val DECIMAL(11, 3);
    DECLARE points_to_add INT;

    -- Lấy thông tin đơn hàng
    SELECT customer_id, total_amount INTO customer_id_val, total_amount_val
    FROM `Order`
    WHERE order_id = p_order_id;

    SET points_to_add = 1;

    -- Cộng điểm cho khách hàng
    IF customer_id_val IS NOT NULL AND points_to_add > 0 THEN
        CALL sp_add_customer_points(customer_id_val, points_to_add);
    END IF;
END//

DELIMITER ;

DELIMITER //

-- Kiểm tra trước khi thêm giảm giá đơn hàng
CREATE TRIGGER before_order_discount_insert
    BEFORE INSERT ON OrderDiscount
    FOR EACH ROW
BEGIN
    DECLARE order_exists BOOLEAN;
    DECLARE discount_exists BOOLEAN;
    DECLARE discount_active BOOLEAN;
    DECLARE discount_valid BOOLEAN;
    DECLARE order_status VARCHAR(20);
    DECLARE order_total DECIMAL(11, 3);
    DECLARE min_order_value DECIMAL(11, 3);
    DECLARE valid_from DATETIME;
    DECLARE valid_until DATETIME;
    DECLARE existing_discount BOOLEAN;
    DECLARE total_discount DECIMAL(11, 3);

    -- Kiểm tra đơn hàng tồn tại
    SELECT EXISTS(SELECT 1 FROM `Order` WHERE order_id = NEW.order_id),
           status, total_amount
    INTO order_exists, order_status, order_total
    FROM `Order`
    WHERE order_id = NEW.order_id;

    IF NOT order_exists THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Đơn hàng không tồn tại';
    END IF;

    -- Kiểm tra đơn hàng chưa hoàn thành hoặc hủy
    IF order_status = 'COMPLETED' OR order_status = 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thêm giảm giá cho đơn hàng đã hoàn thành hoặc hủy';
    END IF;

    -- Kiểm tra giảm giá tồn tại
    SELECT EXISTS(SELECT 1 FROM Discount WHERE discount_id = NEW.discount_id),
           is_active,
           valid_from,
           valid_until,
           min_required_order_value
    INTO discount_exists, discount_active, valid_from, valid_until, min_order_value
    FROM Discount
    WHERE discount_id = NEW.discount_id;

    IF NOT discount_exists THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá không tồn tại';
    END IF;

    -- Kiểm tra giảm giá có hoạt động
    IF NOT discount_active THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá không hoạt động';
    END IF;

    -- Kiểm tra giảm giá còn hạn sử dụng
    SET discount_valid = (valid_from IS NULL OR valid_from <= NOW()) AND (valid_until IS NULL OR valid_until >= NOW());

    IF NOT discount_valid THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá đã hết hạn';
    END IF;

    -- Kiểm tra giá trị đơn hàng tối thiểu
    IF order_total < min_order_value THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị đơn hàng chưa đạt mức tối thiểu để áp dụng giảm giá';
    END IF;

    -- Kiểm tra đơn hàng đã có mã giảm giá này chưa
    SELECT EXISTS(
        SELECT 1 FROM OrderDiscount
        WHERE order_id = NEW.order_id AND discount_id = NEW.discount_id
    ) INTO existing_discount;

    IF existing_discount THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Đơn hàng đã áp dụng mã giảm giá này';
    END IF;

    -- Kiểm tra số tiền giảm giá không âm
    IF NEW.discount_amount < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền giảm giá không thể là số âm';
    END IF;

    -- Kiểm tra số tiền giảm giá không lớn hơn tổng tiền đơn hàng
    SELECT COALESCE(SUM(discount_amount), 0) INTO total_discount
    FROM OrderDiscount
    WHERE order_id = NEW.order_id;

    IF total_discount + NEW.discount_amount > order_total THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tổng số tiền giảm giá không thể lớn hơn tổng tiền đơn hàng';
    END IF;
END //

-- Kiểm tra trước khi cập nhật giảm giá đơn hàng
CREATE TRIGGER before_order_discount_update
    BEFORE UPDATE ON OrderDiscount
    FOR EACH ROW
BEGIN
    DECLARE order_status VARCHAR(20);
    DECLARE order_total DECIMAL(11, 3);
    DECLARE total_discount DECIMAL(11, 3);

    -- Kiểm tra đơn hàng chưa hoàn thành hoặc hủy
    SELECT status, total_amount INTO order_status, order_total
    FROM `Order`
    WHERE order_id = NEW.order_id;

    IF order_status = 'COMPLETED' OR order_status = 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể cập nhật giảm giá cho đơn hàng đã hoàn thành hoặc hủy';
    END IF;

    -- Kiểm tra số tiền giảm giá không âm
    IF NEW.discount_amount < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền giảm giá không thể là số âm';
    END IF;

    -- Kiểm tra số tiền giảm giá không lớn hơn tổng tiền đơn hàng
    SELECT COALESCE(SUM(discount_amount), 0) - OLD.discount_amount INTO total_discount
    FROM OrderDiscount
    WHERE order_id = NEW.order_id;

    IF total_discount + NEW.discount_amount > order_total THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tổng số tiền giảm giá không thể lớn hơn tổng tiền đơn hàng';
    END IF;
END //

-- Cập nhật tổng tiền sau khi thêm/cập nhật/xóa giảm giá
CREATE TRIGGER after_order_discount_insert
    AFTER INSERT ON OrderDiscount
    FOR EACH ROW
BEGIN
    UPDATE `Order`
    SET final_amount = total_amount - (
        SELECT COALESCE(SUM(discount_amount), 0.000)
        FROM OrderDiscount
        WHERE order_id = NEW.order_id
    ),
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = NEW.order_id;
END //

CREATE TRIGGER after_order_discount_update
    AFTER UPDATE ON OrderDiscount
    FOR EACH ROW
BEGIN
    UPDATE `Order`
    SET final_amount = total_amount - (
        SELECT COALESCE(SUM(discount_amount), 0.000)
        FROM OrderDiscount
        WHERE order_id = NEW.order_id
    ),
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = NEW.order_id;
END //

CREATE TRIGGER after_order_discount_delete
    AFTER DELETE ON OrderDiscount
    FOR EACH ROW
BEGIN
    UPDATE `Order`
    SET final_amount = total_amount - (
        SELECT COALESCE(SUM(discount_amount), 0.000)
        FROM OrderDiscount
        WHERE order_id = OLD.order_id
    ),
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = OLD.order_id;

    -- Giảm số lần sử dụng của mã giảm giá
    UPDATE Discount
    SET current_uses = GREATEST(COALESCE(current_uses, 0) - 1, 0)
    WHERE discount_id = OLD.discount_id;
END //

DELIMITER ;

DELIMITER //

-- Thêm giảm giá cho đơn hàng
CREATE PROCEDURE sp_insert_order_discount(
    IN p_order_id INT UNSIGNED,
    IN p_discount_id INT UNSIGNED,
    IN p_discount_amount DECIMAL(11, 3)
)
BEGIN
    INSERT INTO OrderDiscount(order_id, discount_id, discount_amount)
    VALUES(p_order_id, p_discount_id, p_discount_amount);

    SELECT LAST_INSERT_ID() AS order_discount_id;
END //

-- Cập nhật thông tin giảm giá đơn hàng
CREATE PROCEDURE sp_update_order_discount(
    IN p_order_discount_id INT UNSIGNED,
    IN p_discount_amount DECIMAL(11, 3)
)
BEGIN
    UPDATE OrderDiscount
    SET discount_amount = p_discount_amount,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_discount_id = p_order_discount_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa giảm giá đơn hàng
CREATE PROCEDURE sp_delete_order_discount(
    IN p_order_discount_id INT UNSIGNED
)
BEGIN
    DELETE FROM OrderDiscount WHERE order_discount_id = p_order_discount_id;
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa tất cả giảm giá của một đơn hàng
CREATE PROCEDURE sp_delete_order_discounts(
    IN p_order_id INT UNSIGNED
)
BEGIN
    DELETE FROM OrderDiscount WHERE order_id = p_order_id;
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy giảm giá đơn hàng theo ID
CREATE PROCEDURE sp_get_order_discount_by_id(
    IN p_order_discount_id INT UNSIGNED
)
BEGIN
    SELECT od.*, d.name AS discount_name, d.discount_type
    FROM OrderDiscount od
             JOIN Discount d ON od.discount_id = d.discount_id
    WHERE od.order_discount_id = p_order_discount_id;
END //

-- Lấy tất cả giảm giá của một đơn hàng
CREATE PROCEDURE sp_get_order_discounts_by_order(
    IN p_order_id INT UNSIGNED
)
BEGIN
    SELECT od.*, d.name AS discount_name, d.discount_type
    FROM OrderDiscount od
             JOIN Discount d ON od.discount_id = d.discount_id
    WHERE od.order_id = p_order_id
    ORDER BY od.created_at;
END //

-- Tính tổng giảm giá của một đơn hàng
CREATE PROCEDURE sp_calculate_total_discount(
    IN p_order_id INT UNSIGNED
)
BEGIN
    DECLARE total_discount DECIMAL(11, 3);

    SELECT COALESCE(SUM(discount_amount), 0.000)
    INTO total_discount
    FROM OrderDiscount
    WHERE order_id = p_order_id;

    SELECT total_discount;
END //

-- Áp dụng giảm giá cho đơn hàng
CREATE PROCEDURE sp_apply_discount_to_order(
    IN p_order_id INT UNSIGNED,
    IN p_discount_id INT UNSIGNED
)
BEGIN
    DECLARE v_total_amount DECIMAL(11, 3);
    DECLARE v_discount_type ENUM('FIXED', 'PERCENTAGE');
    DECLARE v_discount_value DECIMAL(11, 3);
    DECLARE v_max_discount_amount DECIMAL(11, 3);
    DECLARE v_discount_amount DECIMAL(11, 3);
    DECLARE v_coupon_id INT UNSIGNED;
    DECLARE v_current_uses INT UNSIGNED;
    DECLARE v_max_uses INT UNSIGNED;
    DECLARE v_customer_id INT UNSIGNED;
    DECLARE v_max_uses_per_customer SMALLINT UNSIGNED;
    DECLARE v_customer_uses INT UNSIGNED;

    -- Lấy thông tin đơn hàng
    SELECT total_amount, customer_id INTO v_total_amount, v_customer_id
    FROM `Order`
    WHERE order_id = p_order_id;

    -- Lấy thông tin giảm giá
    SELECT
        discount_type,
        discount_value,
        max_discount_amount,
        current_uses,
        max_uses,
        max_uses_per_customer,
        coupon_id
    INTO
        v_discount_type,
        v_discount_value,
        v_max_discount_amount,
        v_current_uses,
        v_max_uses,
        v_max_uses_per_customer,
        v_coupon_id
    FROM Discount
    WHERE discount_id = p_discount_id;

    -- Kiểm tra số lần sử dụng tối đa
    IF v_max_uses IS NOT NULL AND v_current_uses >= v_max_uses THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá đã hết lượt sử dụng';
    END IF;

    -- Kiểm tra số lần sử dụng tối đa cho mỗi khách hàng
    IF v_customer_id IS NOT NULL AND v_max_uses_per_customer IS NOT NULL THEN
        SELECT COUNT(*) INTO v_customer_uses
        FROM `Order` o
                 JOIN OrderDiscount od ON o.order_id = od.order_id
        WHERE o.customer_id = v_customer_id
          AND od.discount_id = p_discount_id;

        IF v_customer_uses >= v_max_uses_per_customer THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Khách hàng đã sử dụng hết lượt giảm giá';
        END IF;
    END IF;

    -- Tính số tiền giảm giá
    IF v_discount_type = 'FIXED' THEN
        SET v_discount_amount = v_discount_value;
    ELSE -- PERCENTAGE
        SET v_discount_amount = v_total_amount * (v_discount_value / 100);
        -- Kiểm tra giới hạn số tiền giảm giá tối đa
        IF v_max_discount_amount IS NOT NULL AND v_discount_amount > v_max_discount_amount THEN
            SET v_discount_amount = v_max_discount_amount;
        END IF;
    END IF;

    -- Thêm giảm giá vào đơn hàng
    INSERT INTO OrderDiscount(order_id, discount_id, discount_amount)
    VALUES(p_order_id, p_discount_id, v_discount_amount);

    -- Cập nhật số lần sử dụng của giảm giá
    UPDATE Discount
    SET current_uses = COALESCE(current_uses, 0) + 1
    WHERE discount_id = p_discount_id;

    -- Cập nhật tổng tiền sau giảm giá
    UPDATE `Order`
    SET final_amount = total_amount - (
        SELECT COALESCE(SUM(discount_amount), 0.000)
        FROM OrderDiscount
        WHERE order_id = p_order_id
    )
    WHERE order_id = p_order_id;

    SELECT
        LAST_INSERT_ID() AS order_discount_id,
        v_discount_amount AS discount_amount;
END //

DELIMITER ;