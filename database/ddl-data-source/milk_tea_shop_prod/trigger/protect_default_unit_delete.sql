create trigger milk_tea_shop_prod.protect_default_unit_delete
    before delete
    on milk_tea_shop_prod.unit_of_measure
    for each row
begin
    BEGIN
    IF OLD.symbol IN ('mL', 'cái') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa đơn vị đo lường mặc định';
    END IF;
END
    end;

