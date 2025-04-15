DELIMITER //

-- Thêm danh mục sản phẩm mới
CREATE PROCEDURE sp_insert_category(
    IN p_name VARCHAR(100),
    IN p_description VARCHAR(1000),
    IN p_parent_category_id SMALLINT UNSIGNED
)
BEGIN
    INSERT INTO Category(name, description, parent_category_id)
    VALUES(p_name, p_description, p_parent_category_id);

    SELECT LAST_INSERT_ID() AS category_id;
END //

-- Cập nhật danh mục
CREATE PROCEDURE sp_update_category(
    IN p_category_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(100),
    IN p_description VARCHAR(1000),
    IN p_parent_category_id SMALLINT UNSIGNED
)
BEGIN
    UPDATE Category
    SET name = p_name,
        description = p_description,
        parent_category_id = p_parent_category_id,
        updated_at = CURRENT_TIMESTAMP
    WHERE category_id = p_category_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa danh mục
CREATE PROCEDURE sp_delete_category(
    IN p_category_id SMALLINT UNSIGNED
)
BEGIN
    DELETE FROM Category WHERE category_id = p_category_id;
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy tất cả danh mục
CREATE PROCEDURE sp_get_all_categories()
BEGIN
    SELECT c.*, p.name AS parent_category_name
    FROM Category c
             LEFT JOIN Category p ON c.parent_category_id = p.category_id
    ORDER BY c.name;
END //


DELIMITER ;
