-- SQL Server 2022 Triggers for Milk Tea Shop
-- Converted from MySQL to SQL Server
-- Chuyển đổi từ V18.0.2__init_triggers.sql

USE MilkTeaShop;
GO

-- Set required options for working with filtered indexes
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- =====================================================
-- 1. AREA TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm khu vực
CREATE TRIGGER before_area_insert
ON area
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra tên khu vực (3 ký tự, chữ cái, số, gạch dưới, gạch ngang)
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE LEN(LTRIM(RTRIM(name))) != 3 
        OR name NOT LIKE '[a-zA-Z0-9_-][a-zA-Z0-9_-][a-zA-Z0-9_-]'
    )
    BEGIN
        THROW 50001, N'Tên khu vực chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài 3 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra số bàn tối đa phải lớn hơn 0
    IF EXISTS (SELECT 1 FROM inserted WHERE max_tables IS NOT NULL AND max_tables <= 0)
    BEGIN
        THROW 50002, N'Số bàn tối đa phải lớn hơn 0', 1;
        RETURN;
    END
    
    INSERT INTO area (name, max_tables, is_active, description, created_at, updated_at)
    SELECT name, max_tables, is_active, description, GETDATE(), GETDATE() FROM inserted;
END
GO

-- Trigger kiểm tra trước khi cập nhật khu vực
CREATE TRIGGER before_area_update
ON area
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra tên khu vực không được để trống
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(LTRIM(RTRIM(name))) = 0)
    BEGIN
        THROW 50003, N'Tên khu vực không được để trống', 1;
        RETURN;
    END
    
    -- Kiểm tra tên khu vực
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE LEN(LTRIM(RTRIM(name))) != 3 
        OR name NOT LIKE '[a-zA-Z0-9_-][a-zA-Z0-9_-][a-zA-Z0-9_-]'
    )
    BEGIN
        THROW 50004, N'Tên khu vực chỉ được chứa chữ cái, số, dấu gạch dưới, dấu gạch ngang và có độ dài 3 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra số bàn tối đa
    IF EXISTS (SELECT 1 FROM inserted WHERE max_tables IS NOT NULL AND (max_tables <= 0 OR max_tables >= 100))
    BEGIN
        THROW 50005, N'Số bàn tối đa phải lớn hơn 0 và nhỏ hơn 100', 1;
        RETURN;
    END
    
    -- Kiểm tra số bàn tối đa >= số bàn hiện có
    IF EXISTS (
        SELECT 1 FROM inserted i
        WHERE i.max_tables IS NOT NULL 
        AND i.max_tables < (SELECT COUNT(*) FROM service_table WHERE area_id = i.area_id)
    )
    BEGIN
        THROW 50006, N'Số bàn tối đa phải lớn hơn hoặc bằng số bàn hiện có', 1;
        RETURN;
    END
    
    UPDATE a SET 
        name = i.name,
        max_tables = i.max_tables,
        is_active = i.is_active,
        description = i.description,
        updated_at = GETDATE()
    FROM area a
    INNER JOIN inserted i ON a.area_id = i.area_id;
END
GO

-- Trigger xóa khu vực với cascade delete
CREATE TRIGGER before_area_delete
ON area
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Set null tất cả bàn trong khu vực trước
    UPDATE service_table 
    SET area_id = NULL, updated_at = GETDATE()
    WHERE area_id IN (SELECT area_id FROM deleted);
    
    -- Sau đó xóa khu vực
    DELETE FROM area 
    WHERE area_id IN (SELECT area_id FROM deleted);
END
GO

-- =====================================================
-- 2. CATEGORY TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm danh mục
CREATE TRIGGER before_category_insert
ON category
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra tên danh mục
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(LTRIM(RTRIM(name))) = 0 OR LEN(name) > 100)
    BEGIN
        THROW 50007, N'Tên danh mục không được để trống và có độ dài tối đa 100 ký tự', 1;
        RETURN;
    END
    
    INSERT INTO category (name, description, created_at, updated_at)
    SELECT name, description, GETDATE(), GETDATE() FROM inserted;
END
GO

-- Trigger kiểm tra trước khi cập nhật danh mục
CREATE TRIGGER before_category_update
ON category
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra tên danh mục
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(LTRIM(RTRIM(name))) = 0 OR LEN(name) > 100)
    BEGIN
        THROW 50008, N'Tên danh mục không được để trống và có độ dài tối đa 100 ký tự', 1;
        RETURN;
    END

    -- Bảo vệ danh mục mặc định
    IF EXISTS (
        SELECT 1 
        FROM deleted d
        INNER JOIN inserted i ON d.category_id = i.category_id
        WHERE d.name IN (N'TOPPING', N'TOPPING BÁN LẺ')
        AND d.name != i.name
    )
    BEGIN
        THROW 50064, N'Không thể thay đổi tên danh mục mặc định', 1;
        RETURN;
    END
    
    UPDATE c SET 
        name = i.name,
        description = i.description,
        updated_at = GETDATE()
    FROM category c
    INNER JOIN inserted i ON c.category_id = i.category_id;
END
GO

CREATE TRIGGER before_category_delete
ON category
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (SELECT 1 FROM deleted WHERE name IN (N'TOPPING', N'TOPPING BÁN LẺ'))
    BEGIN
        THROW 50065, N'Không thể xóa danh mục mặc định', 1;
        RETURN;
    END
    
    -- Set null category_id của tất cả sản phẩm trong danh mục trước
    UPDATE product 
    SET category_id = NULL, updated_at = GETDATE()
    WHERE category_id IN (
        SELECT category_id FROM deleted
    );
    
    -- Sau đó xóa danh mục
    DELETE FROM category 
    WHERE category_id IN (
        SELECT category_id FROM deleted 
        WHERE name NOT IN (N'TOPPING', N'TOPPING BÁN LẺ')
    );
END
GO

-- =====================================================
-- 3. SERVICE TABLE TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm bàn
CREATE TRIGGER before_service_table_insert
ON service_table
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @max_tables INT, @current_tables INT;
    
    -- Kiểm tra số bàn
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(LTRIM(RTRIM(table_number))) > 10 OR LEN(LTRIM(RTRIM(table_number))) < 3)
    BEGIN
        THROW 50009, N'Số bàn phải từ 3 đến 10 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra giới hạn số bàn trong khu vực
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN area a ON i.area_id = a.area_id
        WHERE a.max_tables IS NOT NULL
        AND (SELECT COUNT(*) FROM service_table WHERE area_id = i.area_id) >= a.max_tables
    )
    BEGIN
        THROW 50010, N'Số lượng bàn đã đạt giới hạn tối đa của khu vực', 1;
        RETURN;
    END

    -- Insert với trạng thái theo khu vực
    INSERT INTO service_table (area_id, table_number, is_active, created_at, updated_at)
    SELECT i.area_id, i.table_number, 
           CASE WHEN a.is_active = 1 THEN i.is_active ELSE 0 END, -- Trạng thái bàn theo khu vực
           GETDATE(), GETDATE() 
    FROM inserted i
    INNER JOIN area a ON i.area_id = a.area_id;
END
GO

-- Trigger kiểm tra trước khi cập nhật bàn
CREATE TRIGGER before_service_table_update
ON service_table
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra số bàn
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(LTRIM(RTRIM(table_number))) < 3 OR LEN(LTRIM(RTRIM(table_number))) > 50)
    BEGIN
        THROW 50011, N'Số bàn phải từ 3 đến 50 ký tự', 1;
        RETURN;
    END
    
    -- Không cho phép vô hiệu hóa bàn đang được sử dụng
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN deleted d ON i.table_id = d.table_id
        WHERE d.is_active = 1 AND i.is_active = 0
        AND EXISTS (
            SELECT 1 FROM order_table ot
            INNER JOIN [order] o ON ot.order_id = o.order_id
            WHERE ot.table_id = i.table_id
            AND (o.status = 'PROCESSING' OR ot.check_out IS NULL)
        )
    )
    BEGIN
        THROW 50012, N'Không thể vô hiệu hóa bàn đang được sử dụng', 1;
        RETURN;
    END

    -- Không cho phép bật bàn khi khu vực đang không hoạt động
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN deleted d ON i.table_id = d.table_id
        INNER JOIN area a ON i.area_id = a.area_id
        WHERE d.is_active = 0 AND i.is_active = 1 AND a.is_active = 0
    )
    BEGIN
        THROW 50013, N'Không thể bật bàn khi khu vực đang không hoạt động', 1;
        RETURN;
    END
    
    UPDATE st SET 
        area_id = i.area_id,
        table_number = i.table_number,
        is_active = CASE 
            WHEN i.is_active = 1 AND a.is_active = 0 THEN 0  -- Không cho bật bàn khi khu vực tắt
            ELSE i.is_active 
        END,
        updated_at = GETDATE()
    FROM service_table st
    INNER JOIN inserted i ON st.table_id = i.table_id
    INNER JOIN area a ON i.area_id = a.area_id;
END
GO

CREATE TRIGGER before_service_table_delete
ON service_table
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1 
        FROM deleted d
        JOIN order_table ot ON d.table_id = ot.table_id
        JOIN [order] o ON ot.order_id = o.order_id
        WHERE o.status = 'PROCESSING' OR ot.check_out IS NULL
    )
    BEGIN
        THROW 50012, N'Không thể xóa bàn đang được sử dụng', 1;
        RETURN;
    END

    DELETE FROM service_table WHERE table_id IN (SELECT table_id FROM deleted);
END
GO

-- =====================================================
-- 4. COUPON TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm mã giảm giá
CREATE TRIGGER before_coupon_insert
ON coupon
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra mã giảm giá (3-15 ký tự, chữ và số)
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE LEN(LTRIM(RTRIM(coupon))) < 3 OR LEN(coupon) > 15
        OR coupon LIKE '%[^a-zA-Z0-9]%'
    )
    BEGIN
        THROW 50013, N'Mã giảm giá phải có độ dài từ 3 đến 15 ký tự và chỉ được chứa chữ cái và số', 1;
        RETURN;
    END
    
    INSERT INTO coupon (coupon, description, created_at, updated_at)
    SELECT UPPER(coupon), description, GETDATE(), GETDATE() FROM inserted;
END
GO

-- Trigger kiểm tra trước khi cập nhật mã giảm giá
CREATE TRIGGER before_coupon_update
ON coupon
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra mã giảm giá
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE LEN(LTRIM(RTRIM(coupon))) < 3 OR LEN(coupon) > 15
        OR coupon LIKE '%[^a-zA-Z0-9]%'
    )
    BEGIN
        THROW 50014, N'Mã giảm giá phải có độ dài từ 3 đến 15 ký tự và chỉ được chứa chữ cái và số', 1;
        RETURN;
    END
    
    UPDATE c SET 
        coupon = UPPER(i.coupon),
        description = i.description,
        updated_at = GETDATE()
    FROM coupon c
    INNER JOIN inserted i ON c.coupon_id = i.coupon_id;
END
GO

-- Trigger kiểm tra trước khi xóa mã giảm giá
CREATE TRIGGER before_coupon_delete
ON coupon
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra coupon có đang được sử dụng không
    IF EXISTS (
        SELECT 1 FROM deleted d
        WHERE EXISTS (SELECT 1 FROM discount WHERE coupon_id = d.coupon_id)
    )
    BEGIN
        THROW 50015, N'Mã coupon đang được sử dụng trong chương trình giảm giá, hãy xóa chương trình giảm giá trước', 1;
        RETURN;
    END
    
    -- Sau đó xóa coupon
    DELETE FROM coupon 
    WHERE coupon_id IN (SELECT coupon_id FROM deleted);
END
GO

-- =====================================================
-- 5. CUSTOMER TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm khách hàng
CREATE TRIGGER before_customer_insert
ON customer
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra họ
    IF EXISTS (SELECT 1 FROM inserted WHERE last_name IS NOT NULL AND LEN(last_name) > 70)
    BEGIN
        THROW 50016, N'Họ không được vượt quá 70 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra tên
    IF EXISTS (SELECT 1 FROM inserted WHERE first_name IS NOT NULL AND LEN(first_name) > 70)
    BEGIN
        THROW 50017, N'Tên không được vượt quá 70 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra số điện thoại Việt Nam (10 chữ số bắt đầu 03,05,07,08,09 hoặc +84 với 9 chữ số)
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE phone NOT LIKE '0[35789][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'
        AND phone NOT LIKE '+84[35789][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'
    )
    BEGIN
        THROW 50018, N'Số điện thoại không hợp lệ', 1;
        RETURN;
    END
    
    -- Kiểm tra email
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE email IS NOT NULL 
        AND (email NOT LIKE '%@%.%' OR email LIKE '%@%@%' OR email LIKE '.%' OR email LIKE '%.')
    )
    BEGIN
        THROW 50019, N'Email không hợp lệ', 1;
        RETURN;
    END
    
    -- Kiểm tra điểm hiện tại
    IF EXISTS (SELECT 1 FROM inserted WHERE current_points < 0)
    BEGIN
        THROW 50020, N'Điểm hiện tại không được âm', 1;
        RETURN;
    END
    
    INSERT INTO customer (account_id, membership_type_id,  last_name, first_name, phone, email, current_points, created_at, updated_at)
    SELECT account_id, membership_type_id, last_name, first_name, phone, email, current_points, GETDATE(), GETDATE() FROM inserted;
END
GO

-- Trigger kiểm tra trước khi cập nhật khách hàng
CREATE TRIGGER before_customer_update
ON customer
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra họ
    IF EXISTS (SELECT 1 FROM inserted WHERE last_name IS NOT NULL AND LEN(last_name) > 70)
    BEGIN
        THROW 50021, N'Họ không được vượt quá 70 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra tên
    IF EXISTS (SELECT 1 FROM inserted WHERE first_name IS NOT NULL AND LEN(first_name) > 70)
    BEGIN
        THROW 50022, N'Tên không được vượt quá 70 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra số điện thoại
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE phone NOT LIKE '0[35789][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'
        AND phone NOT LIKE '+84[35789][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'
    )
    BEGIN
        THROW 50023, N'Số điện thoại không hợp lệ', 1;
        RETURN;
    END
    
    -- Kiểm tra email
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE email IS NOT NULL 
        AND (email NOT LIKE '%@%.%' OR email LIKE '%@%@%' OR email LIKE '.%' OR email LIKE '%.')
    )
    BEGIN
        THROW 50024, N'Email không hợp lệ', 1;
        RETURN;
    END
    
    -- Kiểm tra điểm hiện tại
    IF EXISTS (SELECT 1 FROM inserted WHERE current_points < 0)
    BEGIN
        THROW 50025, N'Điểm hiện tại không được âm', 1;
        RETURN;
    END
    
    UPDATE c SET 
        account_id = i.account_id,
        last_name = i.last_name,
        first_name = i.first_name,
        phone = i.phone,
        email = i.email,
        current_points = i.current_points,
        updated_at = GETDATE()
    FROM customer c
    INNER JOIN inserted i ON c.customer_id = i.customer_id;
END
GO

-- Trigger kiểm tra trước khi xóa khách hàng
CREATE TRIGGER before_customer_delete
ON customer
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra khách hàng có đơn hàng đang xử lý không
    IF EXISTS (
        SELECT 1 FROM deleted d
        WHERE EXISTS (
            SELECT 1 FROM [order] o
            INNER JOIN order_table ot ON o.order_id = ot.order_id
            WHERE o.customer_id = d.customer_id 
            AND (o.status = 'PROCESSING' OR ot.check_out IS NULL)
        )
    )
    BEGIN
        THROW 50026, N'Không thể xóa khách hàng đã có đơn hàng đang chờ xử lý', 1;
        RETURN;
    END
    
    DELETE FROM customer WHERE customer_id IN (SELECT customer_id FROM deleted);
END
GO

-- Trigger sau khi xóa khách hàng
CREATE TRIGGER after_customer_delete
ON customer
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Đặt customer_id = NULL trong order khi xóa khách hàng
    UPDATE [order] 
    SET customer_id = NULL, updated_at = GETDATE()
    WHERE customer_id IN (SELECT customer_id FROM deleted);
    
    -- Xóa tài khoản liên kết
    DELETE FROM account 
    WHERE account_id IN (SELECT account_id FROM deleted WHERE account_id IS NOT NULL);
END
GO

-- =====================================================
-- 6. DISCOUNT TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm discount
CREATE TRIGGER before_discount_insert
ON discount
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra giá trị đơn hàng tối thiểu
    IF EXISTS (SELECT 1 FROM inserted WHERE min_required_order_value < 1000)
    BEGIN
        THROW 50027, N'Giá trị đơn hàng tối thiểu phải lớn hơn hoặc bằng 1000', 1;
        RETURN;
    END
    
    -- Kiểm tra số tiền giảm giá tối đa
    IF EXISTS (SELECT 1 FROM inserted WHERE max_discount_amount < 1000)
    BEGIN
        THROW 50028, N'Số tiền giảm giá tối đa phải lớn hơn hoặc bằng 1000', 1;
        RETURN;
    END
    
    -- Kiểm tra giá trị giảm giá theo loại
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE discount_type = 'PERCENTAGE' AND (discount_value <= 0 OR discount_value > 100)
    )
    BEGIN
        THROW 50029, N'Giá trị giảm giá theo phần trăm phải lớn hơn 0 và nhỏ hơn hoặc bằng 100', 1;
        RETURN;
    END
    
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE discount_type = 'FIXED' AND discount_value < 1000
    )
    BEGIN
        THROW 50030, N'Giá trị giảm giá cố định phải lớn hơn hoặc bằng 1000', 1;
        RETURN;
    END
    
    -- Kiểm tra thời gian hiệu lực
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE valid_from IS NOT NULL AND valid_until IS NOT NULL AND valid_from >= valid_until
    )
    BEGIN
        THROW 50031, N'Thời gian bắt đầu phải trước thời gian kết thúc', 1;
        RETURN;
    END
    
    -- Kiểm tra số lần sử dụng tối đa
    IF EXISTS (SELECT 1 FROM inserted WHERE max_uses IS NOT NULL AND max_uses <= 0)
    BEGIN
        THROW 50032, N'Số lần sử dụng tối đa phải lớn hơn 0', 1;
        RETURN;
    END
    
    -- Kiểm tra giới hạn giảm giá cố định
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE discount_type = 'FIXED' AND discount_value > max_discount_amount
    )
    BEGIN
        THROW 50033, N'Giá trị giảm giá cố định không được lớn hơn số tiền giảm giá tối đa', 1;
        RETURN;
    END
    
    INSERT INTO discount (coupon_id, name, discount_type, discount_value, min_required_order_value, max_discount_amount,
                         min_required_product, valid_from, valid_until, max_uses, max_uses_per_customer, 
                         current_uses, is_active, description, created_at, updated_at)
    SELECT coupon_id, name, discount_type, discount_value, min_required_order_value, max_discount_amount,
           min_required_product, valid_from, valid_until, max_uses, max_uses_per_customer,
           ISNULL(current_uses, 0), is_active, description, GETDATE(), GETDATE() FROM inserted;
END
GO

CREATE TRIGGER before_discount_update
ON discount
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Các kiểm tra validation tương tự như khi insert
    IF EXISTS (SELECT 1 FROM inserted WHERE min_required_order_value < 1000)
    BEGIN
        THROW 50027, N'Giá trị đơn hàng tối thiểu phải lớn hơn hoặc bằng 1000', 1;
        RETURN;
    END
    
    IF EXISTS (SELECT 1 FROM inserted WHERE max_discount_amount < 1000)
    BEGIN
        THROW 50028, N'Số tiền giảm giá tối đa phải lớn hơn hoặc bằng 1000', 1;
        RETURN;
    END
    
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE discount_type = 'PERCENTAGE' AND (discount_value <= 0 OR discount_value > 100)
    )
    BEGIN
        THROW 50029, N'Giá trị giảm giá theo phần trăm phải lớn hơn 0 và nhỏ hơn hoặc bằng 100', 1;
        RETURN;
    END
    
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE discount_type = 'FIXED' AND discount_value < 1000
    )
    BEGIN
        THROW 50030, N'Giá trị giảm giá cố định phải lớn hơn hoặc bằng 1000', 1;
        RETURN;
    END
    
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE valid_from IS NOT NULL AND valid_until IS NOT NULL AND valid_from >= valid_until
    )
    BEGIN
        THROW 50031, N'Thời gian bắt đầu phải trước thời gian kết thúc', 1;
        RETURN;
    END
    
    IF EXISTS (SELECT 1 FROM inserted WHERE max_uses IS NOT NULL AND max_uses <= 0)
    BEGIN
        THROW 50032, N'Số lần sử dụng tối đa phải lớn hơn 0', 1;
        RETURN;
    END
    
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE discount_type = 'FIXED' AND discount_value > max_discount_amount
    )
    BEGIN
        THROW 50033, N'Giá trị giảm giá cố định không được lớn hơn số tiền giảm giá tối đa', 1;
        RETURN;
    END

    UPDATE d
    SET coupon_id = i.coupon_id, 
        discount_type = i.discount_type, 
        discount_value = i.discount_value, 
        min_required_order_value = i.min_required_order_value, 
        max_discount_amount = i.max_discount_amount, 
        min_required_product = i.min_required_product, 
        valid_from = i.valid_from, 
        valid_until = i.valid_until, 
        max_uses = i.max_uses, 
        max_uses_per_customer = i.max_uses_per_customer, 
        current_uses = i.current_uses, 
        is_active = i.is_active, 
        description = i.description, 
        updated_at = GETDATE()
    FROM discount d
    INNER JOIN inserted i ON d.discount_id = i.discount_id;
END
GO

CREATE TRIGGER before_discount_delete
ON discount
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (
        SELECT 1 
        FROM deleted d
        JOIN order_discount od ON d.discount_id = od.discount_id
        JOIN [order] o ON od.order_id = o.order_id
        WHERE o.status = 'PROCESSING'
    )
    BEGIN
        THROW 50033, N'Không thể xóa chương trình giảm giá đang được sử dụng trong đơn hàng đang chờ xử lý', 1;
        RETURN;
    END

    DELETE FROM discount WHERE discount_id IN (SELECT discount_id FROM deleted);
END
GO

-- =====================================================
-- 7. EMPLOYEE TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm nhân viên
CREATE TRIGGER before_employee_insert
ON employee
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra họ
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(last_name) > 70)
    BEGIN
        THROW 50034, N'Họ không được vượt quá 70 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra tên
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(first_name) > 70)
    BEGIN
        THROW 50035, N'Tên không được vượt quá 70 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra số điện thoại
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE phone NOT LIKE '0[35789][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'
        AND phone NOT LIKE '+84[35789][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'
    )
    BEGIN
        THROW 50036, N'Số điện thoại không hợp lệ', 1;
        RETURN;
    END
    
    -- Kiểm tra email
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE email IS NOT NULL 
        AND (email NOT LIKE '%@%.%' OR email LIKE '%@%@%' OR email LIKE '.%' OR email LIKE '%.')
    )
    BEGIN
        THROW 50037, N'Email không hợp lệ', 1;
        RETURN;
    END
    
    INSERT INTO employee (account_id, position, last_name, first_name, phone, email, created_at, updated_at)
    SELECT account_id, position, last_name, first_name, phone, email, GETDATE(), GETDATE() FROM inserted;
END
GO

-- Trigger kiểm tra trước khi xóa nhân viên
CREATE TRIGGER before_employee_delete
ON employee
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra nhân viên có đơn hàng đang xử lý không
    IF EXISTS (
        SELECT 1 FROM deleted d
        WHERE EXISTS (
            SELECT 1 FROM [order] WHERE employee_id = d.employee_id AND status = 'PROCESSING'
        )
    )
    BEGIN
        THROW 50038, N'Không thể xóa nhân viên đang có đơn hàng đang chờ xử lý', 1;
        RETURN;
    END
    
    -- Set null employee_id trong order trước khi xóa nhân viên
    UPDATE [order]
    SET employee_id = NULL, updated_at = GETDATE()
    WHERE employee_id IN (SELECT employee_id FROM deleted);
    
    DELETE FROM employee WHERE employee_id IN (SELECT employee_id FROM deleted);
END
GO

-- Trigger sau khi xóa nhân viên
CREATE TRIGGER after_employee_delete
ON employee
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Đặt employee_id = NULL trong order khi xóa nhân viên
    UPDATE [order] 
    SET employee_id = NULL, updated_at = GETDATE()
    WHERE employee_id IN (SELECT employee_id FROM deleted);
    
    -- Xóa tài khoản liên kết
    DELETE FROM account 
    WHERE account_id IN (SELECT account_id FROM deleted WHERE account_id IS NOT NULL);
END
GO

-- =====================================================
-- 8. ORDER TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm đơn hàng
CREATE TRIGGER before_order_insert
ON [order]
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra trạng thái
    IF EXISTS (SELECT 1 FROM inserted WHERE status IS NULL)
    BEGIN
        THROW 50039, N'Trạng thái đơn hàng không được để trống', 1;
        RETURN;
    END
    
    -- Kiểm tra tổng tiền
    IF EXISTS (SELECT 1 FROM inserted WHERE total_amount IS NOT NULL AND total_amount < 1000)
    BEGIN
        THROW 50040, N'Tổng tiền phải lớn hơn hoặc bằng 1000', 1;
        RETURN;
    END
    
    -- Kiểm tra thành tiền
    IF EXISTS (SELECT 1 FROM inserted WHERE final_amount IS NOT NULL AND final_amount < 1000)
    BEGIN
        THROW 50041, N'Thành tiền phải lớn hơn hoặc bằng 1000', 1;
        RETURN;
    END
    
    -- Kiểm tra điểm
    IF EXISTS (SELECT 1 FROM inserted WHERE point IS NOT NULL AND point < 0)
    BEGIN
        THROW 50042, N'Điểm không được âm', 1;
        RETURN;
    END
    
    INSERT INTO [order] (customer_id, employee_id, status, total_amount, final_amount, point, customize_note, created_at, updated_at)
    SELECT customer_id, employee_id, status, total_amount, final_amount, point, customize_note, GETDATE(), GETDATE() FROM inserted;
END
GO

-- Trigger kiểm tra trước khi cập nhật đơn hàng
CREATE TRIGGER before_order_update
ON [order]
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Không cho phép thay đổi trạng thái từ COMPLETED/CANCELLED
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN deleted d ON i.order_id = d.order_id
        WHERE (d.status = 'COMPLETED' OR d.status = 'CANCELLED') AND i.status != d.status
    )
    BEGIN
        THROW 50043, N'Không thể thay đổi trạng thái của đơn hàng đã hoàn thành hoặc đã hủy', 1;
        RETURN;
    END
    
    -- Kiểm tra logic nghiệp vụ khi chuyển sang COMPLETED
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN deleted d ON i.order_id = d.order_id
        WHERE i.status = 'COMPLETED' AND d.status = 'PROCESSING'
        AND (i.final_amount IS NULL OR i.final_amount < 1000)
    )
    BEGIN
        THROW 50044, N'Không thể hoàn thành đơn hàng: thành tiền phải lớn hơn hoặc bằng 1000', 1;
        RETURN;
    END
    
    -- Kiểm tra có payment khi hoàn thành đơn hàng
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN deleted d ON i.order_id = d.order_id
        WHERE i.status = 'COMPLETED' AND d.status = 'PROCESSING'
        AND NOT EXISTS (SELECT 1 FROM payment WHERE order_id = i.order_id AND status = 'PAID')
    )
    BEGIN
        THROW 50045, N'Không thể hoàn thành đơn hàng: chưa có thanh toán', 1;
        RETURN;
    END
    
    UPDATE o SET 
        customer_id = i.customer_id,
        employee_id = i.employee_id,
        status = i.status,
        total_amount = i.total_amount,
        final_amount = i.final_amount,
        point = i.point,
        customize_note = i.customize_note,
        updated_at = GETDATE()
    FROM [order] o
    INNER JOIN inserted i ON o.order_id = i.order_id;
END
GO

-- Trigger xóa đơn hàng với cascade delete
CREATE TRIGGER before_order_delete
ON [order]
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Xóa tất cả sản phẩm trong đơn hàng
    DELETE FROM order_product 
    WHERE order_id IN (SELECT order_id FROM deleted);
    
    -- Xóa tất cả giảm giá của đơn hàng
    DELETE FROM order_discount 
    WHERE order_id IN (SELECT order_id FROM deleted);
    
    -- Xóa tất cả bàn của đơn hàng
    DELETE FROM order_table 
    WHERE order_id IN (SELECT order_id FROM deleted);
    
    -- Xóa tất cả thanh toán của đơn hàng
    DELETE FROM payment 
    WHERE order_id IN (SELECT order_id FROM deleted);
    
    -- Sau đó xóa đơn hàng
    DELETE FROM [order] 
    WHERE order_id IN (SELECT order_id FROM deleted);
END
GO

-- =====================================================
-- 9. ORDER PRODUCT TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm sản phẩm vào đơn hàng
CREATE TRIGGER before_order_product_insert
ON order_product
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra đơn hàng phải ở trạng thái PROCESSING
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN [order] o ON i.order_id = o.order_id
        WHERE o.status != 'PROCESSING'
    )
    BEGIN
        THROW 50046, N'Không thể thêm sản phẩm vào đơn hàng đã hoàn thành hoặc đã hủy', 1;
        RETURN;
    END
    
    -- Kiểm tra số lượng
    IF EXISTS (SELECT 1 FROM inserted WHERE quantity <= 0)
    BEGIN
        THROW 50047, N'Số lượng sản phẩm phải lớn hơn 0', 1;
        RETURN;
    END
    
    -- Kiểm tra product_price_id có tồn tại và active không
    IF EXISTS (
        SELECT 1 FROM inserted i
        LEFT JOIN product_price pp ON i.product_price_id = pp.product_price_id
        LEFT JOIN product p ON pp.product_id = p.product_id
        WHERE pp.product_price_id IS NULL OR p.is_available = 0
    )
    BEGIN
        THROW 50048, N'Sản phẩm không tồn tại hoặc không khả dụng', 1;
        RETURN;
    END
    
    -- Logic merge sản phẩm trùng - không có constraint unique, trigger tự xử lý
    -- Xử lý từng record trong inserted
    DECLARE @order_id INT, @product_price_id INT, @quantity SMALLINT, @option NVARCHAR(500);
    DECLARE @existing_quantity SMALLINT, @new_total_quantity SMALLINT;
    
    DECLARE cur CURSOR FOR
    SELECT order_id, product_price_id, quantity, [option] FROM inserted;
    
    OPEN cur;
    FETCH NEXT FROM cur INTO @order_id, @product_price_id, @quantity, @option;
    
    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- Kiểm tra sản phẩm đã tồn tại trong đơn hàng chưa (với cùng option)
        SELECT @existing_quantity = quantity
        FROM order_product 
        WHERE order_id = @order_id 
        AND product_price_id = @product_price_id
        AND (([option] IS NULL AND @option IS NULL) OR [option] = @option);
        
        IF @existing_quantity IS NOT NULL
        BEGIN
            -- Nếu đã tồn tại, cập nhật số lượng
            SET @new_total_quantity = @existing_quantity + @quantity;
            
            UPDATE order_product 
            SET quantity = @new_total_quantity,
                updated_at = GETDATE()
            WHERE order_id = @order_id 
            AND product_price_id = @product_price_id
            AND (([option] IS NULL AND @option IS NULL) OR [option] = @option);
        END
        ELSE
        BEGIN
            -- Nếu chưa tồn tại, thêm mới
            INSERT INTO order_product (order_id, product_price_id, quantity, [option], created_at, updated_at)
            VALUES (@order_id, @product_price_id, @quantity, @option, GETDATE(), GETDATE());
        END
        
        FETCH NEXT FROM cur INTO @order_id, @product_price_id, @quantity, @option;
    END
    
    CLOSE cur;
    DEALLOCATE cur;
END
GO

-- Trigger kiểm tra trước khi cập nhật sản phẩm trong đơn hàng
CREATE TRIGGER before_order_product_update
ON order_product
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra đơn hàng phải ở trạng thái PROCESSING
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN [order] o ON i.order_id = o.order_id
        WHERE o.status != 'PROCESSING'
    )
    BEGIN
        THROW 50049, N'Không thể sửa sản phẩm trong đơn hàng đã hoàn thành hoặc đã hủy', 1;
        RETURN;
    END
    
    -- Kiểm tra số lượng
    IF EXISTS (SELECT 1 FROM inserted WHERE quantity <= 0)
    BEGIN
        THROW 50050, N'Số lượng sản phẩm phải lớn hơn 0', 1;
        RETURN;
    END
    
    UPDATE op SET 
        product_price_id = i.product_price_id,
        quantity = i.quantity,
        [option] = i.[option],
        updated_at = GETDATE()
    FROM order_product op
    INNER JOIN inserted i ON op.order_product_id = i.order_product_id;
END
GO

-- Trigger kiểm tra trước khi xóa sản phẩm khỏi đơn hàng
CREATE TRIGGER before_order_product_delete
ON order_product
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra đơn hàng phải ở trạng thái PROCESSING
    IF EXISTS (
        SELECT 1 FROM deleted d
        INNER JOIN [order] o ON d.order_id = o.order_id
        WHERE o.status != 'PROCESSING'
    )
    BEGIN
        THROW 50051, N'Không thể xóa sản phẩm khỏi đơn hàng đã hoàn thành hoặc đã hủy', 1;
        RETURN;
    END
    
    DELETE FROM order_product
    WHERE order_product_id IN (SELECT order_product_id FROM deleted);
END
GO

-- =====================================================
-- 10. ORDER TABLE TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi đặt bàn
CREATE TRIGGER before_order_table_insert
ON order_table
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra đơn hàng phải ở trạng thái PROCESSING
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN [order] o ON i.order_id = o.order_id
        WHERE o.status != 'PROCESSING'
    )
    BEGIN
        THROW 50056, N'Không thể đặt bàn cho đơn hàng không ở trạng thái processing', 1;
        RETURN;
    END
    
    -- Kiểm tra bàn phải hoạt động
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN service_table st ON i.table_id = st.table_id
        WHERE st.is_active = 0
    )
    BEGIN
        THROW 50057, N'Không thể đặt bàn cho bàn không hoạt động', 1;
        RETURN;
    END
    
    -- Kiểm tra bàn có đang được sử dụng không
    IF EXISTS (
        SELECT 1 FROM inserted i
        WHERE EXISTS (
            SELECT 1 FROM order_table ot
            INNER JOIN [order] o ON ot.order_id = o.order_id
            WHERE ot.table_id = i.table_id 
            AND o.status = 'PROCESSING'
            AND ot.check_out IS NULL
        )
    )
    BEGIN
        THROW 50058, N'Bàn đang được sử dụng bởi đơn hàng khác', 1;
        RETURN;
    END
    
    -- Kiểm tra thời gian checkout
    IF EXISTS (SELECT 1 FROM inserted WHERE check_out IS NOT NULL AND check_out <= check_in)
    BEGIN
        THROW 50059, N'Thời gian checkout phải sau thời gian checkin', 1;
        RETURN;
    END
    
    INSERT INTO order_table (order_id, table_id, check_in, check_out, created_at, updated_at)
    SELECT order_id, table_id, check_in, check_out, GETDATE(), GETDATE() FROM inserted;
END
GO

-- Trigger kiểm tra trước khi cập nhật order_table (chủ yếu cho checkout)
CREATE TRIGGER before_order_table_update
ON order_table
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra thời gian checkout hợp lệ
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN deleted d ON i.order_table_id = d.order_table_id
        WHERE i.check_out IS NOT NULL 
        AND (i.check_out <= i.check_in OR i.check_out <= d.check_in)
    )
    BEGIN
        THROW 50060, N'Thời gian checkout phải sau thời gian checkin', 1;
        RETURN;
    END
    
    UPDATE ot SET 
        table_id = i.table_id,
        check_in = i.check_in,
        check_out = i.check_out,
        updated_at = GETDATE()
    FROM order_table ot
    INNER JOIN inserted i ON ot.order_table_id = i.order_table_id;
END
GO

-- =====================================================
-- 11. PAYMENT TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm thanh toán
CREATE TRIGGER before_payment_insert
ON payment
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra đơn hàng phải tồn tại và ở trạng thái PROCESSING
    IF EXISTS (
        SELECT 1 FROM inserted i
        LEFT JOIN [order] o ON i.order_id = o.order_id
        WHERE o.order_id IS NULL OR o.status != 'PROCESSING'
    )
    BEGIN
        THROW 50061, N'Đơn hàng không tồn tại hoặc không ở trạng thái xử lý', 1;
        RETURN;
    END
    
    -- Với trạng thái PAID thì cần kiểm tra final_amount
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN [order] o ON i.order_id = o.order_id
        WHERE i.status = 'PAID' AND (o.final_amount IS NULL OR o.final_amount < 1000)
    )
    BEGIN
        THROW 50062, N'Đơn hàng chưa có thành tiền hợp lệ để thanh toán', 1;
        RETURN;
    END
    
    -- Kiểm tra trạng thái hợp lệ (chỉ 3 trạng thái theo schema)
    IF EXISTS (SELECT 1 FROM inserted WHERE status IS NULL OR status NOT IN ('PROCESSING', 'CANCELLED', 'PAID'))
    BEGIN
        THROW 50063, N'Trạng thái thanh toán không hợp lệ. Cho phép: PROCESSING, CANCELLED, PAID', 1;
        RETURN;
    END
    
    -- Kiểm tra số tiền đã trả (chỉ kiểm tra khi status = 'PAID')
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE status = 'PAID' AND (amount_paid IS NULL OR amount_paid < 0)
    )
    BEGIN
        THROW 50064, N'Khi thanh toán (PAID), số tiền phải được nhập và không âm', 1;
        RETURN;
    END
    
    -- Tự động tính tiền thừa khi thanh toán PAID
    INSERT INTO payment (order_id, payment_method_id, amount_paid, change_amount, status, payment_time, created_at, updated_at)
    SELECT 
        i.order_id, 
        i.payment_method_id, 
        CASE 
            WHEN i.status = 'PAID' THEN i.amount_paid
            ELSE NULL -- Các trạng thái khác không cần amount_paid
        END,
        CASE 
            WHEN i.status = 'PAID' AND i.amount_paid > o.final_amount 
            THEN i.amount_paid - o.final_amount 
            ELSE 0
        END,
        i.status,
        CASE 
            WHEN i.status = 'PAID' THEN ISNULL(i.payment_time, GETDATE())
            ELSE NULL -- Các trạng thái khác chưa có payment_time
        END,
        GETDATE(), 
        GETDATE() 
    FROM inserted i
    INNER JOIN [order] o ON i.order_id = o.order_id;
END
GO

-- Trigger cập nhật payment 
CREATE TRIGGER before_payment_update
ON payment
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Không cho phép thay đổi payment đã PAID thành trạng thái khác
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN deleted d ON i.payment_id = d.payment_id
        WHERE d.status = 'PAID' AND i.status != 'PAID'
    )
    BEGIN
        THROW 50065, N'Không thể thay đổi thanh toán đã hoàn thành', 1;
        RETURN;
    END
    
    -- Kiểm tra khi cập nhật thành PAID
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN deleted d ON i.payment_id = d.payment_id
        INNER JOIN [order] o ON i.order_id = o.order_id
        WHERE i.status = 'PAID' AND d.status != 'PAID'
        AND (i.amount_paid IS NULL OR i.amount_paid < o.final_amount)
    )
    BEGIN
        THROW 50066, N'Số tiền thanh toán không đủ hoặc chưa nhập', 1;
        RETURN;
    END
    
    -- Thực hiện update với tính toán tiền thừa
    UPDATE p 
    SET payment_method_id = i.payment_method_id,
        amount_paid = CASE 
            WHEN i.status = 'PAID' THEN i.amount_paid
            ELSE i.amount_paid 
        END,
        change_amount = CASE 
            WHEN i.status = 'PAID' AND i.amount_paid > o.final_amount 
            THEN i.amount_paid - o.final_amount 
            ELSE ISNULL(i.change_amount, 0)
        END,
        status = i.status,
        payment_time = CASE 
            WHEN i.status = 'PAID' AND d.status != 'PAID' THEN GETDATE()
            WHEN i.status = 'PAID' THEN ISNULL(i.payment_time, p.payment_time)
            ELSE i.payment_time
        END,
        updated_at = GETDATE()
    FROM payment p
    INNER JOIN inserted i ON p.payment_id = i.payment_id
    INNER JOIN deleted d ON i.payment_id = d.payment_id
    INNER JOIN [order] o ON i.order_id = o.order_id;
END
GO

-- Trigger tự động hoàn thành đơn hàng khi thanh toán thành công
CREATE TRIGGER after_payment_complete_order
ON payment
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tự động cập nhật trạng thái đơn hàng thành COMPLETED khi thanh toán PAID
    UPDATE o
    SET status = 'COMPLETED',
        updated_at = GETDATE()
    FROM [order] o
    INNER JOIN inserted i ON o.order_id = i.order_id
    WHERE i.status = 'PAID'
    AND o.status = 'PROCESSING'
    AND i.amount_paid >= o.final_amount;
END
GO

-- =====================================================
-- 12. PRODUCT TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm sản phẩm
CREATE TRIGGER before_product_insert
ON product
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra tên sản phẩm
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(LTRIM(RTRIM(name))) = 0 OR LEN(name) > 100)
    BEGIN
        THROW 50052, N'Tên sản phẩm không được để trống và có độ dài tối đa 100 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra mô tả
    IF EXISTS (SELECT 1 FROM inserted WHERE description IS NOT NULL AND LEN(LTRIM(RTRIM(description))) > 1000)
    BEGIN
        THROW 50053, N'Mô tả sản phẩm không được vượt quá 1000 ký tự', 1;
        RETURN;
    END
    
    INSERT INTO product (category_id, name, description, is_available, is_signature, image_path, created_at, updated_at)
    SELECT category_id, name, description, ISNULL(is_available, 1), ISNULL(is_signature, 0), image_path, GETDATE(), GETDATE() FROM inserted;
END
GO

-- Trigger xóa sản phẩm với cascade delete
CREATE TRIGGER before_product_delete
ON product
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Xóa tất cả giá sản phẩm trước
    DELETE FROM product_price 
    WHERE product_id IN (SELECT product_id FROM deleted);
    
    -- Sau đó xóa sản phẩm
    DELETE FROM product 
    WHERE product_id IN (SELECT product_id FROM deleted);
END
GO

-- =====================================================
-- 13. PRODUCT PRICE TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm giá sản phẩm
CREATE TRIGGER before_product_price_insert
ON product_price
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra giá sản phẩm
    IF EXISTS (SELECT 1 FROM inserted WHERE price < 1000)
    BEGIN
        THROW 50054, N'Giá sản phẩm phải lớn hơn 1000', 1;
        RETURN;
    END
    
    INSERT INTO product_price (product_id, size_id,  price, created_at, updated_at)
    SELECT product_id, size_id,  price,  GETDATE(), GETDATE() FROM inserted;
END
GO

CREATE TRIGGER before_product_price_delete
ON product_price
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1 
        FROM deleted d
        JOIN order_product op ON d.product_price_id = op.product_price_id
        JOIN [order] o ON op.order_id = o.order_id
        WHERE o.status = 'PROCESSING'
    )
    BEGIN
        THROW 50055, N'Không thể xóa giá sản phẩm đang được sử dụng trong đơn hàng', 1;
        RETURN;
    END

    DELETE FROM product_price WHERE product_price_id IN (SELECT product_price_id FROM deleted);
END
GO

-- =====================================================
-- 14. AUTOMATIC CALCULATION TRIGGERS
-- =====================================================

-- Trigger cập nhật tổng tiền đơn hàng khi có thay đổi trên order_product
CREATE TRIGGER after_order_product_change
ON order_product
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @order_id INT;

    SELECT @order_id = ISNULL(i.order_id, d.order_id)
    FROM inserted i
    FULL OUTER JOIN deleted d ON i.order_id = d.order_id;

    IF @order_id IS NOT NULL AND EXISTS (SELECT 1 FROM [order] WHERE order_id = @order_id AND status = 'PROCESSING')
    BEGIN
        DECLARE @new_total_amount DECIMAL(11, 3);
        DECLARE @total_discount_amount DECIMAL(11, 3);
        DECLARE @customer_id INT;

        -- Lấy customer_id
        SELECT @customer_id = customer_id FROM [order] WHERE order_id = @order_id;

        -- 1. Tính lại tổng tiền từ sản phẩm và cập nhật ngay vào bảng order
        SELECT @new_total_amount = ISNULL(SUM(op.quantity * pp.price), 0)
        FROM order_product op
        INNER JOIN product_price pp ON op.product_price_id = pp.product_price_id
        WHERE op.order_id = @order_id;
        
        UPDATE [order] SET total_amount = @new_total_amount, updated_at = GETDATE() WHERE order_id = @order_id;

        -- 2. Cập nhật lại tất cả các discount đang áp dụng cho đơn hàng này
        -- Dùng bảng tạm để tránh các vấn đề về trigger đệ quy khi update/delete trong chính trigger
        CREATE TABLE #temp_discounts (
            order_discount_id INT,
            new_amount DECIMAL(11, 3)
        );

        INSERT INTO #temp_discounts (order_discount_id, new_amount)
        SELECT 
            od.order_discount_id,
            dbo.fn_calculate_discount_amount(od.discount_id, @customer_id, @order_id)
        FROM order_discount od
        WHERE od.order_id = @order_id;
        
        -- Cập nhật hoặc xóa các bản ghi trong order_discount dựa trên kết quả tính toán lại
        UPDATE od
        SET od.discount_amount = td.new_amount, od.updated_at = GETDATE()
        FROM order_discount od
        JOIN #temp_discounts td ON od.order_discount_id = td.order_discount_id
        WHERE td.new_amount > 0;

        DELETE od
        FROM order_discount od
        JOIN #temp_discounts td ON od.order_discount_id = td.order_discount_id
        WHERE td.new_amount <= 0;
        
        DROP TABLE #temp_discounts;

        -- 3. Tính lại tổng giảm giá cuối cùng
        SELECT @total_discount_amount = ISNULL(SUM(discount_amount), 0)
        FROM order_discount
        WHERE order_id = @order_id;

        -- 4. Cập nhật final_amount vào bảng order (sử dụng function tính toán đầy đủ)
        UPDATE [order]
        SET final_amount = dbo.fn_calculate_final_amount(@order_id),
            updated_at = GETDATE()
        WHERE order_id = @order_id;
    END
END
GO

-- Trigger cập nhật thành tiền khi có thay đổi trên order_discount
CREATE TRIGGER after_order_discount_change
ON order_discount
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @order_id INT;

    -- Lấy order_id từ các bản ghi đã thay đổi
    SELECT @order_id = ISNULL(i.order_id, d.order_id)
    FROM inserted i
    FULL OUTER JOIN deleted d ON i.order_id = d.order_id;
    
    -- Cập nhật số lần sử dụng discount
    -- Tăng cho các discount mới được thêm vào
    IF EXISTS (SELECT 1 FROM inserted)
    BEGIN
        UPDATE d
        SET d.current_uses = ISNULL(d.current_uses, 0) + i.count
        FROM discount d
        INNER JOIN (SELECT discount_id, COUNT(*) as count FROM inserted GROUP BY discount_id) i 
        ON d.discount_id = i.discount_id;
    END

    -- Giảm cho các discount bị xóa
    IF EXISTS (SELECT 1 FROM deleted)
    BEGIN
        UPDATE d
        SET d.current_uses = ISNULL(d.current_uses, 0) - del.count
        FROM discount d
        INNER JOIN (SELECT discount_id, COUNT(*) as count FROM deleted GROUP BY discount_id) del
        ON d.discount_id = del.discount_id;
    END
    
    -- Cập nhật lại final_amount nếu đơn hàng đang xử lý
    IF @order_id IS NOT NULL AND EXISTS (SELECT 1 FROM [order] WHERE order_id = @order_id AND status = 'PROCESSING')
    BEGIN
        DECLARE @total_discount_amount DECIMAL(11, 3);

        -- Tính lại tổng giảm giá
        SELECT @total_discount_amount = ISNULL(SUM(discount_amount), 0)
        FROM order_discount
        WHERE order_id = @order_id;

        -- Cập nhật lại final_amount trong bảng order (sử dụng function tính toán đầy đủ)
        UPDATE [order]
        SET final_amount = dbo.fn_calculate_final_amount(@order_id),
            updated_at = GETDATE()
        WHERE order_id = @order_id;
    END
END
GO

-- Trigger cập nhật điểm khách hàng khi hoàn thành đơn hàng
CREATE TRIGGER after_order_update_customer_points
ON [order]
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tích lũy điểm khách hàng khi đơn hàng hoàn thành
    UPDATE c
    SET current_points = current_points + ISNULL(i.point, 1), -- Mặc định 1 điểm nếu NULL
        updated_at = GETDATE()
    FROM customer c
    INNER JOIN inserted i ON c.customer_id = i.customer_id
    INNER JOIN deleted d ON i.order_id = d.order_id
    WHERE i.status = 'COMPLETED' 
    AND d.status != 'COMPLETED'
    AND i.customer_id IS NOT NULL;
END
GO

-- Trigger cập nhật final_amount khi thay đổi customer_id
CREATE TRIGGER after_order_update_recalculate_final_amount
ON [order]
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Chỉ cập nhật final_amount cho các đơn hàng PROCESSING khi có thay đổi customer_id (ảnh hưởng membership discount)
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN deleted d ON i.order_id = d.order_id
        WHERE i.status = 'PROCESSING'
        AND ISNULL(i.customer_id, 0) != ISNULL(d.customer_id, 0)
    )
    BEGIN
        UPDATE o
        SET final_amount = dbo.fn_calculate_final_amount(i.order_id),
            updated_at = GETDATE()
        FROM [order] o
        INNER JOIN inserted i ON o.order_id = i.order_id
        INNER JOIN deleted d ON i.order_id = d.order_id
        WHERE i.status = 'PROCESSING'
        AND ISNULL(i.customer_id, 0) != ISNULL(d.customer_id, 0);
    END
END
GO

-- =====================================================
-- 15. PROTECT DEFAULT DATA TRIGGERS
-- =====================================================

-- Bảo vệ đơn vị đo lường mặc định
CREATE TRIGGER before_unit_of_measure_delete
ON unit_of_measure
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (SELECT 1 FROM deleted WHERE symbol IN (N'mL', N'cái'))
    BEGIN
        THROW 50061, N'Không thể xóa đơn vị đo lường mặc định', 1;
        RETURN;
    END
    
    DELETE FROM unit_of_measure 
    WHERE unit_id IN (SELECT unit_id FROM deleted WHERE symbol NOT IN (N'mL', N'cái'));
END
GO

-- Bảo vệ kích thước sản phẩm mặc định
CREATE TRIGGER protect_default_size_update
ON product_size
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra nếu đang cố gắng thay đổi dữ liệu mặc định
    IF EXISTS (
        SELECT 1 
        FROM deleted d
        INNER JOIN inserted i ON d.size_id = i.size_id
        WHERE d.name IN (N'S', N'M', N'L', N'NA') 
        AND d.unit_id IN (SELECT unit_id FROM unit_of_measure WHERE symbol IN (N'mL', N'cái'))
        AND (d.name != i.name OR d.unit_id != i.unit_id)
    )
    BEGIN
        THROW 50062, N'Không thể thay đổi kích thước mặc định', 1;
        RETURN;
    END
    
    -- Thực hiện update cho những record không phải mặc định
    UPDATE ps
    SET unit_id = i.unit_id,
        name = i.name,
        quantity = i.quantity,
        description = i.description,
        updated_at = GETDATE()
    FROM product_size ps
    INNER JOIN inserted i ON ps.size_id = i.size_id
    WHERE NOT EXISTS (
        SELECT 1 FROM deleted d 
        WHERE d.size_id = i.size_id 
        AND d.name IN (N'S', N'M', N'L', N'NA')
        AND d.unit_id IN (SELECT unit_id FROM unit_of_measure WHERE symbol IN (N'mL', N'cái'))
    );
END
GO

CREATE TRIGGER before_product_size_delete
ON product_size
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (
        SELECT 1 FROM deleted 
        WHERE name IN (N'S', N'M', N'L', N'NA') 
        AND unit_id IN (SELECT unit_id FROM unit_of_measure WHERE symbol IN (N'mL', N'cái'))
    )
    BEGIN
        THROW 50063, N'Không thể xóa kích thước mặc định', 1;
        RETURN;
    END
    
    -- Xóa tất cả giá sản phẩm sử dụng kích thước trước
    DELETE FROM product_price 
    WHERE size_id IN (
        SELECT size_id FROM deleted 
        WHERE name NOT IN (N'S', N'M', N'L', N'NA')
        OR unit_id NOT IN (SELECT unit_id FROM unit_of_measure WHERE symbol IN (N'mL', N'cái'))
    );
    
    -- Sau đó xóa kích thước
    DELETE FROM product_size 
    WHERE size_id IN (
        SELECT size_id FROM deleted 
        WHERE name NOT IN (N'S', N'M', N'L', N'NA')
        OR unit_id NOT IN (SELECT unit_id FROM unit_of_measure WHERE symbol IN (N'mL', N'cái'))
    );
END
GO

-- Bảo vệ loại thành viên mặc định
CREATE TRIGGER before_membership_type_insert
ON membership_type
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra thời hạn phải là ngày trong tương lai
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE valid_until IS NOT NULL 
        AND valid_until <= CAST(GETDATE() AS DATE)
    )
    BEGIN
        THROW 50056, N'Thời hạn membership phải là ngày trong tương lai', 1;
        RETURN;
    END
    
    -- Thực hiện insert
    INSERT INTO membership_type (type, discount_value, discount_unit, required_point, 
                                description, valid_until, is_active, created_at, updated_at)
    SELECT type, discount_value, discount_unit, required_point, 
           description, valid_until, is_active, GETDATE(), GETDATE()
    FROM inserted;
END
GO

CREATE TRIGGER before_membership_type_update
ON membership_type
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra thời hạn phải là ngày trong tương lai
    IF EXISTS (
        SELECT 1 FROM inserted 
        WHERE valid_until IS NOT NULL 
        AND valid_until <= CAST(GETDATE() AS DATE)
    )
    BEGIN
        THROW 50055, N'Thời hạn membership phải là ngày trong tương lai', 1;
        RETURN;
    END
    
    IF EXISTS (
        SELECT 1 
        FROM deleted d
        INNER JOIN inserted i ON d.membership_type_id = i.membership_type_id
        WHERE d.type IN (N'NEWMEM', N'BRONZE', N'SILVER', N'GOLD', N'PLATINUM')
        AND d.type != i.type
    )
    BEGIN
        THROW 50066, N'Không thể thay đổi tên loại thành viên mặc định', 1;
        RETURN;
    END
    
    UPDATE mt
    SET type = i.type,
        discount_value = i.discount_value,
        discount_unit = i.discount_unit,
        required_point = i.required_point,
        description = i.description,
        valid_until = i.valid_until,
        is_active = i.is_active,
        updated_at = GETDATE()
    FROM membership_type mt
    INNER JOIN inserted i ON mt.membership_type_id = i.membership_type_id;
END
GO

CREATE TRIGGER before_membership_type_delete
ON membership_type
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (SELECT 1 FROM deleted WHERE type IN (N'NEWMEM', N'BRONZE', N'SILVER', N'GOLD', N'PLATINUM'))
    BEGIN
        THROW 50067, N'Không thể xóa loại thành viên mặc định', 1;
        RETURN;
    END
    
    -- Chuyển tất cả khách hàng sang loại thành viên có điểm thấp nhất
    UPDATE customer 
    SET membership_type_id = (SELECT membership_type_id FROM membership_type WHERE type = 'NEWMEM'),
        updated_at = GETDATE()
    WHERE membership_type_id IN (
        SELECT membership_type_id FROM deleted 
    );
    
    -- Sau đó xóa loại thành viên
    DELETE FROM membership_type 
    WHERE membership_type_id IN (
        SELECT membership_type_id FROM deleted 
        WHERE type NOT IN (N'NEWMEM', N'BRONZE', N'SILVER', N'GOLD', N'PLATINUM')
    );
END
GO

-- Bảo vệ vai trò mặc định
CREATE TRIGGER before_role_update
ON role
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (
        SELECT 1 
        FROM deleted d
        INNER JOIN inserted i ON d.role_id = i.role_id
        WHERE d.name IN (N'MANAGER', N'STAFF', N'CUSTOMER', N'GUEST')
        AND d.name != i.name
    )
    BEGIN
        THROW 50068, N'Không thể thay đổi tên vai trò mặc định', 1;
        RETURN;
    END
    
    UPDATE r
    SET name = i.name,
        description = i.description,
        updated_at = GETDATE()
    FROM role r
    INNER JOIN inserted i ON r.role_id = i.role_id
    WHERE NOT EXISTS (
        SELECT 1 FROM deleted d 
        WHERE d.role_id = i.role_id 
        AND d.name IN (N'MANAGER', N'STAFF', N'CUSTOMER', N'GUEST')
        AND d.name != i.name
    );
END
GO

CREATE TRIGGER before_role_delete
ON role
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (SELECT 1 FROM deleted WHERE name IN (N'MANAGER', N'STAFF', N'CUSTOMER', N'GUEST'))
    BEGIN
        THROW 50069, N'Không thể xóa vai trò mặc định', 1;
        RETURN;
    END
    
    -- Xóa tất cả tài khoản có vai trò trước
    DELETE FROM account 
    WHERE role_id IN (
        SELECT role_id FROM deleted 
        WHERE name NOT IN (N'MANAGER', N'STAFF', N'CUSTOMER', N'GUEST')
    );
    
    -- Sau đó xóa vai trò
    DELETE FROM role 
    WHERE role_id IN (
        SELECT role_id FROM deleted 
        WHERE name NOT IN (N'MANAGER', N'STAFF', N'CUSTOMER', N'GUEST')
    );
END
GO

-- Bảo vệ phương thức thanh toán mặc định
CREATE TRIGGER before_payment_method_update
ON payment_method
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (
        SELECT 1 
        FROM deleted d
        INNER JOIN inserted i ON d.payment_method_id = i.payment_method_id
        WHERE d.payment_name IN (N'CASH', N'VISA', N'BANKCARD', N'CREDIT_CARD', N'E-WALLET')
        AND d.payment_name != i.payment_name
    )
    BEGIN
        THROW 50070, N'Không thể thay đổi tên phương thức thanh toán mặc định', 1;
        RETURN;
    END
    
    UPDATE pm
    SET payment_name = i.payment_name,
        payment_description = i.payment_description,
        updated_at = GETDATE()
    FROM payment_method pm
    INNER JOIN inserted i ON pm.payment_method_id = i.payment_method_id
    WHERE NOT EXISTS (
        SELECT 1 FROM deleted d 
        WHERE d.payment_method_id = i.payment_method_id 
        AND d.payment_name IN (N'CASH', N'VISA', N'BANKCARD', N'CREDIT_CARD', N'E-WALLET')
        AND d.payment_name != i.payment_name
    );
END
GO

CREATE TRIGGER before_payment_method_delete
ON payment_method
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (SELECT 1 FROM deleted WHERE payment_name IN (N'CASH', N'VISA', N'BANKCARD', N'CREDIT_CARD', N'E-WALLET'))
    BEGIN
        THROW 50071, N'Không thể xóa phương thức thanh toán mặc định', 1;
        RETURN;
    END
    
    DELETE FROM payment_method 
    WHERE payment_method_id IN (
        SELECT payment_method_id FROM deleted 
        WHERE payment_name NOT IN (N'CASH', N'VISA', N'BANKCARD', N'CREDIT_CARD', N'E-WALLET')
    );
END
GO

-- Bảo vệ thông tin cửa hàng
CREATE TRIGGER before_store_insert
ON store
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @store_count INT;
    SELECT @store_count = COUNT(*) FROM store;
    
    IF @store_count > 0
    BEGIN
        THROW 50072, N'Không thể tạo thêm thông tin cửa hàng mới. Chỉ được phép có một bản ghi thông tin cửa hàng.', 1;
        RETURN;
    END
    
    INSERT INTO store (name, address, phone, opening_time, closing_time, email, opening_date, tax_code)
    SELECT name, address, phone, opening_time, closing_time, email, opening_date, tax_code
    FROM inserted;
END
GO

CREATE TRIGGER protect_store_delete
ON store
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @store_count INT;
    SELECT @store_count = COUNT(*) FROM store;
    
    IF @store_count = 1
    BEGIN
        THROW 50073, N'Không thể xóa thông tin cửa hàng duy nhất. Cửa hàng phải luôn có thông tin.', 1;
        RETURN;
    END
    
    DELETE FROM store WHERE store_id IN (SELECT store_id FROM deleted);
END
GO

CREATE TRIGGER protect_store_update
ON store
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (
        SELECT 1 
        FROM deleted d
        INNER JOIN inserted i ON d.store_id = i.store_id
        WHERE d.store_id != i.store_id
    )
    BEGIN
        THROW 50074, N'Không thể thay đổi ID của cửa hàng. Chỉ được phép cập nhật thông tin.', 1;
        RETURN;
    END
    
    UPDATE s
    SET name = i.name,
        address = i.address,
        phone = i.phone,
        opening_time = i.opening_time,
        closing_time = i.closing_time,
        email = i.email,
        opening_date = i.opening_date,
        tax_code = i.tax_code,
        updated_at = GETDATE()
    FROM store s
    INNER JOIN inserted i ON s.store_id = i.store_id;
END
GO

CREATE TRIGGER before_order_discount_insert
ON order_discount
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra đơn hàng phải ở trạng thái PROCESSING
    IF EXISTS (
        SELECT 1 FROM inserted i
        INNER JOIN [order] o ON i.order_id = o.order_id
        WHERE o.status != 'PROCESSING'
    )
    BEGIN
        THROW 50052, N'Không thể áp dụng giảm giá cho đơn hàng đã hoàn thành hoặc đã hủy', 1;
        RETURN;
    END

    -- Lặp qua từng bản ghi đang được chèn
    DECLARE @order_id INT, @discount_id INT, @customer_id INT, @calculated_amount DECIMAL(11, 3);

    DECLARE cur CURSOR FORWARD_ONLY READ_ONLY FOR
    SELECT i.order_id, i.discount_id, o.customer_id
    FROM inserted i
    JOIN [order] o ON i.order_id = o.order_id;

    OPEN cur;
    FETCH NEXT FROM cur INTO @order_id, @discount_id, @customer_id;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- Kiểm tra không bị trùng discount
        IF EXISTS (SELECT 1 FROM order_discount WHERE order_id = @order_id AND discount_id = @discount_id)
        BEGIN
            THROW 50053, N'Mã giảm giá đã được áp dụng cho đơn hàng này', 1;
            RETURN;
        END

        -- Gọi function để tính toán số tiền giảm giá
        SET @calculated_amount = dbo.fn_calculate_discount_amount(@discount_id, @customer_id, @order_id);

        -- Nếu số tiền giảm giá hợp lệ (>0), thì chèn vào bảng
        IF @calculated_amount > 0
        BEGIN
            INSERT INTO order_discount (order_id, discount_id, discount_amount, created_at, updated_at)
            VALUES (@order_id, @discount_id, @calculated_amount, GETDATE(), GETDATE());
        END
        ELSE
        BEGIN
            THROW 50054, N'Mã giảm giá không hợp lệ hoặc không đủ điều kiện áp dụng', 1;
            RETURN;
        END
        
        FETCH NEXT FROM cur INTO @order_id, @discount_id, @customer_id;
    END

    CLOSE cur;
    DEALLOCATE cur;
END
GO

-- Trigger kiểm tra trước khi xóa discount khỏi đơn hàng
CREATE TRIGGER before_order_discount_delete
ON order_discount
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra đơn hàng phải ở trạng thái PROCESSING
    IF EXISTS (
        SELECT 1 FROM deleted d
        INNER JOIN [order] o ON d.order_id = o.order_id
        WHERE o.status != 'PROCESSING'
    )
    BEGIN
        THROW 50055, N'Không thể xóa giảm giá khỏi đơn hàng đã hoàn thành hoặc đã hủy', 1;
        RETURN;
    END
    
    DELETE FROM order_discount
    WHERE order_discount_id IN (SELECT order_discount_id FROM deleted);
END
GO

PRINT N'Đã tạo thành công tất cả triggers cho SQL Server bao gồm các trigger bảo vệ dữ liệu mặc định';
GO 

-- =====================================================
-- 16. ACCOUNT TRIGGERS
-- =====================================================

-- Trigger kiểm tra trước khi thêm tài khoản
CREATE TRIGGER before_account_insert
ON account
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra username
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(LTRIM(RTRIM(username))) < 3 OR LEN(username) > 50)
    BEGIN
        THROW 50075, N'Username phải có độ dài từ 3 đến 50 ký tự', 1;
        RETURN;
    END
    
    -- Kiểm tra username chỉ chứa ký tự chữ cái, số, gạch dưới
    IF EXISTS (SELECT 1 FROM inserted WHERE username LIKE '%[^a-zA-Z0-9_]%')
    BEGIN
        THROW 50076, N'Username chỉ được chứa chữ cái, số và dấu gạch dưới', 1;
        RETURN;
    END
    
    -- Kiểm tra password_hash không được trống
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(LTRIM(RTRIM(password_hash))) = 0)
    BEGIN
        THROW 50077, N'Mật khẩu không được để trống', 1;
        RETURN;
    END
    
    INSERT INTO account (role_id, username, password_hash, is_active, is_locked, token_version, created_at, updated_at)
    SELECT role_id, username, password_hash, ISNULL(is_active, 1), ISNULL(is_locked, 0), ISNULL(token_version, 0), GETDATE(), GETDATE() FROM inserted;
END
GO

-- Bảo vệ tài khoản admin mặc định khỏi việc thay đổi username
CREATE TRIGGER protect_default_admin_update
ON account
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @is_default_admin BIT = 0;
    
    -- Kiểm tra xem có đang cố gắng thay đổi username của admin mặc định không
    SELECT @is_default_admin = 1
    FROM deleted d
    INNER JOIN manager m ON d.account_id = m.account_id
    WHERE d.username = 'admin'
      AND m.email = 'admin@milkteashop.com'
      AND EXISTS (SELECT 1 FROM inserted i WHERE i.account_id = d.account_id AND i.username != 'admin');

    IF @is_default_admin = 1
    BEGIN
        THROW 50078, N'Không thể thay đổi username của tài khoản admin mặc định', 1;
        RETURN;
    END

    -- Thực hiện validation giống như khi insert
    IF EXISTS (SELECT 1 FROM inserted WHERE LEN(LTRIM(RTRIM(username))) < 3 OR LEN(username) > 50)
    BEGIN
        THROW 50079, N'Username phải có độ dài từ 3 đến 50 ký tự', 1;
        RETURN;
    END
    
    IF EXISTS (SELECT 1 FROM inserted WHERE username LIKE '%[^a-zA-Z0-9_]%')
    BEGIN
        THROW 50080, N'Username chỉ được chứa chữ cái, số và dấu gạch dưới', 1;
        RETURN;
    END
    
    IF EXISTS (SELECT 1 FROM inserted WHERE password_hash IS NOT NULL AND LEN(LTRIM(RTRIM(password_hash))) = 0)
    BEGIN
        THROW 50081, N'Mật khẩu không được để trống', 1;
        RETURN;
    END

    -- Thực hiện update nếu không bị chặn
    UPDATE a
    SET role_id = i.role_id,
        username = i.username,
        password_hash = i.password_hash,
        is_active = i.is_active,
        is_locked = i.is_locked,
        updated_at = GETDATE()
    FROM account a
    INNER JOIN inserted i ON a.account_id = i.account_id;
END
GO

-- Bảo vệ tài khoản admin mặc định khỏi việc xóa
CREATE TRIGGER before_account_delete
ON account
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @is_default_admin BIT = 0;
    
    -- Kiểm tra xem có đang cố gắng xóa admin mặc định không
    SELECT @is_default_admin = 1
    FROM deleted d
    INNER JOIN manager m ON d.account_id = m.account_id
    WHERE d.username = 'admin'
      AND m.email = 'admin@milkteashop.com';

    IF @is_default_admin = 1
    BEGIN
        THROW 50082, N'Không thể xóa tài khoản admin mặc định', 1;
        RETURN;
    END

    -- Xóa thông tin khách hàng liên kết
    DELETE FROM customer 
    WHERE account_id IN (SELECT account_id FROM deleted);
    
    -- Xóa thông tin nhân viên liên kết
    DELETE FROM employee 
    WHERE account_id IN (SELECT account_id FROM deleted);
    
    -- Xóa thông tin quản lý liên kết
    DELETE FROM manager 
    WHERE account_id IN (SELECT account_id FROM deleted);

    -- Thực hiện delete account cuối cùng
    DELETE FROM account 
    WHERE account_id IN (SELECT account_id FROM deleted);
END
GO

PRINT N'Đã tạo thành công tất cả triggers cho SQL Server bao gồm triggers bảo vệ admin mặc định';
GO 