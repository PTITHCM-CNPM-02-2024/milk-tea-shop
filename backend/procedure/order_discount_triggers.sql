DELIMITER //

-- Before Insert Trigger
CREATE TRIGGER before_order_discount_insert
BEFORE INSERT ON OrderDiscount
FOR EACH ROW
BEGIN
    -- Kiểm tra discount_amount không âm
    IF NEW.discount_amount < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số tiền giảm giá không được âm';
    END IF;
END //

-- Before Update Trigger
CREATE TRIGGER before_order_discount_update
BEFORE UPDATE ON OrderDiscount
FOR EACH ROW
BEGIN
    -- Kiểm tra discount_amount không âm
    IF NEW.discount_amount < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số tiền giảm giá không được âm';
    END IF;
END //

DELIMITER ; 