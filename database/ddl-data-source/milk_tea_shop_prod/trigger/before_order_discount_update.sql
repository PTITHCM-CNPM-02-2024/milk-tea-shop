create trigger milk_tea_shop_prod.before_order_discount_update
    before update
    on milk_tea_shop_prod.order_discount
    for each row
begin
    BEGIN
    -- Lấy trạng thái của đơn hàng
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');

    IF NEW.order_id IS NOT NULL AND NEW.order_id <> OLD.order_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể cập nhật giảm giá cho đơn hàng khác';
    END IF;

    SELECT status INTO order_status
    FROM `order`
    WHERE order_id = NEW.order_id;

    -- Kiểm tra xem order ở trạng thái khác PROCESSING thì không được cập nhật
    IF order_status = 'COMPLETED' OR order_status = 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể cập nhật giảm giá cho đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra discount_amount không nhỏ hơn 1000
    IF NEW.discount_amount <= 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền giảm giá không được nhỏ hơn 1000 VNĐ';
    END IF;
END
    end;

