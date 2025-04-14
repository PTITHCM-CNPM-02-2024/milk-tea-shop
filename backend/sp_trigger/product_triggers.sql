DELIMITER //

-- Kiểm tra trước khi thêm sản phẩm
CREATE TRIGGER before_product_insert
BEFORE INSERT ON product
FOR EACH ROW
BEGIN
    -- Kiểm tra tên sản phẩm có trống không
    IF LENGTH(TRIM(NEW.name)) = 0 OR LENGTH(NEW.name) > 100 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên sản phẩm không được để trống và có độ dài tối đa 100 ký tự';
    END IF;
    
    -- Kiểm tra mô tả
    IF NEW.description IS NOT NULL AND LENGTH(TRIM(NEW.description)) > 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mô tả sản phẩm không được vượt quá 1000 ký tự';
    END IF;
    
    -- Kiểm tra đường dẫn hình ảnh
    IF NEW.image_path IS NOT NULL AND LENGTH(NEW.image_path) > 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Đường dẫn hình ảnh không được vượt quá 1000 ký tự';
    END IF;
    
    -- Đặt giá trị mặc định cho is_available
    IF NEW.is_available IS NULL THEN
        SET NEW.is_available = 1;
    ELSEIF NEW.is_available NOT IN (0, 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái sẵn có phải là 0 hoặc 1';
    END IF;
    
    -- Đặt giá trị mặc định cho is_signature
    IF NEW.is_signature IS NULL THEN
        SET NEW.is_signature = 0;
    ELSEIF NEW.is_signature NOT IN (0, 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái đặc trưng phải là 0 hoặc 1';
    END IF;
END //

-- Kiểm tra trước khi cập nhật sản phẩm
CREATE TRIGGER before_product_update
BEFORE UPDATE ON product
FOR EACH ROW
BEGIN
    -- Kiểm tra tên sản phẩm
    IF LENGTH(TRIM(NEW.name)) = 0 OR LENGTH(NEW.name) > 100 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên sản phẩm không được để trống và có độ dài tối đa 100 ký tự';
    END IF;
    
    -- Kiểm tra mô tả
    IF NEW.description IS NOT NULL AND LENGTH(TRIM(NEW.description)) > 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mô tả sản phẩm không được vượt quá 1000 ký tự';
    END IF;
    
    -- Kiểm tra đường dẫn hình ảnh
    IF NEW.image_path IS NOT NULL AND LENGTH(NEW.image_path) > 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Đường dẫn hình ảnh không được vượt quá 1000 ký tự';
    END IF;
    
    -- Kiểm tra is_available
    IF NEW.is_available NOT IN (0, 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái sẵn có phải là 0 hoặc 1';
    END IF;
    
    -- Kiểm tra is_signature
    IF NEW.is_signature NOT IN (0, 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái đặc trưng phải là 0 hoặc 1';
    END IF;
END //

-- Kiểm tra trước khi xóa sản phẩm
CREATE TRIGGER before_product_delete
BEFORE DELETE ON product
FOR EACH ROW
BEGIN

END //

-- Sau khi thêm sản phẩm
CREATE TRIGGER after_product_insert
AFTER INSERT ON product
FOR EACH ROW
BEGIN
    -- Ghi log hoặc thực hiện các hành động sau khi thêm sản phẩm
    -- (Có thể để trống nếu không cần xử lý gì)
END //

-- Sau khi cập nhật sản phẩm
CREATE TRIGGER after_product_update
AFTER UPDATE ON product
FOR EACH ROW
BEGIN
    -- Ghi log hoặc thực hiện các hành động sau khi cập nhật sản phẩm
    -- (Có thể để trống nếu không cần xử lý gì)
END //

-- Sau khi xóa sản phẩm
CREATE TRIGGER after_product_delete
AFTER DELETE ON product
FOR EACH ROW
BEGIN
    -- Ghi log hoặc thực hiện các hành động sau khi xóa sản phẩm
    -- (Có thể để trống nếu không cần xử lý gì)
END //

DELIMITER ;