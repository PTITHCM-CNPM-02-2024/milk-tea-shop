-- File: 010_create_agent_jobs.sql
-- Tạo SQL Server Agent Job để thay thế MySQL Event từ V18.0.5__scheduled_events.sql
-- Yêu cầu: SQL Server Agent phải được bật (MSSQL_AGENT_ENABLED=true)

USE msdb;
GO

PRINT N'=== ĐANG TẠO SQL SERVER AGENT JOB ===';

-- Kiểm tra xem SQL Server Agent có đang chạy không
IF EXISTS (SELECT 1 FROM sys.dm_server_services WHERE servicename LIKE '%Agent%' AND status = 4)
BEGIN
    PRINT N'✅ SQL Server Agent đang chạy';
END
ELSE
BEGIN
    PRINT N'⚠️ SQL Server Agent chưa sẵn sàng, sẽ tạo job để sử dụng sau';
END

-- =====================================================
-- JOB: DEACTIVATE EXPIRED DISCOUNTS
-- Thay thế cho event_deactivate_expired_discounts từ MySQL
-- =====================================================

-- Xóa job cũ nếu tồn tại
IF EXISTS (SELECT 1 FROM msdb.dbo.sysjobs WHERE name = N'Deactivate Expired Discounts')
BEGIN
    EXEC msdb.dbo.sp_delete_job @job_name = N'Deactivate Expired Discounts';
    PRINT N'Đã xóa job cũ: Deactivate Expired Discounts';
END

-- Tạo job để vô hiệu hóa discount hết hạn
EXEC msdb.dbo.sp_add_job
    @job_name = N'Deactivate Expired Discounts',
    @description = N'Vô hiệu hóa các discount đã hết hạn - chạy hàng ngày lúc 1:00 AM',
    @category_name = N'Database Maintenance',
    @enabled = 1;

-- Thêm job step
EXEC msdb.dbo.sp_add_jobstep
    @job_name = N'Deactivate Expired Discounts',
    @step_name = N'Deactivate Discounts',
    @subsystem = N'TSQL',
    @command = N'
        BEGIN TRY
            EXEC MilkTeaShop.dbo.sp_deactivate_expired_discounts;
            PRINT N''Deactivate Expired Discounts completed successfully at '' + CONVERT(NVARCHAR, GETDATE(), 121);
        END TRY
        BEGIN CATCH
            DECLARE @ErrorMsg NVARCHAR(4000) = ERROR_MESSAGE();
            PRINT N''Error in Deactivate Expired Discounts: '' + @ErrorMsg;
            THROW;
        END CATCH
    ',
    @database_name = N'MilkTeaShop',
    @retry_attempts = 2,
    @retry_interval = 5;

-- Tạo schedule chạy hàng ngày lúc 1:00 AM (giống như MySQL event)
EXEC msdb.dbo.sp_add_schedule
    @schedule_name = N'Daily at 1 AM',
    @freq_type = 4, -- Daily
    @freq_interval = 1,
    @active_start_time = 010000; -- 01:00:00

-- Gắn schedule vào job
EXEC msdb.dbo.sp_attach_schedule
    @job_name = N'Deactivate Expired Discounts',
    @schedule_name = N'Daily at 1 AM';

-- Gán job cho server
EXEC msdb.dbo.sp_add_jobserver
    @job_name = N'Deactivate Expired Discounts';

PRINT N'✅ Đã tạo job: Deactivate Expired Discounts (chạy hàng ngày 1:00 AM)';

-- =====================================================
-- VERIFICATION & STATUS
-- =====================================================

PRINT N'';
PRINT N'=== THÔNG TIN JOB ===';

-- Hiển thị thông tin job đã tạo
SELECT 
    j.name AS JobName,
    j.description AS Description,
    j.enabled AS Enabled,
    IIF(j.enabled = 1, N'✅ Enabled', N'❌ Disabled') AS Status
FROM msdb.dbo.sysjobs j
WHERE j.name = 'Deactivate Expired Discounts';

-- Hiển thị thông tin schedule
PRINT N'';
PRINT N'=== SCHEDULE ===';
SELECT 
    s.name AS ScheduleName,
    N'Daily' AS Frequency,
    RIGHT('0' + CAST(s.active_start_time / 10000 AS VARCHAR), 2) + ':' +
    RIGHT('0' + CAST((s.active_start_time % 10000) / 100 AS VARCHAR), 2) AS StartTime
FROM msdb.dbo.sysschedules s
WHERE s.name = 'Daily at 1 AM';

PRINT N'';
PRINT N'=== LỆNH KIỂM TRA ===';
PRINT N'Để xem lịch sử chạy job:';
PRINT N'SELECT * FROM msdb.dbo.sysjobhistory WHERE job_id = (SELECT job_id FROM msdb.dbo.sysjobs WHERE name = ''Deactivate Expired Discounts'');';
PRINT N'';
PRINT N'Để chạy job thủ công:';
PRINT N'EXEC msdb.dbo.sp_start_job @job_name = ''Deactivate Expired Discounts'';';

GO 