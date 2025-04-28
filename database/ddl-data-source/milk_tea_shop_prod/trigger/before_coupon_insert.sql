create trigger milk_tea_shop_prod.before_coupon_insert
    before insert
    on milk_tea_shop_prod.coupon
    for each row
begin
    BEGIN
    -- Kiểm tra mã giảm giá
    IF NEW.coupon REGEXP '^[a-zA-Z0-9]{3,15}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá phải có độ dài từ 3 đến 15 ký tự và chỉ được chứa chữ cái và số';
    END IF;
END
    end;

