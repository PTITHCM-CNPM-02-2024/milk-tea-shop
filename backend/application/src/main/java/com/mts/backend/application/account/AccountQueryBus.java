package com.mts.backend.application.account;

import com.mts.backend.application.account.query.AccountByIdQuery;
import com.mts.backend.application.account.query.DefaultAccountQuery;
import com.mts.backend.application.account.query.UserInfoQueryByIdQuery;
import com.mts.backend.application.account.query_handler.GetAccountByIdQueryHandler;
import com.mts.backend.application.account.query_handler.GetAllAccountQueryHandler;
import com.mts.backend.application.account.query_handler.GetUserInfoByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class AccountQueryBus extends AbstractQueryBus {
    
    public AccountQueryBus(GetAllAccountQueryHandler getallAccountQueryHandler,
                           GetAccountByIdQueryHandler getAccountByIdQueryHandler, GetUserInfoByIdQueryHandler getUserInfoByIdQueryHandler) {
        register(DefaultAccountQuery.class, getallAccountQueryHandler);
        register(AccountByIdQuery.class, getAccountByIdQueryHandler);
        register(UserInfoQueryByIdQuery.class, getUserInfoByIdQueryHandler);
    }
}
