create trigger milk_tea_shop_prod.after_customer_delete
    after delete
    on milk_tea_shop_prod.customer
    for each row
begin
    BEGIN
    -- kiểm tra tài khoản có đang liên kết với khách hàng không
    SET @v_has_relations = (
        SELECT EXISTS(
            SELECT 1 FROM account WHERE account_id = OLD.account_id
        )
    );

    IF @v_has_relations THEN
        DELETE FROM account WHERE account_id = OLD.account_id;
    END IF;

END
    end;

