create definer = root@localhost trigger milk_tea_shop_prod.protect_default_payment_method_update
    before update
    on milk_tea_shop_prod.payment_method
    for each row
begin
    -- missing source code
end;

