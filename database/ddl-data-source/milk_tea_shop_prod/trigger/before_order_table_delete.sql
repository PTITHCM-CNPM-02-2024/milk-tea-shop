create trigger milk_tea_shop_prod.before_order_table_delete
    before delete
    on milk_tea_shop_prod.order_table
    for each row
begin
    BEGIN
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');

    -- Kiểm tra xem đơn hàng có đang ở trạng thái PROCESSING không
    SELECT status INTO order_status FROM `order` WHERE order_id = OLD.order_id;

    IF order_status = 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa đặt bàn cho đơn hàng đang xử lý';
    END IF;
END
    end;

