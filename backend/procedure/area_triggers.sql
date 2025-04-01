-- 11. Area

DELIMITER //

-- Kiểm tra trước khi thêm khu vực
CREATE TRIGGER before_area_insert
    BEFORE INSERT ON Area
    FOR EACH ROW
BEGIN
    DECLARE area_exists BOOLEAN;

    -- Kiểm tra tên khu vực
    IF NEW.name IS NULL OR LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực không được để trống';
    END IF;

    -- Kiểm tra tên khu vực đã tồn tại chưa
    SELECT EXISTS(SELECT 1 FROM Area WHERE name = NEW.name) INTO area_exists;

    IF area_exists THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực đã tồn tại';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn 0 nếu được chỉ định
    IF NEW.max_tables IS NOT NULL AND NEW.max_tables <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn 0';
    END IF;
END //

-- Kiểm tra trước khi cập nhật khu vực
CREATE TRIGGER before_area_update
    BEFORE UPDATE ON Area
    FOR EACH ROW
BEGIN
    DECLARE area_exists BOOLEAN;
    DECLARE current_tables INT;

    -- Kiểm tra tên khu vực
    IF NEW.name IS NULL OR LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực không được để trống';
    END IF;

    -- Kiểm tra tên khu vực đã tồn tại chưa (trừ chính nó)
    IF NEW.name != OLD.name THEN
        SELECT EXISTS(SELECT 1 FROM Area WHERE name = NEW.name AND area_id != NEW.area_id)
        INTO area_exists;

        IF area_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Tên khu vực đã tồn tại';
        END IF;
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn 0 nếu được chỉ định
    IF NEW.max_tables IS NOT NULL AND NEW.max_tables <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn 0';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn hoặc bằng số bàn hiện có
    IF NEW.max_tables IS NOT NULL THEN
        SELECT COUNT(*) INTO current_tables FROM ServiceTable WHERE area_id = NEW.area_id;

        IF NEW.max_tables < current_tables THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn hoặc bằng số bàn hiện có';
        END IF;
    END IF;
END //

-- Kiểm tra trước khi xóa khu vực
CREATE TRIGGER before_area_delete
    BEFORE DELETE ON Area
    FOR EACH ROW
BEGIN
    -- Cập nhật bàn để đặt area_id thành NULL
    UPDATE ServiceTable
    SET area_id = NULL,
        updated_at = CURRENT_TIMESTAMP
    WHERE area_id = OLD.area_id;
END //

DELIMITER ;

DELIMITER //

-- Thêm khu vực mới
CREATE PROCEDURE sp_insert_area(
    IN p_name CHAR(3),
    IN p_description VARCHAR(255),
    IN p_max_tables INT,
    IN p_is_active TINYINT(1)
)
BEGIN
    INSERT INTO Area(name, description, max_tables, is_active)
    VALUES(p_name, p_description, p_max_tables, p_is_active);

    SELECT LAST_INSERT_ID() AS area_id;
END //
