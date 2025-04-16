package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.report.response.OrderRevenueByTimeResponse;
import com.mts.backend.application.staff.query.OrderByEmpIdQuery;
import com.mts.backend.application.staff.query.OrderRevenueByTimeAndEmpIdQuery;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
public class GetOrderRevenueByTimeAndEmpIdQueryHandler implements IQueryHandler<OrderRevenueByTimeAndEmpIdQuery, CommandResult> {
    private final JpaOrderRepository orderRepository;


    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(OrderRevenueByTimeAndEmpIdQuery query) {
        Objects.requireNonNull(query,"OrderByEmpIdQuery must not be null");
        
        var fromDate = query.getFromDate().map(d -> d.atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);
        
        var toDate = query.getToDate().map(d -> d.atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);
        
        var result = orderRepository.findRevenueByTimeRange(query.getId().getValue(),
                fromDate, toDate);
        
        var response = result.stream().map(
                r -> {
                    var orderDate = r[0] instanceof java.util.Date i ? i : Optional.empty();
                    var revenue = r[1] instanceof java.math.BigDecimal b ? b : null;
                    
                    return OrderRevenueByTimeResponse.builder()
                            .date(orderDate.toString())
                            .revenue(revenue)
                            .build();
                })
                .toList();
        
        return CommandResult.success(response);
    }
}
