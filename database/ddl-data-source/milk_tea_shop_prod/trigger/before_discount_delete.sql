create trigger milk_tea_shop_prod.before_discount_delete
    before delete
    on milk_tea_shop_prod.discount
    for each row
begin
    BEGIN

    -- Kiểm tra xem discount có đang được sử dụng trong order có status = PROCESSING không, cần join với table order_discount
    SET @v_has_relations = (
        SELECT EXISTS(
            SELECT 1 FROM `order`
                              JOIN order_discount ON `order`.order_id = order_discount.order_id
                              JOIN order_table ot ON `order`.order_id = ot.order_id
            WHERE order_discount.discount_id = OLD.discount_id AND (`order`.status = 'PROCESSING' or ot.check_out IS NULL)
        )
    );

    IF @v_has_relations THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa chương trình giảm giá đang được sử dụng trong đơn hàng đang chờ xử lý';
    END IF;


END
    end;

