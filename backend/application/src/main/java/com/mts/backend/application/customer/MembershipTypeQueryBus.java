package com.mts.backend.application.customer;

import com.mts.backend.application.customer.query.DefaultMemberQuery;
import com.mts.backend.application.customer.query.MemberTypeByIdQuery;
import com.mts.backend.application.customer.query_handler.GetAllMemberTypeQueryHandler;
import com.mts.backend.application.customer.query_handler.GetMemberTypeByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class MembershipTypeQueryBus extends AbstractQueryBus {
    
    public MembershipTypeQueryBus (GetAllMemberTypeQueryHandler getAllMemberTypeQueryHandler, GetMemberTypeByIdQueryHandler getMemberTypeByIdQueryHandler){
        register(DefaultMemberQuery.class, getAllMemberTypeQueryHandler);
        register(MemberTypeByIdQuery.class, getMemberTypeByIdQueryHandler);
    }
}
