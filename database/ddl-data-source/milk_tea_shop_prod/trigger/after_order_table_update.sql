create trigger milk_tea_shop_prod.after_order_table_update
    after update
    on milk_tea_shop_prod.order_table
    for each row
begin
    BEGIN
END
    end;

