DELIMITER //

-- Kiểm tra trước khi thêm nhân viên
CREATE TRIGGER before_employee_insert
    BEFORE INSERT ON Employee
    FOR EACH ROW
BEGIN
    -- Kiểm tra thông tin bắt buộc
    IF NEW.position IS NULL OR LENGTH(TRIM(NEW.position)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Chức vụ không được để trống';
    END IF;

    IF NEW.last_name IS NULL OR LENGTH(TRIM(NEW.last_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được để trống';
    END IF;

    IF NEW.first_name IS NULL OR LENGTH(TRIM(NEW.first_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được để trống';
    END IF;

    -- Kiểm tra số điện thoại hợp lệ
    IF NEW.phone IS NULL OR LENGTH(TRIM(NEW.phone)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không được để trống';
    ELSEIF NEW.phone NOT REGEXP '^[0-9]+$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại chỉ được chứa các chữ số';
    END IF;

    -- Kiểm tra email hợp lệ
    IF NEW.email IS NULL OR LENGTH(TRIM(NEW.email)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không được để trống';
    ELSEIF NEW.email NOT REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Định dạng email không hợp lệ';
    END IF;
END //

-- Kiểm tra trước khi cập nhật nhân viên
CREATE TRIGGER before_employee_update
    BEFORE UPDATE ON Employee
    FOR EACH ROW
BEGIN
    -- Kiểm tra thông tin bắt buộc
    IF NEW.position IS NULL OR LENGTH(TRIM(NEW.position)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Chức vụ không được để trống';
    END IF;

    IF NEW.last_name IS NULL OR LENGTH(TRIM(NEW.last_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được để trống';
    END IF;

    IF NEW.first_name IS NULL OR LENGTH(TRIM(NEW.first_name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được để trống';
    END IF;

    -- Kiểm tra số điện thoại hợp lệ
    IF NEW.phone IS NULL OR LENGTH(TRIM(NEW.phone)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không được để trống';
    ELSEIF NEW.phone NOT REGEXP '^[0-9]+$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại chỉ được chứa các chữ số';
    END IF;

    -- Kiểm tra email hợp lệ
    IF NEW.email IS NULL OR LENGTH(TRIM(NEW.email)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không được để trống';
    ELSEIF NEW.email NOT REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Định dạng email không hợp lệ';
    END IF;
END //

DELIMITER ;