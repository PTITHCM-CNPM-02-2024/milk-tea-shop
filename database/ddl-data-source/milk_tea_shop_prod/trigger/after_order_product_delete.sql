create trigger milk_tea_shop_prod.after_order_product_delete
    after delete
    on milk_tea_shop_prod.order_product
    for each row
begin
    BEGIN
    -- Cập nhật tổng tiền đơn hàng
END
    end;

