create definer = root@localhost trigger milk_tea_shop_prod.before_store_update
    before update
    on milk_tea_shop_prod.Store
    for each row
begin
    -- missing source code
end;

