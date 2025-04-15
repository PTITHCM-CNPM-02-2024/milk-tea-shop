DELIMITER //

-- Thủ tục cấp quyền cho tài khoản theo vai trò
CREATE PROCEDURE sp_grant_permissions_by_role(
    IN p_account_id INT UNSIGNED
)
BEGIN
    DECLARE role_name VARCHAR(50);
    DECLARE username VARCHAR(50);
    DECLARE mysql_username VARCHAR(100);

    -- Lấy thông tin tài khoản và vai trò
    SELECT a.username, r.name INTO username, role_name
    FROM account a
             JOIN role r ON a.role_id = r.role_id
    WHERE a.account_id = p_account_id;

    -- Chỉ xử lý cho MANAGER và STAFF
    IF role_name IN ('MANAGER', 'STAFF') THEN
        -- Tạo tên người dùng MySQL từ username và role
        SET mysql_username = CONCAT(username, '_', LOWER(role_name));

        -- Kiểm tra xem người dùng MySQL đã tồn tại chưa
        SET @user_exists = 0;
        SELECT COUNT(*) INTO @user_exists FROM mysql.user
        WHERE User = mysql_username AND (Host = 'localhost' OR Host = '%');

        -- Nếu chưa tồn tại, tạo người dùng MySQL mới với mật khẩu tạm thời
        IF @user_exists = 0 THEN
            SET @temp_password = CONCAT(username, '_temp_');

            SET @sql_create_user = CONCAT('CREATE USER \'', mysql_username, '\'@\'localhost\' IDENTIFIED BY \'', @temp_password, '\'');
            PREPARE stmt FROM @sql_create_user;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_create_user_remote = CONCAT('CREATE USER \'', mysql_username, '\'@\'%\' IDENTIFIED BY \'', @temp_password, '\'');
            PREPARE stmt FROM @sql_create_user_remote;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        -- Cấp quyền dựa trên vai trò
        IF role_name = 'MANAGER' THEN
            -- Cấp toàn quyền cho MANAGER
            SET @sql_grant = CONCAT('GRANT ALL PRIVILEGES ON milk_tea_shop_prod.* TO \'', mysql_username, '\'@\'localhost\'');
            PREPARE stmt FROM @sql_grant;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_remote = CONCAT('GRANT ALL PRIVILEGES ON milk_tea_shop_prod.* TO \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_remote;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        ELSEIF role_name = 'STAFF' THEN
            -- Cấp quyền hạn chế cho STAFF

            -- Quyền SELECT trên tất cả các bảng
            SET @sql_grant_select = CONCAT('GRANT SELECT ON milk_tea_shop_prod.* TO \'', mysql_username, '\'@\'localhost\', \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_select;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- Quyền thao tác đơn hàng
            SET @sql_grant_order = CONCAT('GRANT INSERT, UPDATE ON milk_tea_shop_prod.order TO \'', mysql_username, '\'@\'localhost\', \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_order;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- Quyền thêm các bảng liên quan đơn hàng
            SET @sql_grant_order_product = CONCAT('GRANT INSERT, UPDATE ON milk_tea_shop_prod.order_product TO \'', mysql_username, '\'@\'localhost\', \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_order_product;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_order_table = CONCAT('GRANT INSERT, UPDATE ON milk_tea_shop_prod.order_table TO \'', mysql_username, '\'@\'localhost\', \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_order_table;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_order_discount = CONCAT('GRANT INSERT, UPDATE ON milk_tea_shop_prod.order_discount TO \'', mysql_username, '\'@\'localhost\', \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_order_discount;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- Quyền thao tác thanh toán
            SET @sql_grant_payment = CONCAT('GRANT INSERT, UPDATE ON milk_tea_shop_prod.payment TO \'', mysql_username, '\'@\'localhost\', \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_payment;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- Quyền thao tác khách hàng
            SET @sql_grant_customer = CONCAT('GRANT INSERT, UPDATE ON milk_tea_shop_prod.customer TO \'', mysql_username, '\'@\'localhost\', \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_customer;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- Quyền thao tác bàn
            SET @sql_grant_table = CONCAT('GRANT UPDATE ON milk_tea_shop_prod.service_table TO \'', mysql_username, '\'@\'localhost\', \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_table;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        -- Áp dụng thay đổi
        FLUSH PRIVILEGES;

        -- Trả về thông tin
        SELECT
            mysql_username AS username,
            role_name AS role,
            'Đã cấp quyền thành công' AS message;
    ELSE
        -- Trả về thông báo
        SELECT 'Không cấp quyền cho vai trò này' AS message;
    END IF;
END //

-- Thủ tục thu hồi quyền khi tài khoản bị khóa
CREATE PROCEDURE sp_revoke_permissions(
    IN p_account_id INT UNSIGNED
)
BEGIN
    DECLARE role_name VARCHAR(50);
    DECLARE username VARCHAR(50);
    DECLARE mysql_username VARCHAR(100);

    -- Lấy thông tin tài khoản và vai trò
    SELECT a.username, r.name INTO username, role_name
    FROM account a
             JOIN role r ON a.role_id = r.role_id
    WHERE a.account_id = p_account_id;

    -- Chỉ xử lý cho MANAGER và STAFF
    IF role_name IN ('MANAGER', 'STAFF') THEN
        -- Tạo tên người dùng MySQL từ username và role
        SET mysql_username = CONCAT(username, '_', LOWER(role_name));

        -- Kiểm tra xem người dùng MySQL có tồn tại không
        SET @user_exists = 0;
        SELECT COUNT(*) INTO @user_exists FROM mysql.user
        WHERE User = mysql_username AND (Host = 'localhost' OR Host = '%');

        IF @user_exists > 0 THEN
            -- Thu hồi tất cả quyền
            SET @sql_revoke_localhost = CONCAT('REVOKE ALL PRIVILEGES ON *.* FROM \'', mysql_username, '\'@\'localhost\'');
            PREPARE stmt FROM @sql_revoke_localhost;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_revoke_remote = CONCAT('REVOKE ALL PRIVILEGES ON *.* FROM \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_revoke_remote;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- Áp dụng thay đổi
            FLUSH PRIVILEGES;

            -- Trả về thông tin
            SELECT
                mysql_username AS username,
                'Đã thu hồi quyền thành công' AS message;
        ELSE
            -- Trả về thông báo
            SELECT 'Người dùng MySQL không tồn tại' AS message;
        END IF;
    ELSE
        -- Trả về thông báo
        SELECT 'Không cần thu hồi quyền cho vai trò này' AS message;
    END IF;
END //

-- Thủ tục khóa/mở khóa tài khoản và xử lý quyền tương ứng
CREATE PROCEDURE sp_lock_unlock_account(
    IN p_account_id INT UNSIGNED,
    IN p_is_locked TINYINT(1)
)
BEGIN
    DECLARE current_lock_status TINYINT(1);

    -- Lấy trạng thái khóa hiện tại
    SELECT is_locked INTO current_lock_status
    FROM account
    WHERE account_id = p_account_id;

    -- Cập nhật trạng thái khóa
    UPDATE account
    SET is_locked = p_is_locked,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;

    -- Xử lý quyền dựa trên trạng thái khóa mới
    IF p_is_locked = 1 THEN
        -- Khóa tài khoản: thu hồi quyền
        CALL sp_revoke_permissions(p_account_id);

        SELECT 'Tài khoản đã bị khóa và quyền đã bị thu hồi' AS message;
    ELSE
        -- Mở khóa tài khoản: cấp lại quyền
        CALL sp_grant_permissions_by_role(p_account_id);

        SELECT 'Tài khoản đã được mở khóa và quyền đã được cấp lại' AS message;
    END IF;
END //

DELIMITER ;

DELIMITER //

-- Thủ tục tạo tài khoản cho MANAGER kết hợp cấp quyền
CREATE PROCEDURE sp_create_manager_account_with_permissions(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100)
)
BEGIN
    DECLARE admin_account_id INT UNSIGNED;
    DECLARE admin_role_id TINYINT UNSIGNED;
    DECLARE password_hash VARCHAR(255);

    -- Bắt đầu transaction
    START TRANSACTION;

    -- Lấy role_id của MANAGER
    SELECT role_id INTO admin_role_id FROM role WHERE name = 'MANAGER';

    -- Mã hóa mật khẩu (trong thực tế, mã hóa nên được thực hiện ở tầng ứng dụng)
    SET password_hash = p_password; -- Giả định đã mã hóa

    -- Tạo tài khoản
    INSERT INTO account (role_id, username, password_hash, is_active, is_locked)
    VALUES (admin_role_id, p_username, password_hash, 1, 0);

    SET admin_account_id = LAST_INSERT_ID();

    -- Tạo thông tin Manager
    INSERT INTO manager (account_id, last_name, first_name, gender, phone, email)
    VALUES (admin_account_id, p_last_name, p_first_name, p_gender, p_phone, p_email);

    -- Cấp quyền cho tài khoản mới
    CALL sp_grant_permissions_by_role(admin_account_id);

    -- Hoàn tất transaction
    COMMIT;

    -- Trả về ID của tài khoản đã tạo
    SELECT admin_account_id AS account_id, 'Đã tạo tài khoản MANAGER và cấp quyền thành công' AS message;
END //

-- Thủ tục tạo tài khoản cho STAFF kết hợp cấp quyền
CREATE PROCEDURE sp_create_staff_account_with_permissions(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    IN p_position VARCHAR(50),
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100)
)
BEGIN
    DECLARE staff_account_id INT UNSIGNED;
    DECLARE staff_role_id TINYINT UNSIGNED;
    DECLARE password_hash VARCHAR(255);

    -- Bắt đầu transaction
    START TRANSACTION;

    -- Lấy role_id của STAFF
    SELECT role_id INTO staff_role_id FROM role WHERE name = 'STAFF';

    -- Mã hóa mật khẩu (trong thực tế, mã hóa nên được thực hiện ở tầng ứng dụng)
    SET password_hash = p_password; -- Giả định đã mã hóa

    -- Tạo tài khoản
    INSERT INTO account (role_id, username, password_hash, is_active, is_locked)
    VALUES (staff_role_id, p_username, password_hash, 1, 0);

    SET staff_account_id = LAST_INSERT_ID();

    -- Tạo thông tin Employee
    INSERT INTO employee (account_id, position, last_name, first_name, gender, phone, email)
    VALUES (staff_account_id, p_position, p_last_name, p_first_name, p_gender, p_phone, p_email);

    -- Cấp quyền cho tài khoản mới
    CALL sp_grant_permissions_by_role(staff_account_id);

    -- Hoàn tất transaction
    COMMIT;

    -- Trả về ID của tài khoản đã tạo
    SELECT staff_account_id AS account_id, 'Đã tạo tài khoản STAFF và cấp quyền thành công' AS message;
END //

DELIMITER ;

-- use procedure sp_create_manager_account_with_permissions
CALL sp_create_manager_account_with_permissions(
    'admin',
    '$2a$10$uIcEvLZvJrDwdpv3sIrce.q3TKaDnrQ6vcL2.H1FnV0U.QLhyukku',
    'NGUYỄN NGỌC',
    'PHÚ',
    'MALE',
    '0987654321',
    'admin@milkteashop.com');

DELIMITER //
-- Bảo vệ tài khoản admin mặc định khỏi việc thay đổi username
CREATE TRIGGER protect_default_admin_update
    BEFORE UPDATE ON account
    FOR EACH ROW
BEGIN
    DECLARE is_default_admin BOOLEAN;

    SELECT EXISTS(
        SELECT 1
        FROM manager m
                 JOIN account a ON m.account_id = a.account_id
        WHERE a.account_id = OLD.account_id
          AND a.username = 'admin'
          AND m.email = 'admin@milkteashop.com'
    ) INTO is_default_admin;

    IF is_default_admin AND NEW.username != 'admin' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi username của tài khoản admin mặc định';
    END IF;
END //

-- Bảo vệ tài khoản admin mặc định khỏi việc xóa
CREATE TRIGGER protect_default_admin_delete
    BEFORE DELETE ON account
    FOR EACH ROW
BEGIN
    DECLARE is_default_admin BOOLEAN;

    SELECT EXISTS(
        SELECT 1
        FROM manager m
                 JOIN account a ON m.account_id = a.account_id
        WHERE a.account_id = OLD.account_id
          AND a.username = 'admin'
          AND m.email = 'admin@milkteashop.com'
    ) INTO is_default_admin;

    IF is_default_admin THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa tài khoản admin mặc định';
    END IF;
END //

DELIMITER ;
