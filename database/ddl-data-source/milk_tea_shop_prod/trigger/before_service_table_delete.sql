create trigger milk_tea_shop_prod.before_service_table_delete
    before delete
    on milk_tea_shop_prod.service_table
    for each row
begin
    BEGIN
    SET @has_active_order = (
        SELECT EXISTS(
            SELECT 1
            FROM order_table ot
                     JOIN `order` o ON ot.order_id = o.order_id
            WHERE ot.table_id = OLD.table_id
              AND (o.status = 'PROCESSING' or ot.check_out IS NULL)
        )
    );

    IF @has_active_order THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa bàn đang được sử dụng';
    END IF;
END
    end;

