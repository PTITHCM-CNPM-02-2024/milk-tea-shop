create procedure milk_tea_shop_prod.sp_report_revenue_by_date_range(IN p_start_date date, IN p_end_date date)
BEGIN
    SELECT
        DATE(o.order_time) AS report_date,
        COUNT(o.order_id) AS total_orders,
        SUM(o.total_amount) AS total_revenue_before_discount,
        SUM(IFNULL(od.total_discount, 0)) AS total_discount_applied, -- Use IFNULL for orders without discounts
        SUM(o.final_amount) AS total_final_revenue
    FROM `order` o
             LEFT JOIN (
        SELECT
            order_id,
            SUM(discount_amount) AS total_discount
        FROM order_discount
        GROUP BY order_id
    ) od ON o.order_id = od.order_id
    WHERE o.status = 'COMPLETED' -- Chỉ tính các đơn hàng đã hoàn thành
      AND DATE(o.order_time) BETWEEN p_start_date AND p_end_date
    GROUP BY DATE(o.order_time)
    ORDER BY report_date;
END;

