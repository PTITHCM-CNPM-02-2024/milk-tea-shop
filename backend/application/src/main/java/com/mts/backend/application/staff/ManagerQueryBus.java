package com.mts.backend.application.staff;

import com.mts.backend.application.staff.query.DefaultManagerQuery;
import com.mts.backend.application.staff.query.ManagerByIdQuery;
import com.mts.backend.application.staff.query.GetManagerByAccountIdQuery;
import com.mts.backend.application.staff.query_handler.GetAllManagerQueryHandler;
import com.mts.backend.application.staff.query_handler.GetManagerByIdQueryHandler;
import com.mts.backend.application.staff.query_handler.GetManagerByAccountIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class ManagerQueryBus extends AbstractQueryBus {
    public ManagerQueryBus (GetAllManagerQueryHandler getAllManagerQueryHandler, GetManagerByIdQueryHandler getManagerByIdQueryHandler, GetManagerByAccountIdQueryHandler getManagerByAccountIdQueryHandler) {
        register(DefaultManagerQuery.class, getAllManagerQueryHandler);
        register(ManagerByIdQuery.class, getManagerByIdQueryHandler);
        register(GetManagerByAccountIdQuery.class, getManagerByAccountIdQueryHandler);
    }
}
