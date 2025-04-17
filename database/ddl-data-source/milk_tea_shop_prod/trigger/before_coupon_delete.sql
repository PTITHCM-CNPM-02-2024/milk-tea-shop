create trigger milk_tea_shop_prod.before_coupon_delete
    before delete
    on milk_tea_shop_prod.coupon
    for each row
begin
    BEGIN
    -- 1. kiểm tra xem coupon có đang được sử dụng trong discount không, trả về id của discount đang sử dụng coupon đó
    SET @v_has_discount = (
        SELECT EXISTS(
            SELECT 1
            FROM discount
            WHERE coupon_id = OLD.coupon_id
        )
    );
-- 2. nếu coupon đang được sử dụng trong discount thì không được xóa
    IF @v_has_discount THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Mã coupon đang được sử dụng trong chương trình giảm giá';
    END IF;

END
    end;

