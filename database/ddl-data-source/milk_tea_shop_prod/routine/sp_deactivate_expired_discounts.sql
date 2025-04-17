create procedure milk_tea_shop_prod.sp_deactivate_expired_discounts()
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
END;

