package com.mts.backend.application.order.query_handler;

import com.mts.backend.application.order.query.OrderDetailQuery;
import com.mts.backend.domain.order.repository.IOrderRepository;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.promotion.repository.IDiscountRepository;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

@Service
public class GetOrderDetailOrderQueryHandler implements IQueryHandler<OrderDetailQuery, CommandResult> {
    private IOrderRepository orderRepository;
    private IProductRepository productRepository;
    private IServiceTableRepository serviceTableRepository;
    private IDiscountRepository discountRepository;
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(OrderDetailQuery query) {
        return null;
    }
}
