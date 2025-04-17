create trigger milk_tea_shop_prod.protect_default_admin_update
    before update
    on milk_tea_shop_prod.account
    for each row
begin
    BEGIN
    DECLARE is_default_admin BOOLEAN;

    SELECT EXISTS(SELECT 1
                  FROM manager m
                           JOIN account a ON m.account_id = a.account_id
                  WHERE a.account_id = OLD.account_id
                    AND a.username = 'admin'
                    AND m.email = 'admin@milkteashop.com')
    INTO is_default_admin;

    IF is_default_admin AND NEW.username != 'admin' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi username của tài khoản admin mặc định';
    END IF;
END
    end;

