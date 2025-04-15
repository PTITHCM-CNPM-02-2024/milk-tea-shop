DELIMITER //

-- Thêm giá sản phẩm mới
CREATE PROCEDURE sp_insert_product_price(
    IN p_product_id MEDIUMINT UNSIGNED,
    IN p_size_id SMALLINT UNSIGNED,
    IN p_price DECIMAL(11, 3)
)
BEGIN
    -- Kiểm tra sản phẩm tồn tại
    DECLARE product_exists BOOLEAN;
    SELECT EXISTS(SELECT 1 FROM Product WHERE product_id = p_product_id) INTO product_exists;
    
    IF NOT product_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Sản phẩm không tồn tại';
    END IF;
    
    -- Kiểm tra size tồn tại
    DECLARE size_exists BOOLEAN;
    SELECT EXISTS(SELECT 1 FROM ProductSize WHERE size_id = p_size_id) INTO size_exists;
    
    IF NOT size_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Kích thước không tồn tại';
    END IF;
    
    -- Kiểm tra nếu đã có giá cho sản phẩm với size này
    DECLARE price_exists BOOLEAN;
    SELECT EXISTS(
        SELECT 1 FROM ProductPrice 
        WHERE product_id = p_product_id AND size_id = p_size_id
    ) INTO price_exists;
    
    IF price_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Đã tồn tại giá cho sản phẩm với kích thước này';
    ELSE
        INSERT INTO ProductPrice(product_id, size_id, price)
        VALUES(p_product_id, p_size_id, p_price);
        
        SELECT LAST_INSERT_ID() AS product_price_id;
    END IF;
END //

-- Cập nhật giá sản phẩm
CREATE PROCEDURE sp_update_product_price(
    IN p_product_price_id INT UNSIGNED,
    IN p_price DECIMAL(11, 3)
)
BEGIN
    UPDATE ProductPrice
    SET price = p_price,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_price_id = p_product_price_id;
    
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy tất cả giá sản phẩm theo sản phẩm
CREATE PROCEDURE sp_get_product_prices(
    IN p_product_id MEDIUMINT UNSIGNED
)
BEGIN
    SELECT pp.*, ps.name AS size_name, u.symbol AS unit_symbol
    FROM ProductPrice pp
    JOIN ProductSize ps ON pp.size_id = ps.size_id
    JOIN UnitOfMeasure u ON ps.unit_id = u.unit_id
    WHERE pp.product_id = p_product_id;
END //

-- Lấy giá sản phẩm theo ID
CREATE PROCEDURE sp_get_product_price_by_id(
    IN p_product_price_id INT UNSIGNED
)
BEGIN
    SELECT pp.*, p.name AS product_name, ps.name AS size_name, u.symbol AS unit_symbol
    FROM ProductPrice pp
    JOIN Product p ON pp.product_id = p.product_id
    JOIN ProductSize ps ON pp.size_id = ps.size_id
    JOIN UnitOfMeasure u ON ps.unit_id = u.unit_id
    WHERE pp.product_price_id = p_product_price_id;
END //

DELIMITER ; 