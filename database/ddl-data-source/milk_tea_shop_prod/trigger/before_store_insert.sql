create trigger milk_tea_shop_prod.before_store_insert
    before insert
    on milk_tea_shop_prod.store
    for each row
begin
    BEGIN
    DECLARE store_count INT;

    -- Đếm số lượng bản ghi hiện có
    SELECT COUNT(*) INTO store_count FROM store;

    -- Nếu đã có bản ghi, từ chối thêm mới
    IF store_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể tạo thêm thông tin cửa hàng mới. Chỉ được phép có một bản ghi thông tin cửa hàng.';
    END IF;
END
    end;

