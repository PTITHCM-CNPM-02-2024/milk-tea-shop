package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.order.response.OrderBasicResponse;
import com.mts.backend.application.staff.query.OrderByEmpIdQuery;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Service
public class GetAllOrderByEmpIdQueryHandler implements IQueryHandler<OrderByEmpIdQuery, CommandResult> {
    private final JpaOrderRepository orderRepository;
    
    public GetAllOrderByEmpIdQueryHandler(JpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(OrderByEmpIdQuery query) {
        Objects.requireNonNull(query, "OrderByEmpIdQuery must not be null");
        
        var fromDate =
                query.getFromDate().map(d -> d.atZone(ZoneId.systemDefault()).toInstant()).orElse(null);
        
        var toDate = 
                query.getToDate().map(d -> d.atZone(ZoneId.systemDefault()).toInstant()).orElse(null);
        
        var orders = orderRepository.findByEmployeeEntity_IdAndOrderTimeBetween(query.getEmployeeId().getValue(),
                fromDate,
                toDate, Pageable.ofSize(query.getSize()).withPage(query.getPage()));
        var response = orders
                .map(r -> {
                    OrderBasicResponse orderBasicResponse = OrderBasicResponse.builder()
                            .orderId(r.getId())
                            .customerId(r.getCustomerEntity().map(CustomerEntity::getId).orElse(null))
                            .employeeId(r.getEmployeeEntity().getId())
                            .orderTime(r.getOrderTime())
                            .finalAmount(r.getFinalAmount().map(Money::getValue).orElse(null))
                            .totalAmount(r.getTotalAmount().map(Money::getValue).orElse(null))
                            .orderStatus(r.getStatus().map(Enum::name).orElse(null))
                            .note(r.getCustomizeNote().orElse(null))
                            .build();
                    return orderBasicResponse;
                });
        
        return CommandResult.success(response);
    }
}
