package com.mts.backend.application.report.query_handler;

import com.mts.backend.application.report.query.OrderRevenueByTimeQuery;
import com.mts.backend.application.report.response.OrderRevenueByTimeResponse;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class GetOrderRevenueByTimeQueryHandler implements IQueryHandler<OrderRevenueByTimeQuery, CommandResult> {
    private final JpaOrderRepository orderRepository;
    
    public GetOrderRevenueByTimeQueryHandler(JpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(OrderRevenueByTimeQuery query) {
        Objects.requireNonNull(query, "OrderRevenueByTimeQuery must not be null");
        
        var fromDate = query.getFromDate().map(d -> d.atZone(ZoneId.systemDefault())
                .toInstant())
                .orElse(null);
        
        var toDate = query.getToDate().map(d -> d.atZone(ZoneId.systemDefault())
                .toInstant())
                .orElse(null);
        
        var result = orderRepository.findRevenueByTimeRange(fromDate, toDate);
        
        if (result.isEmpty()) {
            return CommandResult.success();
        }
        
        var response = result.stream()
                .map(r -> {
                    var orderDate = r[0] instanceof Date i ? i : Optional.empty();
                    var revenue = r[1] instanceof BigDecimal b ? b : null;
                    
                    return OrderRevenueByTimeResponse.builder()
                            .date(orderDate.toString())
                            .revenue(revenue)
                            .build();
                })
                .toList();
        
        return CommandResult.success(response);
    }
}
