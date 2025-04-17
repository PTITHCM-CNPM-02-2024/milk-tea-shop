create trigger milk_tea_shop_prod.before_product_delete
    before delete
    on milk_tea_shop_prod.product
    for each row
begin
    BEGIN

END
    end;

