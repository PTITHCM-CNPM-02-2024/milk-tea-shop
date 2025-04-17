create trigger milk_tea_shop_prod.before_discount_insert
    before insert
    on milk_tea_shop_prod.discount
    for each row
begin
    BEGIN
    -- Kiểm tra giá trị đơn hàng tối thiểu
    IF NEW.min_required_order_value < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị đơn hàng tối thiểu phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra số tiền giảm giá tối đa
    IF NEW.max_discount_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền giảm giá tối đa phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra giá trị giảm giá theo loại
    IF NEW.discount_type = 'PERCENTAGE' AND (NEW.discount_value <= 0 OR NEW.discount_value > 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá theo phần trăm phải lớn hơn 0 và nhỏ hơn hoặc bằng 100';
    ELSEIF NEW.discount_type = 'FIXED' AND NEW.discount_value < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá cố định phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra số lượng sản phẩm tối thiểu
    IF NEW.min_required_product IS NOT NULL AND NEW.min_required_product <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lượng sản phẩm tối thiểu phải lớn hơn 0';
    END IF;

    -- Kiểm tra thời gian hiệu lực
    IF NEW.valid_from IS NOT NULL AND NEW.valid_until IS NOT NULL AND NEW.valid_from >= NEW.valid_until THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời gian bắt đầu phải trước thời gian kết thúc';
    END IF;

    -- Kiểm tra số lần sử dụng tối đa
    IF NEW.max_uses IS NOT NULL AND NEW.max_uses <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lần sử dụng tối đa phải lớn hơn 0';
    END IF;

    -- Kiểm tra số lần sử dụng tối đa cho mỗi khách hàng
    IF NEW.max_uses_per_customer IS NOT NULL AND NEW.max_uses_per_customer <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lần sử dụng tối đa cho mỗi khách hàng phải lớn hơn 0';
    END IF;

    -- Kiểm tra mối quan hệ giữa max_uses và max_uses_per_customer
    IF NEW.max_uses IS NOT NULL AND NEW.max_uses_per_customer IS NOT NULL AND NEW.max_uses < NEW.max_uses_per_customer THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lần sử dụng tối đa phải lớn hơn hoặc bằng số lần sử dụng tối đa cho mỗi khách hàng';
    END IF;

    -- Kiểm tra current_uses
    IF NEW.current_uses IS NOT NULL AND NEW.current_uses < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lần đã sử dụng không được âm';
    END IF;

    -- Kiểm tra mối quan hệ giữa max_uses và current_uses
    IF NEW.max_uses IS NOT NULL AND NEW.current_uses IS NOT NULL AND NEW.max_uses < NEW.current_uses THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lần sử dụng tối đa phải lớn hơn hoặc bằng số lần đã sử dụng';
    END IF;

    -- Kiểm tra mối quan hệ giữa discount_type, discount_value và max_discount_amount
    IF NEW.discount_type = 'FIXED' AND NEW.discount_value > NEW.max_discount_amount THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá cố định không được lớn hơn số tiền giảm giá tối đa';
    END IF;
END
    end;

