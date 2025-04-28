create trigger milk_tea_shop_prod.before_role_insert
    before insert
    on milk_tea_shop_prod.role
    for each row
begin
    BEGIN
    IF NEW.name REGEXP '^[a-zA-Z0-9_]{3,20}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên vai trò phải từ 3 đến 20 ký tự và không được chứa ký tự đặc biệt';
    END IF;
END
    end;

