create trigger milk_tea_shop_prod.before_unit_of_measure_delete
    before delete
    on milk_tea_shop_prod.unit_of_measure
    for each row
begin
    BEGIN
END
    end;

