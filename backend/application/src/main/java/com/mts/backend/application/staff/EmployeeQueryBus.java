package com.mts.backend.application.staff;

import com.mts.backend.application.staff.query.CheckoutTableByEmpIdQuery;
import com.mts.backend.application.staff.query.DefaultEmployeeQuery;
import com.mts.backend.application.staff.query.EmployeeByIdQuery;
import com.mts.backend.application.staff.query_handler.GetAllCheckoutTableByEmpIdQueryHandler;
import com.mts.backend.application.staff.query_handler.GetAllEmployeeQueryHandler;
import com.mts.backend.application.staff.query_handler.GetEmployeeByIdQueryHandler;
import com.mts.backend.application.staff.query_handler.GetEmpByAccountIdQueryHandler;
import com.mts.backend.application.staff.query.GetEmpByAccountIdQuery;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class EmployeeQueryBus extends AbstractQueryBus {
    
    public EmployeeQueryBus(GetAllEmployeeQueryHandler getAllEmployeeQueryHandler,
                            GetEmployeeByIdQueryHandler getEmployeeByIdQueryHandler,
                            GetAllCheckoutTableByEmpIdQueryHandler getAllCheckoutTableByEmpIdQueryHandler,
                            GetEmpByAccountIdQueryHandler getEmpByAccountIdQueryHandler){
        register(DefaultEmployeeQuery.class, getAllEmployeeQueryHandler);
        register(EmployeeByIdQuery.class, getEmployeeByIdQueryHandler);
        register(CheckoutTableByEmpIdQuery.class, getAllCheckoutTableByEmpIdQueryHandler);
        register(GetEmpByAccountIdQuery.class, getEmpByAccountIdQueryHandler);
    }
}
