package com.mts.backend.application.customer;

import com.mts.backend.application.customer.query.CustomerByIdQuery;
import com.mts.backend.application.customer.query.CustomerByPhoneQuery;
import com.mts.backend.application.customer.query.DefaultCustomerQuery;
import com.mts.backend.application.customer.query_handler.GetAllCustomerQueryHandler;
import com.mts.backend.application.customer.query_handler.GetCustomerByIdQueryHandler;
import com.mts.backend.application.customer.query_handler.GetCustomerByPhoneQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class CustomerQueryBus extends AbstractQueryBus {
    
    public CustomerQueryBus (GetAllCustomerQueryHandler getAllCustomerQueryHandler, GetCustomerByPhoneQueryHandler getCustomerByPhoneQueryHandler, GetCustomerByIdQueryHandler getCustomerByIdQueryHandler){
        register(DefaultCustomerQuery.class, getAllCustomerQueryHandler);
        register(CustomerByPhoneQuery.class, getCustomerByPhoneQueryHandler);
        register(CustomerByIdQuery.class, getCustomerByIdQueryHandler);
    }
}
