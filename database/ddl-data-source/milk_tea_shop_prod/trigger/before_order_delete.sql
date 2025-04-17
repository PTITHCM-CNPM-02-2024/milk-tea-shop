create trigger milk_tea_shop_prod.before_order_delete
    before delete
    on milk_tea_shop_prod.`order`
    for each row
begin
    BEGIN
END
    end;

