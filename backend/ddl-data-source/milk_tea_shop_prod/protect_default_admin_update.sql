create definer = root@localhost trigger milk_tea_shop_prod.protect_default_admin_update
    before update
    on milk_tea_shop_prod.account
    for each row
begin
    -- missing source code
end;

