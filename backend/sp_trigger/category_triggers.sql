DELIMITER //

-- Kiểm tra trước khi thêm danh mục
CREATE TRIGGER before_category_insert
BEFORE INSERT ON Category
FOR EACH ROW
BEGIN
    -- Kiểm tra tên danh mục
    IF LENGTH(TRIM(NEW.name)) = 0  OR LENGTH(TRIM(NEW.name)) > 100 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên danh mục không được để trống và có độ dài tối đa 100 ký tự';
    END IF;

    -- Không cho phép thêm danh mục có parent_category_id là id của chính nó
    IF NEW.parent_category_id IS NOT NULL AND NEW.parent_category_id = NEW.category_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể chọn chính mình làm danh mục cha';
    END IF;

    
END //

-- Kiểm tra trước khi cập nhật danh mục
CREATE TRIGGER before_category_update
BEFORE UPDATE ON Category
FOR EACH ROW
BEGIN
    -- Kiểm tra tên danh mục
    IF LENGTH(TRIM(NEW.name)) = 0  OR LENGTH(TRIM(NEW.name)) > 100 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên danh mục không được để trống và có độ dài tối đa 100 ký tự';
    END IF;


END //

-- Kiểm tra trước khi xóa danh mục, chưa cần thiết
CREATE TRIGGER before_category_delete
BEFORE DELETE ON Category
FOR EACH ROW
BEGIN
END //

DELIMITER ;
