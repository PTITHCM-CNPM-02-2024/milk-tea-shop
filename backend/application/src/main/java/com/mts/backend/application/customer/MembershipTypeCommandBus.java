package com.mts.backend.application.customer;

import com.mts.backend.application.customer.command.CreateMembershipCommand;
import com.mts.backend.application.customer.command.UpdateMemberCommand;
import com.mts.backend.application.customer.handler.CreateMembershipCommandHandler;
import com.mts.backend.application.customer.handler.UpdateMemberCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class MembershipTypeCommandBus extends AbstractCommandBus {
    
    public MembershipTypeCommandBus (CreateMembershipCommandHandler createMembershipCommandHandler, UpdateMemberCommandHandler updateMemberCommandHandler) {
        register(UpdateMemberCommand.class, updateMemberCommandHandler);
        register(CreateMembershipCommand.class, createMembershipCommandHandler);
    }
}
