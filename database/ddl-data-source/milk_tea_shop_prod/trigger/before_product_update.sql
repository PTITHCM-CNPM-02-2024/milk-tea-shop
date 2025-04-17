create trigger milk_tea_shop_prod.before_product_update
    before update
    on milk_tea_shop_prod.product
    for each row
begin
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
END
    end;

