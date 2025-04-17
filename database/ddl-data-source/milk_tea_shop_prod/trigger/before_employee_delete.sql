create trigger milk_tea_shop_prod.before_employee_delete
    before delete
    on milk_tea_shop_prod.employee
    for each row
begin
    BEGIN
    -- Kiểm tra xem nhân viên có đang có đơn hàng đang chờ xử lý không
    SET @has_relations = (
        SELECT EXISTS(
            SELECT 1 FROM `order` WHERE employee_id = OLD.employee_id AND status = 'PROCESSING'
        )
    );

    IF @has_relations THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa nhân viên đang có đơn hàng đang chờ xử lý';
    END IF;
END
    end;

