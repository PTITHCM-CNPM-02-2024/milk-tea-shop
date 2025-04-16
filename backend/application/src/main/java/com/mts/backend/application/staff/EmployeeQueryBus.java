package com.mts.backend.application.staff;

import com.mts.backend.application.staff.query.CheckoutTableByEmpIdQuery;
import com.mts.backend.application.staff.query.DefaultEmployeeQuery;
import com.mts.backend.application.staff.query.EmployeeByIdQuery;
import com.mts.backend.application.staff.query_handler.*;
import com.mts.backend.application.staff.query.GetEmpByAccountIdQuery;
import com.mts.backend.application.staff.query.OrderByEmpIdQuery;
import com.mts.backend.application.staff.query.OrderOverviewByEmpIdQuery;
import com.mts.backend.application.staff.query.OrderRevenueByTimeAndEmpIdQuery;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class EmployeeQueryBus extends AbstractQueryBus {
    
    public EmployeeQueryBus(GetAllEmployeeQueryHandler getAllEmployeeQueryHandler,
                            GetEmployeeByIdQueryHandler getEmployeeByIdQueryHandler,
                            GetAllCheckoutTableByEmpIdQueryHandler getAllCheckoutTableByEmpIdQueryHandler,
                            GetEmpByAccountIdQueryHandler getEmpByAccountIdQueryHandler,
                            GetOrderOverviewByEmpIdReportQueryHandler getOrderOverviewByEmpIdReportQueryHandler,
                            GetOrderRevenueByTimeAndEmpIdQueryHandler getOrderRevenueByTimeAndEmpIdQueryHandler,
                            GetAllOrderByEmpIdQueryHandler getAllOrderByEmpIdQueryHandler){
        register(DefaultEmployeeQuery.class, getAllEmployeeQueryHandler);
        register(EmployeeByIdQuery.class, getEmployeeByIdQueryHandler);
        register(CheckoutTableByEmpIdQuery.class, getAllCheckoutTableByEmpIdQueryHandler);
        register(GetEmpByAccountIdQuery.class, getEmpByAccountIdQueryHandler);
        register(OrderOverviewByEmpIdQuery.class, getOrderOverviewByEmpIdReportQueryHandler);
        register(OrderRevenueByTimeAndEmpIdQuery.class, getOrderRevenueByTimeAndEmpIdQueryHandler);
        register(OrderByEmpIdQuery.class, getAllOrderByEmpIdQueryHandler);
    }
}
