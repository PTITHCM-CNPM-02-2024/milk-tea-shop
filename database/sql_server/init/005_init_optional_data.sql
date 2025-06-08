USE MilkTeaShop;
GO

-- Thêm danh mục sản phẩm với auto ID
INSERT INTO category (name, description)
VALUES (UPPER(N'Trân châu đường đen'), N'Các loại đồ uống kết hợp với trân châu đường đen, mang vị ngọt caramel đặc trưng.'),
       (UPPER(N'Trà sữa'), N'Danh mục tổng hợp các loại trà sữa truyền thống và phổ biến.'),
       (UPPER(N'Trà sữa đặc biệt'), N'Các loại trà sữa cao cấp hoặc có công thức đặc biệt, mang hương vị độc đáo.'),
       (UPPER(N'Macchiato/Kem cheese'), N'Các loại trà kết hợp với lớp kem macchiato hoặc kem cheese béo mịn.'),
       (UPPER(N'Hoa hồng'), N'Đồ uống có thành phần từ hoa hồng, mang hương thơm tự nhiên và thanh mát.'),
       (UPPER(N'Trà giải khát'), N'Các loại trà thảo mộc và trà trái cây giúp giải nhiệt và tăng cường sức khỏe.');

-- Lấy ID của các danh mục vừa tạo
DECLARE @cat_tcdd INT = (SELECT category_id FROM category WHERE name = UPPER(N'Trân châu đường đen'));
DECLARE @cat_ts INT = (SELECT category_id FROM category WHERE name = UPPER(N'Trà sữa'));
DECLARE @cat_tsdb INT = (SELECT category_id FROM category WHERE name = UPPER(N'Trà sữa đặc biệt'));
DECLARE @cat_mkc INT = (SELECT category_id FROM category WHERE name = UPPER(N'Macchiato/Kem cheese'));
DECLARE @cat_hh INT = (SELECT category_id FROM category WHERE name = UPPER(N'Hoa hồng'));
DECLARE @cat_tgk INT = (SELECT category_id FROM category WHERE name = UPPER(N'Trà giải khát'));
DECLARE @cat_topping INT = (SELECT category_id FROM category WHERE name = N'TOPPING');
DECLARE @cat_topping_bl INT = (SELECT category_id FROM category WHERE name = N'TOPPING BÁN LẺ');

-- Thêm sản phẩm trân châu đường đen
INSERT INTO product (category_id, name, description, is_available, is_signature, image_path, created_at, updated_at)
VALUES 
(@cat_tcdd, N'Sữa tươi trân châu đường đen', N'Một sự kết hợp hoàn hảo giữa vị ngọt thanh của đường đen và sự béo ngậy của sữa tươi nguyên chất. Điểm nhấn chính là những viên trân châu dai mềm, ngấm đều hương đường, tạo nên hương vị khó quên.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/milk_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_tcdd, N'Caramel trân châu đường đen', N'Đậm đà với lớp caramel mịn màng, hòa quyện cùng vị ngọt dịu của đường đen và trân châu dai mềm. Thức uống này mang lại cảm giác ngọt ngào, béo nhẹ, rất thích hợp cho những tín đồ hảo ngọt.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/caramel_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
(@cat_tcdd, N'Socola trân châu đường đen', N'Hương vị thơm nồng, đậm đà của socola hòa quyện cùng đường đen ngọt thanh. Trân châu dẻo dai làm tăng thêm sự thú vị cho mỗi ngụm thưởng thức. Đây là lựa chọn lý tưởng cho những ai yêu thích sự đậm đà và ngọt ngào.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/socola_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
(@cat_tcdd, N'Cacao trân châu đường đen', N'Một sự kết hợp tuyệt vời giữa vị đắng nhẹ của cacao nguyên chất và ngọt thanh của đường đen. Thức uống này vừa thơm vừa béo, mang lại cảm giác hài hòa và thư giãn.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/cacao_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
(@cat_tcdd, N'Matcha trân châu đường đen', N'Matcha thanh mát, thơm lừng hòa quyện cùng lớp đường đen ngọt ngào và trân châu dai mềm. Sự hòa quyện này đem đến một trải nghiệm trà xanh hiện đại mà không kém phần truyền thống.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/matcha_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_tcdd, N'Khoai môn trân châu đường đen', N'Hương khoai môn tự nhiên, bùi bùi kết hợp cùng vị ngọt thanh của đường đen và trân châu dẻo dai. Một thức uống vừa thơm, vừa đậm vị quê nhà.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/khoaimon_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_tcdd, N'Hoa đậu biếc trân châu đường đen', N'Với sắc xanh bắt mắt từ hoa đậu biếc, kết hợp cùng vị ngọt nhẹ của đường đen và trân châu dẻo dai, đây không chỉ là thức uống ngon mà còn rất tốt cho sức khỏe.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/hoadaubiec_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_tcdd, N'Oolong trân châu đường đen', N'Thưởng thức hương vị thanh tao, nhẹ nhàng của trà Oolong kết hợp cùng lớp đường đen ngọt dịu và trân châu mềm mại. Một sự lựa chọn lý tưởng để thư giãn và tận hưởng sự tinh tế.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/oolong_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_tcdd, N'Trà sữa trân châu đường đen', N'Kinh điển nhưng không bao giờ lỗi thời, trà sữa thơm ngon béo nhẹ hòa quyện cùng đường đen ngọt dịu và trân châu dai mềm. Thức uống này luôn là "must-try" cho bất kỳ ai yêu thích trà sữa.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/milk_tea_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54');

-- Thêm sản phẩm trà sữa
INSERT INTO product (category_id, name, description, is_available, is_signature, image_path, created_at, updated_at)
VALUES 
(@cat_ts, N'Trà sữa truyền thống', N'Hương vị quen thuộc với sự hòa quyện tinh tế giữa trà đen và sữa tươi. Vị ngọt vừa phải, thơm mát, thích hợp cho mọi lứa tuổi.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/truyenthong_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_ts, N'Trà sữa socola', N'Đậm đà và ngọt ngào với hương socola thơm lừng, kết hợp cùng vị béo nhẹ của trà sữa.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/socola_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
(@cat_ts, N'Trà sữa caramel', N'Sự hòa quyện của trà sữa béo mịn và caramel thơm ngọt, mang lại cảm giác ấm áp, đầy mê hoặc.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/caramel_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_ts, N'Trà sữa matcha', N'Matcha thanh mát, thơm phức, kết hợp cùng sữa ngọt dịu tạo nên hương vị cân bằng, thư thái.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/matcha_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
(@cat_ts, N'Trà sữa khoai môn', N'Vị bùi bùi đặc trưng của khoai môn hòa quyện cùng trà sữa béo ngậy, ngọt ngào và hấp dẫn.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/khoaimon_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
(@cat_ts, N'Trà sữa việt quốc', N'Độc đáo với hương thơm và vị chua ngọt nhẹ của việt quất, kết hợp trà sữa làm nổi bật vị giác.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/vietquoc_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
(@cat_ts, N'Trà sữa kiwi', N'Thức uống kết hợp giữa vị ngọt dịu của trà sữa và hương vị chua ngọt, tươi mát từ trái kiwi. Sự pha trộn độc đáo này mang lại cảm giác sảng khoái và đầy năng lượng, rất phù hợp cho những ngày hè.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/kiwi_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_ts, N'Trà sữa đào', N'Ngọt thanh từ trái đào tươi, hòa quyện cùng trà sữa mát lạnh, mang lại cảm giác sảng khoái.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/dao_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
(@cat_ts, N'Trà sữa đác', N'Vị trà sữa truyền thống thêm phần thú vị nhờ topping đác dai ngon, mát lạnh.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/dac_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54');

-- Thêm topping
INSERT INTO product (category_id, name, description, is_available, is_signature, image_path, created_at, updated_at)
VALUES 
(@cat_topping, N'Thạch rau cau', NULL, 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/sixtea_sthh.jpg', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
(@cat_topping, N'Thạch trái cây', NULL, 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/jelly_type.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_topping, N'Thạch cà phê', NULL, 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/jelly_type.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_topping, N'Nha đam', N'Giòn mát, tự nhiên và giàu dưỡng chất, phù hợp với hầu hết các loại thức uống.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/jelly_type.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_topping_bl, N'Bánh Flan', N'Mềm mịn, béo ngậy, tăng thêm phần đặc sắc cho ly trà sữa.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/aloe_vera.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_topping_bl, N'Kim cương đen', N'Trân châu đen cao cấp, dẻo thơm, ngấm vị ngọt đậm đà.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/flan.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
(@cat_topping_bl, N'Phô mai khổng lồ', N'Viên phô mai béo ngậy, tan chảy trong miệng, là lựa chọn tuyệt vời cho những tín đồ phô mai.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/diamond_black.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55');

-- Thêm mã giảm giá
INSERT INTO coupon (coupon, description)
VALUES (N'KHUYENMAI306', N'Khuyến mãi 30/06'),
       (N'GIAMGIA01', N'Mã demo'),
       (N'GIAMGIA02', N'Giảm giá 20% cho đơn hàng thứ hai'),
       (N'GIAMGIA03', N'Giảm giá 30% cho đơn hàng thứ ba'),
       (N'GIAMGIA04', N'Giảm giá 40% cho đơn hàng thứ tư'),
       (N'GIAMGIA05', N'Giảm giá 50% cho đơn hàng thứ năm');

-- Lấy ID của coupon vừa tạo
DECLARE @coupon1 INT = (SELECT coupon_id FROM coupon WHERE coupon = N'KHUYENMAI306');
DECLARE @coupon2 INT = (SELECT coupon_id FROM coupon WHERE coupon = N'GIAMGIA01');

-- Thêm discount
INSERT INTO discount (name, description, coupon_id, discount_value, discount_type, min_required_order_value, max_discount_amount, min_required_product, valid_from, valid_until, current_uses, max_uses, max_uses_per_customer, is_active, created_at, updated_at)
VALUES (N'Khuyến mãi 30/06', NULL, @coupon1, 15.000, 'PERCENTAGE', 2000.000, 40000.000, 2, NULL, '2025-06-30 21:22:16', 0, 10000, NULL, 1, '2025-04-04 21:23:22', '2025-04-05 20:46:31'),
       (N'Khuyến mãi 01/07', NULL, @coupon2, 20000, 'FIXED', 2000.000, 40000.000, 2, '2025-07-01 00:00:00', '2025-07-30 23:59:59', 0, 10000, 1, 1, '2025-04-04 21:23:22', '2025-04-05 20:46:31');

-- Thêm khách hàng
INSERT INTO customer (membership_type_id, account_id, last_name, first_name, phone, email, current_points, gender)
VALUES (1, NULL, N'PHƯƠNG', N'Nguyễn AN', '0387488914', NULL, 4, 'FEMALE'),
       (1, NULL, NULL, NULL, '0387456356', NULL, 1, 'MALE'),
       (3, NULL, N'NAM', N'NGUYỄN PHONG', '0387453356', NULL, 57, 'MALE');

-- Thêm khu vực
INSERT INTO area (name, description, max_tables, is_active, created_at, updated_at)
VALUES ('S01', NULL, NULL, 1, '2025-04-05 09:02:26', '2025-04-05 09:02:26'),
       ('S02', NULL, NULL, 1, '2025-04-05 09:02:40', '2025-04-05 09:02:40'),
       ('S03', NULL, 20, 1, '2025-04-06 08:17:13', '2025-04-06 08:17:13'),
       ('S04', NULL, NULL, 1, '2025-04-06 08:17:26', '2025-04-06 08:17:26');

-- Lấy ID của area vừa tạo
DECLARE @area1 INT = (SELECT area_id FROM area WHERE name = 'S01');
DECLARE @area2 INT = (SELECT area_id FROM area WHERE name = 'S02');
DECLARE @area3 INT = (SELECT area_id FROM area WHERE name = 'S03');
DECLARE @area4 INT = (SELECT area_id FROM area WHERE name = 'S04');

-- Thêm bàn
INSERT INTO service_table (area_id, table_number, is_active, created_at, updated_at)
VALUES (@area1, 'TB01', 1, '2025-04-05 09:01:11', '2025-04-05 09:04:46'),
       (NULL, 'TB02', 1, '2025-04-05 20:25:25', '2025-04-05 20:25:25'),
       (@area2, 'TB03', 0, '2025-04-06 08:19:38', '2025-04-06 08:19:38'),
       (@area3, 'TB04', 1, '2025-04-06 08:19:38', '2025-04-06 08:19:38'),
       (NULL, 'TB05', 1, '2025-04-06 08:19:38', '2025-04-06 08:19:38'),
       (NULL, 'TB06', 1, '2025-04-06 08:19:38', '2025-04-06 08:19:38'),
       (@area4, 'TB07', 1, '2025-04-06 08:19:38', '2025-04-06 08:19:38');

-- Lấy thông tin size để thêm product_price
DECLARE @size_M INT = (SELECT size_id FROM product_size WHERE name = 'M');
DECLARE @size_L INT = (SELECT size_id FROM product_size WHERE name = 'L');
DECLARE @size_topping INT = (SELECT size_id FROM product_size WHERE name = 'NA');

-- Lấy product_id đã được auto generate
DECLARE @p1 INT, @p2 INT, @p3 INT, @p4 INT, @p5 INT, @p6 INT, @p7 INT, @p8 INT, @p9 INT;
DECLARE @p10 INT, @p11 INT, @p12 INT, @p13 INT, @p14 INT, @p15 INT, @p16 INT, @p17 INT, @p18 INT;
DECLARE @pt1 INT, @pt2 INT, @pt3 INT, @pt4 INT, @pt5 INT, @pt6 INT, @pt7 INT;

SELECT @p1 = product_id FROM product WHERE name = N'Sữa tươi trân châu đường đen';
SELECT @p2 = product_id FROM product WHERE name = N'Caramel trân châu đường đen';
SELECT @p3 = product_id FROM product WHERE name = N'Socola trân châu đường đen';
SELECT @p4 = product_id FROM product WHERE name = N'Cacao trân châu đường đen';
SELECT @p5 = product_id FROM product WHERE name = N'Matcha trân châu đường đen';
SELECT @p6 = product_id FROM product WHERE name = N'Khoai môn trân châu đường đen';
SELECT @p7 = product_id FROM product WHERE name = N'Hoa đậu biếc trân châu đường đen';
SELECT @p8 = product_id FROM product WHERE name = N'Oolong trân châu đường đen';
SELECT @p9 = product_id FROM product WHERE name = N'Trà sữa trân châu đường đen';

SELECT @p10 = product_id FROM product WHERE name = N'Trà sữa truyền thống';
SELECT @p11 = product_id FROM product WHERE name = N'Trà sữa socola';
SELECT @p12 = product_id FROM product WHERE name = N'Trà sữa caramel';
SELECT @p13 = product_id FROM product WHERE name = N'Trà sữa matcha';
SELECT @p14 = product_id FROM product WHERE name = N'Trà sữa khoai môn';
SELECT @p15 = product_id FROM product WHERE name = N'Trà sữa việt quốc';
SELECT @p16 = product_id FROM product WHERE name = N'Trà sữa kiwi';
SELECT @p17 = product_id FROM product WHERE name = N'Trà sữa đào';
SELECT @p18 = product_id FROM product WHERE name = N'Trà sữa đác';

SELECT @pt1 = product_id FROM product WHERE name = N'Thạch rau cau';
SELECT @pt2 = product_id FROM product WHERE name = N'Thạch trái cây';
SELECT @pt3 = product_id FROM product WHERE name = N'Thạch cà phê';
SELECT @pt4 = product_id FROM product WHERE name = N'Nha đam';
SELECT @pt5 = product_id FROM product WHERE name = N'Bánh Flan';
SELECT @pt6 = product_id FROM product WHERE name = N'Kim cương đen';
SELECT @pt7 = product_id FROM product WHERE name = N'Phô mai khổng lồ';

-- Thêm giá sản phẩm
INSERT INTO product_price (product_id, size_id, price)
VALUES 
-- Trà sữa trân châu đường đen
(@p1, @size_M, 40000.000), (@p1, @size_L, 45000.000),
(@p2, @size_M, 45000.000), (@p2, @size_L, 50000.000),
(@p3, @size_M, 45000.000), (@p3, @size_L, 50000.000),
(@p4, @size_M, 45000.000), (@p4, @size_L, 50000.000),
(@p5, @size_M, 45000.000), (@p5, @size_L, 50000.000),
(@p6, @size_M, 45000.000), (@p6, @size_L, 50000.000),
(@p7, @size_M, 45000.000), (@p7, @size_L, 50000.000),
(@p8, @size_M, 35000.000), (@p8, @size_L, 40000.000),
(@p9, @size_M, 38000.000), (@p9, @size_L, 40000.000),
-- Trà sữa truyền thống
(@p10, @size_M, 36000.000), (@p10, @size_L, 42000.000),
(@p11, @size_M, 45000.000), (@p11, @size_L, 50000.000),
(@p12, @size_M, 45000.000), (@p12, @size_L, 50000.000),
(@p13, @size_M, 45000.000), (@p13, @size_L, 50000.000),
(@p14, @size_M, 45000.000), (@p14, @size_L, 50000.000),
(@p15, @size_M, 45000.000), (@p15, @size_L, 50000.000),
(@p16, @size_M, 45000.000), (@p16, @size_L, 50000.000),
(@p17, @size_M, 45000.000), (@p17, @size_L, 50000.000),
(@p18, @size_M, 45000.000), (@p18, @size_L, 50000.000),
-- Topping
(@pt1, @size_topping, 5000.000),
(@pt2, @size_topping, 5000.000),
(@pt3, @size_topping, 5000.000),
(@pt4, @size_topping, 8000.000),
(@pt5, @size_topping, 8000.000),
(@pt6, @size_topping, 10000.000),
(@pt7, @size_topping, 10000.000);

PRINT N'Dữ liệu mẫu đã được thêm thành công với auto ID!';
