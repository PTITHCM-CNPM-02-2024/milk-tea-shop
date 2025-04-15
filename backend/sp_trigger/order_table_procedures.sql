DELIMITER //

-- Đặt bàn
CREATE PROCEDURE sp_assign_table_to_order(
    IN p_order_id INT UNSIGNED,
    IN p_table_id SMALLINT UNSIGNED
)
BEGIN
    DECLARE table_available BOOLEAN;
    
    -- Kiểm tra bàn có trống không
    SELECT NOT EXISTS (
        SELECT 1
        FROM OrderTable ot
        JOIN `Order` o ON ot.order_id = o.order_id
        WHERE ot.table_id = p_table_id
          AND ot.check_out IS NULL
          AND o.status = 'PROCESSING'
    ) INTO table_available;
    
    IF NOT table_available THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Bàn đã được sử dụng';
    ELSE
        -- Đặt bàn
        INSERT INTO OrderTable (order_id, table_id, check_in)
        VALUES (p_order_id, p_table_id, NOW());
        
        SELECT LAST_INSERT_ID() AS order_table_id;
    END IF;
END //

-- Checkout bàn
CREATE PROCEDURE sp_checkout_table(
    IN p_order_id INT UNSIGNED,
    IN p_table_id SMALLINT UNSIGNED
)
BEGIN
    UPDATE OrderTable
    SET check_out = NOW(),
        updated_at = CURRENT_TIMESTAMP
    WHERE order_id = p_order_id
      AND table_id = p_table_id
      AND check_out IS NULL;
    
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy thông tin bàn theo đơn hàng
CREATE PROCEDURE sp_get_tables_by_order(
    IN p_order_id INT UNSIGNED
)
BEGIN
    SELECT ot.*, 
           t.table_number,
           a.name AS area_name
    FROM OrderTable ot
    JOIN ServiceTable t ON ot.table_id = t.table_id
    JOIN Area a ON t.area_id = a.area_id
    WHERE ot.order_id = p_order_id;
END //

-- Lấy đơn hàng đang phục vụ tại bàn
CREATE PROCEDURE sp_get_active_order_by_table(
    IN p_table_id SMALLINT UNSIGNED
)
BEGIN
    SELECT o.*
    FROM `Order` o
    JOIN OrderTable ot ON o.order_id = ot.order_id
    WHERE ot.table_id = p_table_id
      AND ot.check_out IS NULL
      AND o.status = 'PROCESSING';
END //

DELIMITER ; 