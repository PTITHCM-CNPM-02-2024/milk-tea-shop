package com.mts.backend.application.order;

import com.mts.backend.application.order.query.DefaultOrderQuery;
import com.mts.backend.application.order.query.OrderByIdQuery;
import com.mts.backend.application.order.query_handler.GetAllOrderQueryHandler;
import com.mts.backend.application.order.query_handler.GetOrderByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class OrderQueryBus extends AbstractQueryBus {
    public OrderQueryBus(GetAllOrderQueryHandler getAllOrderQueryHandler, GetOrderByIdQueryHandler getOrderByIdQueryHandler) {
        register(DefaultOrderQuery.class, getAllOrderQueryHandler);
        register(OrderByIdQuery.class, getOrderByIdQueryHandler);
    }
}
