create trigger milk_tea_shop_prod.before_order_discount_insert
    before insert
    on milk_tea_shop_prod.order_discount
    for each row
begin
    BEGIN
    -- kiểm tra order phải ở trạng thái processing
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thêm giảm giá cho đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra discount_amount không âm
    IF NEW.discount_amount <= 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền giảm giá không được nhỏ hơn 1000 VNĐ';
    END IF;
END
    end;

