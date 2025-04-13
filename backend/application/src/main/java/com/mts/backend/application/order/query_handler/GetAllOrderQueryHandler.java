package com.mts.backend.application.order.query_handler;

import com.mts.backend.application.order.response.OrderBasicResponse;
import com.mts.backend.application.order.response.OrderProductResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mts.backend.application.order.query.DefaultOrderQuery;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;

@Service
public class GetAllOrderQueryHandler implements IQueryHandler<DefaultOrderQuery, CommandResult> {
    private final JpaOrderRepository orderRepository;

    public GetAllOrderQueryHandler(JpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public CommandResult handle(DefaultOrderQuery query) {
        var orders = orderRepository.findAllFetchEmpCus(PageRequest.of(query.getPage(), query.getSize()));
        
        Page<OrderBasicResponse> result = orders.map(order -> OrderBasicResponse.builder()
            .orderId(order.getId())
            .customerId(order.getCustomerEntity().map(CustomerEntity::getId).orElse(null))
            .employeeId(order.getEmployeeEntity().getId())
            .orderTime(order.getOrderTime())
            .orderStatus(order.getStatus().map(Enum::name).orElse(null))
            .totalAmount(order.getTotalAmount().map(Money::getValue).orElse(null))
                .finalAmount(order.getFinalAmount().map(Money::getValue).orElse(null))
                .note(order.getCustomizeNote().orElse(null))
            .build());
        
        return CommandResult.success(result);
    }
}