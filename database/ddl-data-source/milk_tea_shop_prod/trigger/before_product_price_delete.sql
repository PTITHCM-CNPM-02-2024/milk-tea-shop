create trigger milk_tea_shop_prod.before_product_price_delete
    before delete
    on milk_tea_shop_prod.product_price
    for each row
begin
    BEGIN
    SET @order_product_exists = (
        SELECT EXISTS(
            SELECT 1 FROM order_product op INNER JOIN `order` o ON op.order_id = o.order_id
                                       INNER JOIN order_table ot ON o.order_id = ot.order_id
            WHERE product_price_id = OLD.product_price_id AND (o.status = 'PROCESSING' or ot.check_out IS NULL)
        )
    );

    -- Kiểm tra xem giá sản phẩm có được sử dụng trong đơn hàng không

    IF @order_product_exists THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa giá sản phẩm đang được sử dụng trong đơn hàng';
    END IF;
END
    end;

