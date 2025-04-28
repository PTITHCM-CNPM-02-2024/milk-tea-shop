create trigger milk_tea_shop_prod.before_area_insert
    before insert
    on milk_tea_shop_prod.area
    for each row
begin
    BEGIN
    -- Kiểm tra tên khu vực
    IF NEW.name REGEXP '^[a-zA-Z0-9_-]{3}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài 3 ký tự';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn 0 nếu được chỉ định
    IF NEW.max_tables IS NOT NULL AND NEW.max_tables <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn 0';
    END IF;
END
    end;

