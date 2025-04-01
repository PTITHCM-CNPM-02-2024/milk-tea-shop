DELIMITER //

-- Thêm đơn vị tính mới
CREATE PROCEDURE sp_insert_product_unit(
    IN p_name VARCHAR(50),
    IN p_abbreviation VARCHAR(10)
)
BEGIN
    -- Kiểm tra tên đơn vị đã tồn tại chưa
    DECLARE unit_exists BOOLEAN;
    SELECT EXISTS(SELECT 1 FROM ProductUnit WHERE name = p_name) INTO unit_exists;
    
    IF unit_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên đơn vị đã tồn tại';
    ELSE
        -- Kiểm tra viết tắt đã tồn tại chưa
        SELECT EXISTS(SELECT 1 FROM ProductUnit WHERE abbreviation = p_abbreviation) INTO unit_exists;
        
        IF unit_exists THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Viết tắt đơn vị đã tồn tại';
        ELSE
            INSERT INTO ProductUnit(name, abbreviation)
            VALUES(p_name, p_abbreviation);
            
            SELECT LAST_INSERT_ID() AS unit_id;
        END IF;
    END IF;
END //

-- Cập nhật đơn vị tính
CREATE PROCEDURE sp_update_product_unit(
    IN p_unit_id TINYINT UNSIGNED,
    IN p_name VARCHAR(50),
    IN p_abbreviation VARCHAR(10)
)
BEGIN
    -- Kiểm tra tên đơn vị đã tồn tại chưa
    DECLARE name_exists BOOLEAN;
    SELECT EXISTS(
        SELECT 1 FROM ProductUnit 
        WHERE name = p_name AND unit_id != p_unit_id
    ) INTO name_exists;
    
    IF name_exists THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên đơn vị đã tồn tại';
    ELSE
        -- Kiểm tra viết tắt đã tồn tại chưa
        DECLARE abbr_exists BOOLEAN;
        SELECT EXISTS(
            SELECT 1 FROM ProductUnit 
            WHERE abbreviation = p_abbreviation AND unit_id != p_unit_id
        ) INTO abbr_exists;
        
        IF abbr_exists THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Viết tắt đơn vị đã tồn tại';
        ELSE
            UPDATE ProductUnit
            SET name = p_name,
                abbreviation = p_abbreviation,
                updated_at = CURRENT_TIMESTAMP
            WHERE unit_id = p_unit_id;
            
            SELECT ROW_COUNT() > 0 AS success;
        END IF;
    END IF;
END //

-- Xóa đơn vị tính
CREATE PROCEDURE sp_delete_product_unit(
    IN p_unit_id TINYINT UNSIGNED
)
BEGIN
    DECLARE unit_used BOOLEAN;
    
    -- Kiểm tra xem đơn vị đã được sử dụng cho nguyên liệu chưa
    SELECT EXISTS(SELECT 1 FROM Ingredient WHERE unit_id = p_unit_id) INTO unit_used;
    
    IF unit_used THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể xóa đơn vị đã được sử dụng cho nguyên liệu';
    ELSE
        DELETE FROM ProductUnit WHERE unit_id = p_unit_id;
        SELECT ROW_COUNT() > 0 AS success;
    END IF;
END //

-- Lấy tất cả đơn vị tính
CREATE PROCEDURE sp_get_all_product_units()
BEGIN
    SELECT * FROM ProductUnit ORDER BY name;
END //

-- Lấy đơn vị tính theo ID
CREATE PROCEDURE sp_get_product_unit_by_id(
    IN p_unit_id TINYINT UNSIGNED
)
BEGIN
    SELECT * FROM ProductUnit WHERE unit_id = p_unit_id;
END //

DELIMITER ; 