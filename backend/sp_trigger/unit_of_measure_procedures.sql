DELIMITER //

-- Thêm đơn vị tính mới
CREATE PROCEDURE sp_insert_unit_of_measure(
    IN p_name VARCHAR(30),
    IN p_symbol VARCHAR(5),
    IN p_description VARCHAR(1000)
)
BEGIN
    -- Kiểm tra tên đơn vị đã tồn tại chưa
    DECLARE unit_exists BOOLEAN;
    SELECT EXISTS(SELECT 1 FROM UnitOfMeasure WHERE name = p_name) INTO unit_exists;

    IF unit_exists THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đơn vị đã tồn tại';
    ELSE
        -- Kiểm tra ký hiệu đã tồn tại chưa
        SELECT EXISTS(SELECT 1 FROM UnitOfMeasure WHERE symbol = p_symbol) INTO unit_exists;

        IF unit_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Ký hiệu đơn vị đã tồn tại';
        ELSE
            INSERT INTO UnitOfMeasure(name, symbol, description)
            VALUES(p_name, p_symbol, p_description);

            SELECT LAST_INSERT_ID() AS unit_id;
        END IF;
    END IF;
END //

-- Cập nhật đơn vị tính
CREATE PROCEDURE sp_update_unit_of_measure(
    IN p_unit_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(30),
    IN p_symbol VARCHAR(5),
    IN p_description VARCHAR(1000)
)
BEGIN
    -- Kiểm tra tên đơn vị đã tồn tại chưa
    DECLARE name_exists BOOLEAN;
    DECLARE symbol_exists BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM UnitOfMeasure
        WHERE name = p_name AND unit_id != p_unit_id
    ) INTO name_exists;

    IF name_exists THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đơn vị đã tồn tại';
    ELSE
        -- Kiểm tra ký hiệu đã tồn tại chưa
        SELECT EXISTS(
            SELECT 1 FROM UnitOfMeasure
            WHERE symbol = p_symbol AND unit_id != p_unit_id
        ) INTO symbol_exists;

        IF symbol_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Ký hiệu đơn vị đã tồn tại';
        ELSE
            UPDATE UnitOfMeasure
            SET name = p_name,
                symbol = p_symbol,
                description = p_description,
                updated_at = CURRENT_TIMESTAMP
            WHERE unit_id = p_unit_id;

            SELECT ROW_COUNT() > 0 AS success;
        END IF;
    END IF;
END //

-- Lấy tất cả đơn vị tính
CREATE PROCEDURE sp_get_all_units_of_measure()
BEGIN
    SELECT * FROM UnitOfMeasure ORDER BY name;
END //

-- Lấy đơn vị tính theo ID
CREATE PROCEDURE sp_get_unit_of_measure_by_id(
    IN p_unit_id SMALLINT UNSIGNED
)
BEGIN
    SELECT * FROM UnitOfMeasure WHERE unit_id = p_unit_id;
END //

DELIMITER ;