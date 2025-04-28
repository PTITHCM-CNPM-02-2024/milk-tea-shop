create trigger milk_tea_shop_prod.before_role_delete
    before delete
    on milk_tea_shop_prod.role
    for each row
begin
    BEGIN
END
    end;

