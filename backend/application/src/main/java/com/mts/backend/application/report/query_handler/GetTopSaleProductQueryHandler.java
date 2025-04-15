package com.mts.backend.application.report.query_handler;

import com.mts.backend.application.report.query.TopSaleProductQuery;
import com.mts.backend.application.report.response.TopSaleProductResponse;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Objects;

@Service
public class GetTopSaleProductQueryHandler implements IQueryHandler<TopSaleProductQuery, CommandResult> {
    private final JpaOrderRepository orderRepository;
    
    public GetTopSaleProductQueryHandler(JpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(TopSaleProductQuery query) {
        Objects.requireNonNull(query, "TopSaleProductQuery must not be null");
        
        var fromDate = query.getFromDate().map(d -> d.atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);
        
        var toDate = query.getToDate().map(d -> d.atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);
        
       var result = orderRepository.findTopSaleByProduct( fromDate, toDate, Pageable.ofSize(query.getLimit()));
       
        if (result.isEmpty()) {
            return CommandResult.success();
        }
        
        var response = result.stream()
                .map(r -> {
                    var responseItem = TopSaleProductResponse.builder()
                            .productId(r[0] instanceof Integer id ? id : null)
                            .productName(r[1] instanceof ProductName name ? name.getValue() : null)
                            .categoryName(r[2] instanceof CategoryName name ? name.getValue() : null)
                            .quantity(r[3] instanceof Long quantity ? quantity : null)
                            .revenue(r[4] instanceof BigDecimal revenue ? revenue : null)
                            .build();
                    
                    return responseItem;
                })
                .toList();
        
        return CommandResult.success(response);
    }
}
