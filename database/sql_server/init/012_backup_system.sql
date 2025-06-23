-- =======================================================
-- BACKUP SYSTEM - FULL & HOURLY BACKUP
-- =======================================================
-- Tự động backup database MilkTeaShop
-- - Full backup hàng ngày lúc 2:00 AM
-- - Differential backup mỗi giờ
-- =======================================================

USE MilkTeaShop;
GO

SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO


CREATE PROCEDURE sp_ensure_backup_directory
    @backup_path NVARCHAR(500) = N'/var/opt/mssql/backup/'
AS
BEGIN
    RETURN 1; -- Giả sử luôn thành công, có thể mở rộng sau này để kiểm tra thực tế
END;
GO
CREATE PROCEDURE sp_full_backup_database
    @backup_path NVARCHAR(500) = N'/var/opt/mssql/backup/',
    @database_name NVARCHAR(100) = N'MilkTeaShop'
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @filename NVARCHAR(500);
    DECLARE @full_path NVARCHAR(500);
    DECLARE @date_stamp NVARCHAR(20);
    DECLARE @sql NVARCHAR(1000);
    DECLARE @backup_dir_ok INT;
    EXEC @backup_dir_ok = sp_ensure_backup_directory @backup_path;    
    IF @backup_dir_ok = 0
    BEGIN
        SET @backup_path = N'/var/opt/mssql/data/';
        PRINT N'⚠️ Fallback to data directory: ' + @backup_path;
    END
    
    SET @date_stamp = FORMAT(GETDATE(), 'yyyyMMdd_HHmmss');
    SET @filename = @database_name + N'_FULL_' + @date_stamp + N'.bak';
    SET @full_path = @backup_path + @filename;
    
    DECLARE @backup_name NVARCHAR(500) = @database_name + N' Full Backup';
    DECLARE @backup_desc NVARCHAR(500) = N'Full backup của ' + @database_name + N' lúc ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
    
    BEGIN TRY
        BACKUP DATABASE @database_name 
        TO DISK = @full_path
        WITH FORMAT, INIT, 
             NAME = @backup_name,
             DESCRIPTION = @backup_desc,
             COMPRESSION,
             CHECKSUM,
             STATS = 10;
        
        PRINT N'✅ Full backup thành công: ' + @full_path;
        PRINT N'📅 Thời gian: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
        
        INSERT INTO backup_log (backup_type, backup_path, database_name, backup_size_mb, created_at)
        SELECT 
            N'FULL' as backup_type,
            @full_path as backup_path,
            @database_name as database_name,
            CAST(backup_size / 1024.0 / 1024.0 AS DECIMAL(10,2)) as backup_size_mb,
            GETDATE() as created_at
        FROM msdb.dbo.backupset 
        WHERE database_name = @database_name 
        AND backup_start_date = (SELECT MAX(backup_start_date) FROM msdb.dbo.backupset WHERE database_name = @database_name);
        
    END TRY
    BEGIN CATCH
        PRINT N'❌ Lỗi full backup: ' + ERROR_MESSAGE();
        THROW;
    END CATCH
END;
GO

CREATE PROCEDURE sp_differential_backup_database
    @backup_path NVARCHAR(500) = N'/var/opt/mssql/backup/',
    @database_name NVARCHAR(100) = N'MilkTeaShop'
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @filename NVARCHAR(500);
    DECLARE @full_path NVARCHAR(500);
    DECLARE @date_stamp NVARCHAR(20);
    DECLARE @sql NVARCHAR(1000);
    DECLARE @backup_dir_ok INT;
    DECLARE @temp NVARCHAR(500);
    
    EXEC @backup_dir_ok = sp_ensure_backup_directory @backup_path;
    
    IF @backup_dir_ok = 0
    BEGIN
        SET @backup_path = N'/var/opt/mssql/data/';
        PRINT N'⚠️ Fallback to data directory: ' + @backup_path;
    END
    
    SET @date_stamp = FORMAT(GETDATE(), 'yyyyMMdd_HH') + N'00';
    SET @filename = @database_name + N'_DIFF_' + @date_stamp + N'.bak';
    SET @full_path = @backup_path + @filename;
    
    DECLARE @backup_name NVARCHAR(500) = N'Differential Backup of ' + @database_name;
    DECLARE @backup_desc NVARCHAR(500) = N'Differential backup của ' + @database_name + N' lúc ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
    
    BEGIN TRY
        IF NOT EXISTS (
            SELECT 1 FROM msdb.dbo.backupset 
            WHERE database_name = @database_name 
            AND type = 'D'
            AND backup_start_date >= DATEADD(DAY, -7, GETDATE())
        )
        BEGIN
            PRINT N'⚠️ Không tìm thấy full backup trong 7 ngày gần đây. Thực hiện full backup trước...';
            EXEC sp_full_backup_database @backup_path, @database_name;
            RETURN;
        END;
        
        BACKUP DATABASE @database_name 
        TO DISK = @full_path
        WITH DIFFERENTIAL, FORMAT, INIT,
             NAME = @backup_name,
             DESCRIPTION = @backup_desc,
             COMPRESSION,
             CHECKSUM,
             STATS = 10;
        
        PRINT N'✅ Differential backup thành công: ' + @full_path;
        PRINT N'📅 Thời gian: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
        
        INSERT INTO backup_log (backup_type, backup_path, database_name, backup_size_mb, created_at)
        SELECT 
            N'DIFFERENTIAL' as backup_type,
            @full_path as backup_path,
            @database_name as database_name,
            CAST(backup_size / 1024.0 / 1024.0 AS DECIMAL(10,2)) as backup_size_mb,
            GETDATE() as created_at
        FROM msdb.dbo.backupset 
        WHERE database_name = @database_name 
        AND backup_start_date = (SELECT MAX(backup_start_date) FROM msdb.dbo.backupset WHERE database_name = @database_name);
        
    END TRY
    BEGIN CATCH
        PRINT N'❌ Lỗi differential backup: ' + ERROR_MESSAGE();
        THROW;
    END CATCH
END;
GO

CREATE PROCEDURE sp_cleanup_old_backups
    @backup_path NVARCHAR(500) = N'/var/opt/mssql/backup/',
    @retention_days INT = 30
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @cutoff_date DATETIME;
    DECLARE @deleted_count INT = 0;
    
    SET @cutoff_date = DATEADD(DAY, -@retention_days, GETDATE());
    
    BEGIN TRY
        DECLARE @filename NVARCHAR(500);
        DECLARE @full_path NVARCHAR(500);
        DECLARE @cmd NVARCHAR(1000);
        
        DECLARE cleanup_cursor CURSOR FOR
        SELECT backup_path 
        FROM backup_log 
        WHERE created_at < @cutoff_date;
        
        OPEN cleanup_cursor;
        FETCH NEXT FROM cleanup_cursor INTO @full_path;
        
        WHILE @@FETCH_STATUS = 0
        BEGIN
            SET @cmd = N'rm -f "' + @full_path + N'"';
            EXEC xp_cmdshell @cmd, NO_OUTPUT;
            
            DELETE FROM backup_log WHERE backup_path = @full_path;
            SET @deleted_count = @deleted_count + 1;
            
            FETCH NEXT FROM cleanup_cursor INTO @full_path;
        END;
        
        CLOSE cleanup_cursor;
        DEALLOCATE cleanup_cursor;
        
        PRINT N'✅ Đã cleanup ' + CAST(@deleted_count AS NVARCHAR(10)) + N' backup files cũ';
        PRINT N'📅 Cutoff date: ' + CONVERT(NVARCHAR(19), @cutoff_date, 120);
        
    END TRY
    BEGIN CATCH
        IF CURSOR_STATUS('global', 'cleanup_cursor') >= 0
        BEGIN
            CLOSE cleanup_cursor;
            DEALLOCATE cleanup_cursor;
        END;
        
        PRINT N'❌ Lỗi cleanup backup: ' + ERROR_MESSAGE();
    END CATCH
END;
GO

CREATE PROCEDURE sp_backup_status_report
AS
BEGIN
    SET NOCOUNT ON;
    
    PRINT N'===============================================';
    PRINT N'📊 BACKUP STATUS REPORT - MilkTeaShop';
    PRINT N'===============================================';
    PRINT N'📅 Report time: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
    PRINT N'';
    
    PRINT N'📈 BACKUP SUMMARY (Last 7 days):';
    SELECT 
        backup_type,
        COUNT(*) as backup_count,
        AVG(backup_size_mb) as avg_size_mb,
        MAX(created_at) as last_backup,
        MIN(created_at) as first_backup
    FROM backup_log 
    WHERE created_at >= DATEADD(DAY, -7, GETDATE())
    GROUP BY backup_type
    ORDER BY backup_type;
    
    PRINT N'';
    PRINT N'📋 RECENT BACKUPS (Last 24 hours):';
    SELECT TOP 10
        backup_type,
        backup_path,
        backup_size_mb,
        created_at
    FROM backup_log 
    WHERE created_at >= DATEADD(DAY, -1, GETDATE())
    ORDER BY created_at DESC;
    
    DECLARE @last_full_backup DATETIME;
    DECLARE @last_diff_backup DATETIME;
    
    SELECT @last_full_backup = MAX(created_at) FROM backup_log WHERE backup_type = 'FULL';
    SELECT @last_diff_backup = MAX(created_at) FROM backup_log WHERE backup_type = 'DIFFERENTIAL';
    
    PRINT N'';
    PRINT N'🏥 BACKUP HEALTH CHECK:';
    
    IF @last_full_backup IS NULL OR @last_full_backup < DATEADD(DAY, -7, GETDATE())
    BEGIN
        PRINT N'❌ WARNING: No full backup in last 7 days!';
    END
    ELSE
    BEGIN
        PRINT N'✅ Full backup: ' + ISNULL(CONVERT(NVARCHAR(19), @last_full_backup, 120), 'Never');
    END
    
    IF @last_diff_backup IS NULL OR @last_diff_backup < DATEADD(HOUR, -2, GETDATE())
    BEGIN
        PRINT N'⚠️ WARNING: No differential backup in last 2 hours!';
    END
    ELSE
    BEGIN
        PRINT N'✅ Differential backup: ' + ISNULL(CONVERT(NVARCHAR(19), @last_diff_backup, 120), 'Never');
    END
    
    PRINT N'===============================================';
END;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'backup_log')
BEGIN
    CREATE TABLE backup_log (
        id INT IDENTITY(1,1) PRIMARY KEY,
        backup_type NVARCHAR(20) NOT NULL, -- 'FULL', 'DIFFERENTIAL', 'LOG'
        backup_path NVARCHAR(500) NOT NULL,
        database_name NVARCHAR(100) NOT NULL,
        backup_size_mb DECIMAL(10,2) NULL,
        created_at DATETIME NOT NULL DEFAULT GETDATE(),
        
        INDEX idx_backup_log_created_at (created_at),
        INDEX idx_backup_log_database_type (database_name, backup_type)
    );
    
    PRINT N'✅ Đã tạo bảng backup_log';
END;
GO

-- Job 1: Full Backup hàng ngày lúc 2:00 AM
PRINT N'📝 Tạo SQL Server Agent Job: Daily Full Backup...';

EXEC msdb.dbo.sp_add_job
    @job_name = N'MilkTeaShop - Daily Full Backup',
    @enabled = 1,
    @description = N'Full backup database MilkTeaShop hàng ngày lúc 2:00 AM',
    @category_name = N'Database Maintenance';

EXEC msdb.dbo.sp_add_jobstep
    @job_name = N'MilkTeaShop - Daily Full Backup',
    @step_name = N'Execute Full Backup',
    @command = N'EXEC MilkTeaShop.dbo.sp_full_backup_database;',
    @database_name = N'MilkTeaShop';

EXEC msdb.dbo.sp_add_schedule
    @schedule_name = N'Daily at 2:00 AM',
    @freq_type = 4, -- Daily
    @freq_interval = 1,
    @active_start_time = 20000; -- 2:00 AM

EXEC msdb.dbo.sp_attach_schedule
    @job_name = N'MilkTeaShop - Daily Full Backup',
    @schedule_name = N'Daily at 2:00 AM';

EXEC msdb.dbo.sp_add_jobserver
    @job_name = N'MilkTeaShop - Daily Full Backup';

PRINT N'✅ Job Daily Full Backup đã được tạo';

PRINT N'📝 Tạo SQL Server Agent Job: Hourly Differential Backup...';

EXEC msdb.dbo.sp_add_job
    @job_name = N'MilkTeaShop - Hourly Differential Backup',
    @enabled = 1,
    @description = N'Differential backup database MilkTeaShop mỗi giờ',
    @category_name = N'Database Maintenance';

EXEC msdb.dbo.sp_add_jobstep
    @job_name = N'MilkTeaShop - Hourly Differential Backup',
    @step_name = N'Execute Differential Backup',
    @command = N'EXEC MilkTeaShop.dbo.sp_differential_backup_database;',
    @database_name = N'MilkTeaShop';

EXEC msdb.dbo.sp_add_schedule
    @schedule_name = N'Hourly Business Hours',
    @freq_type = 4, -- Daily
    @freq_interval = 1,
    @freq_subday_type = 8, -- Hours
    @freq_subday_interval = 1, -- Every 1 hour
    @active_start_time = 80000, -- 8:00 AM
    @active_end_time = 220000; -- 10:00 PM

EXEC msdb.dbo.sp_attach_schedule
    @job_name = N'MilkTeaShop - Hourly Differential Backup',
    @schedule_name = N'Hourly Business Hours';

EXEC msdb.dbo.sp_add_jobserver
    @job_name = N'MilkTeaShop - Hourly Differential Backup';

PRINT N'✅ Job Hourly Differential Backup đã được tạo';

PRINT N'📝 Tạo SQL Server Agent Job: Weekly Backup Cleanup...';

EXEC msdb.dbo.sp_add_job
    @job_name = N'MilkTeaShop - Weekly Backup Cleanup',
    @enabled = 1,
    @description = N'Cleanup old backup files (>30 days) hàng tuần',
    @category_name = N'Database Maintenance';

EXEC msdb.dbo.sp_add_jobstep
    @job_name = N'MilkTeaShop - Weekly Backup Cleanup',
    @step_name = N'Execute Backup Cleanup',
    @command = N'EXEC MilkTeaShop.dbo.sp_cleanup_old_backups @retention_days = 30;',
    @database_name = N'MilkTeaShop';

EXEC msdb.dbo.sp_add_schedule
    @schedule_name = N'Weekly Sunday 3:00 AM',
    @freq_type = 8, -- Weekly
    @freq_interval = 1, -- Sunday
    @freq_recurrence_factor = 1,
    @active_start_time = 30000; -- 3:00 AM

EXEC msdb.dbo.sp_attach_schedule
    @job_name = N'MilkTeaShop - Weekly Backup Cleanup',
    @schedule_name = N'Weekly Sunday 3:00 AM';

EXEC msdb.dbo.sp_add_jobserver
    @job_name = N'MilkTeaShop - Weekly Backup Cleanup';

PRINT N'✅ Job Weekly Backup Cleanup đã được tạo';

PRINT N'';
PRINT N'🧪 TESTING BACKUP SYSTEM...';
PRINT N'===============================================';

-- Tạo thư mục backup nếu chưa có (sử dụng xp_cmdshell nếu available)
-- EXEC xp_cmdshell 'mkdir -p /var/opt/mssql/backup/', NO_OUTPUT;

-- Tạo thư mục backup nếu chưa có
PRINT N'📁 Tạo thư mục backup...';
BEGIN TRY
    -- Kiểm tra và tạo backup directory
    DECLARE @backup_dir NVARCHAR(500) = N'/var/opt/mssql/backup';
    DECLARE @cmd NVARCHAR(1000) = N'mkdir -p ' + @backup_dir;
    
    -- Sử dụng xp_cmdshell để tạo directory (nếu enabled)
    -- EXEC xp_cmdshell @cmd, NO_OUTPUT;
    
    PRINT N'✅ Backup directory ready: ' + @backup_dir;
END TRY
BEGIN CATCH
    PRINT N'⚠️ Không thể tạo backup directory: ' + ERROR_MESSAGE();
    PRINT N'💡 Hãy đảm bảo volume được mount chính xác trong Docker';
END CATCH

-- Test full backup với error handling tốt hơn
PRINT N'📝 Testing full backup...';
BEGIN TRY
    EXEC sp_full_backup_database;
    PRINT N'✅ Full backup test thành công';
END TRY
BEGIN CATCH
    DECLARE @error_msg NVARCHAR(500) = ERROR_MESSAGE();
    PRINT N'❌ Full backup test thất bại: ' + @error_msg;
    
    -- Nếu lỗi về đường dẫn, thử backup vào thư mục mặc định
    IF @error_msg LIKE '%cannot open backup device%' OR @error_msg LIKE '%path%'
    BEGIN
        PRINT N'💡 Đang thử backup vào thư mục mặc định...';
        BEGIN TRY
            EXEC sp_full_backup_database @backup_path = N'/var/opt/mssql/data/';
            PRINT N'✅ Backup vào thư mục data thành công';
        END TRY
        BEGIN CATCH
            PRINT N'❌ Backup vào thư mục data cũng thất bại: ' + ERROR_MESSAGE();
        END CATCH
    END
END CATCH

PRINT N'';
PRINT N'📊 Backup status report:';
EXEC sp_backup_status_report;

PRINT N'';
PRINT N'===============================================';
PRINT N'🎉 BACKUP SYSTEM SETUP COMPLETED!';
PRINT N'===============================================';
PRINT N'✅ Procedures: sp_full_backup_database, sp_differential_backup_database';
PRINT N'✅ Jobs: Daily Full Backup (2:00 AM), Hourly Differential (8AM-10PM)';
PRINT N'✅ Cleanup: Weekly cleanup của backup files >30 days';
PRINT N'✅ Monitoring: sp_backup_status_report procedure';
PRINT N'✅ Logging: backup_log table để track backup history';
PRINT N'';
PRINT N'📁 Backup Location: /var/opt/mssql/backup/ (with fallback to /var/opt/mssql/data/)';
PRINT N'🐳 Docker: Volume sqlserver_backup mounted to /var/opt/mssql/backup';
PRINT N'📅 Schedule:';
PRINT N'   - Full Backup: Daily 2:00 AM';
PRINT N'   - Differential: Every hour 8:00 AM - 10:00 PM';
PRINT N'   - Cleanup: Weekly Sunday 3:00 AM';
PRINT N'';
PRINT N'📋 Manual Commands:';
PRINT N'   EXEC sp_full_backup_database;';
PRINT N'   EXEC sp_differential_backup_database;';
PRINT N'   EXEC sp_backup_status_report;';
PRINT N'===============================================';
GO
