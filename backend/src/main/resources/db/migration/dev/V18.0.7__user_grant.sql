-- Cải thiện V18.0.7__user_grant.sql - Version đơn giản
SET NAMES 'utf8mb4';
SET CHARACTER SET utf8mb4;

DELIMITER //

CREATE PROCEDURE sp_grant_permissions_by_role(
    IN p_account_id INT UNSIGNED
)
sp_grant_permissions_by_role: BEGIN
    DECLARE role_name VARCHAR(50);
    DECLARE username VARCHAR(50);
    DECLARE mysql_username VARCHAR(100);
    DECLARE db_name VARCHAR(64);
    DECLARE v_account_exists INT DEFAULT 0;

    -- Improved error handling
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            SELECT 'Có lỗi xảy ra khi cấp quyền. Đã rollback.' AS error_message;
        END;

    -- Validate input
    IF p_account_id IS NULL OR p_account_id <= 0 THEN
        SELECT 'Account ID không hợp lệ' AS error_message;
        LEAVE sp_grant_permissions_by_role;
    END IF;

    -- Check if account exists
    SELECT COUNT(*) INTO v_account_exists
    FROM account
    WHERE account_id = p_account_id;

    IF v_account_exists = 0 THEN
        SELECT 'Tài khoản không tồn tại' AS error_message;
        LEAVE sp_grant_permissions_by_role;
    END IF;

    START TRANSACTION;

    -- Get database name
    SELECT DATABASE() INTO db_name;
    IF db_name IS NULL THEN
        SELECT 'Không thể xác định database hiện tại' AS error_message;
        LEAVE sp_grant_permissions_by_role;
    END IF;

    -- Get account and role information
    SELECT a.username, r.name
    INTO username, role_name
    FROM account a
             JOIN role r ON a.role_id = r.role_id
    WHERE a.account_id = p_account_id;

    -- Only process for MANAGER and STAFF
    IF role_name IN ('MANAGER', 'STAFF') THEN
        SET mysql_username = CONCAT(username, '_', LOWER(role_name));

        -- Check if MySQL user exists
        SET @user_exists = 0;
        SELECT COUNT(*) INTO @user_exists
        FROM mysql.user
        WHERE User = mysql_username;

        -- Create user if not exists with username as password
        IF @user_exists = 0 THEN
            SET @sql_create_user = CONCAT(
                    'CREATE USER IF NOT EXISTS \'', mysql_username, '\'@\'%\' ',
                    'IDENTIFIED BY \'', username, '\''
                                   );
            PREPARE stmt FROM @sql_create_user;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        -- Revoke all existing privileges
        SET @sql_revoke_all = CONCAT(
                'REVOKE ALL PRIVILEGES ON *.* FROM \'', mysql_username, '\'@\'%\''
                              );
        PREPARE stmt FROM @sql_revoke_all;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        -- Grant permissions based on role
        IF role_name = 'MANAGER' THEN
            -- Grant all privileges for MANAGER
            SET @sql_grant = CONCAT(
                    'GRANT ALL PRIVILEGES ON ', db_name, '.* TO \'', mysql_username, '\'@\'%\''
                             );
            PREPARE stmt FROM @sql_grant;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SELECT mysql_username AS username,
                   role_name AS role,
                   'Đã cấp toàn quyền cho MANAGER thành công' AS message,
                   CONCAT('Mật khẩu: ', username) AS password_info;

        ELSEIF role_name = 'STAFF' THEN
            -- Grant specific permissions for STAFF based on business requirements

            -- 1. Quyền quản lý tài khoản của mình
            SET @sql_grant_account = CONCAT(
                    'GRANT SELECT, UPDATE ON ', db_name, '.account TO \'', mysql_username, '\'@\'%\''
                                     );
            PREPARE stmt FROM @sql_grant_account;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 2. Quyền quản lý khách hàng (tìm kiếm, tạo mới)
            SET @sql_grant_customer = CONCAT(
                    'GRANT SELECT, INSERT, UPDATE ON ', db_name, '.customer TO \'', mysql_username, '\'@\'%\''
                                      );
            PREPARE stmt FROM @sql_grant_customer;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 3. Quyền xem sản phẩm và giá
            SET @sql_grant_product = CONCAT(
                    'GRANT SELECT ON ', db_name, '.product TO \'', mysql_username, '\'@\'%\''
                                     );
            PREPARE stmt FROM @sql_grant_product;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_product_price = CONCAT(
                    'GRANT SELECT ON ', db_name, '.product_price TO \'', mysql_username, '\'@\'%\''
                                           );
            PREPARE stmt FROM @sql_grant_product_price;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_product_size = CONCAT(
                    'GRANT SELECT ON ', db_name, '.product_size TO \'', mysql_username, '\'@\'%\''
                                          );
            PREPARE stmt FROM @sql_grant_product_size;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 4. Quyền xem danh mục sản phẩm
            SET @sql_grant_category = CONCAT(
                    'GRANT SELECT ON ', db_name, '.category TO \'', mysql_username, '\'@\'%\''
                                      );
            PREPARE stmt FROM @sql_grant_category;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 5. Quyền xem đơn vị tính
            SET @sql_grant_unit = CONCAT(
                    'GRANT SELECT ON ', db_name, '.unit_of_measure TO \'', mysql_username, '\'@\'%\''
                                  );
            PREPARE stmt FROM @sql_grant_unit;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 6. Quyền áp dụng khuyến mãi
            SET @sql_grant_discount = CONCAT(
                    'GRANT SELECT ON ', db_name, '.discount TO \'', mysql_username, '\'@\'%\''
                                      );
            PREPARE stmt FROM @sql_grant_discount;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_coupon = CONCAT(
                    'GRANT SELECT ON ', db_name, '.coupon TO \'', mysql_username, '\'@\'%\''
                                    );
            PREPARE stmt FROM @sql_grant_coupon;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 7. Quyền quản lý bàn
            SET @sql_grant_table = CONCAT(
                    'GRANT SELECT ON ', db_name, '.service_table TO \'', mysql_username, '\'@\'%\''
                                   );
            PREPARE stmt FROM @sql_grant_table;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_area = CONCAT(
                    'GRANT SELECT ON ', db_name, '.area TO \'', mysql_username, '\'@\'%\''
                                  );
            PREPARE stmt FROM @sql_grant_area;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 8. Quyền xem loại thành viên
            SET @sql_grant_membership = CONCAT(
                    'GRANT SELECT ON ', db_name, '.membership_type TO \'', mysql_username, '\'@\'%\''
                                        );
            PREPARE stmt FROM @sql_grant_membership;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 9. Quyền xem phương thức thanh toán
            SET @sql_grant_payment_method = CONCAT(
                    'GRANT SELECT ON ', db_name, '.payment_method TO \'', mysql_username, '\'@\'%\''
                                            );
            PREPARE stmt FROM @sql_grant_payment_method;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 10. Quyền quản lý đơn hàng
            SET @sql_grant_order = CONCAT(
                    'GRANT SELECT, INSERT, UPDATE ON ', db_name, '.`order` TO \'', mysql_username, '\'@\'%\''
                                   );
            PREPARE stmt FROM @sql_grant_order;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 11. Quyền quản lý sản phẩm trong đơn hàng
            SET @sql_grant_order_product = CONCAT(
                    'GRANT SELECT, INSERT, UPDATE ON ', db_name, '.order_product TO \'', mysql_username, '\'@\'%\''
                                           );
            PREPARE stmt FROM @sql_grant_order_product;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 12. Quyền áp dụng giảm giá cho đơn hàng
            SET @sql_grant_order_discount = CONCAT(
                    'GRANT SELECT, INSERT ON ', db_name, '.order_discount TO \'', mysql_username, '\'@\'%\''
                                            );
            PREPARE stmt FROM @sql_grant_order_discount;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 13. Quyền quản lý bàn cho đơn hàng và trả bàn
            SET @sql_grant_order_table = CONCAT(
                    'GRANT SELECT, INSERT, UPDATE ON ', db_name, '.order_table TO \'', mysql_username, '\'@\'%\''
                                         );
            PREPARE stmt FROM @sql_grant_order_table;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 14. Quyền quản lý thanh toán
            SET @sql_grant_payment = CONCAT(
                    'GRANT SELECT, INSERT, UPDATE ON ', db_name, '.payment TO \'', mysql_username, '\'@\'%\''
                                     );
            PREPARE stmt FROM @sql_grant_payment;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 15. Grant execute on specific procedures for order management
            SET @sql_grant_exec_proc1 = CONCAT(
                    'GRANT EXECUTE ON PROCEDURE ', db_name, '.sp_create_full_order TO \'',
                    mysql_username, '\'@\'%\''
                                        );
            PREPARE stmt FROM @sql_grant_exec_proc1;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_exec_proc2 = CONCAT(
                    'GRANT EXECUTE ON PROCEDURE ', db_name, '.sp_insert_customer TO \'',
                    mysql_username, '\'@\'%\''
                                        );
            PREPARE stmt FROM @sql_grant_exec_proc2;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_exec_proc3 = CONCAT(
                    'GRANT EXECUTE ON PROCEDURE ', db_name, '.sp_update_order_table_checkout TO \'',
                    mysql_username, '\'@\'%\''
                                        );
            PREPARE stmt FROM @sql_grant_exec_proc3;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SELECT mysql_username AS username,
                   role_name AS role,
                   'Đã cấp quyền đầy đủ cho STAFF thành công' AS message,
                   CONCAT('Mật khẩu: ', username) AS password_info,
                   'Quyền: Quản lý đơn hàng, khách hàng, bàn, thanh toán và tài khoản cá nhân' AS permissions_summary;
        END IF;

        FLUSH PRIVILEGES;

    ELSE
        SELECT 'Không cấp quyền DB cho vai trò này' AS message;
    END IF;

    COMMIT;
END//

-- Thủ tục thu hồi quyền đơn giản hóa
CREATE PROCEDURE sp_revoke_permissions(
    IN p_account_id INT UNSIGNED
)
BEGIN
    DECLARE role_name VARCHAR(50);
    DECLARE username VARCHAR(50);
    DECLARE mysql_username VARCHAR(100);
    DECLARE db_name VARCHAR(64);

    -- Get database name
    SELECT DATABASE() INTO db_name;

    -- Get account and role information
    SELECT a.username, r.name
    INTO username, role_name
    FROM account a
             JOIN role r ON a.role_id = r.role_id
    WHERE a.account_id = p_account_id;

    -- Only process for MANAGER and STAFF
    IF role_name IN ('MANAGER', 'STAFF') THEN
        SET mysql_username = CONCAT(username, '_', LOWER(role_name));

        -- Check if MySQL user exists
        SET @user_exists = 0;
        SELECT COUNT(*) INTO @user_exists
        FROM mysql.user
        WHERE User = mysql_username;

        IF @user_exists > 0 THEN
            -- Revoke all privileges
            SET @sql_revoke_all = CONCAT(
                    'REVOKE ALL PRIVILEGES ON *.* FROM \'', mysql_username, '\'@\'%\''
                                  );
            PREPARE stmt FROM @sql_revoke_all;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            FLUSH PRIVILEGES;

            SELECT mysql_username AS username,
                   'Đã thu hồi quyền thành công' AS message;
        ELSE
            SELECT 'Người dùng MySQL không tồn tại' AS message;
        END IF;
    ELSE
        SELECT 'Không cần thu hồi quyền cho vai trò này' AS message;
    END IF;
END//

-- Thủ tục khóa/mở khóa tài khoản và xử lý quyền tương ứng (Gọi sp_grant_permissions_by_role đã sửa)
CREATE PROCEDURE sp_lock_unlock_account(
    IN p_account_id INT UNSIGNED,
    IN p_is_locked TINYINT(1)
)
BEGIN
    DECLARE current_lock_status TINYINT(1);

    -- Lấy trạng thái khóa hiện tại
    SELECT is_locked
    INTO current_lock_status
    FROM account
    WHERE account_id = p_account_id;

    -- Chỉ thực hiện nếu trạng thái thay đổi
    IF current_lock_status != p_is_locked THEN
        -- Bắt đầu transaction
        START TRANSACTION;

        -- Cập nhật trạng thái khóa
        UPDATE account
        SET is_locked  = p_is_locked,
            updated_at = CURRENT_TIMESTAMP
        WHERE account_id = p_account_id;

        -- Xử lý quyền dựa trên trạng thái khóa mới
        IF p_is_locked = 1 THEN
            -- Khóa tài khoản: thu hồi quyền
            CALL sp_revoke_permissions(p_account_id);
            SELECT 'Tài khoản đã bị khóa và quyền đã bị thu hồi' AS message;
        ELSE
            -- Mở khóa tài khoản: cấp lại quyền SELECT và EXECUTE giới hạn (sử dụng sp_grant_permissions_by_role đã sửa)
            CALL sp_grant_permissions_by_role(p_account_id);
            SELECT 'Tài khoản đã được mở khóa và quyền đã được cấp lại' AS message;
        END IF;

        -- Hoàn tất transaction
        COMMIT;
    ELSE
        SELECT 'Trạng thái khóa không thay đổi' AS message;
    END IF;
END //

-- Thủ tục tạo tài khoản MANAGER (Gọi sp_grant_permissions_by_role đã sửa)
CREATE PROCEDURE sp_create_manager_account(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255), -- Nên là mật khẩu đã hash từ ứng dụng
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM ('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100),
    OUT p_new_account_id INT UNSIGNED,
    OUT p_status_message VARCHAR(255)
)
-- Label for LEAVE defined directly after BEGIN
create_manager_proc:
BEGIN
    DECLARE manager_role_id TINYINT UNSIGNED;
    DECLARE v_account_id INT UNSIGNED;
    DECLARE v_manager_id INT UNSIGNED;
    -- Use declared local variable for OUT param

    -- Error handling
    DECLARE exit handler for sqlexception
        BEGIN
            ROLLBACK;
            SET p_new_account_id = NULL;
            SET p_status_message = 'Lỗi SQL xảy ra, giao dịch đã được hủy bỏ.';
        END;

    -- Lấy role_id của MANAGER
    SELECT role_id INTO manager_role_id FROM role WHERE name = 'MANAGER';
    IF manager_role_id IS NULL THEN
        SET p_status_message = 'Không tìm thấy vai trò MANAGER.';
        LEAVE create_manager_proc; -- Exit the procedure using label
    END IF;

    -- Bắt đầu transaction
    START TRANSACTION;

    -- 1. Tạo tài khoản sử dụng sp_insert_account
    CALL sp_insert_account(
            manager_role_id,
            p_username,
            p_password, -- Truyền mật khẩu (đã hash hoặc chưa tùy logic ứng dụng)
            0, -- is_active = FALSE (as per user change)
            0, -- is_locked = FALSE
            v_account_id
         );

    IF v_account_id IS NULL THEN
        ROLLBACK;
        SET p_status_message = 'Không thể tạo tài khoản.';
        LEAVE create_manager_proc;
    END IF;

    -- 2. Cấp quyền SELECT cho tài khoản mới (sử dụng sp_grant_permissions_by_role đã sửa)
    CALL sp_grant_permissions_by_role(v_account_id);
    -- Lưu ý: Kiểm tra kết quả của sp_grant_permissions_by_role nếu cần

    -- 3. Tạo thông tin Manager sử dụng sp_insert_manager
    CALL sp_insert_manager(
            v_account_id,
            p_last_name,
            p_first_name,
            p_gender,
            p_phone,
            p_email,
            v_manager_id -- Use declared variable for OUT param
         );

    -- Kiểm tra lỗi sau khi gọi sp_insert_manager
#     IF v_manager_id IS NULL OR v_manager_id <= 0 THEN
#         SIGNAL SQLSTATE '45000'
#             SET MESSAGE_TEXT = 'Không thể tạo thông tin manager.';
#         ROLLBACK;
#         SET p_status_message = 'Không thể tạo thông tin manager.';
#         LEAVE create_manager_proc;
#     END IF;

    -- Hoàn tất transaction
    COMMIT;

    -- Trả về kết quả
    SET p_new_account_id = v_account_id;
    SET p_status_message = 'Đã tạo tài khoản MANAGER và cấp quyền thành công.'; -- Updated message slightly

END create_manager_proc //-- End labeled block

-- Thủ tục tạo tài khoản STAFF (Gọi sp_grant_permissions_by_role đã sửa)
CREATE PROCEDURE sp_create_staff_account(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255), -- Nên là mật khẩu đã hash từ ứng dụng
    IN p_position VARCHAR(50),
    IN p_last_name VARCHAR(70),
    IN p_first_name VARCHAR(70),
    IN p_gender ENUM ('MALE', 'FEMALE', 'OTHER'),
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100),
    OUT p_new_account_id INT UNSIGNED,
    OUT p_status_message VARCHAR(255)
)
-- Label for LEAVE defined directly after BEGIN
create_staff_proc:
BEGIN
    DECLARE staff_role_id TINYINT UNSIGNED;
    DECLARE v_account_id INT UNSIGNED;
    DECLARE v_employee_id INT UNSIGNED;
    -- Use declared local variable for OUT param

    -- Error handling
    DECLARE exit handler for sqlexception
        BEGIN
        END;

    -- Lấy role_id của STAFF
    SELECT role_id INTO staff_role_id FROM role WHERE name = 'STAFF';
    IF staff_role_id IS NULL THEN
        SET p_status_message = 'Không tìm thấy vai trò STAFF.';
        LEAVE create_staff_proc; -- Exit the procedure using label
    END IF;

    -- Bắt đầu transaction
    START TRANSACTION;

    -- 1. Tạo tài khoản sử dụng sp_insert_account
    CALL sp_insert_account(
            staff_role_id,
            p_username,
            p_password,
            0, -- is_active = TRUE (Staff active by default)
            0, -- is_locked = FALSE
            v_account_id
         );

    IF v_account_id IS NULL THEN
        ROLLBACK;
        SET p_status_message = 'Không thể tạo tài khoản.';
        LEAVE create_staff_proc;
    END IF;

    -- 2. Cấp quyền SELECT cho tài khoản mới (sử dụng sp_grant_permissions_by_role đã sửa)
    CALL sp_grant_permissions_by_role(v_account_id);
    -- Lưu ý: Kiểm tra kết quả của sp_grant_permissions_by_role nếu cần

    -- 3. Tạo thông tin Employee sử dụng sp_insert_employee
    CALL sp_insert_employee(
            v_account_id,
            p_position,
            p_last_name,
            p_first_name,
            p_gender,
            p_phone,
            p_email,
            v_employee_id -- Use declared variable for OUT param
         );

    -- Kiểm tra lỗi sau khi gọi sp_insert_employee
    IF v_employee_id IS NULL OR v_employee_id <= 0 THEN
        ROLLBACK;
        SET p_status_message = 'Không thể tạo thông tin employee.';
        LEAVE create_staff_proc;
    END IF;

    -- Hoàn tất transaction
    COMMIT;

    -- Trả về kết quả
    SET p_new_account_id = v_account_id;
    SET p_status_message = 'Đã tạo tài khoản STAFF và cấp quyền thành công.'; -- Updated message slightly

END create_staff_proc// -- End labeled block

-- Bảo vệ tài khoản admin mặc định khỏi việc thay đổi username
CREATE TRIGGER protect_default_admin_update
    BEFORE UPDATE
    ON account
    FOR EACH ROW
BEGIN
    DECLARE is_default_admin BOOLEAN;

    SELECT EXISTS(SELECT 1
                  FROM manager m
                           JOIN account a ON m.account_id = a.account_id
                  WHERE a.account_id = OLD.account_id
                    AND a.username = 'admin'
                    AND m.email = 'admin@milkteashop.com')
    INTO is_default_admin;

    IF is_default_admin AND NEW.username != 'admin' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi username của tài khoản admin mặc định';
    END IF;
END //

-- Bảo vệ tài khoản admin mặc định khỏi việc xóa
CREATE TRIGGER protect_default_admin_delete
    BEFORE DELETE
    ON account
    FOR EACH ROW
BEGIN
    DECLARE is_default_admin BOOLEAN;

    SELECT EXISTS(SELECT 1
                  FROM manager m
                           JOIN account a ON m.account_id = a.account_id
                  WHERE a.account_id = OLD.account_id
                    AND a.username = 'admin'
                    AND m.email = 'admin@milkteashop.com')
    INTO is_default_admin;

    IF is_default_admin THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa tài khoản admin mặc định';
    END IF;
END //

DELIMITER ;

-- Tạo Manager
SET @new_manager_id = NULL;
SET @manager_status = '';
CALL sp_create_manager_account(
        'admin',
        '{bcrypt}$2a$10$WMn1T29Wu7giJtgKJ11GruP95voXc9unATF.0GuCtpOcm8oqkxY92',
        'NGUYỄN NGỌC',
        'PHÚ',
        'MALE',
        '0987654321',
        'admin@milkteashop.com',
        @new_manager_id,
        @manager_status
     );
SELECT @new_manager_id AS manager_account_id, @manager_status AS status;

-- Tạo Staff
SET @new_staff_id = NULL;
SET @staff_status = '';
CALL sp_create_staff_account(
        'vanphong',
        '{bcrypt}$2a$10$WMn1T29Wu7giJtgKJ11GruP95voXc9unATF.0GuCtpOcm8oqkxY92',
        'Nhân Viên Bán Hàng',
        'Nguyễn Văn',
        'Phương',
        'MALE',
        '0985712345',
        'thpa@gmail.com',
        @new_staff_id,
        @staff_status
     );
SELECT @new_staff_id AS staff_account_id, @staff_status AS status;

CALL sp_create_staff_account('thanhphuong',
                             '{bcrypt}$2a$10$WMn1T29Wu7giJtgKJ11GruP95voXc9unATF.0GuCtpOcm8oqkxY92',
                             'Nhân Viên Bán Hàng',
                             'Nguyễn Thành',
                             'Phương',
                             'MALE',
                             '0981234567',
                             'thp@gmail.com',
                             @new_staff_id,
                             @staff_status);
SELECT @new_staff_id AS staff_account_id, @staff_status AS status;

