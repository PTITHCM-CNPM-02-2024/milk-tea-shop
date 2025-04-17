create trigger milk_tea_shop_prod.before_membership_update_check_expiration
    before update
    on milk_tea_shop_prod.membership_type
    for each row
begin
    BEGIN
    -- Nếu cập nhật valid_until, đảm bảo phải là ngày trong tương lai
    IF NEW.valid_until IS NOT NULL AND NEW.valid_until <= CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời hạn membership phải là ngày trong tương lai';
    END IF;
END
    end;

