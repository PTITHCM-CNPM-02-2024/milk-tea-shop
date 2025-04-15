package com.mts.backend.application.report.query_handler;

import com.mts.backend.application.report.query.CatRevenueReportQuery;
import com.mts.backend.application.report.response.CatRevenueByTimeResponse;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.TimeZone;
@Service
public class GetCatRevenueReportQueryHandler implements IQueryHandler<CatRevenueReportQuery, CommandResult> {
    private final JpaOrderRepository orderRepository;
    
    public GetCatRevenueReportQueryHandler(JpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public CommandResult handle(CatRevenueReportQuery query) {
        Objects.requireNonNull(query, "CatRevenueReportQuery must not be null");
        
        var fromDate = query.getFromDate().map(d -> d.atZone(ZoneOffset.systemDefault()).toInstant())
                .orElse(null);
        var toDate = query.getToDate().map(d -> d.atZone(ZoneOffset.systemDefault()).toInstant())
                .orElse(null);
        
        var result = orderRepository.findFinalAmountsByCategoryAndDateRange(fromDate, toDate);
        
        if (result.isEmpty()) {
            return CommandResult.success();
        }
        
        var response = result.stream()
                .map(r -> {
                    var categoryName = r[0] instanceof CategoryName c ? c.getValue() : null;
                    var finalAmount = r[1] instanceof BigDecimal b ? b : null;
                    
                    var responseItem = CatRevenueByTimeResponse.builder()
                            .name(categoryName)
                            .revenue(finalAmount)
                            .fromDate(fromDate.atZone(ZoneOffset.systemDefault()).toLocalDateTime())
                            .toDate(toDate.atZone(ZoneOffset.systemDefault()).toLocalDateTime())
                            .build();
                    
                    return responseItem;
                })
                .toList();
        
        return CommandResult.success(response);
    }
}
