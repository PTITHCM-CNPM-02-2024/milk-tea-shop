create trigger milk_tea_shop_prod.before_service_table_insert
    before insert
    on milk_tea_shop_prod.service_table
    for each row
begin
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

END
    end;

