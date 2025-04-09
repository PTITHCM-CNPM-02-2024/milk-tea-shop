create definer = root@localhost trigger milk_tea_shop_prod.before_store_delete
    before delete
    on milk_tea_shop_prod.Store
    for each row
begin
    -- missing source code
end;

