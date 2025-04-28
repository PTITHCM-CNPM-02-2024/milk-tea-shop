create trigger milk_tea_shop_prod.before_payment_insert
    before insert
    on milk_tea_shop_prod.payment
    for each row
begin
    BEGIN
    -- Kiểm tra trạng thái
    IF NEW.status IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái thanh toán không được để trống';
    END IF;

    -- Kiểm tra số tiền đã trả
    IF NEW.amount_paid IS NOT NULL AND NEW.amount_paid <> 0 AND NEW.amount_paid < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền đã trả phải là 0 hoặc lớn hơn 1000';
    END IF;

    -- Kiểm tra tiền thừa
    IF NEW.change_amount IS NOT NULL AND NEW.change_amount <> 0 AND NEW.change_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tiền thừa phải là 0 hoặc lớn hơn 1000';
    END IF;
END
    end;

