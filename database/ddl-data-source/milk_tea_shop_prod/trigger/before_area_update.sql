create trigger milk_tea_shop_prod.before_area_update
    before update
    on milk_tea_shop_prod.area
    for each row
begin
    BEGIN
    DECLARE current_tables INT;
    DECLARE order_count INT;
    -- Kiểm tra tên khu vực
    IF LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực không được để trống';
    END IF;

    IF  NEW.name REGEXP '^[a-zA-Z0-9_-]{3}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài 3 ký tự';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn 0, nhỏ hơn 100
    IF NEW.max_tables IS NOT NULL AND (NEW.max_tables <= 0 OR NEW.max_tables >= 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn 0 và nhỏ hơn 100';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn hoặc bằng số bàn hiện có
    IF NEW.max_tables IS NOT NULL THEN
        SELECT COUNT(*) INTO current_tables FROM service_table WHERE area_id = NEW.area_id;

        IF NEW.max_tables < current_tables THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn hoặc bằng số bàn hiện có';
        END IF;
    END IF;
END
    end;

