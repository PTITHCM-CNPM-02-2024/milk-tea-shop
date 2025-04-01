DELIMITER //

-- Tạo đơn hàng mới
CREATE PROCEDURE sp_insert_order(
    IN p_customer_id INT UNSIGNED,
    IN p_employee_id INT UNSIGNED,
    IN p_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED'),
    IN p_customize_note VARCHAR(1000)
)
BEGIN
    INSERT INTO `Order`(
        customer_id, employee_id, order_time, total_amount, final_amount,
        status, customize_note, point
    )
    VALUES(
        p_customer_id, p_employee_id, CURRENT_TIMESTAMP, 0, 0,
        p_status, p_customize_note, 0
    );
    SELECT LAST_INSERT_ID() AS order_id;
END //

-- Cập nhật đơn hàng
CREATE PROCEDURE sp_update_order(
    IN p_order_id INT UNSIGNED,
    IN p_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED'),
    IN p_customize_note VARCHAR(1000)
)
BEGIN
    UPDATE `Order`
    SET status = p_status,
        customize_note = p_customize_note,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = p_order_id;
    
    -- Nếu đơn hàng hoàn thành, cộng điểm cho khách hàng
    IF p_status = 'COMPLETED' THEN
        CALL sp_add_points_for_completed_order(p_order_id);
    END IF;
    
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Cập nhật trạng thái đơn hàng
CREATE PROCEDURE sp_update_order_status(
    IN p_order_id INT UNSIGNED,
    IN p_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED')
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
    
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa đơn hàng (chỉ cho phép xóa đơn hàng chưa hoàn thành)
CREATE PROCEDURE sp_delete_order(
    IN p_order_id INT UNSIGNED
)
BEGIN
    DECLARE order_status VARCHAR(20);
    
    -- Kiểm tra trạng thái đơn hàng
    SELECT status INTO order_status FROM `Order` WHERE order_id = p_order_id;
    
    IF order_status = 'COMPLETED' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể xóa đơn hàng đã hoàn thành';
    ELSE
        -- Xóa các bản ghi liên quan
        DELETE FROM OrderProduct WHERE order_id = p_order_id;
        DELETE FROM OrderDiscount WHERE order_id = p_order_id;
        DELETE FROM OrderTable WHERE order_id = p_order_id;
        DELETE FROM Payment WHERE order_id = p_order_id;
        
        -- Xóa đơn hàng
        DELETE FROM `Order` WHERE order_id = p_order_id;
        
        SELECT ROW_COUNT() > 0 AS success;
    END IF;
END //

-- Cộng điểm cho khách hàng khi hoàn thành đơn hàng
CREATE PROCEDURE sp_add_points_for_completed_order(
    IN p_order_id INT UNSIGNED
)
BEGIN
    DECLARE customer_id_val INT UNSIGNED;
    DECLARE final_amount_val DECIMAL(11, 3);
    DECLARE points_to_add INT;
    
    -- Lấy thông tin đơn hàng
    SELECT customer_id, final_amount INTO customer_id_val, final_amount_val
    FROM `Order`
    WHERE order_id = p_order_id;
    
    -- Tính điểm (ví dụ: 10.000 VND = 1 điểm)
    SET points_to_add = FLOOR(final_amount_val / 10000);
    
    -- Cộng điểm cho khách hàng nếu có
    IF customer_id_val IS NOT NULL AND points_to_add > 0 THEN
        -- Cập nhật điểm đơn hàng
        UPDATE `Order`
        SET point = points_to_add
        WHERE order_id = p_order_id;
        
        -- Cộng điểm cho khách hàng
        CALL sp_add_customer_points(customer_id_val, points_to_add);
    END IF;
END //

-- Lấy đơn hàng theo ID
CREATE PROCEDURE sp_get_order_by_id(
    IN p_order_id INT UNSIGNED
)
BEGIN
    SELECT o.*, 
           CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
           CONCAT(e.first_name, ' ', e.last_name) AS employee_name
    FROM `Order` o
    LEFT JOIN Customer c ON o.customer_id = c.customer_id
    LEFT JOIN Employee e ON o.employee_id = e.employee_id
    WHERE o.order_id = p_order_id;
END //

-- Lấy tất cả đơn hàng
CREATE PROCEDURE sp_get_all_orders(
    IN p_limit INT,
    IN p_offset INT
)
BEGIN
    SELECT o.*, 
           CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
           CONCAT(e.first_name, ' ', e.last_name) AS employee_name
    FROM `Order` o
    LEFT JOIN Customer c ON o.customer_id = c.customer_id
    LEFT JOIN Employee e ON o.employee_id = e.employee_id
    ORDER BY o.order_time DESC
    LIMIT p_limit OFFSET p_offset;
END //

-- Lấy đơn hàng theo khách hàng
CREATE PROCEDURE sp_get_orders_by_customer(
    IN p_customer_id INT UNSIGNED
)
BEGIN
    SELECT o.*, 
           CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
           CONCAT(e.first_name, ' ', e.last_name) AS employee_name
    FROM `Order` o
    LEFT JOIN Customer c ON o.customer_id = c.customer_id
    LEFT JOIN Employee e ON o.employee_id = e.employee_id
    WHERE o.customer_id = p_customer_id
    ORDER BY o.order_time DESC;
END //

-- Tính tổng đơn hàng
CREATE PROCEDURE sp_update_order_total(
    IN p_order_id INT UNSIGNED
)
BEGIN
    DECLARE total_amount DECIMAL(11, 3);
    DECLARE discount_amount DECIMAL(11, 3);
    DECLARE final_amount DECIMAL(11, 3);
    
    -- Tính tổng tiền từ các sản phẩm
    SELECT COALESCE(SUM(op.quantity * pp.price), 0) INTO total_amount
    FROM OrderProduct op
    JOIN ProductPrice pp ON op.product_price_id = pp.product_price_id
    WHERE op.order_id = p_order_id;
    
    -- Tính tổng giảm giá
    SELECT COALESCE(SUM(discount_amount), 0) INTO discount_amount
    FROM OrderDiscount
    WHERE order_id = p_order_id;
    
    -- Tính số tiền cuối cùng
    SET final_amount = total_amount - discount_amount;
    IF final_amount < 0 THEN
        SET final_amount = 0;
    END IF;
    
    -- Cập nhật đơn hàng
    UPDATE `Order`
    SET total_amount = total_amount,
        final_amount = final_amount,
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = p_order_id;
END //

DELIMITER ; 