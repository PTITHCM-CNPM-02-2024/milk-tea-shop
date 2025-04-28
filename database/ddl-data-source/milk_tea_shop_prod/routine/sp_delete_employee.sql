create procedure milk_tea_shop_prod.sp_delete_employee(IN p_employee_id int unsigned)
BEGIN
    -- Consider dependencies (`order`.employee_id ON DELETE CASCADE)
    -- Deleting an employee will cascade delete their associated orders.
    -- This might be undesirable; consider changing the foreign key constraint or deactivating the employee instead.
    DELETE FROM employee WHERE employee_id = p_employee_id;
END;

