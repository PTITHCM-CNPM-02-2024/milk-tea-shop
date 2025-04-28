create trigger milk_tea_shop_prod.before_category_delete
    before delete
    on milk_tea_shop_prod.category
    for each row
begin
    BEGIN
END
    end;

