create definer = root@localhost trigger milk_tea_shop_prod.before_store_insert
    before insert
    on milk_tea_shop_prod.store
    for each row
begin
    -- missing source code
end;

