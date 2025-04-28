create trigger milk_tea_shop_prod.after_order_table_delete
    after delete
    on milk_tea_shop_prod.order_table
    for each row
begin
    BEGIN
    -- Thực hiện các hành động sau khi xóa đặt bàn
    -- (Có thể để trống nếu không cần xử lý gì)
END
    end;

