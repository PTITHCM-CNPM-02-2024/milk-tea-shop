create trigger milk_tea_shop_prod.before_unit_of_measure_update
    before update
    on milk_tea_shop_prod.unit_of_measure
    for each row
begin
    BEGIN
    -- Kiểm tra dữ liệu không rỗng nhưng định dạng không đúng
    IF LENGTH(TRIM(NEW.name)) < 1 OR LENGTH(TRIM(NEW.name)) > 30 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đơn vị phải có độ dài từ 1 đến 30 ký tự';
    END IF;

    IF LENGTH(TRIM(NEW.symbol)) < 1 OR LENGTH(TRIM(NEW.symbol)) > 5 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ký hiệu đơn vị phải có độ dài từ 1 đến 5 ký tự';
    END IF;
END
    end;

