create trigger milk_tea_shop_prod.before_product_size_insert
    before insert
    on milk_tea_shop_prod.product_size
    for each row
begin
    BEGIN
    -- Kiểm tra tên kích thước
    IF LENGTH(TRIM(NEW.name)) = 0 OR LENGTH(TRIM(NEW.name)) > 5 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên kích thước phải có độ dài từ 1 đến 5 ký tự';
    END IF;

    -- Kiểm tra số lượng
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lượng phải lớn hơn 0';
    END IF;
END
    end;

