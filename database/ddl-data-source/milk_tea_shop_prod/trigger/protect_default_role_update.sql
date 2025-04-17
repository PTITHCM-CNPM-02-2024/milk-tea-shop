create trigger milk_tea_shop_prod.protect_default_role_update
    before update
    on milk_tea_shop_prod.role
    for each row
begin
    BEGIN
    IF OLD.name IN ('MANAGER', 'STAFF', 'CUSTOMER', 'GUEST') THEN
        IF NEW.name != OLD.name THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể thay đổi tên vai trò mặc định';
        END IF;
    END IF;
END
    end;

