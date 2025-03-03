-- Xóa bảng TableStatus, hiện tại không cần thiết

ALTER TABLE milk_tea_shop_prod.TableStatus
    DROP FOREIGN KEY TableStatus_ibfk_1;

DROP TABLE milk_tea_shop_prod.TableStatus;
