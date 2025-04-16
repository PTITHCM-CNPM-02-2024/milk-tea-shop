package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.report.response.OverviewReportResponse;
import com.mts.backend.application.staff.query.OrderOverviewByEmpIdQuery;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Objects;

@Service
@AllArgsConstructor
public class GetOrderOverviewByEmpIdReportQueryHandler implements IQueryHandler<OrderOverviewByEmpIdQuery, CommandResult> {
    private final JpaOrderRepository orderRepository;
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(OrderOverviewByEmpIdQuery query) {

        Objects.requireNonNull(query, "OrderOverviewByEmpIdQuery must not be null");

        var fromDate = query.getFromDate().map(d -> d.atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);

        var toDate = query.getToDate().map(d -> d.atZone(ZoneId.systemDefault()).toInstant())

                .orElse(null);
        
        
        var sum = orderRepository.getSumRevenueByEmpId(query.getId().getValue(), fromDate, toDate);
        var avg = orderRepository.getAvgRevenueByEmpId(query.getId().getValue(), fromDate, toDate);
        var min = orderRepository.getMaxRevenueByEmpId(query.getId().getValue(), fromDate, toDate);
        var max = orderRepository.getMinRevenueByEmpId(query.getId().getValue(), fromDate, toDate);

        var response = OverviewReportResponse.builder()
                .sumOrderValue(sum)
                .avgOrderValue(avg)
                .minOrderValue(min != null ? min.getValue() : null)
                .maxOrderValue(max != null ? max.getValue() : null)
                .build();

        return CommandResult.success(response);
    }
}
