create procedure milk_tea_shop_prod.sp_create_staff_account(IN p_username varchar(50), IN p_password varchar(255),
                                                            IN p_position varchar(50), IN p_last_name varchar(70),
                                                            IN p_first_name varchar(70),
                                                            IN p_gender enum ('MALE', 'FEMALE', 'OTHER'),
                                                            IN p_phone varchar(15), IN p_email varchar(100),
                                                            OUT p_new_account_id int unsigned,
                                                            OUT p_status_message varchar(255))
create_staff_proc:
BEGIN
    DECLARE staff_role_id TINYINT UNSIGNED;
    DECLARE v_account_id INT UNSIGNED;
    DECLARE v_employee_id INT UNSIGNED;
    -- Use declared local variable for OUT param

    -- Error handling
    DECLARE exit handler for sqlexception
        BEGIN
        END;

    -- Lấy role_id của STAFF
    SELECT role_id INTO staff_role_id FROM role WHERE name = 'STAFF';
    IF staff_role_id IS NULL THEN
        SET p_status_message = 'Không tìm thấy vai trò STAFF.';
        LEAVE create_staff_proc; -- Exit the procedure using label
    END IF;

    -- Bắt đầu transaction
    START TRANSACTION;

    -- 1. Tạo tài khoản sử dụng sp_insert_account
    CALL sp_insert_account(
            staff_role_id,
            p_username,
            p_password,
            0, -- is_active = TRUE (Staff active by default)
            0, -- is_locked = FALSE
            v_account_id
         );

    IF v_account_id IS NULL THEN
        ROLLBACK;
        SET p_status_message = 'Không thể tạo tài khoản.';
        LEAVE create_staff_proc;
    END IF;

    -- 2. Cấp quyền SELECT cho tài khoản mới (sử dụng sp_grant_permissions_by_role đã sửa)
    CALL sp_grant_permissions_by_role(v_account_id);
    -- Lưu ý: Kiểm tra kết quả của sp_grant_permissions_by_role nếu cần

    -- 3. Tạo thông tin Employee sử dụng sp_insert_employee
    CALL sp_insert_employee(
            v_account_id,
            p_position,
            p_last_name,
            p_first_name,
            p_gender,
            p_phone,
            p_email,
            v_employee_id -- Use declared variable for OUT param
         );

    -- Kiểm tra lỗi sau khi gọi sp_insert_employee
    IF v_employee_id IS NULL OR v_employee_id <= 0 THEN
        ROLLBACK;
        SET p_status_message = 'Không thể tạo thông tin employee.';
        LEAVE create_staff_proc;
    END IF;

    -- Hoàn tất transaction
    COMMIT;

    -- Trả về kết quả
    SET p_new_account_id = v_account_id;
    SET p_status_message = 'Đã tạo tài khoản STAFF và cấp quyền thành công.'; -- Updated message slightly

END create_staff_proc;

