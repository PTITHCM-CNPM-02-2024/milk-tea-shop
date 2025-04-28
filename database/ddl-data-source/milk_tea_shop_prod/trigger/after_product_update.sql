create trigger milk_tea_shop_prod.after_product_update
    after update
    on milk_tea_shop_prod.product
    for each row
begin
    BEGIN
    -- Ghi log hoặc thực hiện các hành động sau khi cập nhật sản phẩm
    -- (Có thể để trống nếu không cần xử lý gì)
END
    end;

