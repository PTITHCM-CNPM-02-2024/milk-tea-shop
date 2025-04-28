create trigger milk_tea_shop_prod.before_order_product_update
    before update
    on milk_tea_shop_prod.order_product
    for each row
begin
    BEGIN

    -- Kiểm tra xem order ở trạng thái khác PROCESSING thì không được cập nhật
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;

    IF NEW.order_id IS NOT NULL AND NEW.order_id <> OLD.order_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể cập nhật số lượng sản phẩm cho đơn hàng khác';
    END IF;

    -- Kiểm tra xem order ở trạng thái khác PROCESSING thì không được cập nhật
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không được cập nhật số lượng sản phẩm cho đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra số lượng phải lớn hơn 0
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lượng sản phẩm phải lớn hơn 0';
    END IF;
END
    end;

