-- =======================================================
-- AUTO CANCEL OVERDUE ORDERS & PAYMENTS - 3 MINUTES
-- =======================================================
-- Tự động hủy các order PROCESSING và payment PROCESSING sau 3 phút
-- Schema support: 3 trạng thái cho order và payment
-- =======================================================

USE MilkTeaShop;
GO

-- Set required options
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- =======================================================
-- 1. PROCEDURE HỦY ORDER QUÁ HẠN (3 PHÚT)
-- =======================================================

CREATE PROCEDURE sp_auto_cancel_overdue_orders
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @timeout_minutes INT = 3; -- Hủy sau 3 phút
    DECLARE @cancelled_orders INT = 0;
    DECLARE @cancelled_payments INT = 0;
    DECLARE @error_count INT = 0;
    DECLARE @result_message NVARCHAR(1000);
    
    PRINT N'=== BẮT ĐẦU QUÉT HỦY ORDER QUÁ HẠN ===';
    PRINT N'Thời gian: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
    PRINT N'Timeout: ' + CAST(@timeout_minutes AS NVARCHAR(2)) + N' phút';
    PRINT N'';
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- 1. Hủy tất cả payments PROCESSING quá thời hạn trước
        UPDATE payment
        SET status = 'CANCELLED',
            updated_at = GETDATE()
        WHERE status = 'PROCESSING'
        AND order_id IN (
            SELECT order_id 
            FROM [order] 
            WHERE status = 'PROCESSING'
            AND DATEDIFF(MINUTE, order_time, GETDATE()) > @timeout_minutes
        );
        
        SET @cancelled_payments = @@ROWCOUNT;
        
        -- 2. Hủy tất cả orders PROCESSING quá thời hạn
        UPDATE [order]
        SET status = 'CANCELLED',
            customize_note = ISNULL(customize_note + '. ', '') + 
                           N'Tự động hủy sau ' + CAST(@timeout_minutes AS NVARCHAR(2)) + N' phút không hoạt động',
            updated_at = GETDATE()
        WHERE status = 'PROCESSING'
        AND DATEDIFF(MINUTE, order_time, GETDATE()) > @timeout_minutes;
        
        SET @cancelled_orders = @@ROWCOUNT;
        
        COMMIT TRANSACTION;
        
        -- 3. Thông báo kết quả
        SET @result_message = N'✅ Hoàn thành quét hủy order quá hạn:' + CHAR(13) + CHAR(10) +
                            N'   - Đã hủy ' + CAST(@cancelled_orders AS NVARCHAR(10)) + N' orders' + CHAR(13) + CHAR(10) +
                            N'   - Đã hủy ' + CAST(@cancelled_payments AS NVARCHAR(10)) + N' payments';
        
        PRINT @result_message;
        
        -- 4. Log vào bảng system nếu có orders bị hủy
        IF @cancelled_orders > 0
        BEGIN
            PRINT N'';
            PRINT N'⚠️  CÓ ' + CAST(@cancelled_orders AS NVARCHAR(10)) + N' ĐỠN HÀNG BỊ HỦY TỰ ĐỘNG!';
            PRINT N'   Nguyên nhân: Quá ' + CAST(@timeout_minutes AS NVARCHAR(2)) + N' phút không có hoạt động';
        END
        
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
            
        SET @error_count = 1;
        DECLARE @error_message NVARCHAR(4000) = ERROR_MESSAGE();
        
        PRINT N'❌ LỖI TRONG QUÁ TRÌNH HỦY ORDER QUÁ HẠN:';
        PRINT N'   ' + @error_message;
        
        -- Re-throw error để SQL Agent Job biết có lỗi
        THROW;
    END CATCH
    
    PRINT N'';
    PRINT N'=== KẾT THÚC QUÉT HỦY ORDER QUÁ HẠN ===';
    PRINT N'Thời gian hoàn thành: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
    
    -- Return summary thông qua RETURN code
    -- 0 = success, no orders cancelled
    -- 1 = success, some orders cancelled  
    -- 99 = error occurred
    IF @error_count > 0
        RETURN 99;
    ELSE IF @cancelled_orders > 0
        RETURN 1;
    ELSE
        RETURN 0;
END
GO

-- =======================================================
-- 2. PROCEDURE KIỂM TRA TÌNH TRẠNG ORDER QUÁ HẠN
-- =======================================================

CREATE PROCEDURE sp_check_overdue_orders_status
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @timeout_minutes INT = 3;
    DECLARE @overdue_count INT;
    DECLARE @processing_count INT;
    DECLARE @oldest_order_minutes INT;
    
    -- Đếm orders đang processing
    SELECT @processing_count = COUNT(*)
    FROM [order] 
    WHERE status = 'PROCESSING';
    
    -- Đếm orders quá hạn
    SELECT @overdue_count = COUNT(*)
    FROM [order] 
    WHERE status = 'PROCESSING'
    AND DATEDIFF(MINUTE, order_time, GETDATE()) > @timeout_minutes;
    
    -- Tìm order lâu nhất
    SELECT @oldest_order_minutes = MAX(DATEDIFF(MINUTE, order_time, GETDATE()))
    FROM [order] 
    WHERE status = 'PROCESSING';
    
    -- Thông báo tình trạng
    PRINT N'=== TÌNH TRẠNG ORDER HIỆN TẠI ===';
    PRINT N'Thời gian kiểm tra: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
    PRINT N'';
    PRINT N'📊 Thống kê:';
    PRINT N'   - Tổng orders đang PROCESSING: ' + CAST(ISNULL(@processing_count, 0) AS NVARCHAR(10));
    PRINT N'   - Orders quá hạn (' + CAST(@timeout_minutes AS NVARCHAR(2)) + N' phút): ' + CAST(ISNULL(@overdue_count, 0) AS NVARCHAR(10));
    
    IF @oldest_order_minutes IS NOT NULL
    BEGIN
        PRINT N'   - Order lâu nhất: ' + CAST(@oldest_order_minutes AS NVARCHAR(10)) + N' phút';
        
        IF @oldest_order_minutes > @timeout_minutes
        BEGIN
            PRINT N'';
            PRINT N'⚠️  CẢnh BÁO: Có orders quá hạn cần được xử lý!';
        END
    END
    ELSE
    BEGIN
        PRINT N'   - Không có orders đang xử lý';
    END
    
    PRINT N'';
    
    -- Return số lượng orders quá hạn
    RETURN ISNULL(@overdue_count, 0);
END
GO

-- =======================================================
-- 3. PROCEDURE MAINTENANCE TỔNG HỢP
-- =======================================================

CREATE PROCEDURE sp_order_maintenance
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @return_code INT;
    
    PRINT N'===============================================';
    PRINT N'=== BẮT ĐẦU BẢO TRÌ ORDER VÀ PAYMENT ===';
    PRINT N'===============================================';
    
    BEGIN TRY
        -- 1. Kiểm tra tình trạng trước khi xử lý
        PRINT N'BƯỚC 1: Kiểm tra tình trạng hiện tại';
        EXEC sp_check_overdue_orders_status;
        PRINT N'';
        
        -- 2. Thực hiện hủy order quá hạn
        PRINT N'BƯỚC 2: Thực hiện hủy order quá hạn';
        EXEC @return_code = sp_auto_cancel_overdue_orders;
        PRINT N'';
        
        -- 3. Kiểm tra tình trạng sau khi xử lý
        PRINT N'BƯỚC 3: Kiểm tra tình trạng sau xử lý';
        EXEC sp_check_overdue_orders_status;
        PRINT N'';
        
        -- 4. Thông báo tổng kết
        PRINT N'===============================================';
        PRINT N'=== HOÀN THÀNH BẢO TRÌ ORDER VÀ PAYMENT ===';
        PRINT N'===============================================';
        
        IF @return_code = 1
        BEGIN
            PRINT N'Kết quả: Đã hủy một số orders quá hạn';
        END
        ELSE IF @return_code = 0
        BEGIN
            PRINT N'Kết quả: Không có orders nào cần hủy';
        END
        
        PRINT N'Thời gian hoàn thành: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
        
    END TRY
    BEGIN CATCH
        DECLARE @error_message NVARCHAR(4000) = ERROR_MESSAGE();
        PRINT N'';
        PRINT N'===============================================';
        PRINT N'=== LỖI TRONG QUÁ TRÌNH BẢO TRÌ ===';
        PRINT N'===============================================';
        PRINT N'Lỗi: ' + @error_message;
        PRINT N'Thời gian lỗi: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
        THROW;
    END CATCH
END
GO

-- =======================================================
-- 4. HELPER PROCEDURES
-- =======================================================

-- Procedure để force cancel một order cụ thể
CREATE PROCEDURE sp_force_cancel_order @p_order_id INT,
                                       @p_reason NVARCHAR(255) = N'Force cancelled by admin',
                                       @p_success BIT OUTPUT,
                                       @p_message NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @current_status NVARCHAR(20);
    
    BEGIN TRY
        SELECT @current_status = status FROM [order] WHERE order_id = @p_order_id;
        
        IF @current_status IS NULL
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Đơn hàng không tồn tại';
            RETURN;
        END
        
        IF @current_status != 'PROCESSING'
        BEGIN
            SET @p_success = 0;
            SET @p_message = N'Chỉ có thể hủy đơn hàng đang xử lý. Trạng thái hiện tại: ' + @current_status;
            RETURN;
        END
        
        BEGIN TRANSACTION;
        
        -- Hủy payment trước
        UPDATE payment 
        SET status = 'CANCELLED', updated_at = GETDATE()
        WHERE order_id = @p_order_id AND status = 'PROCESSING';
        
        -- Hủy order
        UPDATE [order]
        SET status = 'CANCELLED',
            customize_note = ISNULL(customize_note + '. ', '') + @p_reason,
            updated_at = GETDATE()
        WHERE order_id = @p_order_id;
        
        COMMIT TRANSACTION;
        
        SET @p_success = 1;
        SET @p_message = N'Đã hủy đơn hàng thành công';
        
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
            
        SET @p_success = 0;
        SET @p_message = N'Lỗi: ' + ERROR_MESSAGE();
    END CATCH
END
GO

-- =======================================================
-- 5. SQL SERVER AGENT JOB SETUP GUIDE
-- =======================================================

PRINT N'===============================================';
PRINT N'=== HƯỚNG DẪN THIẾT LẬP SQL SERVER AGENT JOB ===';
PRINT N'===============================================';
PRINT N'';
PRINT N'1. TẠO JOB AUTO CANCEL OVERDUE ORDERS:';
PRINT N'   - Tên job: "Auto Cancel Overdue Orders"';
PRINT N'   - Command: EXEC MilkTeaShop.dbo.sp_auto_cancel_overdue_orders';
PRINT N'   - Schedule: Mỗi 30 giây (để phát hiện nhanh)';
PRINT N'';
PRINT N'2. TẠO JOB ORDER MAINTENANCE:';
PRINT N'   - Tên job: "Order Maintenance"';
PRINT N'   - Command: EXEC MilkTeaShop.dbo.sp_order_maintenance';
PRINT N'   - Schedule: Mỗi 5 phút (để monitoring)';
PRINT N'';
PRINT N'3. CÁCH CHẠY THỬ NGHIỆM:';
PRINT N'   - EXEC sp_check_overdue_orders_status';
PRINT N'   - EXEC sp_auto_cancel_overdue_orders';
PRINT N'   - EXEC sp_order_maintenance';
PRINT N'';


-- =======================================================
-- AUTOMATED JOB CREATION SCRIPTS (OPTIONAL)
-- =======================================================
-- Uncomment script này để tạo jobs tự động (cần quyền sysadmin)

USE msdb;
GO

-- 1. Tạo job auto cancel overdue orders (chạy mỗi 30 giây)
EXEC dbo.sp_add_job
    @job_name = N'Auto Cancel Overdue Orders',
    @description = N'Automatically cancel processing orders and payments after 3 minutes of inactivity';

EXEC dbo.sp_add_jobstep
    @job_name = N'Auto Cancel Overdue Orders',
    @step_name = N'Cancel Overdue Orders',
    @command = N'EXEC MilkTeaShop.dbo.sp_auto_cancel_overdue_orders',
    @database_name = N'MilkTeaShop';

EXEC dbo.sp_add_schedule
    @schedule_name = N'Every 30 Seconds',
    @freq_type = 4, -- Daily
    @freq_interval = 1,
    @freq_subday_type = 2, -- Seconds
    @freq_subday_interval = 30,
    @active_start_time = 000000, -- 00:00:00
    @active_end_time = 235959; -- 23:59:59

EXEC dbo.sp_attach_schedule
    @job_name = N'Auto Cancel Overdue Orders',
    @schedule_name = N'Every 30 Seconds';

EXEC dbo.sp_add_jobserver
    @job_name = N'Auto Cancel Overdue Orders';

-- 2. Tạo job order maintenance (chạy mỗi 5 phút)
EXEC dbo.sp_add_job
    @job_name = N'Order Maintenance',
    @description = N'Comprehensive order and payment maintenance and monitoring';

EXEC dbo.sp_add_jobstep
    @job_name = N'Order Maintenance',
    @step_name = N'Run Maintenance',
    @command = N'EXEC MilkTeaShop.dbo.sp_order_maintenance',
    @database_name = N'MilkTeaShop';

EXEC dbo.sp_add_schedule
    @schedule_name = N'Every 5 Minutes',
    @freq_type = 4, -- Daily
    @freq_interval = 1,
    @freq_subday_type = 4, -- Minutes
    @freq_subday_interval = 5,
    @active_start_time = 000000, -- 00:00:00
    @active_end_time = 235959; -- 23:59:59

EXEC dbo.sp_attach_schedule
    @job_name = N'Order Maintenance',
    @schedule_name = N'Every 5 Minutes';

EXEC dbo.sp_add_jobserver
    @job_name = N'Order Maintenance';
GO

PRINT N'===============================================';
PRINT N'=== HOÀN THÀNH THIẾT LẬP AUTO CANCEL SYSTEM ===';
PRINT N'===============================================';
PRINT N'✅ Đã tạo các procedures:';
PRINT N'   - sp_auto_cancel_overdue_orders';
PRINT N'   - sp_check_overdue_orders_status';
PRINT N'   - sp_order_maintenance';
PRINT N'   - sp_force_cancel_order';
PRINT N'';
PRINT N'⏱️  Cấu hình timeout: 3 phút';
PRINT N'🔄 Tần suất quét đề xuất: 30 giây';
PRINT N'📋 Monitoring đề xuất: 5 phút';
PRINT N'';
PRINT N'Để bật tự động, hãy tạo SQL Server Agent Jobs theo hướng dẫn ở trên.';

GO 