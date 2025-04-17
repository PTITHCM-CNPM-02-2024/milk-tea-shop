create procedure milk_tea_shop_prod.sp_report_revenue_by_employee(IN p_start_date date, IN p_end_date date)
BEGIN
    SELECT
        e.employee_id,
        CONCAT(e.last_name, ' ', e.first_name) AS employee_name,
        e.position,
        COUNT(o.order_id) AS total_orders_handled,
        SUM(IFNULL(o.final_amount, 0)) AS total_revenue_generated -- Summing final amount handled by employee
    FROM employee e
             JOIN `order` o ON e.employee_id = o.employee_id
    WHERE o.status = 'COMPLETED'
      AND DATE(o.order_time) BETWEEN p_start_date AND p_end_date
    GROUP BY e.employee_id, employee_name, e.position
    ORDER BY total_revenue_generated DESC, employee_name;
END;

