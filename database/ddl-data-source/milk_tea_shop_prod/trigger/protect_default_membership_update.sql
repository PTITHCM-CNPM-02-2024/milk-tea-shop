create trigger milk_tea_shop_prod.protect_default_membership_update
    before update
    on milk_tea_shop_prod.membership_type
    for each row
begin
    BEGIN
    IF OLD.type IN ('NEWMEM', 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM') THEN
        IF NEW.type != OLD.type THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể thay đổi tên loại thành viên mặc định';
        END IF;
    END IF;
END
    end;

