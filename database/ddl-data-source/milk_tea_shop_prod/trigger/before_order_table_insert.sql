create trigger milk_tea_shop_prod.before_order_table_insert
    before insert
    on milk_tea_shop_prod.order_table
    for each row
begin
    BEGIN
    -- đơn hàng phải ở trạng thái processing
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    DECLARE is_active BOOLEAN;
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể đặt bàn cho đơn hàng không ở trạng thái processing';
    END IF;
    -- không được đặt bàn cho bàn không hoạt động
    SELECT is_active INTO is_active FROM `service_table` WHERE table_id = NEW.table_id;
    IF is_active = FALSE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể đặt bàn cho bàn không hoạt động';
    END IF;

    IF NEW.check_out IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể đặt bàn cho đơn hàng đã checkout';
    END IF;

    -- Kiểm tra thời gian vào/ra bàn
    IF NEW.check_out IS NOT NULL AND NEW.check_out < NEW.check_in THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời gian rời bàn phải sau thời gian vào bàn';
    END IF;
END
    end;

