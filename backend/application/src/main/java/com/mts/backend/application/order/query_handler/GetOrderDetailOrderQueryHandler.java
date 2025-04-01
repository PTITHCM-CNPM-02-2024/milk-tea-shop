package com.mts.backend.application.order.query_handler;

import com.mts.backend.application.order.query.OrderDetailQuery;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

@Service
public class GetOrderDetailOrderQueryHandler implements IQueryHandler<OrderDetailQuery, CommandResult> {

    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(OrderDetailQuery query) {
        return null;
    }
}
