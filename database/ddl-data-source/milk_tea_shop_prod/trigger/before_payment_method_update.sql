create trigger milk_tea_shop_prod.before_payment_method_update
    before update
    on milk_tea_shop_prod.payment_method
    for each row
begin
    BEGIN
    -- Kiểm tra tên phương thức thanh toán
    IF LENGTH(TRIM(NEW.payment_name)) < 3 OR LENGTH(TRIM(NEW.payment_name)) > 50 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên phương thức thanh toán phải từ 3 đến 50 ký tự';
    END IF;
END
    end;

