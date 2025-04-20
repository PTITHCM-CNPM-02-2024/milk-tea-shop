package com.mts.backend.api.report.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.application.report.ReportQueryBus;
import com.mts.backend.application.report.query.BasicReportQuery;
import com.mts.backend.application.report.query.CatRevenueReportQuery;
import com.mts.backend.application.report.query.OrderRevenueByTimeQuery;
import com.mts.backend.application.report.query.TopSaleProductQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;

import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/v1/reports")
@RestController
@Tag(name = "Report Controller", description = "Report")
public class ReportController implements IController {
    private final ReportQueryBus reportQueryBus;
    
    public ReportController(ReportQueryBus reportQueryBus) {
        this.reportQueryBus = reportQueryBus;
    }
    
    @GetMapping("/overview")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getOverviewReport() {
        
        var overviewReportQuery = new BasicReportQuery();
        
        var result = reportQueryBus.dispatch(overviewReportQuery);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/cat-revenue")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getCatRevenueReport(@RequestParam(value = "from", required = false) String from,
                                                 @RequestParam(value = "to", required = false) String to ){
        
        var catRevenueReportQuery = CatRevenueReportQuery.builder()
                .fromDate(Objects.isNull(from) ? null : LocalDateTime.parse(from))
                .toDate(Objects.isNull(to) ? null : LocalDateTime.parse(to))
                .build();
        
        var result = reportQueryBus.dispatch(catRevenueReportQuery);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/order-revenue")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getOrderRevenueReport(@RequestParam(value = "from", required = false) String from,
                                                    @RequestParam(value = "to", required = false) String to ) {

        var orderRevenueReportQuery = OrderRevenueByTimeQuery.builder()
                .fromDate(LocalDateTime.parse(from))
                .toDate(LocalDateTime.parse(to))
                .build();

        var result = reportQueryBus.dispatch(orderRevenueReportQuery);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }
    
    @GetMapping("/top-products")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getTopProductsReport(@RequestParam(value = "from", required = false) String from,
                                                   @RequestParam(value = "to", required = false) String to,
                                                  @RequestParam(value = "limit", defaultValue = "10") Integer limit) {

        var topProductsReportQuery = TopSaleProductQuery.builder()
                .fromDate(Objects.isNull(from) ? null : LocalDateTime.parse(from))
                .toDate(Objects.isNull(to) ? null : LocalDateTime.parse(to))
                .limit(limit)
                .build();

        var result = reportQueryBus.dispatch(topProductsReportQuery);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }
}
