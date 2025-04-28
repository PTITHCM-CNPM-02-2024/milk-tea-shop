create procedure milk_tea_shop_prod.sp_lock_unlock_account(IN p_account_id int unsigned, IN p_is_locked tinyint(1))
BEGIN
    DECLARE current_lock_status TINYINT(1);

    -- Lấy trạng thái khóa hiện tại
    SELECT is_locked
    INTO current_lock_status
    FROM account
    WHERE account_id = p_account_id;

    -- Chỉ thực hiện nếu trạng thái thay đổi
    IF current_lock_status != p_is_locked THEN
        -- Bắt đầu transaction
        START TRANSACTION;

        -- Cập nhật trạng thái khóa
        UPDATE account
        SET is_locked  = p_is_locked,
            updated_at = CURRENT_TIMESTAMP
        WHERE account_id = p_account_id;

        -- Xử lý quyền dựa trên trạng thái khóa mới
        IF p_is_locked = 1 THEN
            -- Khóa tài khoản: thu hồi quyền
            CALL sp_revoke_permissions(p_account_id);
            SELECT 'Tài khoản đã bị khóa và quyền đã bị thu hồi' AS message;
        ELSE
            -- Mở khóa tài khoản: cấp lại quyền SELECT và EXECUTE giới hạn (sử dụng sp_grant_permissions_by_role đã sửa)
            CALL sp_grant_permissions_by_role(p_account_id);
            SELECT 'Tài khoản đã được mở khóa và quyền đã được cấp lại' AS message;
        END IF;

        -- Hoàn tất transaction
        COMMIT;
    ELSE
        SELECT 'Trạng thái khóa không thay đổi' AS message;
    END IF;
END;

