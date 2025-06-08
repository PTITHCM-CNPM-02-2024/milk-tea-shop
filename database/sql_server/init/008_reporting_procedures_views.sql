-- SQL Server 2022 Reporting Procedures and Views for Milk Tea Shop
-- Converted from MySQL V18.0.6__reporting_procedures_views.sql

USE MilkTeaShop;
GO

-- =====================================================
-- REPORTING STORED PROCEDURES
-- =====================================================

-- Procedure báo cáo doanh thu theo khoảng thời gian
CREATE PROCEDURE sp_report_revenue_by_date_range
    @p_start_date DATE,
    @p_end_date DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT
        CAST(o.order_time AS DATE) AS report_date,
        COUNT(o.order_id) AS total_orders,
        SUM(o.total_amount) AS total_revenue_before_discount,
        SUM(ISNULL(od.total_discount, 0)) AS total_discount_applied, -- Use ISNULL for orders without discounts
        SUM(o.final_amount) AS total_final_revenue
    FROM [order] o
    LEFT JOIN (
        SELECT
            order_id,
            SUM(discount_amount) AS total_discount
        FROM order_discount
        GROUP BY order_id
    ) od ON o.order_id = od.order_id
    WHERE o.status = 'COMPLETED' -- Chỉ tính các đơn hàng đã hoàn thành
      AND CAST(o.order_time AS DATE) BETWEEN @p_start_date AND @p_end_date
    GROUP BY CAST(o.order_time AS DATE)
    ORDER BY report_date;
END
GO

-- Procedure báo cáo doanh thu theo nhân viên trong khoảng thời gian
CREATE PROCEDURE sp_report_revenue_by_employee
    @p_start_date DATE,
    @p_end_date DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT
        e.employee_id,
        CONCAT(e.last_name, N' ', e.first_name) AS employee_name,
        e.position,
        COUNT(o.order_id) AS total_orders_handled,
        SUM(ISNULL(o.final_amount, 0)) AS total_revenue_generated -- Summing final amount handled by employee
    FROM employee e
    INNER JOIN [order] o ON e.employee_id = o.employee_id
    WHERE o.status = 'COMPLETED'
      AND CAST(o.order_time AS DATE) BETWEEN @p_start_date AND @p_end_date
    GROUP BY e.employee_id, e.last_name, e.first_name, e.position
    ORDER BY total_revenue_generated DESC, employee_name;
END
GO

-- Procedure báo cáo top sản phẩm bán chạy (theo số lượng) trong khoảng thời gian
CREATE PROCEDURE sp_report_top_selling_products
    @p_start_date DATE,
    @p_end_date DATE,
    @p_limit INT -- Số lượng sản phẩm top muốn hiển thị
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP (@p_limit)
        p.product_id,
        p.name AS product_name,
        ps.name AS size_name, -- Include size name
        uom.symbol AS unit_symbol, -- Include unit symbol
        SUM(op.quantity) AS total_quantity_sold,
        SUM(op.quantity * pp.price) AS total_revenue_from_product
    FROM order_product op
    INNER JOIN [order] o ON op.order_id = o.order_id
    INNER JOIN product_price pp ON op.product_price_id = pp.product_price_id
    INNER JOIN product p ON pp.product_id = p.product_id
    INNER JOIN product_size ps ON pp.size_id = ps.size_id -- Join to get size name
    INNER JOIN unit_of_measure uom ON ps.unit_id = uom.unit_id -- Join to get unit symbol
    WHERE o.status = 'COMPLETED'
      AND CAST(o.order_time AS DATE) BETWEEN @p_start_date AND @p_end_date
    GROUP BY p.product_id, p.name, ps.name, uom.symbol -- Group by product and size
    ORDER BY total_quantity_sold DESC;
END
GO

-- =====================================================
-- ADDITIONAL REPORTING PROCEDURES
-- =====================================================

-- Procedure báo cáo doanh thu theo danh mục sản phẩm
CREATE PROCEDURE sp_report_revenue_by_category
    @p_start_date DATE,
    @p_end_date DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT
        c.category_id,
        c.name AS category_name,
        COUNT(DISTINCT o.order_id) AS total_orders,
        SUM(op.quantity) AS total_quantity_sold,
        SUM(op.quantity * pp.price) AS total_revenue
    FROM category c
    INNER JOIN product p ON c.category_id = p.category_id
    INNER JOIN product_price pp ON p.product_id = pp.product_id
    INNER JOIN order_product op ON pp.product_price_id = op.product_price_id
    INNER JOIN [order] o ON op.order_id = o.order_id
    WHERE o.status = 'COMPLETED'
      AND CAST(o.order_time AS DATE) BETWEEN @p_start_date AND @p_end_date
    GROUP BY c.category_id, c.name
    ORDER BY total_revenue DESC;
END
GO

-- Procedure báo cáo khách hàng VIP (theo điểm tích lũy)
CREATE PROCEDURE sp_report_vip_customers
    @p_limit INT = 20
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP (@p_limit)
        c.customer_id,
        CONCAT(c.last_name, N' ', c.first_name) AS customer_name,
        c.phone,
        c.email,
        c.current_points,
        mt.type AS membership_type,
        COUNT(o.order_id) AS total_orders,
        SUM(o.final_amount) AS total_spent
    FROM customer c
    LEFT JOIN membership_type mt ON c.membership_type_id = mt.membership_type_id
    LEFT JOIN [order] o ON c.customer_id = o.customer_id AND o.status = 'COMPLETED'
    GROUP BY c.customer_id, c.last_name, c.first_name, c.phone, c.email, 
             c.current_points, mt.type
    ORDER BY c.current_points DESC, total_spent DESC;
END
GO

-- Procedure báo cáo hiệu suất bàn (table performance)
CREATE PROCEDURE sp_report_table_performance
    @p_start_date DATE,
    @p_end_date DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT
        a.area_id,
        a.name AS area_name,
        st.table_id,
        st.table_number,
        COUNT(ot.order_id) AS total_orders,
        SUM(o.final_amount) AS total_revenue,
        AVG(o.final_amount) AS average_order_value
    FROM area a
    INNER JOIN service_table st ON a.area_id = st.area_id
    LEFT JOIN order_table ot ON st.table_id = ot.table_id
    LEFT JOIN [order] o ON ot.order_id = o.order_id
    WHERE o.status = 'COMPLETED'
      AND CAST(o.order_time AS DATE) BETWEEN @p_start_date AND @p_end_date
    GROUP BY a.area_id, a.name, st.table_id, st.table_number
    ORDER BY total_revenue DESC;
END
GO

-- Procedure báo cáo discount sử dụng
CREATE PROCEDURE sp_report_discount_usage
    @p_start_date DATE,
    @p_end_date DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT
        d.discount_id,
        d.name AS discount_name,
        d.discount_value,
        d.discount_type,
        COUNT(od.order_id) AS times_used,
        SUM(od.discount_amount) AS total_discount_amount
    FROM discount d
    INNER JOIN order_discount od ON d.discount_id = od.discount_id
    INNER JOIN [order] o ON od.order_id = o.order_id
    WHERE o.status = 'COMPLETED'
      AND CAST(o.order_time AS DATE) BETWEEN @p_start_date AND @p_end_date
    GROUP BY d.discount_id, d.name, d.discount_value, d.discount_type
    ORDER BY total_discount_amount DESC;
END
GO

-- =====================================================
-- REPORTING VIEWS
-- =====================================================

-- View tổng quan doanh thu hàng ngày
CREATE VIEW vw_daily_revenue_summary
AS
SELECT
    CAST(o.order_time AS DATE) AS report_date,
    COUNT(o.order_id) AS total_orders,
    SUM(o.total_amount) AS gross_revenue,
    SUM(ISNULL(od.total_discount, 0)) AS total_discounts,
    SUM(o.final_amount) AS net_revenue,
    AVG(o.final_amount) AS average_order_value
FROM [order] o
LEFT JOIN (
    SELECT 
        order_id,
        SUM(discount_amount) AS total_discount
    FROM order_discount
    GROUP BY order_id
) od ON o.order_id = od.order_id
WHERE o.status = 'COMPLETED'
GROUP BY CAST(o.order_time AS DATE);
GO

-- View top sản phẩm bán chạy trong tuần
CREATE VIEW vw_weekly_top_products
AS
SELECT
    p.product_id,
    p.name AS product_name,
    SUM(op.quantity) AS total_quantity_sold,
    SUM(op.quantity * pp.price) AS total_revenue
FROM product p
INNER JOIN product_price pp ON p.product_id = pp.product_id
INNER JOIN order_product op ON pp.product_price_id = op.product_price_id
INNER JOIN [order] o ON op.order_id = o.order_id
WHERE o.status = 'COMPLETED'
  AND o.order_time >= DATEADD(DAY, -7, GETDATE())
GROUP BY p.product_id, p.name;
GO

-- View khách hàng hoạt động trong tháng
CREATE VIEW vw_monthly_active_customers
AS
SELECT
    c.customer_id,
    CONCAT(c.last_name, N' ', c.first_name) AS customer_name,
    c.phone,
    mt.type AS membership_type,
    COUNT(o.order_id) AS orders_this_month,
    SUM(o.final_amount) AS total_spent_this_month
FROM customer c
LEFT JOIN membership_type mt ON c.membership_type_id = mt.membership_type_id
INNER JOIN [order] o ON c.customer_id = o.customer_id
WHERE o.status = 'COMPLETED'
  AND o.order_time >= DATEADD(MONTH, -1, GETDATE())
GROUP BY c.customer_id, c.last_name, c.first_name, c.phone, mt.type;
GO

-- View hiệu suất nhân viên
CREATE VIEW vw_employee_performance
AS
SELECT
    e.employee_id,
    CONCAT(e.last_name, N' ', e.first_name) AS employee_name,
    e.position,
    COUNT(o.order_id) AS total_orders_handled,
    SUM(o.final_amount) AS total_revenue_generated,
    AVG(o.final_amount) AS average_order_value,
    MAX(o.order_time) AS last_order_time
FROM employee e
LEFT JOIN [order] o ON e.employee_id = o.employee_id AND o.status = 'COMPLETED'
GROUP BY e.employee_id, e.last_name, e.first_name, e.position;
GO

PRINT N'Đã tạo thành công các reporting procedures và views';
GO 