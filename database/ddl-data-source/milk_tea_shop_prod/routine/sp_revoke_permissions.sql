create procedure milk_tea_shop_prod.sp_revoke_permissions(IN p_account_id int unsigned)
BEGIN
    DECLARE role_name VARCHAR(50);
    DECLARE username VARCHAR(50);
    DECLARE mysql_username VARCHAR(100);
    DECLARE db_name VARCHAR(64);
    -- Biến lưu tên database

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

        -- Kiểm tra xem người dùng MySQL có tồn tại không
        SET @user_exists = 0;
        SELECT COUNT(*)
        INTO @user_exists
        FROM mysql.user
        WHERE User = mysql_username
          AND (Host = 'localhost' OR Host = '%');

        IF @user_exists > 0 THEN
            -- Thu hồi tất cả quyền trên database cụ thể
            SET @sql_revoke_localhost =
                    CONCAT('REVOKE ALL PRIVILEGES ON ', db_name, '.* FROM \'', mysql_username,
                           '\'@\'localhost\'');
            PREPARE stmt FROM @sql_revoke_localhost;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET @sql_revoke_remote = CONCAT('REVOKE ALL PRIVILEGES ON ', db_name, '.* FROM \'', mysql_username,
                                            '\'@\'%\'');
            PREPARE stmt FROM @sql_revoke_remote;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- Áp dụng thay đổi
            FLUSH PRIVILEGES;

            -- Trả về thông tin
            SELECT mysql_username                  AS username,
                   ' Đã thu hồi quyền thành công ' AS message;
        ELSE
            -- Trả về thông báo
            SELECT ' Người dùng MySQL không tồn tại ' AS message;
        END IF;
    ELSE
        -- Trả về thông báo
        SELECT ' Không cần thu hồi quyền cho vai trò này ' AS message;
    END IF;
END;

