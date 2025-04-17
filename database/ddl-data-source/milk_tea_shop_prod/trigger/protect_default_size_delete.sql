create trigger milk_tea_shop_prod.protect_default_size_delete
    before delete
    on milk_tea_shop_prod.product_size
    for each row
begin
    BEGIN
    IF OLD.name IN ('S', 'M', 'L', 'NA') AND OLD.unit_id IN (SELECT unit_id FROM unit_of_measure WHERE symbol IN ('mL', 'cái')) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi kích thước mặc định';
    END IF;
END
    end;

