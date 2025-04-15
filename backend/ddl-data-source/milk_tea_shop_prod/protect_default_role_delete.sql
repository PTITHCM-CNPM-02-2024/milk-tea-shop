create definer = root@localhost trigger milk_tea_shop_prod.protect_default_role_delete
    before delete
    on milk_tea_shop_prod.role
    for each row
begin
    -- missing source code
end;

