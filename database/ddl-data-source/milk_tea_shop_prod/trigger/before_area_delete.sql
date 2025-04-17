create trigger milk_tea_shop_prod.before_area_delete
    before delete
    on milk_tea_shop_prod.area
    for each row
begin
    BEGIN
END
    end;

