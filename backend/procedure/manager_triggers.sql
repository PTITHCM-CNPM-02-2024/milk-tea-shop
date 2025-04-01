-- 5. Manager
DELIMITER //

-- Kiểm tra trước khi thêm quản lý
CREATE TRIGGER before_manager_insert
    BEFORE INSERT ON Manager
    FOR EACH ROW
BEGIN
    DECLARE account_used BOOLEAN;
    DECLARE email_used BOOLEAN;
    DECLARE phone_used BOOLEAN;

    -- Kiểm tra tên
    IF NEW.first_name IS NULL OR LENGTH(TRIM(NEW.first_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được để trống';
    END IF;

    -- Kiểm tra họ
    IF NEW.last_name IS NULL OR LENGTH(TRIM(NEW.last_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được để trống';
    END IF;

    -- Kiểm tra email
    IF NEW.email IS NULL OR LENGTH(TRIM(NEW.email)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không được để trống';
    ELSEIF NEW.email NOT REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone IS NULL OR LENGTH(TRIM(NEW.phone)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không được để trống';
    ELSEIF NEW.phone NOT REGEXP '^[0-9]{10,15}$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra account_id duy nhất
    IF NEW.account_id IS NOT NULL THEN
        SELECT EXISTS(SELECT 1 FROM Manager WHERE account_id = NEW.account_id) INTO account_used;

        IF account_used THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Tài khoản đã được liên kết với quản lý khác';
        END IF;
    END IF;

    -- Kiểm tra email duy nhất
    SELECT EXISTS(SELECT 1 FROM Manager WHERE email = NEW.email) INTO email_used;

    IF email_used THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email đã tồn tại';
    END IF;

    -- Kiểm tra số điện thoại duy nhất
    SELECT EXISTS(SELECT 1 FROM Manager WHERE phone = NEW.phone) INTO phone_used;

    IF phone_used THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại đã tồn tại';
    END IF;
END //

-- Kiểm tra trước khi cập nhật quản lý
CREATE TRIGGER before_manager_update
    BEFORE UPDATE ON Manager
    FOR EACH ROW
BEGIN
    DECLARE account_used BOOLEAN;
    DECLARE email_used BOOLEAN;
    DECLARE phone_used BOOLEAN;

    -- Kiểm tra tên
    IF NEW.first_name IS NULL OR LENGTH(TRIM(NEW.first_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được để trống';
    END IF;

    -- Kiểm tra họ
    IF NEW.last_name IS NULL OR LENGTH(TRIM(NEW.last_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được để trống';
    END IF;

    -- Kiểm tra email
    IF NEW.email IS NULL OR LENGTH(TRIM(NEW.email)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không được để trống';
    ELSEIF NEW.email NOT REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone IS NULL OR LENGTH(TRIM(NEW.phone)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không được để trống';
    ELSEIF NEW.phone NOT REGEXP '^[0-9]{10,15}$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra account_id duy nhất
    IF NEW.account_id IS NOT NULL AND (OLD.account_id IS NULL OR NEW.account_id != OLD.account_id) THEN
        SELECT EXISTS(SELECT 1 FROM Manager WHERE account_id = NEW.account_id AND manager_id != NEW.manager_id) INTO account_used;

        IF account_used THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Tài khoản đã được liên kết với quản lý khác';
        END IF;
    END IF;

    -- Kiểm tra email duy nhất
    IF NEW.email != OLD.email THEN
        SELECT EXISTS(SELECT 1 FROM Manager WHERE email = NEW.email AND manager_id != NEW.manager_id) INTO email_used;

        IF email_used THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Email đã tồn tại';
        END IF;
    END IF;

    -- Kiểm tra số điện thoại duy nhất
    IF NEW.phone != OLD.phone THEN
        SELECT EXISTS(SELECT 1 FROM Manager WHERE phone = NEW.phone AND manager_id != NEW.manager_id) INTO phone_used;

        IF phone_used THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Số điện thoại đã tồn tại';
        END IF;
    END IF;
END //

DELIMITER ;