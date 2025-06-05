-- File: src/main/resources/db/migration/dev/V1.0.5__scheduled_events.sql
-- Chứa các Stored Procedures và Events được lên lịch.

-- Procedure để vô hiệu hóa các discount đã hết hạn
SET NAMES 'utf8mb4';
SET CHARACTER SET utf8mb4;
DELIMITER //

CREATE PROCEDURE sp_deactivate_expired_discounts()
BEGIN
    -- Ghi log bắt đầu (Tùy chọn)
    -- INSERT INTO event_log (event_name, start_time) VALUES ('sp_deactivate_expired_discounts', NOW());

    UPDATE discount
    SET is_active = 0,
        updated_at = CURRENT_TIMESTAMP
    WHERE is_active = 1 -- Chỉ cập nhật những cái đang active
      AND valid_until < CURDATE(); -- Sử dụng '<' để chỉ vô hiệu hóa SAU ngày hết hạn.
                                -- Nếu muốn vô hiệu hóa VÀO ngày hết hạn, dùng '<='

    -- Ghi log kết thúc (Tùy chọn)
    -- INSERT INTO event_log (event_name, end_time, status) VALUES ('sp_deactivate_expired_discounts', NOW(), 'Completed');

    -- Error handling có thể được thêm vào nếu cần
END //

DELIMITER //

CREATE EVENT event_deactivate_expired_discounts
ON SCHEDULE EVERY 1 DAY
STARTS TIMESTAMP(CURRENT_DATE, '01:00:00') -- Bắt đầu vào 1 giờ sáng ngày hiện tại
DO
BEGIN
    -- Gọi stored procedure
    CALL sp_deactivate_expired_discounts();
END //-- Kết thúc DO block

DELIMITER ;

-- Các events và procedures khác (ví dụ: hủy order tự động) có thể được thêm ở đây. 