-- SQL Server 2022 Membership Trigger Events for Milk Tea Shop
-- Converted from MySQL V18.0.4__membership_trigger_event.sql

USE MilkTeaShop;
GO

-- =====================================================
-- MEMBERSHIP MANAGEMENT PROCEDURES
-- =====================================================

-- Thủ tục reset membership về NEWMEM khi hết hạn
CREATE PROCEDURE sp_reset_expired_memberships
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @newmem_id TINYINT;
    DECLARE @rows_affected INT;
    DECLARE @result_message NVARCHAR(500);
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Lấy ID của loại thành viên NEWMEM
        SELECT @newmem_id = membership_type_id
        FROM membership_type
        WHERE type = 'NEWMEM';
        
        IF @newmem_id IS NULL
        BEGIN
            THROW 50057, N'Không tìm thấy loại thành viên NEWMEM', 1;
            RETURN;
        END
        
        -- Tìm và cập nhật các khách hàng có loại thành viên đã hết hạn
        UPDATE c
        SET membership_type_id = @newmem_id,
            current_points = 0,
            updated_at = GETDATE()
        FROM customer c
        INNER JOIN membership_type mt ON c.membership_type_id = mt.membership_type_id
        WHERE mt.valid_until IS NOT NULL
          AND mt.valid_until < CAST(GETDATE() AS DATE)
          AND mt.type != 'NEWMEM';
        
        SET @rows_affected = @@ROWCOUNT;
        SET @result_message = N'Đã reset ' + CAST(@rows_affected AS NVARCHAR(10)) + N' khách hàng về loại thành viên NEWMEM do hết hạn';
        PRINT @result_message;
        
        -- Tự động cập nhật valid_until về sau 1 năm cho các membership đã hết hạn
        UPDATE membership_type
        SET valid_until = DATEADD(YEAR, 1, CAST(GETDATE() AS DATE)),
            updated_at = GETDATE()
        WHERE valid_until IS NOT NULL
          AND valid_until < CAST(GETDATE() AS DATE)
          AND type != 'NEWMEM';
        
        SET @rows_affected = @@ROWCOUNT;
        SET @result_message = N'Đã cập nhật thời hạn cho ' + CAST(@rows_affected AS NVARCHAR(10)) + N' loại thành viên thêm 1 năm';
        PRINT @result_message;
        
        COMMIT TRANSACTION;
        
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        PRINT N'Error occurred - transaction rolled back: ' + @ErrorMessage;
        THROW;
    END CATCH
END
GO

-- Thủ tục tái cấp lại thành viên dựa trên điểm hiện tại
CREATE PROCEDURE sp_recalculate_customer_memberships
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @rows_affected INT;
    DECLARE @result_message NVARCHAR(500);
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Cập nhật loại thành viên dựa trên điểm hiện tại
        UPDATE c
        SET membership_type_id = qualified_mt.membership_type_id,
            updated_at = GETDATE()
        FROM customer c
        CROSS APPLY (
            SELECT TOP 1 mt.membership_type_id
            FROM membership_type mt
            WHERE c.current_points >= mt.required_point
              AND (mt.valid_until IS NULL OR mt.valid_until > CAST(GETDATE() AS DATE))
              AND mt.is_active = 1
            ORDER BY mt.required_point DESC
        ) qualified_mt
        WHERE c.current_points > 0;
        
        SET @rows_affected = @@ROWCOUNT;
        SET @result_message = N'Đã tái cấp loại thành viên cho ' + CAST(@rows_affected AS NVARCHAR(10)) + N' khách hàng dựa trên điểm hiện tại';
        PRINT @result_message;
        
        COMMIT TRANSACTION;
        
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        PRINT N'Đã xảy ra lỗi - transaction đã được rollback: ' + @ErrorMessage;
        THROW;
    END CATCH
END
GO

-- Thủ tục đồng bộ điểm từ các đơn hàng COMPLETED từ đầu năm đến hiện tại
CREATE PROCEDURE sp_sync_year_to_date_customer_points
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @current_year INT = YEAR(GETDATE());
    DECLARE @rows_affected INT;
    DECLARE @result_message NVARCHAR(500);
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Tạo bảng tạm để lưu kết quả tính toán
        CREATE TABLE #temp_customer_points (
            customer_id INT,
            calculated_points INT
        );
        
        -- Tính tổng điểm từ các đơn hàng hoàn thành của khách hàng từ đầu năm đến hiện tại
        INSERT INTO #temp_customer_points (customer_id, calculated_points)
        SELECT 
            o.customer_id,
            SUM(o.point) AS calculated_points
        FROM [order] o
        WHERE o.customer_id IS NOT NULL
          AND o.status = 'COMPLETED'
          AND o.point IS NOT NULL
          AND YEAR(o.order_time) = @current_year
          AND o.order_time <= GETDATE()
        GROUP BY o.customer_id;
        
        -- Cập nhật điểm cho khách hàng nếu có sự khác biệt
        UPDATE c
        SET current_points = tp.calculated_points,
            updated_at = GETDATE()
        FROM customer c
        INNER JOIN #temp_customer_points tp ON c.customer_id = tp.customer_id
        WHERE c.current_points <> tp.calculated_points;
        
        SET @rows_affected = @@ROWCOUNT;
        SET @result_message = N'Đã đồng bộ điểm cho ' + CAST(@rows_affected AS NVARCHAR(10)) + N' khách hàng từ đơn hàng năm ' + CAST(@current_year AS NVARCHAR(4));
        PRINT @result_message;
        
        -- Xóa bảng tạm
        DROP TABLE #temp_customer_points;
        
        COMMIT TRANSACTION;
        
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        -- Xóa bảng tạm nếu có lỗi
        IF OBJECT_ID('tempdb..#temp_customer_points') IS NOT NULL
            DROP TABLE #temp_customer_points;
        
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        PRINT N'Đã xảy ra lỗi - transaction đã được rollback: ' + @ErrorMessage;
        THROW;
    END CATCH
END
GO

-- =====================================================
-- DAILY MEMBERSHIP MAINTENANCE PROCEDURE
-- =====================================================

-- Thủ tục tổng hợp chạy hàng ngày để kiểm tra membership hết hạn
CREATE PROCEDURE sp_daily_membership_maintenance
AS
BEGIN
    SET NOCOUNT ON;
    
    PRINT N'=== BẮT ĐẦU BẢO TRÌ MEMBERSHIP HÀNG NGÀY ===';
    PRINT N'Thời gian: ' + CONVERT(NVARCHAR(19), GETDATE(), 120);
    
    BEGIN TRY
        -- 1. Đồng bộ điểm khách hàng
        PRINT N'1. Đồng bộ điểm khách hàng...';
        EXEC sp_sync_year_to_date_customer_points;
        
        -- 2. Reset thành viên hết hạn
        PRINT N'2. Reset thành viên hết hạn...';
        EXEC sp_reset_expired_memberships;
        
        -- 3. Tái cấp loại thành viên dựa trên điểm hiện tại
        PRINT N'3. Tái cấp loại thành viên...';
        EXEC sp_recalculate_customer_memberships;
        
        PRINT N'=== HOÀN THÀNH BẢO TRÌ MEMBERSHIP HÀNG NGÀY ===';
        
    END TRY
    BEGIN CATCH
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        PRINT N'LỖI TRONG QUÁ TRÌNH BẢO TRÌ MEMBERSHIP: ' + @ErrorMessage;
        THROW;
    END CATCH
END
GO

USE msdb;
GO

EXEC dbo.sp_add_job
    @job_name = N'Daily Membership Maintenance',
    @description = N'Daily maintenance for membership types and customer points';

EXEC dbo.sp_add_jobstep
    @job_name = N'Daily Membership Maintenance',
    @step_name = N'Run Membership Maintenance',
    @command = N'EXEC MilkTeaShop.dbo.sp_daily_membership_maintenance',
    @database_name = N'MilkTeaShop';

EXEC dbo.sp_add_schedule
    @schedule_name = N'Daily at 1 AM',
    @freq_type = 4, -- Daily
    @freq_interval = 1,
    @active_start_time = 010000; -- 01:00:00

EXEC dbo.sp_attach_schedule
    @job_name = N'Daily Membership Maintenance',
    @schedule_name = N'Daily at 1 AM';

EXEC dbo.sp_add_jobserver
    @job_name = N'Daily Membership Maintenance';


PRINT N'✅ Job Daily Membership Maintenance đã được tạo';
PRINT N'Đã tạo thành công các triggers và procedures cho quản lý membership';
GO 