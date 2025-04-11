-- ---------------------------------------------------------Dữ lệu mặc định------------------------------------------------

-- Dữ liệu cho bảng unit_of_measure
INSERT INTO unit_of_measure (name, symbol, description)
VALUES ('Militer', 'mL', 'Đơn vị đo lường thể tích'),
       ('Piece', 'cái', 'Đơn vị đếm số lượng');

-- Dữ liệu cho bảng product_size
INSERT INTO product_size (unit_id, name, quantity, description)
VALUES ((SELECT unit_id FROM unit_of_measure WHERE symbol = 'mL'), 'S', 450, 'Size nhỏ - 450mL'),
       ((SELECT unit_id FROM unit_of_measure WHERE symbol = 'mL'), 'M', 650, 'Size vừa - 650mL'),
       ((SELECT unit_id FROM unit_of_measure WHERE symbol = 'mL'), 'L', 800, 'Size lớn - 800mL'),
       ((SELECT unit_id FROM unit_of_measure WHERE symbol = 'cái'), 'NA', 1, 'Đơn vị (cái/phần)');

-- Dữ liệu cho bảng category
INSERT INTO category (category_id, name, description)
VALUES (1,'TOPPING', 'Các loại topping bổ sung cho đồ uống'),   
       (2,'TOPPING BÁN LẺ', 'Các loại topping được bán riêng lẻ');

-- Dữ liệu cho bảng membership_type
INSERT INTO membership_type (type, discount_value, discount_unit, required_point, description, is_active, valid_until)
VALUES ('NEWMEM', 0.000, 'FIXED', 0, 'Thành viên mới', 1, null),
       ('BRONZE', 1000, 'FIXED', 20, 'Thành viên hạng đồng', 1, TIME(DATE_ADD(NOW(), INTERVAL 365 DAY))),
       ('SILVER', 2000, 'FIXED', 50, 'Thành viên hạng bạc', 1, TIME(DATE_ADD(NOW(), INTERVAL 365 DAY))),
       ('GOLD', 1, 'PERCENTAGE', 100, 'Thành viên hạng vàng', 1, TIME(DATE_ADD(NOW(), INTERVAL 365 DAY))),
       ('PLATINUM', 2, 'PERCENTAGE', 200, 'Thành viên hạng bạch kim', 1, TIME(DATE_ADD(NOW(), INTERVAL 365 DAY)));

-- Dữ liệu cho bảng role
INSERT INTO role (name, description)
VALUES ('MANAGER', 'Quản trị viên - có toàn quyền quản lý hệ thống'),
       ('STAFF', 'Nhân viên - phục vụ và xử lý đơn hàng'),
       ('CUSTOMER', 'Khách hàng - người mua hàng'),
       ('GUEST', 'Khách vãng lai - người dùng chưa đăng ký');

-- Dữ liệu cho bảng payment_method
INSERT INTO payment_method (payment_name, payment_description)
VALUES ('CASH', 'Thanh toán bằng tiền mặt'),
       ('VISA', 'Thanh toán bằng thẻ Visa'),
       ('BANKCARD', 'Thanh toán bằng thẻ ngân hàng'),
       ('CREDIT_CARD', 'Thanh toán bằng thẻ tín dụng'),
       ('E-WALLET', 'Thanh toán bằng ví điện tử');

-- Dữ liệu cho bảng store
INSERT INTO store (name, address, phone, opening_time, closing_time, email, opening_date, tax_code)
    VALUE ('Milk Tea Shop',
           '123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh',
           '0987654321',
           '11:00:00',
           '22:00:00',
           'info@coffeeshop.com',
           '2023-01-01',
           '0123456789');



-- Triggers để bảo vệ dữ liệu mặc định
DELIMITER //

CREATE TRIGGER protect_default_unit_delete
    BEFORE DELETE
    ON unit_of_measure
    FOR EACH ROW
BEGIN
    IF OLD.symbol IN ('mL', 'cái') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa đơn vị đo lường mặc định';
    END IF;
END //

-- Bảo vệ kích thước sản phẩm mặc định
CREATE TRIGGER protect_default_size_update
    BEFORE UPDATE
    ON product_size
    FOR EACH ROW
BEGIN
    IF OLD.name IN ('S', 'M', 'L', 'NA') THEN
        IF NEW.name != OLD.name OR NEW.unit_id != OLD.unit_id THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể thay đổi kích thước mặc định';
        END IF;
    END IF;
END //

CREATE TRIGGER protect_default_size_delete
    BEFORE DELETE
    ON product_size
    FOR EACH ROW
BEGIN
    IF OLD.name IN ('S', 'M', 'L', 'NA') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa kích thước mặc định';
    END IF;
END //

-- Bảo vệ danh mục mặc định
CREATE TRIGGER protect_default_category_update
    BEFORE UPDATE
    ON category
    FOR EACH ROW
BEGIN
    IF OLD.name IN ('TOPPING', 'TOPPING BÁN LẺ') THEN
        IF NEW.name != OLD.name THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể thay đổi tên danh mục mặc định';
        END IF;
    END IF;
END //

CREATE TRIGGER protect_default_category_delete
    BEFORE DELETE
    ON category
    FOR EACH ROW
BEGIN
    IF OLD.name IN ('TOPPING', 'TOPPING BÁN LẺ') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa danh mục mặc định';
    END IF;
END //

-- Bảo vệ loại thành viên mặc định
CREATE TRIGGER protect_default_membership_update
    BEFORE UPDATE
    ON membership_type
    FOR EACH ROW
BEGIN
    IF OLD.type IN ('NEWMEM', 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM') THEN
        IF NEW.type != OLD.type THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể thay đổi tên loại thành viên mặc định';
        END IF;
    END IF;
END //

CREATE TRIGGER protect_default_membership_delete
    BEFORE DELETE
    ON membership_type
    FOR EACH ROW
BEGIN
    IF OLD.type IN ('NEWMEM', 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa loại thành viên mặc định';
    END IF;
END //

-- Bảo vệ vai trò mặc định
CREATE TRIGGER protect_default_role_update
    BEFORE UPDATE
    ON role
    FOR EACH ROW
BEGIN
    IF OLD.name IN ('MANAGER', 'STAFF', 'CUSTOMER', 'GUEST') THEN
        IF NEW.name != OLD.name THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể thay đổi tên vai trò mặc định';
        END IF;
    END IF;
END //

CREATE TRIGGER protect_default_role_delete
    BEFORE DELETE
    ON role
    FOR EACH ROW
BEGIN
    IF OLD.name IN ('MANAGER', 'STAFF', 'CUSTOMER', 'GUEST') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa vai trò mặc định';
    END IF;
END //

-- Bảo vệ phương thức thanh toán mặc định
CREATE TRIGGER protect_default_payment_method_update
    BEFORE UPDATE
    ON payment_method
    FOR EACH ROW
BEGIN
    IF OLD.payment_name IN ('CASH', 'VISA', 'BANKCARD', 'CREDIT_CARD', 'E-WALLET') THEN
        IF NEW.payment_name != OLD.payment_name THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể thay đổi tên phương thức thanh toán mặc định';
        END IF;
    END IF;
END //

CREATE TRIGGER protect_default_payment_method_delete
    BEFORE DELETE
    ON payment_method
    FOR EACH ROW
BEGIN
    IF OLD.payment_name IN ('CASH', 'VISA', 'BANKCARD', 'CREDIT_CARD', 'E-WALLET') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa phương thức thanh toán mặc định';
    END IF;
END //

DELIMITER ;

DELIMITER //

-- Trigger ngăn chặn việc thêm mới nếu đã tồn tại thông tin cửa hàng
CREATE TRIGGER before_store_insert
    BEFORE INSERT ON store
    FOR EACH ROW
BEGIN
    DECLARE store_count INT;

    -- Đếm số lượng bản ghi hiện có
    SELECT COUNT(*) INTO store_count FROM store;

    -- Nếu đã có bản ghi, từ chối thêm mới
    IF store_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể tạo thêm thông tin cửa hàng mới. Chỉ được phép có một bản ghi thông tin cửa hàng.';
    END IF;
END //

-- Trigger ngăn chặn việc xóa thông tin cửa hàng duy nhất
CREATE TRIGGER before_store_delete
    BEFORE DELETE ON store
    FOR EACH ROW
BEGIN
    DECLARE store_count INT;

    -- Đếm số lượng bản ghi hiện có
    SELECT COUNT(*) INTO store_count FROM store;

    -- Nếu chỉ có một bản ghi, từ chối xóa
    IF store_count = 1 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa thông tin cửa hàng duy nhất. Cửa hàng phải luôn có thông tin.';
    END IF;
END //

DELIMITER ;

DELIMITER //

-- Trigger chỉ cho phép cập nhật thông tin, không thay đổi ID
CREATE TRIGGER before_store_update
    BEFORE UPDATE ON store
    FOR EACH ROW
BEGIN
    -- Đảm bảo không thay đổi ID
    IF NEW.store_id != OLD.store_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi ID của cửa hàng. Chỉ được phép cập nhật thông tin.';
    END IF;
END //

DELIMITER ;
