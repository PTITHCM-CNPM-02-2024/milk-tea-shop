create definer = root@localhost trigger milk_tea_shop_prod.protect_default_size_update
    before update
    on milk_tea_shop_prod.product_size
    for each row
begin
    -- missing source code
end;

