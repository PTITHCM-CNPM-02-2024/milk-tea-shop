
DELIMITER //

-- Thêm bàn mới
CREATE PROCEDURE sp_insert_service_table(
    IN p_area_id SMALLINT UNSIGNED,
    IN p_table_number VARCHAR(10),
    IN p_is_active TINYINT(1)
)
BEGIN
    INSERT INTO ServiceTable(area_id, table_number, is_active)
    VALUES(p_area_id, p_table_number, p_is_active);

    SELECT LAST_INSERT_ID() AS table_id;
END //

-- Cập nhật thông tin bàn
CREATE PROCEDURE sp_update_service_table(
    IN p_table_id SMALLINT UNSIGNED,
    IN p_area_id SMALLINT UNSIGNED,
    IN p_table_number VARCHAR(10),
    IN p_is_active TINYINT(1)
)
BEGIN
    UPDATE ServiceTable
    SET area_id = p_area_id,
        table_number = p_table_number,
        is_active = p_is_active,
        updated_at = CURRENT_TIMESTAMP
    WHERE table_id = p_table_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa bàn
CREATE PROCEDURE sp_delete_service_table(
    IN p_table_id SMALLINT UNSIGNED
)
BEGIN
    DELETE FROM ServiceTable WHERE table_id = p_table_id;
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy bàn theo ID
CREATE PROCEDURE sp_get_service_table_by_id(
    IN p_table_id SMALLINT UNSIGNED
)
BEGIN
    SELECT t.*, a.name AS area_name
    FROM ServiceTable t
             LEFT JOIN Area a ON t.area_id = a.area_id
    WHERE t.table_id = p_table_id;
END //

-- Lấy bàn theo khu vực
CREATE PROCEDURE sp_get_service_tables_by_area(
    IN p_area_id SMALLINT UNSIGNED
)
BEGIN
    SELECT t.*, a.name AS area_name
    FROM ServiceTable t
             LEFT JOIN Area a ON t.area_id = a.area_id
    WHERE t.area_id = p_area_id
    ORDER BY t.table_number;
END //

-- Lấy tất cả bàn
CREATE PROCEDURE sp_get_all_service_tables()
BEGIN
    SELECT t.*, a.name AS area_name
    FROM ServiceTable t
             LEFT JOIN Area a ON t.area_id = a.area_id
    ORDER BY COALESCE(a.name, 'ZZZ'), t.table_number;
END //

-- Lấy bàn đang hoạt động
CREATE PROCEDURE sp_get_active_service_tables()
BEGIN
    SELECT t.*, a.name AS area_name
    FROM ServiceTable t
             LEFT JOIN Area a ON t.area_id = a.area_id
    WHERE t.is_active = 1
    ORDER BY COALESCE(a.name, 'ZZZ'), t.table_number;
END //

-- Kiểm tra trạng thái bàn
CREATE PROCEDURE sp_check_table_availability(
    IN p_table_id SMALLINT UNSIGNED
)
BEGIN
    DECLARE has_active_order BOOLEAN;

    SELECT EXISTS(
        SELECT 1
        FROM OrderTable ot
                 JOIN `Order` o ON ot.order_id = o.order_id
        WHERE ot.table_id = p_table_id
          AND ot.check_out IS NULL
          AND o.status != 'CANCELLED'
    ) INTO has_active_order;

    SELECT
        t.table_id,
        t.table_number,
        t.is_active,
        a.name AS area_name,
        has_active_order,
        IF(has_active_order, 'Đang sử dụng', IF(t.is_active, 'Sẵn sàng', 'Không sẵn sàng')) AS status
    FROM ServiceTable t
             LEFT JOIN Area a ON t.area_id = a.area_id
    WHERE t.table_id = p_table_id;
END //

DELIMITER ;