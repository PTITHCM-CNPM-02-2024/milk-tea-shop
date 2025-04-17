create trigger milk_tea_shop_prod.after_order_table_insert
    after insert
    on milk_tea_shop_prod.order_table
    for each row
begin
    BEGIN
    -- Cập nhật trạng thái bàn hoặc thực hiện các hành động khác sau khi đặt bàn
    -- (Có thể để trống nếu không cần xử lý gì)
END
    end;

