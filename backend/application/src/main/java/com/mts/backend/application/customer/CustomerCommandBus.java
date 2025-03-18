package com.mts.backend.application.customer;

import com.mts.backend.application.customer.command.CreateCustomerCommand;
import com.mts.backend.application.customer.command.UpdateCustomerCommand;
import com.mts.backend.application.customer.command.UpdateMemberForCustomer;
import com.mts.backend.application.customer.handler.CreateCustomerCommandHandler;
import com.mts.backend.application.customer.handler.UpdateCustomerCommandHandler;
import com.mts.backend.application.customer.handler.UpdateMemberForCustomerCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class CustomerCommandBus extends AbstractCommandBus {
    
    public CustomerCommandBus (CreateCustomerCommandHandler createCustomerCommandHandler, UpdateMemberForCustomerCommandHandler updateMemberForCustomerCommandHandler, UpdateCustomerCommandHandler updateCustomerCommandHandler) {
        register(CreateCustomerCommand.class, createCustomerCommandHandler);
        register(UpdateCustomerCommand.class, updateCustomerCommandHandler);
        register(UpdateMemberForCustomer.class, updateMemberForCustomerCommandHandler);
    }
}
