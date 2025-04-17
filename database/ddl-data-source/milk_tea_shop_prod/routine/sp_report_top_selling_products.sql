create procedure milk_tea_shop_prod.sp_report_top_selling_products(IN p_start_date date, IN p_end_date date, IN p_limit int)
BEGIN
    SELECT
        p.product_id,
        p.name AS product_name,
        ps.name AS size_name, -- Include size name
        uom.symbol AS unit_symbol, -- Include unit symbol
        SUM(op.quantity) AS total_quantity_sold,
        SUM(op.quantity * pp.price) AS total_revenue_from_product
    FROM order_product op
             JOIN `order` o ON op.order_id = o.order_id
             JOIN product_price pp ON op.product_price_id = pp.product_price_id
             JOIN product p ON pp.product_id = p.product_id
             JOIN product_size ps ON pp.size_id = ps.size_id -- Join to get size name
             JOIN unit_of_measure uom ON ps.unit_id = uom.unit_id -- Join to get unit symbol
    WHERE o.status = 'COMPLETED'
      AND DATE(o.order_time) BETWEEN p_start_date AND p_end_date
    GROUP BY p.product_id, p.name, ps.name, uom.symbol -- Group by product and size
    ORDER BY total_quantity_sold DESC
    LIMIT p_limit;
END;

