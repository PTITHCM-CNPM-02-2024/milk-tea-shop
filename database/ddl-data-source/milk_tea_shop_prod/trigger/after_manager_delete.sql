create trigger milk_tea_shop_prod.after_manager_delete
    after delete
    on milk_tea_shop_prod.manager
    for each row
begin
    BEGIN
    -- kiểm tra tài khoản có đang liên kết với quản lý không
    SET @has_relations = (
        SELECT EXISTS(
            SELECT 1 FROM account WHERE account_id = OLD.account_id
        )
    );

    IF @has_relations THEN
        DELETE FROM account WHERE account_id = OLD.account_id;
    END IF;
END
    end;

