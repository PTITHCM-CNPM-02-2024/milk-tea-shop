create trigger milk_tea_shop_prod.before_store_update
    before update
    on milk_tea_shop_prod.store
    for each row
begin
    BEGIN
    -- Đảm bảo không thay đổi ID
    IF NEW.store_id != OLD.store_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi ID của cửa hàng. Chỉ được phép cập nhật thông tin.';
    END IF;
END
    end;

