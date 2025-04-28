create procedure milk_tea_shop_prod.sp_delete_account(IN p_account_id int unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies:
    -- customer.account_id ON DELETE SET NULL
    -- employee.account_id ON DELETE RESTRICT
    -- manager.account_id ON DELETE RESTRICT
    -- Deletion might fail if used by employee or manager.
    -- Setting customer.account_id to NULL is handled automatically.

    SET @v_has_employee = (
        SELECT EXISTS(
            SELECT 1 FROM employee WHERE account_id = p_account_id
        )
    );

    SET @v_has_manager = (
        SELECT EXISTS(
            SELECT 1 FROM manager WHERE account_id = p_account_id
        )
    );

    SET @v_has_customer = (
        SELECT EXISTS(
            SELECT 1 FROM customer WHERE account_id = p_account_id
        )
    );

    IF @v_has_employee THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không thể xóa tài khoản đang được sử dụng bởi nhân viên';
    ELSEIF @v_has_manager THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không thể xóa tài khoản đang được sử dụng bởi quản lý';
    ELSEIF @v_has_customer THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không thể xóa tài khoản đang được sử dụng bởi khách hàng';
    ELSE
        DELETE FROM account WHERE account_id = p_account_id;
        SET p_row_count = ROW_COUNT();
    END IF;
END;

