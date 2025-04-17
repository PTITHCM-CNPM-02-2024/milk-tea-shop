create trigger milk_tea_shop_prod.protect_default_category_update
    before update
    on milk_tea_shop_prod.category
    for each row
begin
    BEGIN
    IF OLD.name IN ('TOPPING', 'TOPPING BÁN LẺ') THEN
        IF NEW.name != OLD.name THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể thay đổi tên danh mục mặc định';
        END IF;
    END IF;
END
    end;

