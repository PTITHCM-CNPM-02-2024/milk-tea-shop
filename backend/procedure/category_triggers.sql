-- 6. Category
DELIMITER //

-- Kiểm tra trước khi thêm danh mục
CREATE TRIGGER before_category_insert
    BEFORE INSERT ON Category
    FOR EACH ROW
BEGIN
    DECLARE category_exists BOOLEAN;
    DECLARE parent_exists BOOLEAN;


    -- Kiểm tra tên danh mục
    IF NEW.name IS NULL OR LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên danh mục không được để trống';
    END IF;

    -- Kiểm tra tên danh mục đã tồn tại chưa
    SELECT EXISTS(SELECT 1 FROM Category WHERE name = NEW.name) INTO category_exists;

    IF category_exists THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên danh mục đã tồn tại';
    END IF;

    -- Kiểm tra danh mục cha tồn tại nếu được chỉ định
    IF NEW.parent_category_id IS NOT NULL THEN
        SELECT EXISTS(SELECT 1 FROM Category WHERE category_id = NEW.parent_category_id)
        INTO parent_exists;

        IF NOT parent_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Danh mục cha không tồn tại';
        END IF;
    END IF;
END //

-- Kiểm tra trước khi cập nhật danh mục
CREATE TRIGGER before_category_update
    BEFORE UPDATE ON Category
    FOR EACH ROW
BEGIN
    DECLARE category_exists BOOLEAN;
    DECLARE parent_exists BOOLEAN;
    DECLARE is_child BOOLEAN;


    -- Kiểm tra tên danh mục
    IF NEW.name IS NULL OR LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên danh mục không được để trống';
    END IF;

    -- Kiểm tra tên danh mục đã tồn tại chưa (trừ chính nó)
    IF NEW.name != OLD.name THEN
        SELECT EXISTS(SELECT 1 FROM Category WHERE name = NEW.name AND category_id != NEW.category_id)
        INTO category_exists;

        IF category_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Tên danh mục đã tồn tại';
        END IF;
    END IF;

    -- Kiểm tra danh mục cha tồn tại nếu được chỉ định
    IF NEW.parent_category_id IS NOT NULL THEN
        SELECT EXISTS(SELECT 1 FROM Category WHERE category_id = NEW.parent_category_id)
        INTO parent_exists;

        IF NOT parent_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Danh mục cha không tồn tại';
        END IF;

        -- Ngăn chặn việc chọn chính nó làm danh mục cha
        IF NEW.parent_category_id = NEW.category_id THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể chọn chính mình làm danh mục cha';
        END IF;

        -- Ngăn chặn tạo vòng tròn trong phân cấp danh mục
        WITH RECURSIVE CategoryHierarchy AS (
            -- Anchor: danh mục con trực tiếp
            SELECT category_id FROM Category WHERE parent_category_id = NEW.category_id
            UNION ALL
            -- Recursive: danh mục con ở các cấp thấp hơn
            SELECT c.category_id
            FROM Category c
                     JOIN CategoryHierarchy ch ON c.parent_category_id = ch.category_id
        )
        SELECT EXISTS(SELECT 1 FROM CategoryHierarchy WHERE category_id = NEW.parent_category_id)
        INTO is_child;

        IF is_child THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể chọn danh mục con làm danh mục cha (tạo vòng tròn)';
        END IF;
    END IF;
END //

-- Kiểm tra trước khi xóa danh mục
CREATE TRIGGER before_category_delete
    BEFORE DELETE ON Category
    FOR EACH ROW
BEGIN
    -- Kiểm tra danh mục có sản phẩm không - nếu có thì đặt sản phẩm thành NULL
    UPDATE Product
    SET category_id = NULL,
        updated_at = CURRENT_TIMESTAMP
    WHERE category_id = OLD.category_id;

    -- Cập nhật lại danh mục con thành NULL
    UPDATE Category
    SET parent_category_id = NULL,
        updated_at = CURRENT_TIMESTAMP
    WHERE parent_category_id = OLD.category_id;
END //

DELIMITER ;
