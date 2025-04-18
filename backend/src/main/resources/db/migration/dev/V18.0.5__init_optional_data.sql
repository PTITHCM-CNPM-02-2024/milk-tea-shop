call sp_create_staff_account_with_permissions(
    'thanhphuong',
    '$2a$10$E4vnUguH/3GzHHgvDoRSau.w0lfHaQv5Au5l6bB.01pCBT/hO56B6pring',
    'Nhân Viên Bán Hàng',
    'Nguyễn Thành',
    'Phương',
    'MALE',
    '0981234567',
    'thp@gmail.com');

call sp_create_staff_account_with_permissions(
     'vanphong',
        '$2a$10$E4vnUguH/3GzHHgvDoRSau.w0lfHaQv5Au5l6bB.01pCBT/hO56B6pring',
        'Nhân Viên Bán Hàng',
        'Nguyễn Văn',
     'Phương',
     'MALE',
     '09857123456',
     'thpa@gmail.com');
    


INSERT INTO category (category_id, name, description)
VALUES (3,UPPER('Trân châu đường đen'), 'Các loại đồ uống kết hợp với trân châu đường đen, mang vị ngọt caramel đặc 
trưng.') ,
       (4,UPPER('Trà sữa'), 'Danh mục tổng hợp các loại trà sữa truyền thống và phổ biến.'),
       (5,UPPER('Trà sữa đặc biệt'), 'Các loại trà sữa cao cấp hoặc có công thức đặc biệt, mang hương vị độc đáo.'),
       (6,UPPER('Macchiato/Kem cheese'), 'Các loại trà kết hợp với lớp kem macchiato hoặc kem cheese béo mịn.'),
       (7,UPPER('Hoa hồng'), 'Đồ uống có thành phần từ hoa hồng, mang hương thơm tự nhiên và thanh mát.'),
       (8,UPPER('Trà giải khát'), 'Các loại trà thảo mộc và trà trái cây giúp giải nhiệt và tăng cường sức khỏe.');

insert into product (product_id, category_id, name, description, is_available, is_signature, image_path, created_at, updated_at)
values  (1001, 3, 'Sữa tươi trân châu đường đen', 'Một sự kết hợp hoàn hảo giữa vị ngọt thanh của đường đen và sự béo ngậy của sữa tươi nguyên chất. Điểm nhấn chính là những viên trân châu dai mềm, ngấm đều hương đường, tạo nên hương vị khó quên.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/milk_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (1002, 3, 'Caramel trân châu đường đen', 'Đậm đà với lớp caramel mịn màng, hòa quyện cùng vị ngọt dịu của đường đen và trân châu dai mềm. Thức uống này mang lại cảm giác ngọt ngào, béo nhẹ, rất thích hợp cho những tín đồ hảo ngọt.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/caramel_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (1003, 3, 'Socola trân châu đường đen', 'Hương vị thơm nồng, đậm đà của socola hòa quyện cùng đường đen ngọt thanh. Trân châu dẻo dai làm tăng thêm sự thú vị cho mỗi ngụm thưởng thức. Đây là lựa chọn lý tưởng cho những ai yêu thích sự đậm đà và ngọt ngào.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/socola_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (1004, 3, 'Cacao trân châu đường đen', 'Một sự kết hợp tuyệt vời giữa vị đắng nhẹ của cacao nguyên chất và ngọt thanh của đường đen. Thức uống này vừa thơm vừa béo, mang lại cảm giác hài hòa và thư giãn.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/cacao_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (1005, 3, 'Matcha trân châu đường đen', 'Matcha thanh mát, thơm lừng hòa quyện cùng lớp đường đen ngọt ngào và trân châu dai mềm. Sự hòa quyện này đem đến một trải nghiệm trà xanh hiện đại mà không kém phần truyền thống.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/matcha_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (1006, 3, 'Khoai môn trân châu đường đen', 'Hương khoai môn tự nhiên, bùi bùi kết hợp cùng vị ngọt thanh của đường đen và trân châu dẻo dai. Một thức uống vừa thơm, vừa đậm vị quê nhà.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/khoaimon_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (1007, 3, 'Hoa đậu biếc trân châu đường đen', 'Với sắc xanh bắt mắt từ hoa đậu biếc, kết hợp cùng vị ngọt nhẹ của đường đen và trân châu dẻo dai, đây không chỉ là thức uống ngon mà còn rất tốt cho sức khỏe.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/hoadaubiec_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (1008, 3, 'Oolong trân châu đường đen', 'Thưởng thức hương vị thanh tao, nhẹ nhàng của trà Oolong kết hợp cùng lớp đường đen ngọt dịu và trân châu mềm mại. Một sự lựa chọn lý tưởng để thư giãn và tận hưởng sự tinh tế.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/oolong_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (1009, 3, 'Trà sữa trân châu đường đen', 'Kinh điển nhưng không bao giờ lỗi thời, trà sữa thơm ngon béo nhẹ hòa quyện cùng đường đen ngọt dịu và trân châu dai mềm. Thức uống này luôn là "must-try" cho bất kỳ ai yêu thích trà sữa.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/milk_tea_tcdd.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (2001, 4, 'Trà sữa truyền thống', 'Hương vị quen thuộc với sự hòa quyện tinh tế giữa trà đen và sữa tươi. Vị ngọt vừa phải, thơm mát, thích hợp cho mọi lứa tuổi.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/truyenthong_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (2002, 4, 'Trà sữa socola', 'Đậm đà và ngọt ngào với hương socola thơm lừng, kết hợp cùng vị béo nhẹ của trà sữa.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/socola_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (2003, 4, 'Trà sữa caramel', 'Sự hòa quyện của trà sữa béo mịn và caramel thơm ngọt, mang lại cảm giác ấm áp, đầy mê hoặc.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/caramel_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (2004, 4, 'Trà sữa matcha', 'Matcha thanh mát, thơm phức, kết hợp cùng sữa ngọt dịu tạo nên hương vị cân bằng, thư thái.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/matcha_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (2005, 4, 'Trà sữa khoai môn', 'Vị bùi bùi đặc trưng của khoai môn hòa quyện cùng trà sữa béo ngậy, ngọt ngào và hấp dẫn.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/khoaimon_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (2006, 4, 'Trà sữa việt quốc', 'Độc đáo với hương thơm và vị chua ngọt nhẹ của việt quất, kết hợp trà sữa làm nổi bật vị giác.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/vietquoc_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (2007, 4, 'Trà sữa kiwi', 'Thức uống kết hợp giữa vị ngọt dịu của trà sữa và hương vị chua ngọt, tươi mát từ trái kiwi. Sự pha trộn độc đáo này mang lại cảm giác sảng khoái và đầy năng lượng, rất phù hợp cho những ngày hè.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/kiwi_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (2008, 4, 'Trà sữa đào', 'Ngọt thanh từ trái đào tươi, hòa quyện cùng trà sữa mát lạnh, mang lại cảm giác sảng khoái.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/dao_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (2009, 4, 'Trà sữa đác', 'Vị trà sữa truyền thống thêm phần thú vị nhờ topping đác dai ngon, mát lạnh.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/dac_ts.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (3001, 5, 'Trà sữa The Way', 'Công thức độc quyền với hương vị đặc biệt, béo ngậy và thơm ngon, làm bạn phải say mê từ ngụm đầu tiên.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/theway_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3002, 5, 'Trà sữa Vani', 'Mùi hương vani dịu nhẹ, ngọt ngào, kết hợp trà sữa làm nên thức uống tinh tế và đầy cuốn hút.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/vani_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3003, 5, 'Trà sữa mầm cây', 'Sự kết hợp sáng tạo với topping xanh mát từ thiên nhiên, vừa ngon miệng vừa tốt cho sức khỏe.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/mamcay_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3004, 5, 'Trà sữa hạt dẻ', 'Hương vị hạt dẻ bùi bùi hòa quyện cùng trà sữa ngọt dịu, mang lại cảm giác ấm áp.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/hatde_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3005, 5, 'Trà sữa Oolong', 'Thanh mát với hương trà oolong đậm đà, kết hợp cùng sữa ngọt dịu, làm nổi bật sự tinh tế.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/olong_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3006, 5, 'Trà sữa Hokaido', 'Đậm đà, béo ngậy với sữa Hokkaido thơm ngon, mang phong cách Nhật Bản đặc trưng.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/hokaido_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3008, 5, 'Trà sữa bánh Flan', 'Sự hòa quyện giữa vị ngọt béo của trà sữa và topping bánh flan mềm mịn, thơm ngon.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/traicay_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (3009, 5, 'Trà sữa Kim cương Đen', 'Trân châu kim cương đen dẻo dai kết hợp trà sữa đậm đà, tạo nên sức hút khó cưỡng.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/plan_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (3010, 5, 'Trà sữa hoa đậu biếc', 'Màu xanh độc đáo từ hoa đậu biếc, hòa quyện trà sữa thanh mát, đẹp mắt và thơm ngon.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/blackdiamond_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3011, 5, 'Trà sữa tứ quý mật ong', 'Sự kết hợp giữa bốn loại trà đặc biệt, thêm mật ong nguyên chất, mang lại vị thanh tao, ngọt dịu.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/hoadaubiec_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3012, 5, 'Trà sữa hạnh phúc', 'Công thức pha chế đặc biệt, mang lại cảm giác ngọt ngào và niềm vui cho mọi người.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/tuquymatong_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3013, 5, 'Trà sữa Matcha Pudding đậu đỏ', 'Matcha thơm lừng kết hợp pudding mịn màng và đậu đỏ bùi bùi, tạo nên thức uống hấp dẫn.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/happy_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3014, 5, 'Trà sữa khoai môn Pudding đậu đỏ', 'Hương khoai môn tự nhiên cùng pudding mềm mịn và đậu đỏ, tăng thêm phần đặc sắc.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/matchapuddingdaudo_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3015, 5, 'Trà sữa Socola Cake Cream', 'Lớp kem bánh mềm mịn, thơm mát kết hợp socola đậm đà, tạo nên thức uống ngọt ngào khó quên.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/khoaimonpuddingdaudo_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (3016, 5, 'Trà sữa Matcha Cake Cream', 'Matcha thanh mát thêm phần lôi cuốn với lớp kem bánh béo ngậy, mang lại trải nghiệm đẳng cấp.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/socolacakecream_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (4001, 6, 'Hồng trà Machiato/Kem chese', 'Hồng trà đậm đà với lớp kem cheese mặn ngọt hài hòa, tạo nên một ly trà độc đáo.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/matchacakecream_tsdb.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (4002, 6, 'Trà oolong Machiato/Kem chese', 'Trà oolong thanh tao kết hợp lớp kem béo ngậy, mang đến hương vị khó cưỡng.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/hongtra_mkc.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (4003, 6, 'Trà oolong đào Machiato/Kem chese', 'Vị đào thơm lừng hòa quyện cùng trà oolong và lớp kem cheese béo mịn.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/olong_mkc.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (4004, 6, 'Trà gạo rang Machiato/Kem chese', 'Thơm lừng với vị trà gạo rang độc đáo, thêm kem cheese tạo nên một ly trà đậm đà, lạ miệng.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/olongdao_mkc.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (4005, 6, 'Matcha Machiato/Kem chese', 'Vị trà xanh tươi mát kết hợp lớp kem cheese béo ngậy, làm nên thức uống đầy tinh tế.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/gaorang_mkc.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (4006, 6, 'Hoa đậu biếc Machiato/Kem chese', 'Màu sắc bắt mắt từ hoa đậu biếc và lớp kem cheese thơm ngon, là lựa chọn hoàn hảo cho người yêu cái đẹp.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/matcha_mkc.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (5001, 7, 'Sữa tươi hoa hồng trân châu', 'Hương hoa hồng nhẹ nhàng kết hợp sữa tươi và trân châu dai mềm, mang lại cảm giác lãng mạn.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/hoadaubiec_mkc.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (5002, 7, 'Sữa tươi hoa hồng nha đam', 'Sữa tươi béo nhẹ cùng nha đam giòn mát, thêm hương hoa hồng làm thức uống thêm phần đặc biệt.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/tranchau_sthh.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (5003, 7, 'Trà hoa hồng Machiato', 'Trà hoa hồng thanh tao cùng lớp kem cheese ngậy béo, mang lại sự hài hòa độc đáo.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/nhadam_sthh.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (5004, 7, 'Trà hoa hồng Galaxy', 'Sắc màu galaxy cuốn hút, cùng hương thơm hoa hồng ngọt ngào, tạo nên thức uống tinh tế.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/machiatotea_sthh.png', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (5005, 7, 'Trà hoa hồng Lệ Chi', 'Hương vị thanh mát từ hoa hồng và trái vải tươi, mang đến cảm giác ngọt ngào, dễ chịu.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/galaxytea_sthh.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (5006, 7, 'Lục trà hoa hồng nha đam', 'Lục trà thanh mát kết hợp hương hoa hồng thơm dịu và nha đam giòn sật, là lựa chọn tuyệt vời cho ngày hè.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/lechitea_sthh.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (6001, 1, 'Thạch rau cau', null, 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/sixtea_sthh.jpg', '2025-04-02 07:51:34', '2025-04-06 06:59:54'),
        (6002, 1, 'Thạch trái cây', null, 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/jelly_type.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (6003, 1, 'Thạch cà phê', null, 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/jelly_type.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (6004, 1, 'Nha đam', 'Giòn mát, tự nhiên và giàu dưỡng chất, phù hợp với hầu hết các loại thức uống.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/jelly_type.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (6005, 2, 'Bánh Flan', 'Mềm mịn, béo ngậy, tăng thêm phần đặc sắc cho ly trà sữa.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/aloe_vera.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (6006, 2, 'Kim cương đen', 'Trân châu đen cao cấp, dẻo thơm, ngấm vị ngọt đậm đà.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/flan.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55'),
        (6007, 2, 'Phô mai khổng lồ', 'Viên phô mai béo ngậy, tan chảy trong miệng, là lựa chọn tuyệt vời cho những 
tín đồ phô mai.', 1, 1, 'https://pub-6eff879fefb648ca96740b42eb728d1d.r2.dev/diamond_black.png', '2025-04-02 07:51:34', '2025-04-06 06:59:55');

insert into product_price ( product_id, size_id, price)
values  (1001, 2, 40000.000),
        ( 1001, 3, 45000.000),
        (1002, 2, 45000.000),
        ( 1002, 3, 50000.000),
        (1003, 2, 45000.000),
        ( 1003, 3, 50000.000),
        ( 1004, 2, 45000.000),
        ( 1004, 3, 50000.000),
        (1005, 2, 45000.000),
        ( 1005, 3, 50000.000),
        ( 1006, 2, 45000.000),
        ( 1006, 3, 50000.000),
        ( 1007, 2, 45000.000),
        ( 1007, 3, 50000.000),
        ( 1008, 2, 35000.000),
        (1008, 3, 40000.000),
        (1009, 2, 38000.000),
        ( 1009, 3, 40000.000),
        ( 2001, 2, 36000.000),
        ( 2001, 3, 42000.000),
        ( 2002, 2, 45000.000),
        ( 2002, 3, 50000.000),
        ( 2003, 2, 45000.000),
        (2003, 3, 50000.000),
        (2004, 2, 45000.000),
        (2004, 3, 50000.000),
        (2005, 2, 45000.000),
        (2005, 3, 50000.000),
        ( 2006, 2, 45000.000),
        (2006, 3, 50000.000),
        ( 2007, 2, 45000.000),
        (2007, 3, 50000.000),
        ( 2008, 2, 45000.000),
        (2008, 3, 50000.000),
        (2009, 2, 45000.000),
        (2009, 3, 50000.000),
        (3001, 2, 45000.000),
        (3001, 3, 50000.000),
        ( 3002, 2, 45000.000),
        ( 3002, 3, 50000.000),
        ( 3003, 3, 55000.000),
        (3004, 2, 45000.000),
        ( 3004, 3, 50000.000),
        (3005, 2, 45000.000),
        ( 3005, 3, 50000.000),
        (3006, 2, 45000.000),
        ( 3006, 3, 50000.000),
        ( 3008, 2, 45000.000),
        (3008, 3, 50000.000),
        ( 3009, 2, 45000.000),
        ( 3009, 3, 50000.000),
        (3010, 2, 45000.000),
        ( 3010, 3, 50000.000),
        ( 3011, 2, 45000.000),
        (3011, 3, 50000.000),
        (3012, 2, 45000.000),
        ( 3012, 3, 50000.000),
        ( 3013, 2, 45000.000),
        ( 3013, 3, 50000.000),
        ( 3014, 2, 45000.000),
        ( 3014, 3, 50000.000),
        ( 3015, 2, 45000.000),
        ( 3015, 3, 50000.000),
        (3016, 2, 45000.000),
        ( 3016, 3, 50000.000),
        ( 4001, 3, 55000.000),
        ( 4002, 3, 55000.000),
        ( 4003, 3, 55000.000),
        ( 4004, 3, 55000.000),
        ( 4005, 3, 55000.000),
        (4006, 3, 55000.000),
        ( 5001, 3, 55000.000),
        ( 5002, 3, 55000.000),
        (5003, 3, 55000.000),
        ( 5004, 3, 55000.000),
        ( 5005, 3, 55000.000),
        ( 5006, 3, 55000.000),
        ( 6001, 4, 5000.000),
        ( 6002, 4, 5000.000),
        ( 6003, 4, 5000.000),
        ( 6004, 4, 8000.000),
        ( 6005, 4, 8000.000),
        (6006, 4, 10000.000),
        ( 6007, 4, 10000.000);

insert into coupon(coupon_id, coupon, description)
values (1, 'KHUYENMAI304', 'Khuyến mãi 30/04'),
       (2, 'GIAMGIA01', 'Mã demo'),
       (3, 'GIAMGIA02', 'Giảm giá 20% cho đơn hàng thứ hai'),
       (4, 'GIAMGIA03', 'Giảm giá 30% cho đơn hàng thứ ba'),
       (5, 'GIAMGIA04', 'Giảm giá 40% cho đơn hàng thứ tư'),
       (6, 'GIAMGIA05', 'Giảm giá 50% cho đơn hàng thứ năm');

insert into discount (discount_id, name, description, coupon_id, discount_value, discount_type, min_required_order_value, max_discount_amount, min_required_product, valid_from, valid_until, current_uses, max_uses, max_uses_per_customer, is_active, created_at, updated_at)
values  (1, 'Khuyến mãi 30/04', null, 1, 15.000, 'PERCENTAGE', 2000.000, 40000.000, 2, null, '2025-05-30 21:22:16', 
         0, 10000, null, 1, '2025-04-04 21:23:22', '2025-04-05 20:46:31'),
        (2, 'Khuyến mãi 01/05', null, 2, 20000, 'FIXED', 2000.000, 40000.000, 2, null, '2025-05-30 21:22:16',
            0, 10000, 1, 1, '2025-04-04 21:23:22', '2025-04-05 20:46:31');


insert into customer (customer_id, membership_type_id, account_id, last_name, first_name, phone, 
                                         email, current_points, gender)
values  (4143047638, 1, null, 'PHƯƠNG', 'Nguyễn AN', '0987488914', null, 4, 'FEMALE'),
        (4198039702, 1, null, null, null, '0987456356', null, 1, 'MALE'),
        (4268434481, 3, null, 'NAM', 'NGUYỄN PHONG', '0902147456', null, 57, 'MALE');


insert into area (area_id, name, description, max_tables, is_active, created_at, updated_at)
values  (1, 'S01', null, null, 1, '2025-04-05 09:02:26', '2025-04-05 09:02:26'),
        (2, 'S02', null, null, 1, '2025-04-05 09:02:40', '2025-04-05 09:02:40'),
        (3, 'S03', null, 20, 1, '2025-04-06 08:17:13', '2025-04-06 08:17:13'),
        (4, 'S04', null, null, 1, '2025-04-06 08:17:26', '2025-04-06 08:17:26');

insert into service_table (table_id, area_id, table_number, is_active, created_at, updated_at)
values  (1, 1, 'TB01', 1, '2025-04-05 09:01:11', '2025-04-05 09:04:46'),
        (2, null, 'TB02', 1, '2025-04-05 20:25:25', '2025-04-05 20:25:25'),
        (7, 2, 'TB03', 0, '2025-04-06 08:19:38', '2025-04-06 08:19:38'),
        (8, 3, 'TB04', 1, '2025-04-06 08:19:38', '2025-04-06 08:19:38'),
        (9, null, 'TB05', 1, '2025-04-06 08:19:38', '2025-04-06 08:19:38'),
        (10, null, 'TB06', 1, '2025-04-06 08:19:38', '2025-04-06 08:19:38'),
        (11, 4, 'TB07', 1, '2025-04-06 08:19:38', '2025-04-06 08:19:38');









