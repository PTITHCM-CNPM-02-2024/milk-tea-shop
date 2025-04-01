-- 17. Coupon
DELIMITER //

-- Kiểm tra trước khi thêm mã giảm giá
CREATE TRIGGER before_coupon_insert
    BEFORE INSERT ON Coupon
    FOR EACH ROW
BEGIN
    DECLARE coupon_exists BOOLEAN;

    -- Kiểm tra mã giảm giá
    IF NEW.coupon IS NULL OR LENGTH(TRIM(NEW.coupon)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá không được để trống';
    END IF;

    -- Kiểm tra định dạng mã giảm giá (chỉ cho phép chữ và số)
    IF NEW.coupon REGEXP '[^a-zA-Z0-9]' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá chỉ được chứa ký tự chữ và số';
    END IF;

    -- Kiểm tra mã giảm giá đã tồn tại chưa
    SELECT EXISTS(SELECT 1 FROM Coupon WHERE coupon = NEW.coupon) INTO coupon_exists;

    IF coupon_exists THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá đã tồn tại';
    END IF;
END //

-- Kiểm tra trước khi cập nhật mã giảm giá
CREATE TRIGGER before_coupon_update
    BEFORE UPDATE ON Coupon
    FOR EACH ROW
BEGIN
    DECLARE coupon_exists BOOLEAN;

    -- Kiểm tra mã giảm giá
    IF NEW.coupon IS NULL OR LENGTH(TRIM(NEW.coupon)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá không được để trống';
    END IF;

    -- Kiểm tra định dạng mã giảm giá (chỉ cho phép chữ và số)
    IF NEW.coupon REGEXP '[^a-zA-Z0-9]' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá chỉ được chứa ký tự chữ và số';
    END IF;

    -- Kiểm tra mã giảm giá đã tồn tại chưa (trừ chính nó)
    IF NEW.coupon != OLD.coupon THEN
        SELECT EXISTS(SELECT 1 FROM Coupon WHERE coupon = NEW.coupon AND coupon_id != NEW.coupon_id)
        INTO coupon_exists;

        IF coupon_exists THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Mã giảm giá đã tồn tại';
        END IF;
    END IF;
END //

-- Kiểm tra trước khi xóa mã giảm giá
CREATE TRIGGER before_coupon_delete
    BEFORE DELETE ON Coupon
    FOR EACH ROW
BEGIN
    -- Kiểm tra mã giảm giá có liên kết với chương trình giảm giá không
    DECLARE discount_count INT;
    SELECT COUNT(*) INTO discount_count FROM Discount WHERE coupon_id = OLD.coupon_id;

    IF discount_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa mã giảm giá đang được sử dụng trong chương trình giảm giá';
    END IF;
END //

DELIMITER ;
