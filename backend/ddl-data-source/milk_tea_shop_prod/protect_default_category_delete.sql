create definer = root@localhost trigger milk_tea_shop_prod.protect_default_category_delete
    before delete
    on milk_tea_shop_prod.Category
    for each row
begin
    -- missing source code
end;

