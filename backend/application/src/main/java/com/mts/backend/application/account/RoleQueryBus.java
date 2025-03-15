package com.mts.backend.application.account;

import com.mts.backend.application.account.query.DefaultRoleQuery;
import com.mts.backend.application.account.query.RoleByIdQuery;
import com.mts.backend.application.account.query_handler.GetAllRoleQueryHandler;
import com.mts.backend.application.account.query_handler.GetRoleByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class RoleQueryBus extends AbstractQueryBus {
    public RoleQueryBus(GetAllRoleQueryHandler getAllRoleQueryHandler, GetRoleByIdQueryHandler getRoleByIdQueryHandler) {
        register(DefaultRoleQuery.class, getAllRoleQueryHandler);
        register(RoleByIdQuery.class, getRoleByIdQueryHandler);
    }
}
