create trigger milk_tea_shop_prod.before_customer_delete
    before delete
    on milk_tea_shop_prod.customer
    for each row
begin
    BEGIN

    SET @v_has_processing_order = (
        SELECT EXISTS(
            SELECT 1 FROM `order` JOIN order_table ON `order`.order_id = order_table.order_id
            WHERE `order`.customer_id = OLD.customer_id AND (`order`.status = 'PROCESSING' or order_table.check_out IS NULL)
        )
    );

    IF @v_has_processing_order THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa khách hàng đã có đơn hàng đang chờ xử lý';
    END IF;
END
    end;

