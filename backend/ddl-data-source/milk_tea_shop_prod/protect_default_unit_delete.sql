create definer = root@localhost trigger milk_tea_shop_prod.protect_default_unit_delete
    before delete
    on milk_tea_shop_prod.unit_of_measure
    for each row
begin
    -- missing source code
end;

