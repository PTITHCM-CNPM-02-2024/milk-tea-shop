create trigger milk_tea_shop_prod.before_store_delete
    before delete
    on milk_tea_shop_prod.store
    for each row
begin
    BEGIN
    DECLARE store_count INT;

    -- Đếm số lượng bản ghi hiện có
    SELECT COUNT(*) INTO store_count FROM store;

    -- Nếu chỉ có một bản ghi, từ chối xóa
    IF store_count = 1 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa thông tin cửa hàng duy nhất. Cửa hàng phải luôn có thông tin.';
    END IF;
END
    end;

