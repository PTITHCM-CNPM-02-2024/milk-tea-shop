create definer = root@localhost trigger milk_tea_shop_prod.protect_default_admin_delete
    before delete
    on milk_tea_shop_prod.Account
    for each row
begin
    -- missing source code
end;

