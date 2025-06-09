-- SQL Server 2022 Scheduled Events for Milk Tea Shop
-- Converted from MySQL V18.0.5__scheduled_events.sql

USE MilkTeaShop;
GO

-- =====================================================
-- DISCOUNT MANAGEMENT PROCEDURES
-- =====================================================

-- Procedure để vô hiệu hóa các discount đã hết hạn
CREATE PROCEDURE sp_deactivate_expired_discounts
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @rows_affected INT;
    DECLARE @result_message NVARCHAR(500);
    
    BEGIN TRY
        -- Ghi log bắt đầu (Tùy chọn)
        PRINT N'Bắt đầu vô hiệu hóa các discount đã hết hạn...';
        PRINT N'Thời gian: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
        
        -- Cập nhật trạng thái discount đã hết hạn
        UPDATE discount
        SET is_active = 0,
            updated_at = GETDATE()
        WHERE is_active = 1 -- Chỉ cập nhật những cái đang active
          AND valid_until < CAST(GETDATE() AS DATE); -- Sử dụng '<' để chỉ vô hiệu hóa SAU ngày hết hạn.
                                                    -- Nếu muốn vô hiệu hóa VÀO ngày hết hạn, dùng '<='
        
        SET @rows_affected = @@ROWCOUNT;
        SET @result_message = N'Đã vô hiệu hóa ' + CAST(@rows_affected AS NVARCHAR(10)) + N' discount hết hạn';
        PRINT @result_message;
        
        -- Ghi log kết thúc (Tùy chọn)
        PRINT N'Hoàn thành vô hiệu hóa discount lúc: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
        
    END TRY
    BEGIN CATCH
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        PRINT N'Lỗi trong quá trình vô hiệu hóa discount: ' + @ErrorMessage;
        THROW;
    END CATCH
END
GO

-- =====================================================
-- COMPREHENSIVE DAILY MAINTENANCE PROCEDURE
-- =====================================================

-- Thủ tục tổng hợp bảo trì hàng ngày  
CREATE PROCEDURE sp_daily_system_maintenance
AS
BEGIN
    SET NOCOUNT ON;
    
    PRINT N'===============================================';
    PRINT N'=== BẮT ĐẦU BẢO TRÌ HỆ THỐNG HÀNG NGÀY ===';
    PRINT N'===============================================';
    PRINT N'Thời gian bắt đầu: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
    PRINT N'';
    
    BEGIN TRY
        -- 1. Vô hiệu hóa các discount đã hết hạn
        PRINT N'>>> BƯỚC 1: Vô hiệu hóa discount hết hạn';
        EXEC sp_deactivate_expired_discounts;
        PRINT N'';
        
        -- 2. Bảo trì membership (nếu procedure tồn tại)
        IF OBJECT_ID('dbo.sp_daily_membership_maintenance', 'P') IS NOT NULL
        BEGIN
            PRINT N'>>> BƯỚC 2: Bảo trì membership';
            EXEC sp_daily_membership_maintenance;
            PRINT N'';
        END
        ELSE
        BEGIN
            PRINT N'>>> BƯỚC 2: Bỏ qua bảo trì membership (procedure chưa được tạo)';
            PRINT N'';
        END
        
        -- 3. Hủy order tự động được xử lý bằng job riêng biệt (sp_auto_cancel_overdue_orders)
        -- 4. Có thể thêm các tác vụ bảo trì khác ở đây
        
        PRINT N'===============================================';
        PRINT N'=== HOÀN THÀNH BẢO TRÌ HỆ THỐNG ===';
        PRINT N'===============================================';
        PRINT N'Thời gian hoàn thành: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
        
    END TRY
    BEGIN CATCH
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        PRINT N'';
        PRINT N'===============================================';
        PRINT N'=== LỖI TRONG QUÁ TRÌNH BẢO TRÌ HỆ THỐNG ===';
        PRINT N'===============================================';
        PRINT N'Lỗi: ' + @ErrorMessage;
        PRINT N'Thời gian lỗi: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
        THROW;
    END CATCH
END
GO

-- =====================================================
-- AUTO ORDER CANCELLATION - REMOVED
-- =====================================================
-- SP này đã được thay thế bằng sp_auto_cancel_overdue_orders 
-- trong file 011_auto_cancel_overdue_orders.sql với tính năng nâng cao hơn

-- =====================================================
-- SQL SERVER AGENT JOB SETUP SCRIPTS
-- =====================================================

PRINT N'=== HƯỚNG DẪN THIẾT LẬP SQL SERVER AGENT JOBS ===';
PRINT N'';
PRINT N'1. JOB DAILY SYSTEM MAINTENANCE:';
PRINT N'   - Tên job: "Daily System Maintenance"';
PRINT N'   - Command: EXEC sp_daily_system_maintenance';  
PRINT N'   - Schedule: Hàng ngày lúc 01:00 AM';
PRINT N'';
PRINT N'2. JOB DISCOUNT DEACTIVATION (tùy chọn riêng biệt):';
PRINT N'   - Tên job: "Deactivate Expired Discounts"';
PRINT N'   - Command: EXEC sp_deactivate_expired_discounts';
PRINT N'   - Schedule: Hàng ngày lúc 01:30 AM';
PRINT N'';
PRINT N'3. JOB AUTO CANCEL ORDERS:';
PRINT N'   - Được xử lý bởi file 011_auto_cancel_overdue_orders.sql';
PRINT N'   - Command: EXEC sp_auto_cancel_overdue_orders';
PRINT N'   - Schedule: Mỗi 30 giây';
PRINT N'';

-- =====================================================
-- AUTOMATED JOB CREATION SCRIPTS
-- =====================================================
USE msdb;
GO

-- 1. Tạo job chính cho bảo trì hàng ngày
EXEC dbo.sp_add_job
    @job_name = N'Daily System Maintenance',
    @description = N'Daily maintenance for discounts, memberships and system cleanup';

EXEC dbo.sp_add_jobstep
    @job_name = N'Daily System Maintenance',
    @step_name = N'Run Daily Maintenance',
    @command = N'EXEC MilkTeaShop.dbo.sp_daily_system_maintenance',
    @database_name = N'MilkTeaShop';

EXEC dbo.sp_add_schedule
    @schedule_name = N'Daily at 1 AM',
    @freq_type = 4, -- Daily
    @freq_interval = 1,
    @active_start_time = 010000; -- 01:00:00

EXEC dbo.sp_attach_schedule
    @job_name = N'Daily System Maintenance',
    @schedule_name = N'Daily at 1 AM';

EXEC dbo.sp_add_jobserver
    @job_name = N'Daily System Maintenance';

-- 2. Job hủy order tự động được xử lý trong file 011_auto_cancel_overdue_orders.sql

-- 3. Tạo job riêng cho discount deactivation (nếu muốn tách riêng)
EXEC dbo.sp_add_job
    @job_name = N'Deactivate Expired Discounts',
    @description = N'Deactivate discounts that have passed their expiration date';

EXEC dbo.sp_add_jobstep
    @job_name = N'Deactivate Expired Discounts',
    @step_name = N'Deactivate Discounts',
    @command = N'EXEC MilkTeaShop.dbo.sp_deactivate_expired_discounts',
    @database_name = N'MilkTeaShop';

EXEC dbo.sp_add_schedule
    @schedule_name = N'Daily at 1:30 AM',
    @freq_type = 4, -- Daily
    @freq_interval = 1,
    @active_start_time = 013000; -- 01:30:00

EXEC dbo.sp_attach_schedule
    @job_name = N'Deactivate Expired Discounts',
    @schedule_name = N'Daily at 1:30 AM';

EXEC dbo.sp_add_jobserver
    @job_name = N'Deactivate Expired Discounts';

PRINT N'✅ Job Daily System Maintenance đã được tạo';
PRINT N'✅ Job Deactivate Expired Discounts đã được tạo';
PRINT N'Đã tạo thành công các procedures cho scheduled events';
PRINT N'Các procedures có thể được gọi thủ công hoặc thông qua SQL Server Agent Jobs';
GO 