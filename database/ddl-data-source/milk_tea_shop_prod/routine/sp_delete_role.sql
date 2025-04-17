create procedure milk_tea_shop_prod.sp_delete_role(IN p_role_id tinyint unsigned, OUT p_row_count int)
BEGIN
    -- Consider dependencies (account.role_id ON DELETE RESTRICT)
    -- Deletion might fail if accounts use this role.
    SET @v_has_account = (
        SELECT EXISTS(
            SELECT 1 FROM account WHERE role_id = p_role_id
        )
    );

    IF @v_has_account THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không thể xóa vai trò đang được sử dụng bởi tài khoản';
    ELSE
        DELETE FROM role WHERE role_id = p_role_id;
        SET p_row_count = ROW_COUNT();
    END IF;
END;

