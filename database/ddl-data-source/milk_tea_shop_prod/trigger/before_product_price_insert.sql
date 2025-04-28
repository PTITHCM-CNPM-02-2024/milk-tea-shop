create trigger milk_tea_shop_prod.before_product_price_insert
    before insert
    on milk_tea_shop_prod.product_price
    for each row
begin
    BEGIN
    -- Kiểm tra giá sản phẩm
    IF NEW.price < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá sản phẩm phải lớn hơn 1000';
    END IF;
END
    end;

