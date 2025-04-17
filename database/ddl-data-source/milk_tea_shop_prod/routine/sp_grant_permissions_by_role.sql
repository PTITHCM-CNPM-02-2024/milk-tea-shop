create procedure milk_tea_shop_prod.sp_grant_permissions_by_role(IN p_account_id int unsigned)
BEGIN
    DECLARE role_name VARCHAR(50);
    DECLARE username VARCHAR(50);
    DECLARE mysql_username VARCHAR(100);
    DECLARE db_name VARCHAR(64);
    DECLARE exit_handler BOOLEAN DEFAULT FALSE;

    -- Declare handler for errors to ensure transaction rollback
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        BEGIN
            SET exit_handler = TRUE;
        END;

    START TRANSACTION;

    -- Lấy tên database hiện tại
    SELECT DATABASE() INTO db_name;

    -- Lấy thông tin tài khoản và vai trò
    SELECT a.username, r.name
    INTO username, role_name
    FROM account a
             JOIN role r ON a.role_id = r.role_id
    WHERE a.account_id = p_account_id;

    -- Chỉ xử lý cho MANAGER và STAFF
    IF role_name IN ('MANAGER', 'STAFF') THEN
        -- Tạo tên người dùng MySQL từ username và role
        SET mysql_username = CONCAT(username, '_', LOWER(role_name));

        -- Kiểm tra xem người dùng MySQL đã tồn tại chưa
        SET @user_exists = 0;
        SELECT COUNT(*)
        INTO @user_exists
        FROM mysql.user
        WHERE User = mysql_username
          AND (Host = 'localhost' OR Host = '%');

        -- Nếu chưa tồn tại, tạo người dùng MySQL mới với mật khẩu tạm thời
        IF @user_exists = 0 THEN
            SET @temp_password = CONCAT(username, '_temp_pwd');

            -- Create localhost user
            SET @sql_create_user_localhost =
                    CONCAT('CREATE USER \'', mysql_username, '\'@\'localhost\' IDENTIFIED BY \'', @temp_password, '\'');
            PREPARE stmt FROM @sql_create_user_localhost;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- Create remote user
            SET @sql_create_user_remote =
                    CONCAT('CREATE USER \'', mysql_username, '\'@\'%\' IDENTIFIED BY \'', @temp_password, '\'');
            PREPARE stmt FROM @sql_create_user_remote;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        -- Thu hồi tất cả quyền cũ trước khi cấp mới
        SET @sql_revoke_localhost =
                CONCAT('REVOKE ALL PRIVILEGES ON ', db_name, '.* FROM \'', mysql_username, '\'@\'localhost\'');
        PREPARE stmt FROM @sql_revoke_localhost;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @sql_revoke_remote =
                CONCAT('REVOKE ALL PRIVILEGES ON ', db_name, '.* FROM \'', mysql_username, '\'@\'%\'');
        PREPARE stmt FROM @sql_revoke_remote;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        -- Thu hồi quyền EXECUTE trên tất cả routines
        SET @sql_revoke_execute_localhost =
                CONCAT('REVOKE EXECUTE ON ', db_name, '.* FROM \'', mysql_username, '\'@\'localhost\'');
        PREPARE stmt FROM @sql_revoke_execute_localhost;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @sql_revoke_execute_remote =
                CONCAT('REVOKE EXECUTE ON ', db_name, '.* FROM \'', mysql_username, '\'@\'%\'');
        PREPARE stmt FROM @sql_revoke_execute_remote;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        IF role_name = 'MANAGER' THEN
            -- Cấp toàn quyền cho MANAGER
            SET @sql_grant_all_localhost =
                    CONCAT('GRANT ALL PRIVILEGES ON ', db_name, '.* TO \'', mysql_username, '\'@\'localhost\'');
            PREPARE stmt FROM @sql_grant_all_localhost;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_all_remote =
                    CONCAT('GRANT ALL PRIVILEGES ON ', db_name, '.* TO \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_all_remote;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_execute_localhost =
                    CONCAT('GRANT EXECUTE ON ', db_name, '.* TO \'', mysql_username, '\'@\'localhost\'');
            PREPARE stmt FROM @sql_grant_execute_localhost;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_execute_remote =
                    CONCAT('GRANT EXECUTE ON ', db_name, '.* TO \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_execute_remote;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SELECT mysql_username AS username,
                   role_name AS role,
                   'Đã cấp toàn quyền(ALL PRIVILEGES + EXECUTE) thành công' AS message;

        ELSEIF role_name = 'STAFF' THEN
            -- Cấp quyền SELECT trên tất cả các bảng
            SET @sql_grant_select_localhost =
                    CONCAT('GRANT SELECT ON ', db_name, '.* TO \'', mysql_username, '\'@\'localhost\'');
            PREPARE stmt FROM @sql_grant_select_localhost;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_select_remote =
                    CONCAT('GRANT SELECT ON ', db_name, '.* TO \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_select_remote;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- Grant execute on specific procedures
            SET @sql_grant_exec_proc1 =
                    CONCAT('GRANT EXECUTE ON PROCEDURE ', db_name, '.sp_create_full_order TO \'',
                           mysql_username, '\'@\'localhost\', \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_exec_proc1;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_grant_exec_proc2 =
                    CONCAT('GRANT EXECUTE ON PROCEDURE ', db_name, '.sp_insert_customer TO \'',
                           mysql_username, '\'@\'localhost\', \'', mysql_username, '\'@\'%\'');
            PREPARE stmt FROM @sql_grant_exec_proc2;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SELECT mysql_username AS username,
                   role_name AS role,
                   'Đã cấp quyền SELECT và EXECUTE giới hạn cho STAFF thành công' AS message;
        END IF;

        -- Áp dụng thay đổi
        FLUSH PRIVILEGES;

    ELSE
        -- Trả về thông báo cho các vai trò khác
        SELECT 'Không cấp quyền DB cho vai trò này' AS message;
    END IF;

    -- Commit or rollback based on whether we encountered errors
    IF exit_handler THEN
        ROLLBACK;
        SELECT 'Có lỗi xảy ra, đã rollback' AS error_message;
    ELSE
        COMMIT;
    END IF;
END;

