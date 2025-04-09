DELIMITER //
-- Cập nhật thông tin khu vực
CREATE PROCEDURE sp_update_area(
    IN p_area_id SMALLINT UNSIGNED,
    IN p_name CHAR(3),
    IN p_description VARCHAR(255),
    IN p_max_tables INT,
    IN p_is_active TINYINT(1)
)
BEGIN
    UPDATE Area
    SET name = p_name,
        description = p_description,
        max_tables = p_max_tables,
        is_active = p_is_active,
        updated_at = CURRENT_TIMESTAMP
    WHERE area_id = p_area_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa khu vực
CREATE PROCEDURE sp_delete_area(
    IN p_area_id SMALLINT UNSIGNED
)
BEGIN
    DELETE FROM Area WHERE area_id = p_area_id;
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy khu vực theo ID
CREATE PROCEDURE sp_get_area_by_id(
    IN p_area_id SMALLINT UNSIGNED
)
BEGIN
    SELECT * FROM Area WHERE area_id = p_area_id;
END //

-- Lấy tất cả khu vực
CREATE PROCEDURE sp_get_all_areas()
BEGIN
    SELECT * FROM Area ORDER BY name;
END //

-- Lấy khu vực còn hoạt động
CREATE PROCEDURE sp_get_active_areas()
BEGIN
    SELECT * FROM Area WHERE is_active = 1 ORDER BY name;
END //

DELIMITER ;
