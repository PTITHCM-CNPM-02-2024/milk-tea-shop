create trigger milk_tea_shop_prod.before_category_update
    before update
    on milk_tea_shop_prod.category
    for each row
begin
    BEGIN
    -- Kiểm tra tên danh mục
    IF LENGTH(TRIM(NEW.name)) = 0  OR LENGTH(TRIM(NEW.name)) > 100 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên danh mục không được để trống và có độ dài tối đa 100 ký tự';
    END IF;


END
    end;

