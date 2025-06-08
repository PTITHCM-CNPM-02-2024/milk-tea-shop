-- Dữ liệu mặc định cho SQL Server 2022 - Milk Tea Shop
-- Converted from MySQL to SQL Server

USE MilkTeaShop;
GO

-- Dữ liệu cho bảng unit_of_measure
INSERT INTO unit_of_measure (name, symbol, description)
VALUES (N'Militer', N'mL', N'Đơn vị đo lường thể tích'),
       (N'Piece', N'cái', N'Đơn vị đếm số lượng');

-- Dữ liệu cho bảng product_size
SET IDENTITY_INSERT product_size ON;
INSERT INTO product_size (size_id, unit_id, name, quantity, description)
VALUES (1, (SELECT unit_id FROM unit_of_measure WHERE symbol = N'mL'), N'S', 450, N'Size nhỏ - 450mL'),
       (2, (SELECT unit_id FROM unit_of_measure WHERE symbol = N'mL'), N'M', 650, N'Size vừa - 650mL'),
       (3, (SELECT unit_id FROM unit_of_measure WHERE symbol = N'mL'), N'L', 800, N'Size lớn - 800mL'),
       (4, (SELECT unit_id FROM unit_of_measure WHERE symbol = N'cái'), N'NA', 1, N'Đơn vị (cái/phần)');
SET IDENTITY_INSERT product_size OFF;

-- Dữ liệu cho bảng category
SET IDENTITY_INSERT category ON;
INSERT INTO category (category_id, name, description)
VALUES (1, N'TOPPING', N'Các loại topping bổ sung cho đồ uống'),   
       (2, N'TOPPING BÁN LẺ', N'Các loại topping được bán riêng lẻ');
SET IDENTITY_INSERT category OFF;

-- Dữ liệu cho bảng membership_type
INSERT INTO membership_type (type, discount_value, discount_unit, required_point, description, is_active, valid_until)
VALUES (N'NEWMEM', 0.000, N'FIXED', 0, N'Thành viên mới', 1, NULL),
       (N'BRONZE', 1000, N'FIXED', 20, N'Thành viên hạng đồng', 1, DATEADD(DAY, 365, GETDATE())),
       (N'SILVER', 2000, N'FIXED', 50, N'Thành viên hạng bạc', 1, DATEADD(DAY, 365, GETDATE())),
       (N'GOLD', 1, N'PERCENTAGE', 100, N'Thành viên hạng vàng', 1, DATEADD(DAY, 365, GETDATE())),
       (N'PLATINUM', 2, N'PERCENTAGE', 200, N'Thành viên hạng bạch kim', 1, DATEADD(DAY, 365, GETDATE()));

-- Dữ liệu cho bảng role
INSERT INTO role (name, description)
VALUES (N'MANAGER', N'Quản trị viên - có toàn quyền quản lý hệ thống'),
       (N'STAFF', N'Nhân viên - phục vụ và xử lý đơn hàng'),
       (N'CUSTOMER', N'Khách hàng - người mua hàng'),
       (N'GUEST', N'Khách vãng lai - người dùng chưa đăng ký');

-- Dữ liệu cho bảng payment_method
INSERT INTO payment_method (payment_name, payment_description)
VALUES (N'CASH', N'Thanh toán bằng tiền mặt'),
       (N'VISA', N'Thanh toán bằng thẻ Visa'),
       (N'BANKCARD', N'Thanh toán bằng thẻ ngân hàng'),
       (N'CREDIT_CARD', N'Thanh toán bằng thẻ tín dụng'),
       (N'E-WALLET', N'Thanh toán bằng ví điện tử');

-- Dữ liệu cho bảng store
INSERT INTO store (name, address, phone, opening_time, closing_time, email, opening_date, tax_code)
VALUES (N'Milk Tea Shop',
        N'123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh',
        N'0987654321',
        '11:00:00',
        '22:00:00',
        N'info@coffeeshop.com',
        '2023-01-01',
        N'0123456789');

GO 