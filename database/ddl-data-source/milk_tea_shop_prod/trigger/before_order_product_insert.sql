create trigger milk_tea_shop_prod.before_order_product_insert
    before insert
    on milk_tea_shop_prod.order_product
    for each row
begin
    BEGIN

    -- đơn hàng phải ở trạng thái processing
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thêm sản phẩm vào đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra số lượng phải lớn hơn 0
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lượng sản phẩm phải lớn hơn 0';
    END IF;
END
    end;

