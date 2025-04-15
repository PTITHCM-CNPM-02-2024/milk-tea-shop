-- Thêm sản phẩm mới vào đơn hàng
CREATE PROCEDURE sp_add_order_product(
    IN p_order_id INT UNSIGNED,
    IN p_product_price_id INT UNSIGNED,
    IN p_quantity SMALLINT UNSIGNED,
    IN p_option VARCHAR(1000)
)
BEGIN
    DECLARE order_exists BOOLEAN;
    
    -- Kiểm tra đơn hàng có tồn tại không
    SELECT EXISTS(SELECT 1 FROM `Order` WHERE order_id = p_order_id) INTO order_exists;
    
    IF NOT order_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Đơn hàng không tồn tại';
    ELSE
        -- Thêm sản phẩm mới vào đơn hàng
        INSERT INTO OrderProduct(order_id, product_price_id, quantity, `option`)
        VALUES(p_order_id, p_product_price_id, p_quantity, p_option);
    END IF;
    
    -- Cập nhật tổng tiền đơn hàng
    CALL sp_update_order_total(p_order_id);
    
    SELECT TRUE AS success;
END //

-- Cập nhật sản phẩm trong đơn hàng
CREATE PROCEDURE sp_update_order_product(
    IN p_order_product_id INT UNSIGNED,
    IN p_quantity SMALLINT UNSIGNED,
    IN p_option VARCHAR(1000)
)
BEGIN
    DECLARE order_id_val INT UNSIGNED;
    
    -- Lấy order_id
    SELECT order_id INTO order_id_val
    FROM OrderProduct
    WHERE order_product_id = p_order_product_id;
    
    IF order_id_val IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Sản phẩm không tồn tại trong đơn hàng';
    END IF;
    
    -- Cập nhật sản phẩm
    UPDATE OrderProduct
    SET quantity = p_quantity,
        `option` = p_option
    WHERE order_product_id = p_order_product_id;
    
    -- Cập nhật tổng tiền đơn hàng
    CALL sp_update_order_total(order_id_val);
    
    SELECT TRUE AS success;
END //

-- Xóa sản phẩm khỏi đơn hàng
CREATE PROCEDURE sp_delete_order_product(
    IN p_order_product_id INT UNSIGNED
)
BEGIN
    DECLARE order_id_val INT UNSIGNED;
    
    -- Lấy order_id
    SELECT order_id INTO order_id_val
    FROM OrderProduct
    WHERE order_product_id = p_order_product_id;
    
    IF order_id_val IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Sản phẩm không tồn tại trong đơn hàng';
    END IF;
    
    -- Xóa sản phẩm
    DELETE FROM OrderProduct
    WHERE order_product_id = p_order_product_id;
    
    -- Cập nhật tổng tiền đơn hàng
    CALL sp_update_order_total(order_id_val);
    
    SELECT TRUE AS success;
END //

-- Lấy tất cả sản phẩm trong đơn hàng
CREATE PROCEDURE sp_get_order_products(
    IN p_order_id INT UNSIGNED
)
BEGIN
    SELECT op.*, 
           pp.price, 
           p.name AS product_name,
           ps.name AS size_name,
           (op.quantity * pp.price) AS subtotal
    FROM OrderProduct op
    JOIN ProductPrice pp ON op.product_price_id = pp.product_price_id
    JOIN Product p ON pp.product_id = p.product_id
    JOIN ProductSize ps ON pp.size_id = ps.size_id
    WHERE op.order_id = p_order_id;
END //

DELIMITER ; 