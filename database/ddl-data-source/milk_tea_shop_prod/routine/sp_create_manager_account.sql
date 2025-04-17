create procedure milk_tea_shop_prod.sp_create_manager_account(IN p_username varchar(50), IN p_password varchar(255),
                                                              IN p_last_name varchar(70), IN p_first_name varchar(70),
                                                              IN p_gender enum ('MALE', 'FEMALE', 'OTHER'),
                                                              IN p_phone varchar(15), IN p_email varchar(100),
                                                              OUT p_new_account_id int unsigned,
                                                              OUT p_status_message varchar(255))
create_manager_proc:
BEGIN
    DECLARE manager_role_id TINYINT UNSIGNED;
    DECLARE v_account_id INT UNSIGNED;
    DECLARE v_manager_id INT UNSIGNED;
    -- Use declared local variable for OUT param

    -- Error handling
    DECLARE exit handler for sqlexception
        BEGIN
            ROLLBACK;
            SET p_new_account_id = NULL;
            SET p_status_message = 'Lỗi SQL xảy ra, giao dịch đã được hủy bỏ.';
        END;

    -- Lấy role_id của MANAGER
    SELECT role_id INTO manager_role_id FROM role WHERE name = 'MANAGER';
    IF manager_role_id IS NULL THEN
        SET p_status_message = 'Không tìm thấy vai trò MANAGER.';
        LEAVE create_manager_proc; -- Exit the procedure using label
    END IF;

    -- Bắt đầu transaction
    START TRANSACTION;

    -- 1. Tạo tài khoản sử dụng sp_insert_account
    CALL sp_insert_account(
            manager_role_id,
            p_username,
            p_password, -- Truyền mật khẩu (đã hash hoặc chưa tùy logic ứng dụng)
            0, -- is_active = FALSE (as per user change)
            0, -- is_locked = FALSE
            v_account_id
         );

    IF v_account_id IS NULL THEN
        ROLLBACK;
        SET p_status_message = 'Không thể tạo tài khoản.';
        LEAVE create_manager_proc;
    END IF;

    -- 2. Cấp quyền SELECT cho tài khoản mới (sử dụng sp_grant_permissions_by_role đã sửa)
    CALL sp_grant_permissions_by_role(v_account_id);
    -- Lưu ý: Kiểm tra kết quả của sp_grant_permissions_by_role nếu cần

    -- 3. Tạo thông tin Manager sử dụng sp_insert_manager
    CALL sp_insert_manager(
            v_account_id,
            p_last_name,
            p_first_name,
            p_gender,
            p_phone,
            p_email,
            v_manager_id -- Use declared variable for OUT param
         );

    -- Kiểm tra lỗi sau khi gọi sp_insert_manager
    IF v_manager_id IS NULL OR v_manager_id <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể tạo thông tin manager.';
        ROLLBACK;
        SET p_status_message = 'Không thể tạo thông tin manager.';
        LEAVE create_manager_proc;
    END IF;

    -- Hoàn tất transaction
    COMMIT;

    -- Trả về kết quả
    SET p_new_account_id = v_account_id;
    SET p_status_message = 'Đã tạo tài khoản MANAGER và cấp quyền thành công.'; -- Updated message slightly

END create_manager_proc;

