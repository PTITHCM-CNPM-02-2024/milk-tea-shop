create trigger milk_tea_shop_prod.protect_default_membership_delete
    before delete
    on milk_tea_shop_prod.membership_type
    for each row
begin
    BEGIN
    IF OLD.type IN ('NEWMEM', 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa loại thành viên mặc định';
    END IF;
END
    end;

