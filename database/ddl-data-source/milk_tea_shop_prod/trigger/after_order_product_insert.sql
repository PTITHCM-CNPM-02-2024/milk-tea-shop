create trigger milk_tea_shop_prod.after_order_product_insert
    after insert
    on milk_tea_shop_prod.order_product
    for each row
begin
    BEGIN

END
    end;

