DELIMITER //

-- Kiểm tra trước khi thêm danh mục
CREATE TRIGGER before_category_insert
BEFORE INSERT ON category
FOR EACH ROW
BEGIN
    -- Kiểm tra tên danh mục
    IF LENGTH(TRIM(NEW.name)) = 0  OR LENGTH(TRIM(NEW.name)) > 100 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên danh mục không được để trống và có độ dài tối đa 100 ký tự';
    END IF;

    
END //

-- Kiểm tra trước khi cập nhật danh mục
CREATE TRIGGER before_category_update
BEFORE UPDATE ON category
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
BEFORE DELETE ON category
FOR EACH ROW
BEGIN
END //

DELIMITER ;
