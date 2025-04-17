create trigger milk_tea_shop_prod.protect_default_category_delete
    before delete
    on milk_tea_shop_prod.category
    for each row
begin
    BEGIN
    IF OLD.name IN ('TOPPING', 'TOPPING BÁN LẺ') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa danh mục mặc định';
    END IF;
END
    end;

