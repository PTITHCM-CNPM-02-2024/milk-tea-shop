-- SQL Server 2022 User Grant Procedures for Milk Tea Shop
-- Converted from MySQL V18.0.7__user_grant.sql
-- Note: SQL Server uses different user/login management compared to MySQL

USE MilkTeaShop;
GO

-- =====================================================
-- SQL SERVER LOGIN/USER MANAGEMENT PROCEDURES
-- =====================================================
-- Note: SQL Server separates logins (server-level) from users (database-level)
-- This implementation focuses on database-level user management

-- Procedure để cấp quyền theo role
CREATE PROCEDURE sp_grant_permissions_by_role
    @p_account_id INT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @role_name NVARCHAR(50);
    DECLARE @username NVARCHAR(50);
    DECLARE @sqlserver_username NVARCHAR(100);
    DECLARE @db_name NVARCHAR(128);
    DECLARE @v_account_exists INT = 0;
    DECLARE @sql NVARCHAR(MAX);

    BEGIN TRY
        -- Validate input
        IF @p_account_id IS NULL OR @p_account_id <= 0
        BEGIN
            PRINT N'Account ID không hợp lệ';
            RETURN;
        END

        -- Check if account exists
        SELECT @v_account_exists = COUNT(*)
        FROM account
        WHERE account_id = @p_account_id;

        IF @v_account_exists = 0
        BEGIN
            PRINT N'Tài khoản không tồn tại';
            RETURN;
        END

        -- Get database name
        SET @db_name = DB_NAME();
        IF @db_name IS NULL
        BEGIN
            PRINT N'Không thể xác định database hiện tại';
            RETURN;
        END

        -- Get account and role information
        SELECT @username = a.username, @role_name = r.name
        FROM account a
        INNER JOIN role r ON a.role_id = r.role_id
        WHERE a.account_id = @p_account_id;

        -- Only process for MANAGER and STAFF
        IF @role_name IN ('MANAGER', 'STAFF')
        BEGIN
            SET @sqlserver_username = @username + N'_' + LOWER(@role_name);

            -- Create database user if not exists (assumes login already exists)
            IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = @sqlserver_username AND type = 'S')
            BEGIN
                -- Note: In production, the login should be created at server level first
                SET @sql = N'CREATE USER [' + @sqlserver_username + N'] WITHOUT LOGIN';
                EXEC sp_executesql @sql;
                PRINT N'Đã tạo database user: ' + @sqlserver_username;
            END

            -- Grant permissions based on role
            IF @role_name = 'MANAGER'
            BEGIN
                -- Grant db_datareader and db_datawriter roles for MANAGER
                SET @sql = N'ALTER ROLE db_datareader ADD MEMBER [' + @sqlserver_username + N']';
                EXEC sp_executesql @sql;
                
                SET @sql = N'ALTER ROLE db_datawriter ADD MEMBER [' + @sqlserver_username + N']';
                EXEC sp_executesql @sql;
                
                SET @sql = N'ALTER ROLE db_ddladmin ADD MEMBER [' + @sqlserver_username + N']';
                EXEC sp_executesql @sql;

                PRINT N'Đã cấp toàn quyền cho MANAGER: ' + @sqlserver_username;
            END
            ELSE IF @role_name = 'STAFF'
            BEGIN
                -- Grant specific permissions for STAFF
                SET @sql = N'ALTER ROLE db_datareader ADD MEMBER [' + @sqlserver_username + N']';
                EXEC sp_executesql @sql;

                -- Grant limited write permissions to specific tables
                DECLARE @tables TABLE (table_name NVARCHAR(128));
                INSERT INTO @tables VALUES 
                    ('customer'), ('[order]'), ('order_product'), ('order_discount'), 
                    ('order_table'), ('payment');

                DECLARE @table_name NVARCHAR(128);
                DECLARE table_cursor CURSOR FOR SELECT table_name FROM @tables;
                
                OPEN table_cursor;
                FETCH NEXT FROM table_cursor INTO @table_name;
                
                WHILE @@FETCH_STATUS = 0
                BEGIN
                    SET @sql = N'GRANT INSERT, UPDATE ON ' + @table_name + N' TO [' + @sqlserver_username + N']';
                    EXEC sp_executesql @sql;
                    FETCH NEXT FROM table_cursor INTO @table_name;
                END
                
                CLOSE table_cursor;
                DEALLOCATE table_cursor;

                -- Grant execute permissions on specific procedures
                DECLARE @procedures TABLE (proc_name NVARCHAR(128));
                INSERT INTO @procedures VALUES 
                    ('sp_create_full_order'), ('sp_insert_customer'), 
                    ('sp_update_order_table_checkout');

                DECLARE @proc_name NVARCHAR(128);
                DECLARE proc_cursor CURSOR FOR SELECT proc_name FROM @procedures;
                
                OPEN proc_cursor;
                FETCH NEXT FROM proc_cursor INTO @proc_name;
                
                WHILE @@FETCH_STATUS = 0
                BEGIN
                    IF OBJECT_ID(@proc_name, 'P') IS NOT NULL
                    BEGIN
                        SET @sql = N'GRANT EXECUTE ON [' + @proc_name + N'] TO [' + @sqlserver_username + N']';
                        EXEC sp_executesql @sql;
                    END
                    FETCH NEXT FROM proc_cursor INTO @proc_name;
                END
                
                CLOSE proc_cursor;
                DEALLOCATE proc_cursor;

                PRINT N'Đã cấp quyền đầy đủ cho STAFF: ' + @sqlserver_username;
            END
        END
        ELSE
        BEGIN
            PRINT N'Không cấp quyền DB cho vai trò này: ' + @role_name;
        END

    END TRY
    BEGIN CATCH
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        PRINT N'Có lỗi xảy ra khi cấp quyền: ' + @ErrorMessage;
        THROW;
    END CATCH
END
GO

-- Thủ tục thu hồi quyền
CREATE PROCEDURE sp_revoke_permissions
    @p_account_id INT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @role_name NVARCHAR(50);
    DECLARE @username NVARCHAR(50);
    DECLARE @sqlserver_username NVARCHAR(100);
    DECLARE @sql NVARCHAR(MAX);

    BEGIN TRY
        -- Get account and role information
        SELECT @username = a.username, @role_name = r.name
        FROM account a
        INNER JOIN role r ON a.role_id = r.role_id
        WHERE a.account_id = @p_account_id;

        -- Only process for MANAGER and STAFF
        IF @role_name IN ('MANAGER', 'STAFF')
        BEGIN
            SET @sqlserver_username = @username + N'_' + LOWER(@role_name);

            -- Check if database user exists
            IF EXISTS (SELECT 1 FROM sys.database_principals WHERE name = @sqlserver_username AND type = 'S')
            BEGIN
                -- Remove from database roles
                SET @sql = N'ALTER ROLE db_datareader DROP MEMBER [' + @sqlserver_username + N']';
                EXEC sp_executesql @sql;
                
                SET @sql = N'ALTER ROLE db_datawriter DROP MEMBER [' + @sqlserver_username + N']';
                EXEC sp_executesql @sql;
                
                SET @sql = N'ALTER ROLE db_ddladmin DROP MEMBER [' + @sqlserver_username + N']';
                EXEC sp_executesql @sql;

                PRINT N'Đã thu hồi quyền thành công cho: ' + @sqlserver_username;
            END
            ELSE
            BEGIN
                PRINT N'Database user không tồn tại: ' + @sqlserver_username;
            END
        END
        ELSE
        BEGIN
            PRINT N'Không cần thu hồi quyền cho vai trò này: ' + @role_name;
        END

    END TRY
    BEGIN CATCH
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        PRINT N'Lỗi khi thu hồi quyền: ' + @ErrorMessage;
        THROW;
    END CATCH
END
GO

-- Thủ tục khóa/mở khóa tài khoản
CREATE PROCEDURE sp_lock_unlock_account
    @p_account_id INT,
    @p_is_locked BIT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @current_lock_status BIT;

    BEGIN TRY
        -- Lấy trạng thái khóa hiện tại
        SELECT @current_lock_status = is_locked
        FROM account
        WHERE account_id = @p_account_id;

        -- Chỉ thực hiện nếu trạng thái thay đổi
        IF @current_lock_status != @p_is_locked
        BEGIN
            BEGIN TRANSACTION;

            -- Cập nhật trạng thái khóa
            UPDATE account
            SET is_locked = @p_is_locked,
                updated_at = GETDATE()
            WHERE account_id = @p_account_id;

            -- Xử lý quyền dựa trên trạng thái khóa mới
            IF @p_is_locked = 1
            BEGIN
                -- Khóa tài khoản: thu hồi quyền
                EXEC sp_revoke_permissions @p_account_id;
                PRINT N'Tài khoản đã bị khóa và quyền đã bị thu hồi';
            END
            ELSE
            BEGIN
                -- Mở khóa tài khoản: cấp lại quyền
                EXEC sp_grant_permissions_by_role @p_account_id;
                PRINT N'Tài khoản đã được mở khóa và quyền đã được cấp lại';
            END

            COMMIT TRANSACTION;
        END
        ELSE
        BEGIN
            PRINT N'Trạng thái khóa không thay đổi';
        END

    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        PRINT N'Lỗi khi xử lý khóa/mở khóa tài khoản: ' + @ErrorMessage;
        THROW;
    END CATCH
END
GO

-- Thủ tục tạo tài khoản MANAGER
CREATE PROCEDURE sp_create_manager_account
    @p_username NVARCHAR(50),
    @p_password NVARCHAR(255), -- Nên là mật khẩu đã hash từ ứng dụng
    @p_last_name NVARCHAR(70),
    @p_first_name NVARCHAR(70),
    @p_gender NVARCHAR(10), -- Changed from ENUM to NVARCHAR
    @p_phone NVARCHAR(15),
    @p_email NVARCHAR(100),
    @p_new_account_id INT OUTPUT,
    @p_status_message NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @manager_role_id TINYINT;
    DECLARE @v_account_id INT;
    DECLARE @v_manager_id INT;

    BEGIN TRY
        -- Validate gender input
        IF @p_gender NOT IN ('MALE', 'FEMALE', 'OTHER')
        BEGIN
            SET @p_status_message = N'Giới tính không hợp lệ. Chỉ chấp nhận: MALE, FEMALE, OTHER';
            RETURN;
        END

        -- Lấy role_id của MANAGER
        SELECT @manager_role_id = role_id FROM role WHERE name = 'MANAGER';
        IF @manager_role_id IS NULL
        BEGIN
            SET @p_status_message = N'Không tìm thấy vai trò MANAGER.';
            RETURN;
        END

        BEGIN TRANSACTION;

        -- 1. Tạo tài khoản sử dụng sp_insert_account
        EXEC sp_insert_account
            @manager_role_id,
            @p_username,
            @p_password,
            0, -- is_active = FALSE
            0, -- is_locked = FALSE
            @v_account_id OUTPUT;

        IF @v_account_id IS NULL
        BEGIN
            SET @p_status_message = N'Không thể tạo tài khoản.';
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- 2. Cấp quyền cho tài khoản mới
        EXEC sp_grant_permissions_by_role @v_account_id;

        -- 3. Tạo thông tin Manager sử dụng sp_insert_manager
        EXEC sp_insert_manager
            @v_account_id,
            @p_last_name,
            @p_first_name,
            @p_gender,
            @p_phone,
            @p_email,
            @v_manager_id OUTPUT;

        COMMIT TRANSACTION;

        -- Trả về kết quả
        SET @p_new_account_id = @v_account_id;
        SET @p_status_message = N'Đã tạo tài khoản MANAGER và cấp quyền thành công.';

    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        SET @p_new_account_id = NULL;
        SET @p_status_message = N'Lỗi SQL xảy ra, giao dịch đã được hủy bỏ: ' + ERROR_MESSAGE();
    END CATCH
END
GO

-- Thủ tục tạo tài khoản STAFF
CREATE PROCEDURE sp_create_staff_account
    @p_username NVARCHAR(50),
    @p_password NVARCHAR(255),
    @p_position NVARCHAR(50),
    @p_last_name NVARCHAR(70),
    @p_first_name NVARCHAR(70),
    @p_gender NVARCHAR(10), -- Changed from ENUM to NVARCHAR
    @p_phone NVARCHAR(15),
    @p_email NVARCHAR(100),
    @p_new_account_id INT OUTPUT,
    @p_status_message NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @staff_role_id TINYINT;
    DECLARE @v_account_id INT;
    DECLARE @v_employee_id INT;

    BEGIN TRY
        -- Validate gender input
        IF @p_gender NOT IN ('MALE', 'FEMALE', 'OTHER')
        BEGIN
            SET @p_status_message = N'Giới tính không hợp lệ. Chỉ chấp nhận: MALE, FEMALE, OTHER';
            RETURN;
        END

        -- Lấy role_id của STAFF
        SELECT @staff_role_id = role_id FROM role WHERE name = 'STAFF';
        IF @staff_role_id IS NULL
        BEGIN
            SET @p_status_message = N'Không tìm thấy vai trò STAFF.';
            RETURN;
        END

        BEGIN TRANSACTION;

        -- 1. Tạo tài khoản sử dụng sp_insert_account
        EXEC sp_insert_account
            @staff_role_id,
            @p_username,
            @p_password,
            0, -- is_active = FALSE 
            0, -- is_locked = FALSE
            @v_account_id OUTPUT;

        IF @v_account_id IS NULL
        BEGIN
            SET @p_status_message = N'Không thể tạo tài khoản.';
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- 2. Cấp quyền cho tài khoản mới
        EXEC sp_grant_permissions_by_role @v_account_id;

        -- 3. Tạo thông tin Employee sử dụng sp_insert_employee
        EXEC sp_insert_employee
            @v_account_id,
            @p_position,
            @p_last_name,
            @p_first_name,
            @p_gender,
            @p_phone,
            @p_email,
            @v_employee_id OUTPUT;

        IF @v_employee_id IS NULL OR @v_employee_id <= 0
        BEGIN
            SET @p_status_message = N'Không thể tạo thông tin employee.';
            ROLLBACK TRANSACTION;
            RETURN;
        END

        COMMIT TRANSACTION;

        -- Trả về kết quả
        SET @p_new_account_id = @v_account_id;
        SET @p_status_message = N'Đã tạo tài khoản STAFF và cấp quyền thành công.';

    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        SET @p_new_account_id = NULL;
        SET @p_status_message = N'Lỗi SQL xảy ra, giao dịch đã được hủy bỏ: ' + ERROR_MESSAGE();
    END CATCH
END
GO

-- =====================================================
-- ACCOUNT PROTECTION TRIGGERS
-- =====================================================

-- Đã di chuyển các trigger protect admin vào file 004_init_triggers.sql

-- =====================================================
-- CREATE DEFAULT ACCOUNTS
-- =====================================================

PRINT N'Bắt đầu tạo tài khoản mặc định...';

-- Tạo Manager admin
DECLARE @new_manager_id INT;
DECLARE @manager_status NVARCHAR(255);

PRINT N'Đang tạo tài khoản Manager admin...';

EXEC sp_create_manager_account
    @p_username = 'admin',
    @p_password = '{bcrypt}$2a$10$WMn1T29Wu7giJtgKJ11GruP95voXc9unATF.0GuCtpOcm8oqkxY92',
    @p_last_name = N'NGUYỄN NGỌC',
    @p_first_name = N'PHÚ',
    @p_gender = 'MALE',
    @p_phone = '0987654321',
    @p_email = 'admin@milkteashop.com',
    @p_new_account_id = @new_manager_id OUTPUT,
    @p_status_message = @manager_status OUTPUT;

PRINT N'Kết quả tạo Manager - Account ID: ' + ISNULL(CAST(@new_manager_id AS NVARCHAR(10)), 'NULL') + N', Status: ' + ISNULL(@manager_status, 'NULL');

-- Tạo Staff vanphong
DECLARE @new_staff_id INT;
DECLARE @staff_status NVARCHAR(255);

PRINT N'Đang tạo tài khoản Staff vanphong...';

EXEC sp_create_staff_account
    @p_username = 'vanphong',
    @p_password = '{bcrypt}$2a$10$WMn1T29Wu7giJtgKJ11GruP95voXc9unATF.0GuCtpOcm8oqkxY92',
    @p_position = N'Nhân Viên Bán Hàng',
    @p_last_name = N'Nguyễn Văn',
    @p_first_name = N'Phương',
    @p_gender = 'MALE',
    @p_phone = '0356789012',
    @p_email = 'thpa@gmail.com',
    @p_new_account_id = @new_staff_id OUTPUT,
    @p_status_message = @staff_status OUTPUT;

PRINT N'Kết quả tạo Staff vanphong - Account ID: ' + ISNULL(CAST(@new_staff_id AS NVARCHAR(10)), 'NULL') + N', Status: ' + ISNULL(@staff_status, 'NULL');

-- Tạo Staff thanhphuong
PRINT N'Đang tạo tài khoản Staff thanhphuong...';

EXEC sp_create_staff_account
    @p_username = 'thanhphuong',
    @p_password = '{bcrypt}$2a$10$WMn1T29Wu7giJtgKJ11GruP95voXc9unATF.0GuCtpOcm8oqkxY92',
    @p_position = N'Nhân Viên Bán Hàng',
    @p_last_name = N'Nguyễn Thành',
    @p_first_name = N'Phương',
    @p_gender = 'MALE',
    @p_phone = '0981234567',
    @p_email = 'thp@gmail.com',
    @p_new_account_id = @new_staff_id OUTPUT,
    @p_status_message = @staff_status OUTPUT;

PRINT N'Kết quả tạo Staff thanhphuong - Account ID: ' + ISNULL(CAST(@new_staff_id AS NVARCHAR(10)), 'NULL') + N', Status: ' + ISNULL(@staff_status, 'NULL');

PRINT N'';
PRINT N'=== LƯU Ý VỀ QUẢN LÝ NGƯỜI DÙNG SQL SERVER ===';
PRINT N'1. SQL Server phân biệt Login (server-level) và User (database-level)';
PRINT N'2. Procedures này chỉ quản lý database users, không tạo server logins';
PRINT N'3. Trong production, cần tạo logins trước khi tạo users';
PRINT N'4. Sử dụng Windows Authentication hoặc SQL Authentication tùy môi trường';
PRINT N'';
PRINT N'Đã tạo thành công các procedures quản lý người dùng và tài khoản mặc định';
GO 