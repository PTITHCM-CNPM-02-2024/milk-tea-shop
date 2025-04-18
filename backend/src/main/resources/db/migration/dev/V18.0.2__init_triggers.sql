-- 11. Area

DELIMITER //

-- Kiểm tra trước khi thêm khu vực
CREATE TRIGGER before_area_insert
    BEFORE INSERT ON area
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên khu vực
    IF NEW.name REGEXP '^[a-zA-Z0-9_-]{3}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài 3 ký tự';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn 0 nếu được chỉ định
    IF NEW.max_tables IS NOT NULL AND NEW.max_tables <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn 0';
    END IF;
END //

-- Kiểm tra trước khi cập nhật khu vực
CREATE TRIGGER before_area_update
    BEFORE UPDATE ON area
    FOR EACH ROW
BEGIN
    DECLARE current_tables INT;
    DECLARE order_count INT;
    -- Kiểm tra tên khu vực
    IF LENGTH(TRIM(NEW.name)) = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực không được để trống';
    END IF;

    IF  NEW.name REGEXP '^[a-zA-Z0-9_-]{3}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên khu vực chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài 3 ký tự';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn 0, nhỏ hơn 100
    IF NEW.max_tables IS NOT NULL AND (NEW.max_tables <= 0 OR NEW.max_tables >= 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn 0 và nhỏ hơn 100';
    END IF;

    -- Kiểm tra số bàn tối đa phải lớn hơn hoặc bằng số bàn hiện có
    IF NEW.max_tables IS NOT NULL THEN
        SELECT COUNT(*) INTO current_tables FROM service_table WHERE area_id = NEW.area_id;

        IF NEW.max_tables < current_tables THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Số bàn tối đa phải lớn hơn hoặc bằng số bàn hiện có';
        END IF;
    END IF;

    IF OLD.is_active <> NEW.is_active AND NEW.is_active = 0 THEN
        -- Nếu là 0 cần kiểm tra order status = processing
        SELECT COUNT(*) INTO order_count FROM `order` WHERE area_id = NEW.area_id AND status = 'PROCESSING';

        IF order_count > 0 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể deactive khu vực có đơn hàng đang xử lý';
        END IF;
    END IF;
END //

-- Kiểm tra trước khi xóa khu vực
CREATE TRIGGER before_area_delete
    BEFORE DELETE ON area
    FOR EACH ROW
BEGIN
END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm danh mục
CREATE TRIGGER before_category_insert
    BEFORE INSERT ON category
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên danh mục
    IF LENGTH(TRIM(NEW.name)) = 0  OR LENGTH(TRIM(NEW.name)) > 100 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên danh mục không được để trống và có độ dài tối đa 100 ký tự';
    END IF;


END //

-- Kiểm tra trước khi cập nhật danh mục
CREATE TRIGGER before_category_update
    BEFORE UPDATE ON category
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên danh mục
    IF LENGTH(TRIM(NEW.name)) = 0  OR LENGTH(TRIM(NEW.name)) > 100 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên danh mục không được để trống và có độ dài tối đa 100 ký tự';
    END IF;


END //

-- Kiểm tra trước khi xóa danh mục, chưa cần thiết
CREATE TRIGGER before_category_delete
    BEFORE DELETE ON category
    FOR EACH ROW
BEGIN
END //

DELIMITER ;
-- ----------------------------------------------------------------------
-- 12. Service Table

DELIMITER //

-- Kiểm tra trước khi thêm bàn
CREATE TRIGGER before_service_table_insert
    BEFORE INSERT ON service_table
    FOR EACH ROW
BEGIN
    DECLARE max_tables INT;
    DECLARE current_tables INT;
    -- Kiểm tra số bàn
    IF LENGTH(TRIM(NEW.table_number)) > 10 OR LENGTH(TRIM(NEW.table_number)) < 3 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn phải từ 3 đến 10 ký tự';
    END IF;

    -- Kiểm tra khu vực tồn tại nếu được chỉ định
    IF NEW.area_id IS NOT NULL THEN
        -- Kiểm tra số lượng bàn không vượt quá giới hạn của khu vực
        SELECT max_tables INTO max_tables FROM area WHERE area_id = NEW.area_id;
        SELECT COUNT(*) INTO current_tables FROM service_table WHERE area_id = NEW.area_id;

        IF max_tables IS NOT NULL AND current_tables >= max_tables THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Số lượng bàn đã đạt giới hạn tối đa của khu vực';
        END IF;

    END IF;

END //

-- Kiểm tra trước khi cập nhật bàn
CREATE TRIGGER before_service_table_update
    BEFORE UPDATE ON service_table
    FOR EACH ROW
BEGIN
    DECLARE max_tables INT;
    DECLARE current_tables INT;
    DECLARE has_active_order BOOLEAN;
    -- Kiểm tra số bàn
    IF LENGTH(TRIM(NEW.table_number)) < 3 OR LENGTH(TRIM(NEW.table_number)) > 50 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số bàn phải từ 3 đến 50 ký tự';
    END IF;

    -- Kiểm tra khu vực tồn tại nếu được chỉ định
    IF NEW.area_id IS NOT NULL AND NEW.area_id <> OLD.area_id AND OLD.area_id IS NOT NULL THEN
        SELECT max_tables INTO max_tables FROM area WHERE area_id = NEW.area_id;
        SELECT COUNT(*) INTO current_tables FROM service_table WHERE area_id = NEW.area_id;

        IF max_tables IS NOT NULL AND current_tables >= max_tables THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Số lượng bàn đã đạt giới hạn tối đa của khu vực';
        END IF;
    END IF;

    -- Không cho phép vô hiệu hóa bàn đang được sử dụng
    IF OLD.is_active = 1 AND NEW.is_active = 0 THEN
        SELECT EXISTS(
            SELECT 1
            FROM order_table ot
                     JOIN `order` o ON ot.order_id = o.order_id
            WHERE ot.table_id = NEW.table_id
              AND ot.check_out IS NULL
        ) INTO has_active_order;

        IF has_active_order THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể vô hiệu hóa bàn đang được sử dụng';
        END IF;
    END IF;
END //

-- Kiểm tra trước khi xóa bàn
CREATE TRIGGER before_service_table_delete
    BEFORE DELETE ON service_table
    FOR EACH ROW
BEGIN
    DECLARE has_active_order BOOLEAN;

    SELECT EXISTS(
        SELECT 1
        FROM order_table ot
                 JOIN `order` o ON ot.order_id = o.order_id
        WHERE ot.table_id = OLD.table_id
          AND ot.check_out IS NULL
    ) INTO has_active_order;

    IF has_active_order THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa bàn đang được sử dụng';
    END IF;
END //

DELIMITER ;
-- -----------------------------------------------------------------------
-- 17. Coupon
DELIMITER //

-- Kiểm tra trước khi thêm mã giảm giá
CREATE TRIGGER before_coupon_insert
    BEFORE INSERT ON coupon
    FOR EACH ROW
BEGIN
    -- Kiểm tra mã giảm giá
    IF NEW.coupon REGEXP '^[a-zA-Z0-9]{3,15}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá phải có độ dài từ 3 đến 15 ký tự và chỉ được chứa chữ cái và số';
    END IF;
END //

-- Kiểm tra trước khi cập nhật mã giảm giá
CREATE TRIGGER before_coupon_update
    BEFORE UPDATE ON coupon
    FOR EACH ROW
BEGIN
    -- Kiểm tra mã giảm giá
    IF NEW.coupon REGEXP '^[a-zA-Z0-9]{3,15}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã giảm giá phải có độ dài từ 3 đến 15 ký tự và chỉ được chứa chữ cái và số';
    END IF;
END //

-- Kiểm tra trước khi xóa mã giảm giá, cần kiểm tra xem có đang được sử dụng bởi discount không
CREATE TRIGGER before_coupon_delete
    BEFORE DELETE ON coupon
    FOR EACH ROW
BEGIN
    DECLARE discount_count BOOLEAN;

    -- Kiểm tra xem coupon có đang được sử dụng trong discount không
    SELECT EXISTS(SELECT 1 FROM discount WHERE coupon_id = OLD.coupon_id) INTO discount_count;

    IF discount_count THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa mã giảm giá đang được sử dụng trong chương trình giảm giá, vui lòng xóa discount trước';
    END IF;

END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm khách hàng
CREATE TRIGGER before_customer_insert
    BEFORE INSERT ON customer
    FOR EACH ROW
BEGIN
    -- Kiểm tra họ
    IF NEW.last_name IS NOT NULL AND LENGTH(NEW.last_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra tên
    IF NEW.first_name IS NOT NULL AND LENGTH(NEW.first_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra email
    IF NEW.email IS NOT NULL AND NEW.email REGEXP '[a-zA-Z0-9_!#$%&\'*+/=?\`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;

    -- Kiểm tra điểm hiện tại
    IF NEW.current_points < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm hiện tại không được âm';
    END IF;
END //

-- Kiểm tra trước khi cập nhật khách hàng
CREATE TRIGGER before_customer_update
    BEFORE UPDATE ON customer
    FOR EACH ROW
BEGIN
    -- Kiểm tra họ
    IF NEW.last_name IS NOT NULL AND LENGTH(NEW.last_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra tên
    IF NEW.first_name IS NOT NULL AND LENGTH(NEW.first_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra email
    IF NEW.email IS NOT NULL AND NEW.email REGEXP '[a-zA-Z0-9_!#$%&\'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;

    -- Kiểm tra điểm hiện tại
    IF NEW.current_points < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm hiện tại không được âm';
    END IF;
END //

DELIMITER ;

DELIMITER //

-- Kiểm tra trước khi xóa khách hàng
CREATE TRIGGER before_customer_delete
    BEFORE DELETE ON customer
    FOR EACH ROW
BEGIN
    -- Kiểm tra xem khách hàng có đang có đơn hàng đang chờ xử lý không
    DECLARE has_relations BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM `order` WHERE customer_id = OLD.customer_id AND status = 'PROCESSING'
    ) INTO has_relations;

    IF has_relations THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa khách hàng đã có đơn hàng đang chờ xử lý';
    END IF;
END //

DELIMITER ;

DELIMITER //
-- sau khi xóa khách hàng xóa tài khoản liên kết
CREATE TRIGGER after_customer_delete
    AFTER DELETE ON customer
    FOR EACH ROW
BEGIN
    -- kiểm tra tài khoản có đang liên kết với khách hàng không
    DECLARE has_relations BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM account WHERE account_id = OLD.account_id
    ) INTO has_relations;

    IF has_relations THEN
        DELETE FROM account WHERE account_id = OLD.account_id;
    END IF;

END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm discount
CREATE TRIGGER before_discount_insert
    BEFORE INSERT ON discount
    FOR EACH ROW
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
END //

-- Kiểm tra trước khi cập nhật discount
CREATE TRIGGER before_discount_update
    BEFORE UPDATE ON discount
    FOR EACH ROW
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
END //
-- trước khi xóa discount
CREATE TRIGGER before_discount_delete
    BEFORE DELETE ON discount
    FOR EACH ROW
BEGIN
    DECLARE order_count BOOLEAN;

    -- Kiểm tra xem discount có đang được sử dụng trong order có status = PROCESSING không, cần join với table order_discount
    SELECT EXISTS(
        SELECT 1 FROM `order`
                          JOIN order_discount ON `order`.order_id = order_discount.order_id
        WHERE order_discount.discount_id = OLD.discount_id AND `order`.status = 'PROCESSING'
    ) INTO order_count;

    IF order_count THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa chương trình giảm giá đang được sử dụng trong đơn hàng đang chờ xử lý';
    END IF;


END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm nhân viên
CREATE TRIGGER before_employee_insert
    BEFORE INSERT ON employee
    FOR EACH ROW
BEGIN
    -- Kiểm tra họ
    IF LENGTH(NEW.last_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra tên
    IF LENGTH(NEW.first_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra email
    IF NEW.email IS NOT NULL AND NEW.email REGEXP '[a-zA-Z0-9_!#$%&\'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;
END //

-- Kiểm tra trước khi cập nhật nhân viên
CREATE TRIGGER before_employee_update
    BEFORE UPDATE ON employee
    FOR EACH ROW
BEGIN
    -- Kiểm tra họ
    IF LENGTH(NEW.last_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra tên
    IF LENGTH(NEW.first_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra email
    IF NEW.email IS NOT NULL AND NEW.email REGEXP '[a-zA-Z0-9_!#$%&\'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;
END //


-- Kiểm tra trước khi xóa nhân viên
CREATE TRIGGER before_employee_delete
    BEFORE DELETE ON employee
    FOR EACH ROW
BEGIN
    -- Kiểm tra xem nhân viên có đang có đơn hàng đang chờ xử lý không
    DECLARE has_relations BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM `order` WHERE employee_id = OLD.employee_id AND status = 'PROCESSING'
    ) INTO has_relations;

    IF has_relations THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa nhân viên đang có đơn hàng đang chờ xử lý';
    END IF;
END //


-- sau khi xóa nhân viên xóa tài khoản liên kết
CREATE TRIGGER after_employee_delete
    AFTER DELETE ON employee
    FOR EACH ROW
BEGIN
    -- kiểm tra tài khoản có đang liên kết với nhân viên không
    DECLARE has_relations BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM account WHERE account_id = OLD.account_id
    ) INTO has_relations;

    IF has_relations THEN
        DELETE FROM account WHERE account_id = OLD.account_id;
    END IF;
END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm quản lý
CREATE TRIGGER before_manager_insert
    BEFORE INSERT ON manager
    FOR EACH ROW
BEGIN
    -- Kiểm tra họ
    IF LENGTH(NEW.last_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra tên
    IF LENGTH(NEW.first_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra email
    IF NEW.email REGEXP '^[a-zA-Z0-9_!#$%&\'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;
END //

-- Kiểm tra trước khi cập nhật quản lý
CREATE TRIGGER before_manager_update
    BEFORE UPDATE ON manager
    FOR EACH ROW
BEGIN
    -- Kiểm tra họ
    IF LENGTH(NEW.last_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Họ không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra tên
    IF LENGTH(NEW.first_name) > 70 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên không được vượt quá 70 ký tự';
    END IF;

    -- Kiểm tra số điện thoại
    IF NEW.phone REGEXP '(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số điện thoại không hợp lệ';
    END IF;

    -- Kiểm tra email
    IF NEW.email REGEXP '^[a-zA-Z0-9_!#$%&\'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email không hợp lệ';
    END IF;
END //


-- Kiểm tra trước khi xóa quản lý
CREATE TRIGGER after_manager_delete
    AFTER DELETE ON manager
    FOR EACH ROW
BEGIN
    -- kiểm tra tài khoản có đang liên kết với quản lý không
    DECLARE has_relations BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM account WHERE account_id = OLD.account_id
    ) INTO has_relations;

    IF has_relations THEN
        DELETE FROM account WHERE account_id = OLD.account_id;
    END IF;
END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm membership_type
CREATE TRIGGER before_membership_type_insert
    BEFORE INSERT ON membership_type
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên loại thành viên
    IF LENGTH(TRIM(NEW.type)) = 0 OR LENGTH(TRIM(NEW.type)) > 50 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên loại thành viên phải có độ dài từ 1 đến 50 ký tự';
    END IF;

    -- Kiểm tra giá trị giảm giá
    IF NEW.discount_unit = 'PERCENTAGE' AND (NEW.discount_value < 0 OR NEW.discount_value > 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá phần trăm phải nằm trong khoảng từ 0 đến 100%';
    ELSEIF NEW.discount_unit = 'FIXED' AND NEW.discount_value <> 0 AND NEW.discount_value < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá cố định phải là 0 hoặc lớn hơn 1000';
    END IF;

    -- Kiểm tra điểm yêu cầu
    IF NEW.required_point < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm yêu cầu không được âm';
    END IF;
END //

-- Kiểm tra trước khi cập nhật membership_type
CREATE TRIGGER before_membership_type_update
    BEFORE UPDATE ON membership_type
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên loại thành viên
    IF LENGTH(TRIM(NEW.type)) = 0 OR LENGTH(TRIM(NEW.type)) > 50 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên loại thành viên phải có độ dài từ 1 đến 50 ký tự';
    END IF;

    -- Kiểm tra giá trị giảm giá
    IF NEW.discount_unit = 'PERCENTAGE' AND (NEW.discount_value < 0 OR NEW.discount_value > 100) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá phần trăm phải nằm trong khoảng từ 0 đến 100%';
    ELSEIF NEW.discount_unit = 'FIXED' AND NEW.discount_value <> 0 AND NEW.discount_value < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá trị giảm giá cố định phải là 0 hoặc lớn hơn 1000';
    END IF;

    -- Kiểm tra điểm yêu cầu
    IF NEW.required_point < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm yêu cầu không được âm';
    END IF;
END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Before Insert Trigger
CREATE TRIGGER before_order_discount_insert
    BEFORE INSERT ON order_discount
    FOR EACH ROW
BEGIN
    -- kiểm tra order phải ở trạng thái processing
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thêm giảm giá cho đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra discount_amount không âm
    IF NEW.discount_amount <= 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền giảm giá không được nhỏ hơn 1000 VNĐ';
    END IF;
END //

-- Before Update Trigger
CREATE TRIGGER before_order_discount_update
    BEFORE UPDATE ON order_discount
    FOR EACH ROW
BEGIN
    -- Lấy trạng thái của đơn hàng
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');

    IF NEW.order_id IS NOT NULL AND NEW.order_id <> OLD.order_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể cập nhật giảm giá cho đơn hàng khác';
    END IF;

    SELECT status INTO order_status
    FROM `order`
    WHERE order_id = NEW.order_id;

    -- Kiểm tra xem order ở trạng thái khác PROCESSING thì không được cập nhật
    IF order_status = 'COMPLETED' OR order_status = 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể cập nhật giảm giá cho đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra discount_amount không nhỏ hơn 1000
    IF NEW.discount_amount <= 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số tiền giảm giá không được nhỏ hơn 1000 VNĐ';
    END IF;
END //

-- Before Delete Trigger cho OrderDiscount
CREATE TRIGGER before_order_discount_delete
    BEFORE DELETE ON order_discount
    FOR EACH ROW
BEGIN

END //

DELIMITER ; 
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm sản phẩm vào đơn hàng
CREATE TRIGGER before_order_product_insert
    BEFORE INSERT ON order_product
    FOR EACH ROW
BEGIN

    -- đơn hàng phải ở trạng thái processing
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thêm sản phẩm vào đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra số lượng phải lớn hơn 0
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lượng sản phẩm phải lớn hơn 0';
    END IF;
END //

-- Kiểm tra trước khi cập nhật sản phẩm trong đơn hàng
CREATE TRIGGER before_order_product_update
    BEFORE UPDATE ON order_product
    FOR EACH ROW
BEGIN

    -- Kiểm tra xem order ở trạng thái khác PROCESSING thì không được cập nhật
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;

    IF NEW.order_id IS NOT NULL AND NEW.order_id <> OLD.order_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể cập nhật số lượng sản phẩm cho đơn hàng khác';
    END IF;

    -- Kiểm tra xem order ở trạng thái khác PROCESSING thì không được cập nhật
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không được cập nhật số lượng sản phẩm cho đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra số lượng phải lớn hơn 0
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lượng sản phẩm phải lớn hơn 0';
    END IF;
END //

-- Cập nhật tổng tiền sau khi thêm sản phẩm
CREATE TRIGGER after_order_product_insert
    AFTER INSERT ON order_product
    FOR EACH ROW
BEGIN

END //

-- Cập nhật tổng tiền sau khi cập nhật sản phẩm
CREATE TRIGGER after_order_product_update
    AFTER UPDATE ON order_product
    FOR EACH ROW
BEGIN

END //

-- Cập nhật tổng tiền sau khi xóa sản phẩm
CREATE TRIGGER after_order_product_delete
    AFTER DELETE ON order_product
    FOR EACH ROW
BEGIN
    -- Cập nhật tổng tiền đơn hàng
END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi đặt bàn
CREATE TRIGGER before_order_table_insert
    BEFORE INSERT ON order_table
    FOR EACH ROW
BEGIN
    -- đơn hàng phải ở trạng thái processing
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    DECLARE is_active BOOLEAN;
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;
    IF order_status <> 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể đặt bàn cho đơn hàng không ở trạng thái processing';
    END IF;
    -- không được đặt bàn cho bàn không hoạt động
    SELECT is_active INTO is_active FROM `service_table` WHERE table_id = NEW.table_id;
    IF is_active = FALSE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể đặt bàn cho bàn không hoạt động';
    END IF;

    IF NEW.check_out IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể đặt bàn cho đơn hàng đã checkout';
    END IF;

    -- Kiểm tra thời gian vào/ra bàn
    IF NEW.check_out IS NOT NULL AND NEW.check_out < NEW.check_in THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thời gian rời bàn phải sau thời gian vào bàn';
    END IF;
END //

-- Kiểm tra trước khi cập nhật đặt bàn
CREATE TRIGGER before_order_table_update
    BEFORE UPDATE ON order_table
    FOR EACH ROW
BEGIN

    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');
    DECLARE is_active BOOLEAN;
    -- không được cập nhật order_id
    IF NEW.order_id IS NOT NULL AND NEW.order_id <> OLD.order_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể cập nhật order_id';
    END IF;
    -- nếu đơn hàng đang ở trạng thái processing thì được cập nhật, ngược lại không được cập nhật
    SELECT status INTO order_status FROM `order` WHERE order_id = NEW.order_id;
    IF order_status = 'PROCESSING' THEN
        -- không được cập nhật bàn không hoạt động
        SELECT is_active INTO is_active FROM `service_table` WHERE table_id = NEW.table_id;
        IF is_active is not null and is_active = FALSE THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể cập nhật đặt bàn cho bàn không hoạt động';
        END IF;
    ELSE
        IF NEW.check_in IS NOT NULL AND NEW.check_in <> OLD.check_in THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Không thể cập nhật check_in';
        END IF;
        IF NEW.check_out IS NOT NULL AND NEW.check_out <> OLD.check_out THEN
            IF OLD.check_out IS NOT NULL THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Không thể cập nhật check_out cho đơn hàng đã checkout';
            END IF;
            IF NEW.check_out < NEW.check_in THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Không thể cập nhật check_out trước check_in';
            END IF;
        END IF;
    END IF;

END //

-- Kiểm tra trước khi xóa đặt bàn
CREATE TRIGGER before_order_table_delete
    BEFORE DELETE ON order_table
    FOR EACH ROW
BEGIN
    DECLARE order_status ENUM('PROCESSING', 'CANCELLED', 'COMPLETED');

    -- Kiểm tra xem đơn hàng có đang ở trạng thái PROCESSING không
    SELECT status INTO order_status FROM `order` WHERE order_id = OLD.order_id;

    IF order_status = 'PROCESSING' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa đặt bàn cho đơn hàng đang xử lý';
    END IF;
END //

-- Sau khi thêm đặt bàn
CREATE TRIGGER after_order_table_insert
    AFTER INSERT ON order_table
    FOR EACH ROW
BEGIN
    -- Cập nhật trạng thái bàn hoặc thực hiện các hành động khác sau khi đặt bàn
    -- (Có thể để trống nếu không cần xử lý gì)
END //

-- Sau khi cập nhật đặt bàn
CREATE TRIGGER after_order_table_update
    AFTER UPDATE ON order_table
    FOR EACH ROW
BEGIN
END //

-- Sau khi xóa đặt bàn
CREATE TRIGGER after_order_table_delete
    AFTER DELETE ON order_table
    FOR EACH ROW
BEGIN
    -- Thực hiện các hành động sau khi xóa đặt bàn
    -- (Có thể để trống nếu không cần xử lý gì)
END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm đơn hàng
CREATE TRIGGER before_order_insert
    BEFORE INSERT ON `order`
    FOR EACH ROW
BEGIN
    -- Kiểm tra trạng thái
    IF NEW.status IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái đơn hàng không được để trống';
    END IF;

    -- Kiểm tra tổng tiền
    IF NEW.total_amount IS NOT NULL AND NEW.total_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tổng tiền phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra thành tiền
    IF NEW.final_amount IS NOT NULL AND NEW.final_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thành tiền phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra mối quan hệ giữa total_amount và final_amount
    IF NEW.total_amount IS NOT NULL AND NEW.final_amount IS NOT NULL AND NEW.final_amount > NEW.total_amount THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thành tiền không được lớn hơn tổng tiền';
    END IF;

    -- Kiểm tra điểm
    IF NEW.point IS NOT NULL AND NEW.point < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm không được âm';
    END IF;
END //

-- Kiểm tra trước khi cập nhật đơn hàng
CREATE TRIGGER before_order_update
    BEFORE UPDATE ON `order`
    FOR EACH ROW
BEGIN
    -- Kiểm tra trạng thái
    IF NEW.status IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái đơn hàng không được để trống';
    ELSEIF (OLD.status = 'COMPLETED' OR OLD.status = 'CANCELLED') AND NEW.status <> OLD.status THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi trạng thái của đơn hàng đã hoàn thành hoặc đã hủy';
    END IF;

    -- Kiểm tra tổng tiền
    IF NEW.total_amount IS NOT NULL AND NEW.total_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tổng tiền phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra thành tiền
    IF NEW.final_amount IS NOT NULL AND NEW.final_amount < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thành tiền phải lớn hơn hoặc bằng 1000';
    END IF;

    -- Kiểm tra mối quan hệ giữa total_amount và final_amount
    IF NEW.total_amount IS NOT NULL AND NEW.final_amount IS NOT NULL AND NEW.final_amount > NEW.total_amount THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Thành tiền không được lớn hơn tổng tiền';
    END IF;

    -- Kiểm tra điểm
    IF NEW.point IS NOT NULL AND NEW.point < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Điểm không được âm';
    END IF;
END //

-- không cho phép xóa đơn hàng, cần xóa từ nhân viên    
CREATE TRIGGER before_order_delete
    BEFORE DELETE ON `order`
    FOR EACH ROW
BEGIN
END //

DELIMITER ;
-- ----------------------------------------------------------------------
-- 16. PaymentMethod
DELIMITER //

-- Kiểm tra trước khi thêm phương thức thanh toán
CREATE TRIGGER before_payment_method_insert
    BEFORE INSERT ON payment_method
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên phương thức thanh toán
    IF LENGTH(TRIM(NEW.payment_name)) < 3 OR LENGTH(TRIM(NEW.payment_name)) > 50 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên phương thức thanh toán phải từ 3 đến 50 ký tự';
    END IF;
END //

-- Kiểm tra trước khi cập nhật phương thức thanh toán
CREATE TRIGGER before_payment_method_update
    BEFORE UPDATE ON payment_method
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên phương thức thanh toán
    IF LENGTH(TRIM(NEW.payment_name)) < 3 OR LENGTH(TRIM(NEW.payment_name)) > 50 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên phương thức thanh toán phải từ 3 đến 50 ký tự';
    END IF;
END //

-- Kiểm tra trước khi xóa phương thức thanh toán
CREATE TRIGGER before_payment_method_delete
    BEFORE DELETE ON payment_method
    FOR EACH ROW
BEGIN
END //

DELIMITER ;
-- ----------------------------------------------------------------------
-- 15. Payment

DELIMITER //

-- Kiểm tra trước khi thêm thanh toán
CREATE TRIGGER before_payment_insert
    BEFORE INSERT ON payment
    FOR EACH ROW
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
END //

DELIMITER ;

DELIMITER //

-- Kiểm tra trước khi cập nhật thanh toán
CREATE TRIGGER before_payment_update
    BEFORE UPDATE ON payment
    FOR EACH ROW
BEGIN
    -- không cho phép cập nhật trạng thái từ PAID về PROCESSING
    IF NEW.status is not null and OLD.status = 'PAID' and NEW.status <> 'PAID' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi trạng thái thanh toán của thanh toán đã hoàn tất';
    END IF;

    -- không cho phép cập nhật trạng thái từ CANCELLED về PROCESSING
    IF NEW.status is not null and OLD.status = 'CANCELLED' and NEW.status <> 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể thay đổi trạng thái thanh toán của thanh toán đã hủy';
    END IF;

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
END //

DELIMITER ;
DELIMITER //

-- Kiểm tra trước khi thêm giá sản phẩm
CREATE TRIGGER before_product_price_insert
    BEFORE INSERT ON product_price
    FOR EACH ROW
BEGIN
    -- Kiểm tra giá sản phẩm
    IF NEW.price < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá sản phẩm phải lớn hơn 1000';
    END IF;
END //

-- Kiểm tra trước khi cập nhật giá sản phẩm
CREATE TRIGGER before_product_price_update
    BEFORE UPDATE ON product_price
    FOR EACH ROW
BEGIN
    -- Kiểm tra giá sản phẩm
    IF NEW.price < 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá sản phẩm phải lớn hơn 1000';
    END IF;
END //

-- Kiểm tra trước khi xóa giá sản phẩm
CREATE TRIGGER before_product_price_delete
    BEFORE DELETE ON product_price
    FOR EACH ROW
BEGIN
    DECLARE order_product_exists BOOLEAN;

    SELECT EXISTS(
        SELECT 1 FROM order_product op INNER JOIN `order` o ON op.order_id = o.order_id
        WHERE product_price_id = OLD.product_price_id AND o.status = 'PROCESSING'
    ) INTO order_product_exists;

    -- Kiểm tra xem giá sản phẩm có được sử dụng trong đơn hàng không

    IF order_product_exists THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không thể xóa giá sản phẩm đang được sử dụng trong đơn hàng';
    END IF;
END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm kích thước sản phẩm
CREATE TRIGGER before_product_size_insert
    BEFORE INSERT ON product_size
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên kích thước
    IF LENGTH(TRIM(NEW.name)) = 0 OR LENGTH(TRIM(NEW.name)) > 5 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên kích thước phải có độ dài từ 1 đến 5 ký tự';
    END IF;

    -- Kiểm tra số lượng
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lượng phải lớn hơn 0';
    END IF;
END //

-- Kiểm tra trước khi cập nhật kích thước sản phẩm
CREATE TRIGGER before_product_size_update
    BEFORE UPDATE ON product_size
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên kích thước
    IF LENGTH(TRIM(NEW.name)) = 0 OR LENGTH(TRIM(NEW.name)) > 5 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên kích thước phải có độ dài từ 1 đến 5 ký tự';
    END IF;

    -- Kiểm tra số lượng
    IF NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Số lượng phải lớn hơn 0';
    END IF;
END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm sản phẩm
CREATE TRIGGER before_product_insert
    BEFORE INSERT ON product
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên sản phẩm có trống không
    IF LENGTH(TRIM(NEW.name)) = 0 OR LENGTH(NEW.name) > 100 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên sản phẩm không được để trống và có độ dài tối đa 100 ký tự';
    END IF;

    -- Kiểm tra mô tả
    IF NEW.description IS NOT NULL AND LENGTH(TRIM(NEW.description)) > 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mô tả sản phẩm không được vượt quá 1000 ký tự';
    END IF;

    -- Kiểm tra đường dẫn hình ảnh
    IF NEW.image_path IS NOT NULL AND LENGTH(NEW.image_path) > 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Đường dẫn hình ảnh không được vượt quá 1000 ký tự';
    END IF;

    -- Đặt giá trị mặc định cho is_available
    IF NEW.is_available IS NULL THEN
        SET NEW.is_available = 1;
    ELSEIF NEW.is_available NOT IN (0, 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái sẵn có phải là 0 hoặc 1';
    END IF;

    -- Đặt giá trị mặc định cho is_signature
    IF NEW.is_signature IS NULL THEN
        SET NEW.is_signature = 0;
    ELSEIF NEW.is_signature NOT IN (0, 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái đặc trưng phải là 0 hoặc 1';
    END IF;
END //

-- Kiểm tra trước khi cập nhật sản phẩm
CREATE TRIGGER before_product_update
    BEFORE UPDATE ON product
    FOR EACH ROW
BEGIN
    -- Kiểm tra tên sản phẩm
    IF LENGTH(TRIM(NEW.name)) = 0 OR LENGTH(NEW.name) > 100 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên sản phẩm không được để trống và có độ dài tối đa 100 ký tự';
    END IF;

    -- Kiểm tra mô tả
    IF NEW.description IS NOT NULL AND LENGTH(TRIM(NEW.description)) > 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mô tả sản phẩm không được vượt quá 1000 ký tự';
    END IF;

    -- Kiểm tra đường dẫn hình ảnh
    IF NEW.image_path IS NOT NULL AND LENGTH(NEW.image_path) > 1000 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Đường dẫn hình ảnh không được vượt quá 1000 ký tự';
    END IF;

    -- Kiểm tra is_available
    IF NEW.is_available NOT IN (0, 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái sẵn có phải là 0 hoặc 1';
    END IF;

    -- Kiểm tra is_signature
    IF NEW.is_signature NOT IN (0, 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trạng thái đặc trưng phải là 0 hoặc 1';
    END IF;
END //

-- Kiểm tra trước khi xóa sản phẩm
CREATE TRIGGER before_product_delete
    BEFORE DELETE ON product
    FOR EACH ROW
BEGIN

END //

-- Sau khi thêm sản phẩm
CREATE TRIGGER after_product_insert
    AFTER INSERT ON product
    FOR EACH ROW
BEGIN
    -- Ghi log hoặc thực hiện các hành động sau khi thêm sản phẩm
    -- (Có thể để trống nếu không cần xử lý gì)
END //

-- Sau khi cập nhật sản phẩm
CREATE TRIGGER after_product_update
    AFTER UPDATE ON product
    FOR EACH ROW
BEGIN
    -- Ghi log hoặc thực hiện các hành động sau khi cập nhật sản phẩm
    -- (Có thể để trống nếu không cần xử lý gì)
END //

-- Sau khi xóa sản phẩm
CREATE TRIGGER after_product_delete
    AFTER DELETE ON product
    FOR EACH ROW
BEGIN
    -- Ghi log hoặc thực hiện các hành động sau khi xóa sản phẩm
    -- (Có thể để trống nếu không cần xử lý gì)
END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm vai trò
CREATE TRIGGER before_role_insert
    BEFORE INSERT ON role
    FOR EACH ROW
BEGIN
    IF NEW.name REGEXP '^[a-zA-Z0-9_]{3,20}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên vai trò phải từ 3 đến 20 ký tự và không được chứa ký tự đặc biệt';
    END IF;
END //

-- Kiểm tra trước khi cập nhật vai trò
CREATE TRIGGER before_role_update
    BEFORE UPDATE ON role
    FOR EACH ROW
BEGIN
    IF NEW.name REGEXP '^[a-zA-Z0-9_]{3,20}$' = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên vai trò phải từ 3 đến 20 ký tự và không được chứa ký tự đặc biệt';
    END IF;
END //

-- Kiểm tra trước khi xóa vai trò
CREATE TRIGGER before_role_delete
    BEFORE DELETE ON role
    FOR EACH ROW
BEGIN
END //

DELIMITER ;
-- ----------------------------------------------------------------------
DELIMITER //

-- Kiểm tra trước khi thêm đơn vị tính
CREATE TRIGGER before_unit_of_measure_insert
    BEFORE INSERT ON unit_of_measure
    FOR EACH ROW
BEGIN
    -- Kiểm tra dữ liệu không rỗng nhưng định dạng không đúng
    IF LENGTH(TRIM(NEW.name)) < 1 OR LENGTH(TRIM(NEW.name)) > 30 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đơn vị phải có độ dài từ 1 đến 30 ký tự';
    END IF;

    IF LENGTH(TRIM(NEW.symbol)) < 1 OR LENGTH(TRIM(NEW.symbol)) > 5 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ký hiệu đơn vị phải có độ dài từ 1 đến 5 ký tự';
    END IF;
END //

-- Kiểm tra trước khi cập nhật đơn vị tính
CREATE TRIGGER before_unit_of_measure_update
    BEFORE UPDATE ON unit_of_measure
    FOR EACH ROW
BEGIN
    -- Kiểm tra dữ liệu không rỗng nhưng định dạng không đúng
    IF LENGTH(TRIM(NEW.name)) < 1 OR LENGTH(TRIM(NEW.name)) > 30 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tên đơn vị phải có độ dài từ 1 đến 30 ký tự';
    END IF;

    IF LENGTH(TRIM(NEW.symbol)) < 1 OR LENGTH(TRIM(NEW.symbol)) > 5 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ký hiệu đơn vị phải có độ dài từ 1 đến 5 ký tự';
    END IF;
END //

-- Kiểm tra trước khi xóa đơn vị tính
CREATE TRIGGER before_unit_of_measure_delete
    BEFORE DELETE ON unit_of_measure
    FOR EACH ROW
BEGIN
END //

DELIMITER ;
-- -----------------------------------------------------------------------
