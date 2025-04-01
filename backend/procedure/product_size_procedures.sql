DELIMITER //

-- Thêm kích thước sản phẩm mới
CREATE PROCEDURE sp_insert_product_size(
    IN p_unit_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(5),
    IN p_quantity SMALLINT UNSIGNED,
    IN p_description VARCHAR(1000)
)
BEGIN
    -- Kiểm tra đơn vị tính tồn tại
    DECLARE unit_exists BOOLEAN;
    SELECT EXISTS(SELECT 1 FROM UnitOfMeasure WHERE unit_id = p_unit_id) 
    INTO unit_exists;
    
    IF NOT unit_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Đơn vị tính không tồn tại';
    END IF;
    
    -- Kiểm tra tên kích thước đã tồn tại chưa
    DECLARE size_exists BOOLEAN;
    SELECT EXISTS(
        SELECT 1 FROM ProductSize 
        WHERE unit_id = p_unit_id AND name = p_name
    ) INTO size_exists;
    
    IF size_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Kích thước đã tồn tại cho đơn vị tính này';
    ELSE
        INSERT INTO ProductSize(unit_id, name, quantity, description)
        VALUES(p_unit_id, p_name, p_quantity, p_description);
        
        SELECT LAST_INSERT_ID() AS size_id;
    END IF;
END //

-- Cập nhật kích thước sản phẩm
CREATE PROCEDURE sp_update_product_size(
    IN p_size_id SMALLINT UNSIGNED,
    IN p_unit_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(5),
    IN p_quantity SMALLINT UNSIGNED,
    IN p_description VARCHAR(1000)
)
BEGIN
    -- Kiểm tra đơn vị tính tồn tại
    DECLARE unit_exists BOOLEAN;
    SELECT EXISTS(SELECT 1 FROM UnitOfMeasure WHERE unit_id = p_unit_id) 
    INTO unit_exists;
    
    IF NOT unit_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Đơn vị tính không tồn tại';
    END IF;
    
    -- Kiểm tra tên kích thước đã tồn tại chưa ở đơn vị khác
    DECLARE name_exists BOOLEAN;
    SELECT EXISTS(
        SELECT 1 FROM ProductSize 
        WHERE unit_id = p_unit_id AND name = p_name AND size_id != p_size_id
    ) INTO name_exists;
    
    IF name_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Kích thước đã tồn tại cho đơn vị tính này';
    ELSE
        UPDATE ProductSize
        SET unit_id = p_unit_id,
            name = p_name,
            quantity = p_quantity,
            description = p_description,
            updated_at = CURRENT_TIMESTAMP
        WHERE size_id = p_size_id;
        
        SELECT ROW_COUNT() > 0 AS success;
    END IF;
END //

-- Lấy tất cả kích thước sản phẩm
CREATE PROCEDURE sp_get_all_product_sizes()
BEGIN
    SELECT ps.*, u.name AS unit_name, u.symbol AS unit_symbol
    FROM ProductSize ps
    JOIN UnitOfMeasure u ON ps.unit_id = u.unit_id
    ORDER BY ps.name;
END //

-- Lấy kích thước sản phẩm theo ID
CREATE PROCEDURE sp_get_product_size_by_id(
    IN p_size_id SMALLINT UNSIGNED
)
BEGIN
    SELECT ps.*, u.name AS unit_name, u.symbol AS unit_symbol
    FROM ProductSize ps
    JOIN UnitOfMeasure u ON ps.unit_id = u.unit_id
    WHERE ps.size_id = p_size_id;
END //

DELIMITER ; 