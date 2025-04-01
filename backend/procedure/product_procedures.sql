DELIMITER //

-- Thêm sản phẩm mới
CREATE PROCEDURE sp_insert_product(
    IN p_category_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(100),
    IN p_description VARCHAR(1000),
    IN p_image_path VARCHAR(1000),
    IN p_is_available TINYINT(1),
    IN p_is_signature TINYINT(1)
)
BEGIN
    INSERT INTO Product(category_id, name, description, image_path, is_available, is_signature)
    VALUES(p_category_id, p_name, p_description, p_image_path, 
           COALESCE(p_is_available, 1), COALESCE(p_is_signature, 0));
    
    SELECT LAST_INSERT_ID() AS product_id;
END //

-- Cập nhật sản phẩm
CREATE PROCEDURE sp_update_product(
    IN p_product_id MEDIUMINT UNSIGNED,
    IN p_category_id SMALLINT UNSIGNED,
    IN p_name VARCHAR(100),
    IN p_description VARCHAR(1000),
    IN p_image_path VARCHAR(1000),
    IN p_is_available TINYINT(1),
    IN p_is_signature TINYINT(1)
)
BEGIN
    UPDATE Product
    SET category_id = p_category_id,
        name = p_name,
        description = p_description,
        image_path = p_image_path,
        is_available = p_is_available,
        is_signature = p_is_signature,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_id = p_product_id;
    
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy tất cả sản phẩm theo danh mục
CREATE PROCEDURE sp_get_products_by_category(
    IN p_category_id SMALLINT UNSIGNED
)
BEGIN
    SELECT p.*, c.name AS category_name
    FROM Product p
    JOIN Category c ON p.category_id = c.category_id
    WHERE p_category_id IS NULL OR p.category_id = p_category_id
    ORDER BY p.name;
END //

-- Lấy sản phẩm theo ID
CREATE PROCEDURE sp_get_product_by_id(
    IN p_product_id MEDIUMINT UNSIGNED
)
BEGIN
    SELECT p.*, c.name AS category_name
    FROM Product p
    JOIN Category c ON p.category_id = c.category_id
    WHERE p.product_id = p_product_id;
END //

-- Lấy sản phẩm có sẵn
CREATE PROCEDURE sp_get_available_products(
    IN p_category_id SMALLINT UNSIGNED
)
BEGIN
    SELECT p.*, c.name AS category_name
    FROM Product p
    JOIN Category c ON p.category_id = c.category_id
    WHERE p.is_available = 1
      AND (p_category_id IS NULL OR p.category_id = p_category_id)
    ORDER BY p.name;
END //

DELIMITER ; 