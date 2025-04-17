create trigger milk_tea_shop_prod.before_order_discount_delete
    before delete
    on milk_tea_shop_prod.order_discount
    for each row
begin
    BEGIN

END
    end;

