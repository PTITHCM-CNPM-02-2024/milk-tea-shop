
DELIMITER //

-- Thêm mã giảm giá mới
CREATE PROCEDURE sp_insert_coupon(
    IN p_coupon VARCHAR(15),
    IN p_description VARCHAR(1000)
)
BEGIN
    INSERT INTO Coupon(coupon, description)
    VALUES(p_coupon, p_description);

    SELECT LAST_INSERT_ID() AS coupon_id;
END //

-- Cập nhật thông tin mã giảm giá
CREATE PROCEDURE sp_update_coupon(
    IN p_coupon_id INT UNSIGNED,
    IN p_coupon VARCHAR(15),
    IN p_description VARCHAR(1000)
)
BEGIN
    UPDATE Coupon
    SET coupon = p_coupon,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE coupon_id = p_coupon_id;

    SELECT ROW_COUNT() > 0 AS success;
END //

-- Xóa mã giảm giá
CREATE PROCEDURE sp_delete_coupon(
    IN p_coupon_id INT UNSIGNED
)
BEGIN
    DELETE FROM Coupon WHERE coupon_id = p_coupon_id;
    SELECT ROW_COUNT() > 0 AS success;
END //

-- Lấy mã giảm giá theo ID
CREATE PROCEDURE sp_get_coupon_by_id(
    IN p_coupon_id INT UNSIGNED
)
BEGIN
    SELECT * FROM Coupon WHERE coupon_id = p_coupon_id;
END //

-- Lấy mã giảm giá theo mã
CREATE PROCEDURE sp_get_coupon_by_code(
    IN p_coupon VARCHAR(15)
)
BEGIN
    SELECT * FROM Coupon WHERE coupon = p_coupon;
END //

-- Lấy tất cả mã giảm giá
CREATE PROCEDURE sp_get_all_coupons()
BEGIN
    SELECT * FROM Coupon ORDER BY coupon;
END //

-- Tìm kiếm mã giảm giá
CREATE PROCEDURE sp_search_coupons(
    IN p_keyword VARCHAR(100)
)
BEGIN
    SELECT c.*
    FROM Coupon c
    WHERE c.coupon LIKE CONCAT('%', p_keyword, '%')
       OR c.description LIKE CONCAT('%', p_keyword, '%')
    ORDER BY c.coupon;
END //

DELIMITER ;