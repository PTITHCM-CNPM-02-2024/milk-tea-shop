create trigger milk_tea_shop_prod.protect_default_payment_method_update
    before update
    on milk_tea_shop_prod.payment_method
    for each row
begin
    BEGIN
    IF OLD.payment_name IN ('CASH', 'VISA', 'BANKCARD', 'CREDIT_CARD', 'E-WALLET') THEN
        IF NEW.payment_name != OLD.payment_name THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể thay đổi tên phương thức thanh toán mặc định';
        END IF;
    END IF;
END
    end;

