create trigger milk_tea_shop_prod.before_payment_method_delete
    before delete
    on milk_tea_shop_prod.payment_method
    for each row
begin
    BEGIN
END
    end;

