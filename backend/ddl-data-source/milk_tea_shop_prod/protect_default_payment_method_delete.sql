create definer = root@localhost trigger milk_tea_shop_prod.protect_default_payment_method_delete
    before delete
    on milk_tea_shop_prod.PaymentMethod
    for each row
begin
    -- missing source code
end;

