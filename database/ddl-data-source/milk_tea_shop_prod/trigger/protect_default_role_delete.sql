create trigger milk_tea_shop_prod.protect_default_role_delete
    before delete
    on milk_tea_shop_prod.role
    for each row
begin
    BEGIN
    IF OLD.name IN ('MANAGER', 'STAFF', 'CUSTOMER', 'GUEST') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa vai trò mặc định';
    END IF;
END
    end;

