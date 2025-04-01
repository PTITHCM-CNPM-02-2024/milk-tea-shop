

DELIMITER //

-- Thêm phương thức thanh toán mới
CREATE PROCEDURE sp_insert_payment_method(
    IN p_payment_name VARCHAR(50),
    IN p_payment_description VARCHAR(255)
)
BEGIN
    INSERT INTO PaymentMethod(payment_name, payment_description)
    VALUES(p_payment_name, p_payment_description);

    SELECT LAST_INSERT_ID() AS payment_method_id;
END //

-- Cập nhật thông tin phương thức thanh toán
CREATE PROCEDURE sp_update_payment_method(
    IN p_payment_method_id TINYINT UNSIGNED,
    IN p_payment_name VARCHAR(50),
    IN p_payment_description VARCHAR(255)
)
BEGIN
    UPDATE PaymentMethod
    SET payment_name = p_payment_name,
        payment_description = p_payment_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE payment_method_id = p_payment_method_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa phương thức thanh toán
CREATE PROCEDURE sp_delete_payment_method(
    IN p_payment_method_id TINYINT UNSIGNED
)
BEGIN
    DELETE FROM PaymentMethod WHERE payment_method_id = p_payment_method_id;
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy phương thức thanh toán theo ID
CREATE PROCEDURE sp_get_payment_method_by_id(
    IN p_payment_method_id TINYINT UNSIGNED
)
BEGIN
    SELECT * FROM PaymentMethod WHERE payment_method_id = p_payment_method_id;
END //

-- Lấy tất cả phương thức thanh toán
CREATE PROCEDURE sp_get_all_payment_methods()
BEGIN
    SELECT * FROM PaymentMethod ORDER BY payment_name;
END //

-- Kiểm tra phương thức thanh toán đang được sử dụng
CREATE PROCEDURE sp_check_payment_method_in_use(
    IN p_payment_method_id TINYINT UNSIGNED
)
BEGIN
    DECLARE payment_count INT;
    SELECT COUNT(*) INTO payment_count FROM Payment WHERE payment_method_id = p_payment_method_id;

    SELECT
        payment_count > 0 AS in_use,
        payment_count AS usage_count;
END //

DELIMITER ;