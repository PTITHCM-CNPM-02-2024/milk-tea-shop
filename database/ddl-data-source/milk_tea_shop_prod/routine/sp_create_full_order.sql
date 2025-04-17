create procedure milk_tea_shop_prod.sp_create_full_order(IN p_customer_id int unsigned, IN p_employee_id int unsigned,
                                                         IN p_customize_note varchar(1000), IN p_point int unsigned,
                                                         IN p_order_products json, IN p_order_discounts json,
                                                         IN p_order_tables json,
                                                         IN p_payment_method_id tinyint unsigned,
                                                         IN p_amount_paid decimal(11, 3),
                                                         OUT p_new_order_id int unsigned,
                                                         OUT p_final_total decimal(11, 3), OUT p_change decimal(11, 3),
                                                         OUT p_status_message varchar(255))
order_proc: BEGIN -- Thêm nhãn 'order_proc' ở đây
    -- Variables
    DECLARE v_order_id INT UNSIGNED;
    DECLARE v_total_amount DECIMAL(11, 3) DEFAULT 0.000;
    DECLARE v_total_discount_amount DECIMAL(11, 3) DEFAULT 0.000;
    DECLARE v_final_amount DECIMAL(11, 3) DEFAULT 0.000;
    DECLARE v_payment_status ENUM('PROCESSING', 'CANCELLED', 'PAID');
    DECLARE v_change_amount DECIMAL(11, 3) DEFAULT 0.000;
    DECLARE v_order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

    -- Variables for loops
    DECLARE i INT DEFAULT 0;
    DECLARE product_count INT DEFAULT 0;
    DECLARE discount_count INT DEFAULT 0;
    DECLARE table_count INT DEFAULT 0;

    DECLARE v_product_price_id INT UNSIGNED;
    DECLARE v_quantity SMALLINT UNSIGNED;
    DECLARE v_option VARCHAR(500);
    DECLARE v_price DECIMAL(11, 3);

    DECLARE v_discount_id INT UNSIGNED;
    DECLARE v_applied_discount DECIMAL(11, 3);

    DECLARE v_table_id SMALLINT UNSIGNED;
    DECLARE v_check_in DATETIME;

    -- Error handling
    DECLARE exit handler for sqlexception
        BEGIN
            ROLLBACK;
            SET p_new_order_id = NULL;
            SET p_final_total = NULL;
            SET p_change = NULL;
            SET p_status_message = 'Lỗi xảy ra, giao dịch đã được hủy bỏ';
            -- Consider logging the specific SQL error here for debugging
        END;

    -- Input validation (basic)
    IF p_employee_id IS NULL OR p_payment_method_id IS NULL THEN
        SET p_status_message = 'Employee ID và Payment Method ID là bắt buộc.';
        LEAVE order_proc; -- Sử dụng nhãn 'order_proc' thay vì 'BEGIN'
    END IF;
    IF JSON_VALID(p_order_products) = 0 OR JSON_LENGTH(p_order_products) = 0 THEN
        SET p_status_message = 'Đơn hàng phải chứa ít nhất một sản phẩm hợp lệ.';
        LEAVE order_proc; -- Sử dụng nhãn 'order_proc' thay vì 'BEGIN'
    END IF;
    -- Add more JSON validation if needed for discounts/tables


    -- Start transaction
    START TRANSACTION;

    -- 1. Insert into `order` table (initial amounts are 0) -- chèn vào bảng order với các giá trị khởi tạo là 0
    CALL sp_insert_order(p_customer_id, p_employee_id, v_order_time, 0.000, 0.000, 'PROCESSING', p_customize_note, p_point, v_order_id);
    SET p_new_order_id = v_order_id; -- Set output order ID

    -- 2. Insert into `order_product` and calculate total_amount -- chèn vào bảng order_product và tính toán total_amount
    SET product_count = JSON_LENGTH(p_order_products);
    WHILE i < product_count DO
            -- Extract product details from JSON array element -- trích xuất thông tin sản phẩm từ phần tử mảng JSON
            SET v_product_price_id = JSON_UNQUOTE(JSON_EXTRACT(p_order_products, CONCAT('$[', i, '].product_price_id')));
            SET v_quantity = JSON_UNQUOTE(JSON_EXTRACT(p_order_products, CONCAT('$[', i, '].quantity')));
            SET v_option = JSON_UNQUOTE(JSON_EXTRACT(p_order_products, CONCAT('$[', i, '].option'))); -- Handle potential NULL

            -- Basic validation -- kiểm tra dữ liệu sản phẩm
            IF v_product_price_id IS NULL OR v_quantity IS NULL OR v_quantity <= 0 THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Dữ liệu sản phẩm không hợp lệ tìm thấy trong đơn hàng (ID hoặc số lượng).';
            END IF;

            -- Get price for calculation -- lấy giá sản phẩm cho tính toán
            SELECT price INTO v_price FROM product_price WHERE product_price_id = v_product_price_id;
            IF v_price IS NULL THEN
                -- Product price not found, trigger rollback
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không tìm thấy giá sản phẩm trong đơn hàng.';
            END IF;

            -- Insert into order_product -- chèn vào bảng order_product
            CALL sp_insert_order_product(v_order_id, v_product_price_id, v_quantity, v_option, @op_id); -- Using session variable temporarily

            -- Accumulate total amount -- tích lũy tổng số tiền
            SET v_total_amount = v_total_amount + (v_price * v_quantity);

            SET i = i + 1;
        END WHILE;

    -- Update order with calculated total_amount (final_amount is still 0) -- cập nhật đơn hàng với tổng số tiền đã tính toán (final_amount vẫn là 0)
    CALL sp_update_order_amounts(v_order_id, v_total_amount, 0.000);

    -- 3. Apply discounts, calculate total_discount_amount, AND INCREMENT USAGE
    SET i = 0;
    IF JSON_VALID(p_order_discounts) = 1 THEN
        SET discount_count = JSON_LENGTH(p_order_discounts);
        WHILE i < discount_count DO
                SET v_discount_id = JSON_UNQUOTE(JSON_EXTRACT(p_order_discounts, CONCAT('$[', i, '].discount_id')));

                IF v_discount_id IS NOT NULL THEN
                    SET v_applied_discount = fn_calculate_and_validate_discount(
                            v_discount_id,
                            p_customer_id,
                            v_total_amount,
                            v_order_time,
                            v_total_discount_amount
                                             );

                    IF v_applied_discount > 0 THEN
                        IF NOT EXISTS (SELECT 1 FROM order_discount WHERE order_id = v_order_id AND discount_id = v_discount_id) THEN
                            CALL sp_insert_order_discount(v_order_id, v_discount_id, v_applied_discount, @od_id);
                            SET v_total_discount_amount = v_total_discount_amount + v_applied_discount;

                            -- **Tăng số lần sử dụng discount**
                            UPDATE discount
                            SET current_uses = IFNULL(current_uses, 0) + 1,
                                updated_at = CURRENT_TIMESTAMP
                            WHERE discount_id = v_discount_id;
                            -- Lưu ý: Chưa kiểm tra current_uses > max_uses ở đây,
                            -- việc này nên được xử lý bởi trigger hoặc logic khác nếu cần chặn vượt quá.
                        END IF;
                    END IF;
                END IF;

                SET i = i + 1;
            END WHILE;
    END IF;

    -- 4. Calculate final_amount and update order
    SET v_final_amount = v_total_amount - v_total_discount_amount;
    -- Ensure final amount is not negative
    IF v_final_amount < 0 THEN
        SET v_final_amount = 0;
    END IF;

    CALL sp_update_order_amounts(v_order_id, v_total_amount, v_final_amount);
    SET p_final_total = v_final_amount; -- Set output final total

    -- 5. Insert into `order_table` -- chèn vào bảng order_table
    SET i = 0; -- Reset loop counter
    IF JSON_VALID(p_order_tables) = 1 THEN
        SET table_count = JSON_LENGTH(p_order_tables);
        WHILE i < table_count DO
                SET v_table_id = JSON_UNQUOTE(JSON_EXTRACT(p_order_tables, CONCAT('$[', i, '].table_id')));
                SET v_check_in = JSON_UNQUOTE(JSON_EXTRACT(p_order_tables, CONCAT('$[', i, '].check_in')));

                IF v_table_id IS NOT NULL AND v_check_in IS NOT NULL THEN
                    -- Basic validation for check_in time format could be added here if needed -- kiểm tra định dạng thời gian check_in có thể được thêm vào đây nếu cần
                    -- Check if this table combination already exists for the order -- kiểm tra xem bàn này đã tồn tại trong đơn hàng chưa
                    IF NOT EXISTS (SELECT 1 FROM order_table WHERE order_id = v_order_id AND table_id = v_table_id) THEN
                        CALL sp_insert_order_table(v_order_id, v_table_id, v_check_in, NULL, @ot_id); -- check_out is initially NULL
                    END IF;
                END IF;
                SET i = i + 1;
            END WHILE;
    END IF;

    -- 6. Insert into `payment` -- chèn vào bảng payment
    IF p_amount_paid IS NULL THEN -- Handle case where payment might be deferred or invalid input -- xử lý trường hợp thanh toán có thể bị trì hoãn hoặc dữ liệu nhập không hợp lệ
        SET v_payment_status = 'PROCESSING';
        SET v_change_amount = 0.000;
        SET p_amount_paid = 0.000; -- Ensure amount paid is not NULL for insert
    ELSEIF p_amount_paid >= v_final_amount THEN
        SET v_payment_status = 'PAID';
        SET v_change_amount = p_amount_paid - v_final_amount;
    ELSE -- Partial payment or less than total -- thanh toán một phần hoặc nhỏ hơn tổng số tiền
        SET v_payment_status = 'PROCESSING'; -- Or potentially another status like 'PARTIAL' if added to ENUM
        SET v_change_amount = 0.000; -- No change if not fully paid
    -- Consider adding logic here if partial payments need specific handling
    END IF;
    SET p_change = v_change_amount; -- Set output change -- thiết lập số tiền thay đổi  

    CALL sp_insert_payment(v_order_id, p_payment_method_id, v_payment_status, p_amount_paid, v_change_amount, CURRENT_TIMESTAMP, @pay_id);

    -- 7. Update final order status AND customer points
    IF v_payment_status = 'PAID' THEN
        CALL sp_update_order_status(v_order_id, 'COMPLETED');

        -- **Cộng điểm cho khách hàng nếu có**
        IF p_customer_id IS NOT NULL AND p_point > 0 THEN
            UPDATE customer
            SET current_points = current_points + p_point,
                updated_at = CURRENT_TIMESTAMP
            WHERE customer_id = p_customer_id;
        END IF;

    ELSE
        CALL sp_update_order_status(v_order_id, 'PROCESSING');
    END IF;

    -- Commit transaction -- xác nhận giao dịch
    COMMIT;
    SET p_status_message = 'Đơn hàng đã được tạo thành công.';

END;

